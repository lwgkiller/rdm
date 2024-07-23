package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.serviceEngineering.core.dao.JxcshcDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import org.apache.commons.lang.StringUtils;
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
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class JxcshcService {
    private static Logger logger = LoggerFactory.getLogger(JxcshcService.class);
    @Autowired
    private JxcshcDao jxcshcDao;

    @Autowired
    private XcmgProjectManager xcmgProjectManager;

    @Autowired
    private RdmZhglUtil rdmZhglUtil;

    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;

    // ..
    public JsonPageResult<?> jxcshcListQuery(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
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
        // 增加角色过滤的条件（需要自己办理的目前已包含在下面的条件中）
        addJxcshcRoleParam(params, ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<Map<String, Object>> list = jxcshcDao.jxcshcListQuery(params);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Map<String, Object> jxcshc : list) {
            if (jxcshc.get("CREATE_TIME_") != null) {
                jxcshc.put("CREATE_TIME_", sdf.format(jxcshc.get("CREATE_TIME_")));
            }
        }
        // 查询当前处理人
        xcmgProjectManager.setTaskCurrentUser(list);
        result.setData(list);
        int count = jxcshcDao.countJxcshcQuery(params);
        result.setTotal(count);
        return result;
    }

    // 1、admin所有，相当于不加条件；
    // 2、分管领导、服务工程人员、其他人看所有提交的，自己的；
    private void addJxcshcRoleParam(Map<String, Object> params, String userId, String userNo) {
        params.put("currentUserId", userId);
        if (userNo.equalsIgnoreCase("admin")) {
            return;
        }
        boolean isAllDataQuery = rdmZhglUtil.judgeIsPointRoleUser(userId, RdmConst.AllDATA_QUERY_NAME);
        if (isAllDataQuery) {
            params.put("roleName", "fgld");
            return;
        }
        params.put("roleName", "ptyg");
    }

    // ..
    public JsonResult deleteJxcshc(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        Map<String, Object> param = new HashMap<>();
        param.put("ids", businessIds);
        param.put("masterIds", businessIds);
        List<JSONObject> files = jxcshcDao.queryFileList(param);
        String filePathBase = WebAppUtil.getProperty("jxbzzbFilePathBase");
        if (files.size() > 0) {
            for (JSONObject oneFile : files) {
                rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"), oneFile.getString("fileName"),
                    oneFile.getString("masterId"), filePathBase);
            }
            for (String id : ids) {
                rdmZhglFileManager.deleteDirFromDisk(id, filePathBase);
            }
            jxcshcDao.delFileByMasterId(param);
        }
        jxcshcDao.deleteJxcshc(param);
        return result;
    }

    public JSONObject getJxcshcDetail(String jxcshcId) {
        JSONObject detailObj = jxcshcDao.queryJxcshcById(jxcshcId);
        if (detailObj == null) {
            return new JSONObject();
        }
        return detailObj;
    }

    public void createJxcshc(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        jxcshcDao.insertJxcshc(formData);
    }

    public void updateJxcshc(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        jxcshcDao.updateJxcshc(formData);
    }

    public void saveUploadFiles(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            logger.warn("没有找到上传的参数");
            return;
        }
        // 多附件上传需要用到的MultipartHttpServletRequest
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap == null || fileMap.isEmpty()) {
            logger.warn("没有找到上传的文件");
            return;
        }
        String filePathBase = WebAppUtil.getProperty("jxbzzbFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find jxbzzbFilePathBase");
            return;
        }
        try {
            String id = null;
            String masterId = toGetParamVal(parameters.get("masterId"));
            String productType = toGetParamVal(parameters.get("productType"));
            String versionType = toGetParamVal(parameters.get("versionType"));
            String applicationNumber = toGetParamVal(parameters.get("applicationNumber"));
            String salesModel = toGetParamVal(parameters.get("salesModel"));
            String fileType = toGetParamVal(parameters.get("fileType"));
            String fileLanguage = toGetParamVal(parameters.get("fileLanguage"));
            String fileVersionType = toGetParamVal(parameters.get("fileVersionType"));
            if("1001".equals(masterId)){
                id=fileType;
            }else {
                id = IdUtil.getId();
            }

            String fileName = toGetParamVal(parameters.get("fileName"));
            if (!StringUtils.isBlank(productType) && !StringUtils.isBlank(versionType)
                && !StringUtils.isBlank(applicationNumber) && !StringUtils.isBlank(salesModel)) {
                String productTypeText = "履挖";
                String str = "-检修标准值表-";
                String versionTypeText = "";
                String endStr = ".xlsx";
                if (fileName.endsWith(".pdf")) {
                    endStr = ".pdf";
                } else if (fileName.endsWith(".xls")) {
                    endStr = ".xls";
                }
                if (productType.equals("lunWa")) {
                    productTypeText = "轮挖";
                }else if (productType.equals("teWa")) {
                    productTypeText = "特挖";
                }else if (productType.equals("dianWa")) {
                    productTypeText = "电挖";
                }
                if (fileVersionType.equals("常规版")) {
                    versionTypeText = "常规版";
                } else if (fileVersionType.equals("测试版")) {
                    versionTypeText = "测试版";
                } else if (fileVersionType.equals("完整版")){
                    versionTypeText = "完整版";
                }else if (fileVersionType.equals("测试版修订")){
                    versionTypeText = "测试版修订";
                }
                fileName =
                    productTypeText + str + versionTypeText + "-" + salesModel + "-" + applicationNumber + endStr;
                Map<String, Object> map = new HashMap<>();
                map.put("masterId", masterId);
                map.put("fileName", fileName);
                map.put("fileLanguage", fileLanguage);
                JSONObject file = jxcshcDao.queryFileByMasterIdAndFileName(map);
                if (file != null) {
                    String fileId = file.getString("id");
                    rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, masterId, filePathBase);
                    jxcshcDao.deleteFileById(fileId);
                }
            }
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();
            // 写入数据库
            JSONObject fileInfo = new JSONObject();
            fileInfo.put("id", id);
            fileInfo.put("fileName", fileName);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("masterId", masterId);
            fileInfo.put("fileType", fileType);
            fileInfo.put("fileLanguage", fileLanguage);
            fileInfo.put("fileVersionType", fileVersionType);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            jxcshcDao.addFileInfos(fileInfo);

            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + masterId;
            File pathFile = new File(filePath);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fileFullPath = filePath + File.separator + id + "." + suffix;
            File file = new File(fileFullPath);
            FileCopyUtils.copy(mf.getBytes(), file);

        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }

    public List<JSONObject> queryFileList(Map<String, Object> params) {
        List<JSONObject> fileList = jxcshcDao.queryFileList(params);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (fileList.size() > 0) {
            for (JSONObject file : fileList) {
                if (file.get("CREATE_TIME_") != null) {
                    file.put("CREATE_TIME_", sdf.format(file.get("CREATE_TIME_")));
                }
            }
        }
        return fileList;
    }


    public void delUploadFile(String id, String fileName, String masterId) {
        String filePath = WebAppUtil.getProperty("jxbzzbFilePathBase");
        rdmZhglFileManager.deleteOneFileFromDisk(id, fileName, masterId, filePath);
        jxcshcDao.deleteFileById(id);
    }

}
