package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.dao.MaintenanceManualDao;
import com.redxun.serviceEngineering.core.dao.MaintenanceManualfileDao;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MaintenanceManualfileService {
    private static Logger logger = LoggerFactory.getLogger(MaintenanceManualfileService.class);
    @Autowired
    private MaintenanceManualfileDao maintenanceManualfileDao;
    @Autowired
    private MaintenanceManualDao maintenanceManualDao;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;

    //..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);
        List<JSONObject> businessList = null;
        int businessListCount;
        //由于最后三个条件出现后，会调用不同的查询，来避免join的重复结果干扰主查询的结果
        //所以，加了一个隐藏参数isComplex，有最后三个条件就往后端传true，否则就往后端
        //传false，根据这个值来判断调用哪一个查询
        if (!params.containsKey("isComplex") ||
                params.get("isComplex").toString().equalsIgnoreCase("false")) {
            businessList = maintenanceManualfileDao.dataListQuery(params);
            businessListCount = maintenanceManualfileDao.countDataListQuery(params);
        } else {
            businessList = maintenanceManualfileDao.dataListQuery2(params);
            businessListCount = maintenanceManualfileDao.countDataListQuery2(params);
        }
        if (businessList.size() > 0) {
            for (JSONObject jsonObject : businessList) {
                boolean existFile = checkFileExist(jsonObject.getString("id"));
                jsonObject.put("existFile", existFile);
            }
        }
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    //..
    private boolean checkFileExist(String id) {
        File file = null;
        file = findFile(id);
        if (file == null || !file.exists()) {
            return false;
        }
        return true;
    }

    //..
    private File findFile(String id) {
        if (StringUtils.isBlank(id)) {
            logger.warn("id is blank");
            return null;
        }
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringUploadPosition", "maintenanceManual").getValue();
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find filePathBase");
            return null;
        }
        String fileFullPath = filePathBase + File.separator + id + ".pdf";
        File file = new File(fileFullPath);
        if (!file.exists()) {
            return null;
        }
        return file;
    }

    //..
    private void getListParams(Map<String, Object> params, HttpServletRequest request) {
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "manualCode");
            params.put("sortOrder", "desc");
        }
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    // 数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
//                    if ("communicateStartTime".equalsIgnoreCase(name)) {
//                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8));
//                    }
//                    if ("communicateEndTime".equalsIgnoreCase(name)) {
//                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16));
//                    }
                    params.put(name, value);
                }
            }
        }

        // 增加分页条件
//        params.put("startIndex", 0);
//        params.put("pageSize", 20);
        String pageIndex = request.getParameter("pageIndex");
        String pageSize = request.getParameter("pageSize");
        if (StringUtils.isNotBlank(pageIndex) && StringUtils.isNotBlank(pageSize)) {
            params.put("startIndex", Integer.parseInt(pageSize) * Integer.parseInt(pageIndex));
            params.put("pageSize", Integer.parseInt(pageSize));
        }
    }

    //..
    public void Preview(HttpServletRequest request, HttpServletResponse response) {
        String id = RequestUtil.getString(request, "id");
        File file = findFile(id);
        if (file != null && file.exists()) {
            byte[] data = null;
            try {
                FileInputStream input = new FileInputStream(file);
                data = new byte[input.available()];
                input.read(data);
                response.getOutputStream().write(data);
                input.close();
            } catch (Exception e) {
                logger.error(id + "文件处理异常：" + e.getMessage());
            }
        }
    }

    //..
    public ResponseEntity<byte[]> Download(HttpServletRequest request, String id, String description) {
        try {
            if (StringUtils.isBlank(id)) {
                logger.error("id is blank");
                return null;
            }
            String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                    "serviceEngineeringUploadPosition", "maintenanceManual").getValue();
            if (StringUtils.isBlank(filePathBase)) {
                logger.error("can't find filePathBase");
                return null;
            }
            String fileName = id + ".pdf";
            String originalPdfFullFilePath = filePathBase + File.separator + fileName;
            File originalPdfFile = new File(originalPdfFullFilePath);
            if (!originalPdfFile.exists()) {
                logger.error("can't find originalPdfFile " + originalPdfFullFilePath);
                return null;
            }
            byte[] fileByteArr = new byte[0];
            fileByteArr = FileUtils.readFileToByteArray(originalPdfFile);

            // 下载文件的名字强制为“编号 标准名.pdf”修改文件名的编码格式
            String downloadFileName = description;
            String finalDownloadFileName = new String(downloadFileName.getBytes("UTF-8"), "ISO8859-1");
            // 设置httpHeaders,使浏览器响应下载
            HttpHeaders headers = new HttpHeaders();
            // 告诉浏览器执行下载的操作，“attachment”告诉了浏览器进行下载,下载的文件 文件名为 finalDownloadFileName
            headers.setContentDispositionFormData("attachment", finalDownloadFileName);
            // 设置响应方式为二进制，以二进制流传输
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<byte[]>(fileByteArr, headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception in publicDownload", e);
            return null;
        }
    }

    //..
    public JSONObject queryDataById(String id) {
        JSONObject jsonObject = maintenanceManualfileDao.queryDataById(id);
        return jsonObject;
    }

    //..
    public void deleteBusiness(JSONObject result, String id) {
        try {
            maintenanceManualfileDao.deleteBusiness(id);
            maintenanceManualfileDao.deleteBusinessLogs(id);
            deleteFileFromDisk(id);
            result.put("message", "删除成功！");
        } catch (Exception e) {
            logger.error("Exception in delete", e);
            result.put("message", "系统异常！");
        }
    }

    //..
    private void deleteFileFromDisk(String id) {
        if (StringUtils.isBlank(id)) {
            logger.warn("id is blank");
            return;
        }
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringUploadPosition", "maintenanceManual").getValue();
        // 处理下载目录的删除
        String fileFullPath = filePathBase + File.separator + id + ".pdf";
        File file = new File(fileFullPath);
        file.delete();
    }

    //..
    public void releaseBusiness(JSONObject result, String id) {
        try {
            //找设，销，物，语，是否CE,册编码，测版本相同的手册，将它们的状态都变为历史版本；
            //将本条状态变为可打印
            JSONObject jsonObject = maintenanceManualfileDao.queryDataById(id);
            Map<String, Object> param = new HashMap<>();
            param.put("salesModel", jsonObject.getString("salesModel"));
            param.put("designModel", jsonObject.getString("designModel"));
            param.put("materialCode", jsonObject.getString("materialCode"));
            param.put("manualLanguage", jsonObject.getString("manualLanguage"));
            param.put("isCE", jsonObject.getString("isCE"));
            param.put("manualCode", jsonObject.getString("manualCode"));
            param.put("manualEdition", jsonObject.getString("manualEdition"));
            List<JSONObject> jsonObjects = maintenanceManualfileDao.dataListQueryQuick(param);
            for (JSONObject maintenanceManualfile : jsonObjects) {
                if (maintenanceManualfile.getString("manualStatus").equalsIgnoreCase("可打印")) {
                    maintenanceManualfile.put("manualStatus", "历史版本");
                    maintenanceManualfile.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    maintenanceManualfile.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    maintenanceManualfileDao.updateBusiness(maintenanceManualfile);
                }
            }
            jsonObject.put("manualStatus", "可打印");
            jsonObject.put("keyUserId", ContextUtil.getCurrentUserId());
            jsonObject.put("keyUser", ContextUtil.getCurrentUser().getFullname());
            jsonObject.put("publishTime", DateFormatUtil.format(new Date(), "yyyy-MM-dd"));
            //....
            maintenanceManualfileDao.updateBusiness(jsonObject);
            JSONObject params = new JSONObject();
            params.put("roleKey", "maintenanceManualPrinter");
            List<JSONObject> users = maintenanceManualfileDao.getUsersByRolekey(params);
            params.clear();
            params.put("roleKey", "maintenanceManualAdmin");
            List<JSONObject> users2 = maintenanceManualfileDao.getUsersByRolekey(params);
            users.addAll(users2);
            //....
            StringBuilder stringBuilder = new StringBuilder();
            for (JSONObject user : users) {
                stringBuilder.append(user.getString("USER_ID_")).append(",");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            sendDingDing(jsonObject, stringBuilder.toString());
            result.put("message", "发布成功！");
        } catch (Exception e) {
            logger.error("Exception in delete", e);
            result.put("message", "系统异常！");
        }
    }

    //..
    public void sendDingDing(JSONObject jsonObject, String userIds) {
        JSONObject noticeObj = new JSONObject();
        String content = StringUtil.format("【操保手册发布通知】手册编码:${manualCode},设计型号:${designModel}," +
                "销售型号:${salesModel},物料编码:${salesModel},是否CE:${salesModel}。可打印 ", jsonObject);
        content += XcmgProjectUtil.getNowLocalDateStr(DateUtil.DATE_FORMAT_FULL);
        noticeObj.put("content", content);
        sendDDNoticeManager.sendNoticeForCommon(userIds, noticeObj);
    }

    //..
    public void refreshBusiness(JSONObject result) {
        try {
            //在maintenanceManual里获取所有信息，遍历这些信息，
            //如果maintenanceManualfile里没有设，销，物，语，是否CE五个匹配相同的记录，则新增一个，状态设置为“编制中”
            List<JSONObject> maintenanceManualList = maintenanceManualDao.dataListQuery(null);
            for (JSONObject maintenanceManualJsonObject : maintenanceManualList) {
                Map<String, Object> paramsfile = new HashMap<>();
                paramsfile.put("salesModel", maintenanceManualJsonObject.getString("salesModel"));
                paramsfile.put("designModel", maintenanceManualJsonObject.getString("designModel"));
                paramsfile.put("materialCode", maintenanceManualJsonObject.getString("materialCode"));
                paramsfile.put("manualLanguage", maintenanceManualJsonObject.getString("manualLanguage"));
                paramsfile.put("isCE", maintenanceManualJsonObject.getString("isCE"));
                List<JSONObject> maintenanceManualfileList = maintenanceManualfileDao.dataListQuery(paramsfile);
                if (maintenanceManualfileList.size() == 0) {
                    JSONObject maintenanceManualfileJsonObject = new JSONObject();
                    maintenanceManualfileJsonObject.put("id", IdUtil.getId());
                    maintenanceManualfileJsonObject.put("salesModel", maintenanceManualJsonObject.getString("salesModel"));
                    maintenanceManualfileJsonObject.put("designModel", maintenanceManualJsonObject.getString("designModel"));
                    maintenanceManualfileJsonObject.put("materialCode", maintenanceManualJsonObject.getString("materialCode"));
                    maintenanceManualfileJsonObject.put("manualLanguage", maintenanceManualJsonObject.getString("manualLanguage"));
                    maintenanceManualfileJsonObject.put("manualCode", maintenanceManualJsonObject.getString("manualCode"));
                    maintenanceManualfileJsonObject.put("isCE", maintenanceManualJsonObject.getString("isCE"));
                    maintenanceManualfileJsonObject.put("manualStatus", "编辑中");
                    maintenanceManualfileJsonObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    maintenanceManualfileJsonObject.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    maintenanceManualfileJsonObject.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
                    maintenanceManualfileDao.insertBusiness(maintenanceManualfileJsonObject);
                }
            }

            result.put("message", "获取成功！");
        } catch (Exception e) {
            logger.error("Exception in delete", e);
            result.put("message", "系统异常！");
        }
    }

    //..
    public void saveBusiness(JSONObject result, HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            Map<String, String[]> parameters = multipartRequest.getParameterMap();
            MultipartFile fileObj = multipartRequest.getFile("businessFile");
            if (parameters == null || parameters.isEmpty()) {
                logger.error("表单内容为空！");
                result.put("message", "操作失败，表单内容为空！");
                result.put("success", false);
                return;
            }

            Map<String, Object> objBody = new HashMap<>();
            constructBusinessParam(parameters, objBody);
            addOrUpdateBusiness(objBody, fileObj);
            result.put("message", "保存成功！");
            result.put("id", objBody.get("id"));
            result.put("success", true);
        } catch (Exception e) {
            logger.error("Exception in savePublicStandard", e);
            result.put("message", "系统异常！");
            result.put("success", false);
        }
    }

    //..
    private void constructBusinessParam(Map<String, String[]> parameters, Map<String, Object> objBody) {
        if (parameters.get("id") != null && parameters.get("id").length != 0
                && StringUtils.isNotBlank(parameters.get("id")[0])) {
            objBody.put("id", parameters.get("id")[0]);
        }
        if (parameters.get("salesModel") != null && parameters.get("salesModel").length != 0
                && StringUtils.isNotBlank(parameters.get("salesModel")[0])) {
            objBody.put("salesModel", parameters.get("salesModel")[0]);
        }
        if (parameters.get("designModel") != null && parameters.get("designModel").length != 0
                && StringUtils.isNotBlank(parameters.get("designModel")[0])) {
            objBody.put("designModel", parameters.get("designModel")[0]);
        }
        if (parameters.get("materialCode") != null && parameters.get("materialCode").length != 0
                && StringUtils.isNotBlank(parameters.get("materialCode")[0])) {
            objBody.put("materialCode", parameters.get("materialCode")[0]);
        }
        if (parameters.get("manualDescription") != null && parameters.get("manualDescription").length != 0
                && StringUtils.isNotBlank(parameters.get("manualDescription")[0])) {
            objBody.put("manualDescription", parameters.get("manualDescription")[0]);
        }
        if (parameters.get("manualLanguage") != null && parameters.get("manualLanguage").length != 0
                && StringUtils.isNotBlank(parameters.get("manualLanguage")[0])) {
            objBody.put("manualLanguage", parameters.get("manualLanguage")[0]);
        }
        if (parameters.get("manualCode") != null && parameters.get("manualCode").length != 0
                && StringUtils.isNotBlank(parameters.get("manualCode")[0])) {
            objBody.put("manualCode", parameters.get("manualCode")[0]);
        }
        if (parameters.get("manualVersion") != null && parameters.get("manualVersion").length != 0
                && StringUtils.isNotBlank(parameters.get("manualVersion")[0])) {
            objBody.put("manualVersion", parameters.get("manualVersion")[0]);
        }
        if (parameters.get("manualEdition") != null && parameters.get("manualEdition").length != 0
                && StringUtils.isNotBlank(parameters.get("manualEdition")[0])) {
            objBody.put("manualEdition", parameters.get("manualEdition")[0]);
        }
        if (parameters.get("isCE") != null && parameters.get("isCE").length != 0
                && StringUtils.isNotBlank(parameters.get("isCE")[0])) {
            objBody.put("isCE", parameters.get("isCE")[0]);
        }
        if (parameters.get("CEStatus") != null && parameters.get("CEStatus").length != 0
                && StringUtils.isNotBlank(parameters.get("CEStatus")[0])) {
            objBody.put("CEStatus", parameters.get("CEStatus")[0]);
        }
        if (parameters.get("keyUserId") != null && parameters.get("keyUserId").length != 0
                && StringUtils.isNotBlank(parameters.get("keyUserId")[0])) {
            objBody.put("keyUserId", parameters.get("keyUserId")[0]);
        }
        if (parameters.get("keyUser") != null && parameters.get("keyUser").length != 0
                && StringUtils.isNotBlank(parameters.get("keyUser")[0])) {
            objBody.put("keyUser", parameters.get("keyUser")[0]);
        }
        if (parameters.get("publishTime") != null && parameters.get("publishTime").length != 0
                && StringUtils.isNotBlank(parameters.get("publishTime")[0])) {
            objBody.put("publishTime", parameters.get("publishTime")[0]);
        }
        if (parameters.get("manualStatus") != null && parameters.get("manualStatus").length != 0
                && StringUtils.isNotBlank(parameters.get("manualStatus")[0])) {
            objBody.put("manualStatus", parameters.get("manualStatus")[0]);
        }
        if (parameters.get("remark") != null && parameters.get("remark").length != 0
                && StringUtils.isNotBlank(parameters.get("remark")[0])) {
            objBody.put("remark", parameters.get("remark")[0]);
        }
        if (parameters.get("cpzgId") != null && parameters.get("cpzgId").length != 0
                && StringUtils.isNotBlank(parameters.get("cpzgId")[0])) {
            objBody.put("cpzgId", parameters.get("cpzgId")[0]);
        }
        if (parameters.get("cpzgName") != null && parameters.get("cpzgName").length != 0
                && StringUtils.isNotBlank(parameters.get("cpzgName")[0])) {
            objBody.put("cpzgName", parameters.get("cpzgName")[0]);
        }
    }

    //..
    private void addOrUpdateBusiness(Map<String, Object> objBody, MultipartFile fileObj) throws IOException {
        String id = objBody.get("id") == null ? "" : objBody.get("id").toString();
        if (StringUtils.isBlank(id)) {
            //新增文件
            String newId = IdUtil.getId();
            if (fileObj != null) {
                updateFile2Disk(newId, fileObj);
                objBody.put("manualDescription", fileObj.getOriginalFilename());
            }
            objBody.put("id", newId);
            objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            objBody.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            objBody.put("manualStatus", "编辑中");
            maintenanceManualfileDao.insertBusiness(objBody);
        } else {
            if (fileObj != null) {
                updateFile2Disk(id, fileObj);
                objBody.put("manualDescription", fileObj.getOriginalFilename());
            }
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            maintenanceManualfileDao.updateBusiness(objBody);
        }
    }

    //..
    private void updateFile2Disk(String id, MultipartFile fileObj) throws IOException {
        if (StringUtils.isBlank(id) || fileObj == null) {
            logger.warn("no id or fileObj");
            return;
        }
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringUploadPosition", "maintenanceManual").getValue();
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find filePathBase");
            return;
        }

        // 处理下载目录的更新
        File pathFile = new File(filePathBase);
        // 目录不存在则创建
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        String fileFullPath = filePathBase + File.separator + id + ".pdf";
        File file = new File(fileFullPath);
        // 文件存在则更新掉
        if (file.exists()) {
            logger.warn("File " + fileFullPath + " will be deleted");
            file.delete();
        }
        FileCopyUtils.copy(fileObj.getBytes(), file);
    }

    //..
    public List<JSONObject> getLogList(String businessId) {
        List<JSONObject> logList = maintenanceManualfileDao.queryLogList(businessId);
        return logList;
    }

    //..
    public void exportList(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> params = RequestUtil.getParameterValueMap(request, true);
        JsonPageResult result = dataListQuery(request, response);
        List<Map<String, Object>> listData = result.getData();
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "操保手册列表";
        String excelName = nowDate + title;
        String[] fieldNames = {"销售型号", "设计型号", "物料编码", "文件名称", "产品主管", "手册语言", "手册编码",
                "GSS版本", "手册版本", "是否CE版", "归档人", "发布时间", "文件状态", "备注", "需求单号", "申请时间", "需求时间"};

        String[] fieldCodes = {"salesModel", "designModel", "materialCode", "manualDescription", "cpzgName", "manualLanguage", "manualCode",
                "manualVersion", "manualEdition", "isCE", "keyUser", "publishTime", "manualStatus", "remark", "demandListNo", "applyTime", "demandTime"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }
}
