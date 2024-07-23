package com.redxun.rdmZhgl.core.service;

import java.io.File;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.rdmCommon.core.util.RdmConst;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmZhgl.core.dao.JsmmDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;

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
 * @author liangchuanjiang
 * @since 2021/2/23 10:43
 */
@Service
public class JsmmService {
    private Logger logger = LoggerFactory.getLogger(JsmmService.class);
    @Autowired
    private JsmmDao jsmmDao;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;

    public JsonPageResult<?> getJsmmList(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        rdmZhglUtil.addOrder(request, params, "jsmmNumber", "desc");
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    // 数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
                    if ("rdTimeStart".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8));
                    }
                    if ("rdTimeEnd".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16));
                    }
                    params.put(name, value);
                }
            }
        }
        // 增加分页条件
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
        }

        // 增加角色过滤的条件（需要自己办理的目前已包含在下面的条件中）
        addJsmmRoleParam(params, ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<Map<String, Object>> jsmmList = jsmmDao.queryJsmmList(params);
        for (Map<String, Object> jsmm : jsmmList) {
            if (jsmm.get("jlTime") != null) {
                jsmm.put("jlTime", DateUtil.formatDate((Date)jsmm.get("jlTime"), "yyyy-MM-dd"));
            }
            if (jsmm.get("rdTime") != null) {
                jsmm.put("rdTime", DateUtil.formatDate((Date)jsmm.get("rdTime"), "yyyy-MM-dd"));
            }
            if (jsmm.get("CREATE_TIME_") != null) {
                jsmm.put("CREATE_TIME_", DateUtil.formatDate((Date)jsmm.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        // 查询当前处理人
        xcmgProjectManager.setTaskCurrentUser(jsmmList);
        result.setData(jsmmList);
        int countJsmmDataList = jsmmDao.countJsmmList(params);
        result.setTotal(countJsmmDataList);
        return result;
    }

    // 1、admin所有，相当于不加条件；
    // 2、分管领导、专利工程师、计管部负责人看所有提交的，自己的；
    // 3、本部门负责人看本部门已提交的，自己的，可见的已认定成功的；
    // 4、其他人看可见的已认定成功的，自己的。
    private void addJsmmRoleParam(Map<String, Object> params, String userId, String userNo) {
        params.put("currentUserId", userId);
        if (userNo.equalsIgnoreCase("admin")) {
            return;
        }
        boolean isFgld = rdmZhglUtil.judgeUserIsFgld(userId);
        if (isFgld) {
            params.put("roleName", "fgld");
            return;
        }
        boolean isZlgcs = rdmZhglUtil.judgeIsPointRoleUser(userId, RdmConst.ZLGCS);
        if (isZlgcs) {
            params.put("roleName", "zlgcs");
            return;
        }
        boolean isJsglbRespUser = rdmZhglUtil.judgeUserIsJsglbRespUser(userId);
        if (isJsglbRespUser) {
            params.put("roleName", "jsglbRespUser");
            return;
        }
        JSONObject currentUserDepInfo = xcmgProjectManager.isCurrentUserDepRespman();
        if (currentUserDepInfo.getString("result").equalsIgnoreCase("success")
            && currentUserDepInfo.getBooleanValue("isDepRespMan")) {
            params.put("roleName", "deptRespUser");
            params.put("deptId", currentUserDepInfo.getString("currentUserMainDepId"));
            return;
        }
        params.put("roleName", "ptyg");
    }

    public void exportJsmmList(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = getJsmmList(request, response, false);
        List<Map<String, Object>> listData = result.getData();
        for (Map<String, Object> oneMap : listData) {
            oneMap.put("statusName", convertStatusName(oneMap.get("status")));
            oneMap.put("whetherJLName", convertJLStatusName(oneMap.get("whetherJL")));
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "技术秘密列表";
        String excelName = nowDate + title;
        String[] fieldNames = {"秘密编号", "秘密名称", "知悉范围", "申请人", "申请人部门", "申请时间", "认定状态", "认定时间", "奖励状态", "奖励时间", "完成人"};
        String[] fieldCodes = {"jsmmNumber", "jsmmName", "readUserNames", "applyUserName", "applyUserDeptName",
            "CREATE_TIME_", "statusName", "rdTime", "whetherJLName", "jlTime", "finishUserNames"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    private String convertStatusName(Object statusKey) {
        if (statusKey == null) {
            return "";
        }
        String statusKeyStr = statusKey.toString();
        switch (statusKeyStr) {
            case "DRAFTED":
                return "草稿";
            case "RUNNING":
                return "认定中";
            case "SUCCESS_END":
                return "通过认定";
            case "DISCARD_END":
                return "未通过认定";
        }
        return "";
    }

    private String convertJLStatusName(Object statusKey) {
        if (statusKey == null) {
            return "未奖励";
        }
        String statusKeyStr = statusKey.toString();
        if (statusKeyStr.equalsIgnoreCase("yes")) {
            return "已奖励";
        }
        return "未奖励";
    }

    public JSONObject getJsmmDetail(String jsmmId) {
        JSONObject detailObj = jsmmDao.queryJsmmById(jsmmId);
        if (detailObj == null) {
            return new JSONObject();
        }

        return detailObj;
    }

    public void createJsmm(JSONObject formData) {
        formData.put("jsmmId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        if (StringUtils.isBlank(formData.getString("whetherJL"))) {
            formData.put("whetherJL", "no");
        }
        jsmmDao.insertJsmm(formData);
    }

    public void updateJsmm(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        jsmmDao.updateJsmm(formData);
    }

    public List<JSONObject> getJsmmFileList(List<String> jsmmIdList) {
        List<JSONObject> jsmmFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("jsmmIds", jsmmIdList);
        jsmmFileList = jsmmDao.queryJsmmFileList(param);
        return jsmmFileList;
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
        String filePathBase = WebAppUtil.getProperty("jsmmFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find jsmmFilePathBase");
            return;
        }
        try {
            String jsmmId = toGetParamVal(parameters.get("jsmmId"));
            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String typeId = toGetParamVal(parameters.get("typeId"));

            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();

            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + jsmmId;
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
            fileInfo.put("jsmmId", jsmmId);
            fileInfo.put("typeId", typeId);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            jsmmDao.addJsmmFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public void deleteOneJsmmFile(String fileId, String fileName, String jsmmId) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, jsmmId, WebAppUtil.getProperty("jsmmFilePathBase"));
        Map<String, Object> param = new HashMap<>();
        param.put("id", fileId);
        jsmmDao.deleteJsmmFile(param);
    }

    public String toGetJsmmNumber() {
        String nowYearStart = DateFormatUtil.getNowByString("yyyy") + "-01-01";
        String nowYearStartUTC = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(nowYearStart), -8));
        Map<String, Object> param = new HashMap<>();
        param.put("applyTimeStart", nowYearStartUTC);
        JSONObject maxJsmm = jsmmDao.queryMaxJsmmNum(param);
        int existNumber = 0;
        if (maxJsmm != null) {
            existNumber = Integer.parseInt(maxJsmm.getString("jsmmNumber").split("-", -1)[2]);
        }
        int thisNumber = existNumber + 1;
        if (thisNumber < 10) {
            return "JSMM-" + DateFormatUtil.getNowByString("yyyy") + "-0" + thisNumber;
        } else {
            return "JSMM-" + DateFormatUtil.getNowByString("yyyy") + "-" + thisNumber;
        }
    }

    // 查询附件类型
    public List<JSONObject> queryJsmmFileTypes() {
        return jsmmDao.queryJsmmFileTypes();
    }

    // 更新奖励状态
    public void updateJlStatus(JSONObject result, JSONObject postObj) {
        jsmmDao.updateJlInfo(postObj);
        result.put("result", true);
        result.put("message", "操作成功");
    }

    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }

    /**
     * 删除技术秘密和磁盘中的文件
     * 
     * @param ids
     * @return
     */
    public JsonResult deleteJsmm(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> jsmmIds = Arrays.asList(ids);
        List<JSONObject> files = getJsmmFileList(jsmmIds);
        String jsmmFilePathBase = WebAppUtil.getProperty("jsmmFilePathBase");
        for (JSONObject oneFile : files) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"), oneFile.getString("fileName"),
                oneFile.getString("jsmmId"), jsmmFilePathBase);
        }
        for (String oneJsmmId : ids) {
            rdmZhglFileManager.deleteDirFromDisk(oneJsmmId, jsmmFilePathBase);
        }
        Map<String, Object> param = new HashMap<>();
        param.put("jsmmIds", jsmmIds);
        jsmmDao.deleteJsmmFile(param);
        jsmmDao.deleteJsmm(param);
        return result;
    }
}
