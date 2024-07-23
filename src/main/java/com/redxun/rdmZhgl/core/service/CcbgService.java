package com.redxun.rdmZhgl.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.org.api.model.IGroup;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.dao.CcbgDao;
import com.redxun.rdmZhgl.core.dao.JsmmDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.sys.org.manager.GroupServiceImpl;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

/**
 * 应用模块名称
 * <p>
 * 代码描述
 * <p>
 * Copyright: Copyright (C) 2021 XXX, Inc. All rights reserved.
 * <p>
 * Company: 徐工挖掘机械有限公司
 * <p>
 *
 * @author liwenguang
 * @since 2021/2/23 10:43
 */
@Service
public class CcbgService {
    private Logger logger = LoggerFactory.getLogger(CcbgService.class);
    @Autowired
    private CcbgDao ccbgDao;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private BpmInstManager bpmInstManager;

    //..
    public JsonPageResult<?> getCcgbList(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    // 数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
                    params.put(name, value);
                }
            }
        }
        // 增加分页条件
        if (doPage) {
            params.put("startIndex", 0);
            params.put("pageSize", 20);
            String pageIndex = request.getParameter("pageIndex");
            String pageSize = request.getParameter("pageSize");
            if (StringUtils.isNotBlank(pageIndex) && StringUtils.isNotBlank(pageSize)) {
                params.put("startIndex", Integer.parseInt(pageSize) * Integer.parseInt(pageIndex));
                params.put("pageSize", Integer.parseInt(pageSize));
            }
        }

        // 增加角色过滤的条件（需要自己办理的目前已包含在下面的条件中）方法保留，暂时不用
//        addCcgbRoleParam(params, ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        params.put("currentUserId", ContextUtil.getCurrentUserId());
        List<Map<String, Object>> ccbgList = ccbgDao.queryCcbgList(params);
        for (Map<String, Object> ccbg : ccbgList) {
            if (ccbg.get("CREATE_TIME_") != null) {
                ccbg.put("CREATE_TIME_", DateUtil.formatDate((Date) ccbg.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
            //获取附件名称列表
            List<String> FileNamesList = ccbgDao.getFileNamesListByMainId(ccbg.get("id"));
            //如果有附件名称
            if (FileNamesList.size() > 0) {
                ccbg.put("fileName", FileNamesList.get(0).split(",")[0]);
                ccbg.put("firstFileId", FileNamesList.get(0).split(",")[1]);
                ccbg.put("fileSize", FileNamesList.get(0).split(",")[2]);
            }
        }
        // 查询当前处理人
        xcmgProjectManager.setTaskCurrentUser(ccbgList);
        result.setData(ccbgList);
        int countCcbgDataList = ccbgDao.countCcbgList(params);
        result.setTotal(countCcbgDataList);
        return result;
    }

    //..
    private void addCcgbRoleParam(Map<String, Object> params, String userId, String userNo) {
        boolean ccbgzy = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), RdmConst.CCBGZY);
        if (!userNo.equalsIgnoreCase("admin") && !ccbgzy) {
            params.put("currentUserId", userId);
            //boolean isFgld = rdmZhglUtil.judgeUserIsFgld(userId);
            boolean isFgld = rdmZhglUtil.judgeIsPointRoleUser(userId, RdmConst.AllDATA_QUERY_NAME);
            if (!isFgld) {
                isFgld = rdmZhglUtil.judgeIsPointRoleUser(userId, RdmConst.JSZX_DZBSJ);
            }
            boolean isFgzr = rdmZhglUtil.judgeIsPointRoleUser(userId, RdmConst.JSZX_ZR);
            if (isFgld) {
                params.put("roleName", "fgld");
                return;
            } else if (isFgzr) {
                params.put("roleName", "fgzr");
            } else {//不是分管领导就看本部门的
                params.put("roleName", "other");
                Map<String, Object> queryUserParam = new HashMap<>();
                queryUserParam.put("userId", userId);
                List<String> typeKeyList = new ArrayList<>();
                typeKeyList.add("GROUP-USER-BELONG");
                queryUserParam.put("typeKeyList", typeKeyList);
                List<Map<String, Object>> queryDeptResult = xcmgProjectOtherDao.queryUserDeps(queryUserParam);
                if (queryDeptResult != null && !queryDeptResult.isEmpty()) {
                    params.put("groupId", queryDeptResult.get(0).get("PARTY1_"));
                    return;
                }

            }
        }
    }

    public JSONObject getCcbgDetail(String ccbgId) {
        JSONObject detailObj = ccbgDao.queryCcbgById(ccbgId);
        if (detailObj == null) {
            return new JSONObject();
        }

        return detailObj;
    }

    //..
    public void createCcbg(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        //@lwgkiller:此处是因为草稿状态无节点，提交后首节点会跳过，因此默认将首节点（编制中）的编号进行初始化写入
        formData.put("businessStatus", "editing");
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        ccbgDao.insertCcbg(formData);
    }

    //..
    public void updateCcbg(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        ccbgDao.updateCcbg(formData);
    }

    //..
    public List<JSONObject> getCcbgFileList(List<String> ccbgIdList) {
        List<JSONObject> ccbgFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("ccbgIds", ccbgIdList);
        ccbgFileList = ccbgDao.queryCcbgFileList(param);
        return ccbgFileList;
    }

    //..
    public void saveUploadFiles(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            logger.warn("没有找到上传的参数");
            return;
        }
        // 多附件上传需要用到的MultipartHttpServletRequest
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap == null || fileMap.isEmpty()) {
            logger.warn("没有找到上传的文件");
            return;
        }
        String filePathBase = WebAppUtil.getProperty("ccbgFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find ccbgFilePathBase");
            return;
        }
        try {
            String ccbgId = toGetParamVal(parameters.get("ccbgId"));
            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String fileDesc = toGetParamVal(parameters.get("fileDesc"));

            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();

            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + ccbgId;
            File pathFile = new File(filePath);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fileFullPath = filePath + File.separator + id + "." + suffix;
            File file = new File(fileFullPath);
            FileCopyUtils.copy(mf.getBytes(), file);

            // 写入数据库
            JSONObject fileInfo = new JSONObject();
            fileInfo.put("id", id);
            fileInfo.put("fileName", fileName);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("ccbgId", ccbgId);
            fileInfo.put("fileDesc", fileDesc);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            ccbgDao.addCcbgFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    //..
    public void deleteOneCcbgFile(String fileId, String fileName, String ccbgId) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, ccbgId, WebAppUtil.getProperty("ccbgFilePathBase"));
        Map<String, Object> param = new HashMap<>();
        param.put("id", fileId);
        ccbgDao.deleteCcbgFile(param);
    }


    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }

    //..
    public JsonResult deleteCcbg(String[] ids, String[] instIds) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> ccbgIds = Arrays.asList(ids);
        List<JSONObject> files = getCcbgFileList(ccbgIds);
        String ccbgFilePathBase = WebAppUtil.getProperty("ccbgFilePathBase");
        for (JSONObject oneFile : files) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"),
                    oneFile.getString("fileName"), oneFile.getString("reportId"), ccbgFilePathBase);
        }
        for (String oneCcbgId : ids) {
            rdmZhglFileManager.deleteDirFromDisk(oneCcbgId, ccbgFilePathBase);
        }
        Map<String, Object> param = new HashMap<>();
        param.put("ccbgIds", ccbgIds);
        ccbgDao.deleteCcbgFile(param);
        ccbgDao.deleteCcbg(param);
        for (String oneInstId : instIds) {
            // 删除实例,不是同步删除，但是总量是能一对一的
            bpmInstManager.deleteCascade(oneInstId, "");
        }
        return result;
    }
}
