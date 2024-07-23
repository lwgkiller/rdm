package com.redxun.rdmZhgl.core.service;

import java.io.File;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.rdmCommon.core.util.RdmCommonUtil;
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
import com.redxun.rdmZhgl.core.dao.JszbDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;

@Service
public class JszbService {
    private Logger logger = LoggerFactory.getLogger(JszbService.class);
    @Autowired
    private JszbDao jszbDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;

    public JsonPageResult<?> getJszbList(HttpServletRequest request, HttpServletResponse response,
        Map<String, Object> params, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        rdmZhglUtil.addOrder(request, params, "CREATE_TIME_", "desc");
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    params.put(name, value);
                }
            }
        }
        // 增加分页条件
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
        }
        // 增加角色过滤的条件
        RdmCommonUtil.addListAllQueryRoleExceptDraft(params, ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<Map<String, Object>> jszbList = jszbDao.queryJszbList(params);
        for (Map<String, Object> jszb : jszbList) {
            if (jszb.get("lxTime") != null) {
                jszb.put("lxTime", DateUtil.formatDate((Date)jszb.get("lxTime"), "yyyy-MM-dd"));
            }
            if (jszb.get("yjFinishTime") != null) {
                jszb.put("yjFinishTime", DateUtil.formatDate((Date)jszb.get("yjFinishTime"), "yyyy-MM-dd"));
            }
        }
        // 查询当前处理人
        xcmgProjectManager.setTaskCurrentUser(jszbList);
        result.setData(jszbList);
        int countjszbDataList = jszbDao.countJszbList(params);
        result.setTotal(countjszbDataList);
        return result;
    }


    public void createJszb(JSONObject formData) {
        formData.put("jszbId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        if (StringUtils.isBlank(formData.getString("lxTime"))) {
            formData.put("lxTime", null);
        }
        if (StringUtils.isBlank(formData.getString("yjFinishTime"))) {
            formData.put("yjFinishTime", null);
        }
        jszbDao.insertJszb(formData);
    }

    public void updateJszb(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        if (StringUtils.isBlank(formData.getString("lxTime"))) {
            formData.put("lxTime", null);
        }
        if (StringUtils.isBlank(formData.getString("yjFinishTime"))) {
            formData.put("yjFinishTime", null);
        }
        jszbDao.updateJszb(formData);
    }

    public JSONObject getJszbDetail(String jszbId) {
        JSONObject detailObj = jszbDao.queryJszbById(jszbId);
        if (detailObj.get("lxTime") != null) {
            detailObj.put("lxTime", DateUtil.formatDate((Date)detailObj.get("lxTime"), "yyyy-MM-dd"));
        }
        if (detailObj.get("yjFinishTime") != null) {
            detailObj.put("yjFinishTime", DateUtil.formatDate((Date)detailObj.get("yjFinishTime"), "yyyy-MM-dd"));
        }
        return detailObj;
    }

    public List<JSONObject> getJszbFileList(List<String> jszbIdList) {
        Map<String, Object> param = new HashMap<>();
        param.put("jszbIds", jszbIdList);
        return jszbDao.queryJszbFileList(param);
    }

    // 查询附件类型
    public List<JSONObject> queryJszbFileTypes(Map<String, Object> param) {
        return jszbDao.queryJszbFileTypes(param);
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
        String filePathBase = WebAppUtil.getProperty("jszbFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find kjlwFilePathBase");
            return;
        }
        try {
            String jszbId = toGetParamVal(parameters.get("jszbId"));
            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String ckId = toGetParamVal(parameters.get("ckId"));
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();

            // 向下载目录中写入文件
            String filePath = filePathBase;
            if (StringUtils.isNotBlank(jszbId)) {
                filePath += File.separator + jszbId;
            }

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
            fileInfo.put("jszbId", jszbId);
            fileInfo.put("ckId", ckId);
            fileInfo.put("fileName", fileName);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            jszbDao.addJszbFileInfos(fileInfo);
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

    public void deleteOneJszbFile(String fileId, String fileName, String jszbId) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, jszbId, WebAppUtil.getProperty("jszbFilePathBase"));
        Map<String, Object> param = new HashMap<>();
        param.put("id", fileId);
        jszbDao.deleteJszbFile(param);
    }

    public JsonResult deleteJszb(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> jszbIds = Arrays.asList(ids);
        List<JSONObject> files = getJszbFileList(jszbIds);
        String jszbFilePathBase = WebAppUtil.getProperty("jszbFilePathBase");
        for (JSONObject oneFile : files) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"), oneFile.getString("fileName"),
                oneFile.getString("jszbId"), jszbFilePathBase);
        }
        for (String oneJszbId : ids) {
            rdmZhglFileManager.deleteDirFromDisk(oneJszbId, jszbFilePathBase);
        }
        Map<String, Object> param = new HashMap<>();
        param.put("jszbIds", jszbIds);
        jszbDao.deleteJszbFile(param);
        jszbDao.deleteJszb(param);
        return result;
    }

    public void exportJszbExcel(HttpServletResponse response, HttpServletRequest request) {
        Map<String, Object> params = new HashMap<>();
        JsonPageResult queryDataResult = getJszbList(request, response, params, false);
        String gslclb = "";
        if (params.get("gslclb") != null && StringUtils.isNotBlank(params.get("gslclb").toString())) {
            gslclb = params.get("gslclb").toString();
        }
        List<String> fieldNamesList = null;
        List<String> fieldCodesList = null;
        String titleName = "";
        switch (gslclb) {
            case "招标流程":
                titleName = "常规招标列表";
                fieldNamesList = Arrays.asList("招标部门", "经办人", "招标项目名称", "项目编号", "招标总价或项目预算", "招标组织形式", "招标形式", "信息发布方式",
                    "立项时间", "预计完成时间", "流程状态");
                fieldCodesList = Arrays.asList("zbbmName", "jbrName", "zbName", "xmNum", "zbjg", "zbzzxs", "zbxs",
                    "publishType", "lxTime", "yjFinishTime", "status");
                break;
            case "询比价流程":
                titleName = "询比价招标列表";
                fieldNamesList =
                    Arrays.asList("询比价部门", "经办人", "询比价项目名称", "项目编号", "询比价总价或项目预算", "信息发布方式", "立项时间", "预计完成时间", "流程状态");
                fieldCodesList = Arrays.asList("zbbmName", "jbrName", "zbName", "xmNum", "zbjg", "publishType",
                    "lxTime", "yjFinishTime", "status");
                break;
            case "特批流程":
                titleName = "特批招标列表";
                fieldNamesList = Arrays.asList("特批申请部门", "经办人", "项目名称", "项目预算", "拟采用采购方式", "立项时间", "预计完成时间", "流程状态");
                fieldCodesList =
                    Arrays.asList("zbbmName", "jbrName", "zbName", "zbjg", "cgfs", "lxTime", "yjFinishTime", "status");
                break;
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String excelName = nowDate + titleName;

        // 再处理数据，补充缺失的数据
        List<Map<String, Object>> queryData = queryDataResult.getData();
        for (Map<String, Object> oneData : queryData) {
            if (oneData.get("status") != null && StringUtils.isNotBlank(oneData.get("status").toString())) {
                String status = oneData.get("status").toString();
                switch (status) {
                    case "SUCCESS_END":
                        status = "成功结束";
                        break;
                    case "DRAFTED":
                        status = "草稿";
                        break;
                    case "RUNNING":
                        status = "审批中";
                        break;
                    case "DISCARD_END":
                        status = "流程作废";
                        break;
                }
                oneData.put("status", status);
            }
        }
        String[] fieldNamesArr = fieldNamesList.toArray(new String[0]);
        String[] fieldCodesArr = fieldCodesList.toArray(new String[0]);
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(queryData, fieldNamesArr, fieldCodesArr, titleName);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }
}
