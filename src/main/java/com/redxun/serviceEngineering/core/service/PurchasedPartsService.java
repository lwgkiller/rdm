package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.dao.PurchasedPartsDao;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.org.manager.OsUserManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
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
import java.util.*;

@Service
public class PurchasedPartsService {
    private static Logger logger = LoggerFactory.getLogger(PurchasedPartsService.class);
    @Autowired
    private PurchasedPartsDao purchasedPartsDao;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;
    @Autowired
    private OsUserManager osUserManager;

    //..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);
        List<JSONObject> businessList = purchasedPartsDao.dataListQuery(params);
        int businessListCount = purchasedPartsDao.countDataListQuery(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    //
    private void getListParams(Map<String, Object> params, HttpServletRequest request) {
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "collectionInformationSubmissionDate");
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
        params.put("startIndex", 0);
        params.put("pageSize", 20);
        String pageIndex = request.getParameter("pageIndex");
        String pageSize = request.getParameter("pageSize");
        if (StringUtils.isNotBlank(pageIndex) && StringUtils.isNotBlank(pageSize)) {
            params.put("startIndex", Integer.parseInt(pageSize) * Integer.parseInt(pageIndex));
            params.put("pageSize", Integer.parseInt(pageSize));
        }
    }

    //..
    public JsonResult deleteData(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIds);
        purchasedPartsDao.deleteData(param);
        return result;
    }

    //..
    public void saveData(JsonResult result, String dataStr) {
        JSONObject dataObj = JSONObject.parseObject(dataStr);
        if (dataObj == null || dataObj.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("表单内容为空，操作失败！");
            return;
        }
        if (StringUtils.isBlank(dataObj.getString("id"))) {
            dataObj.put("id", IdUtil.getId());
            dataObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            dataObj.put("CREATE_TIME_", new Date());
            purchasedPartsDao.insertData(dataObj);
            result.setData(dataObj.getString("id"));
        } else {
            dataObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            dataObj.put("UPDATE_TIME_", new Date());
            purchasedPartsDao.updateData(dataObj);
            result.setData(dataObj.getString("id"));
        }
    }

    //..
    public JSONObject queryDataById(String businessId) {
        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(businessId)) {
            return result;
        }
        JSONObject meetingObj = purchasedPartsDao.queryDataById(businessId);
        if (meetingObj == null) {
            return result;
        }
        return meetingObj;
    }

    //..
    public JsonPageResult<?> weeklyReportListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getWeeklyReportListParams(params, request);
        List<JSONObject> businessList = purchasedPartsDao.weeklyReportListQuery(params);
        if (businessList.size() > 0) {
            for (JSONObject jsonObject : businessList) {
                boolean existFile = checkWeeklyReportFileExist(jsonObject.getString("id"));
                jsonObject.put("existFile", existFile);
            }
        }
        int businessListCount = purchasedPartsDao.countWeeklyReportListQuery(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    //..
    public JSONObject queryWeeklyReportById(String id) {
        JSONObject jsonObject = purchasedPartsDao.queryWeeklyReportById(id);
        return jsonObject;
    }

    //..
    private void getWeeklyReportListParams(Map<String, Object> params, HttpServletRequest request) {
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "signMonth");
            params.put("sortOrder", "asc");
        }
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
        params.put("startIndex", 0);
        params.put("pageSize", 20);
        String pageIndex = request.getParameter("pageIndex");
        String pageSize = request.getParameter("pageSize");
        if (StringUtils.isNotBlank(pageIndex) && StringUtils.isNotBlank(pageSize)) {
            params.put("startIndex", Integer.parseInt(pageSize) * Integer.parseInt(pageIndex));
            params.put("pageSize", Integer.parseInt(pageSize));
        }
    }

    //..
    private boolean checkWeeklyReportFileExist(String id) {
        File file = null;
        file = findWeeklyReportFile(id);
        if (file == null || !file.exists()) {
            return false;
        }
        return true;
    }

    //..
    private File findWeeklyReportFile(String id) {
        if (StringUtils.isBlank(id)) {
            logger.warn("id is blank");
            return null;
        }
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringUploadPosition", "purchasedPartsWeeklyReport").getValue();
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
    public void weeklyReportPreview(HttpServletRequest request, HttpServletResponse response) {
        String id = RequestUtil.getString(request, "id");
        File file = findWeeklyReportFile(id);
        if (file != null && file.exists()) {
            byte[] data = null;
            try {
                FileInputStream input = new FileInputStream(file);
                data = new byte[input.available()];
                input.read(data);
                response.getOutputStream().write(data);
                input.close();
            } catch (Exception e) {
                logger.error("外购件图册收集及制作周报表" + id + "文件处理异常：" + e.getMessage());
            }
        }
    }

    //..
    public ResponseEntity<byte[]> weeklyReportDownload(HttpServletRequest request, String id, String description) {
        try {
            if (StringUtils.isBlank(id)) {
                logger.error("id is blank");
                return null;
            }
            String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                    "serviceEngineeringUploadPosition", "purchasedPartsWeeklyReport").getValue();
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
            String downloadFileName = description + ".pdf";
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
    public void deleteWeeklyReport(JSONObject result, String id) {
        try {
            Map<String, Object> param = new HashMap<>();
            param.put("id", id);
            purchasedPartsDao.deleteWeeklyReportById(id);
            deleteWeeklyReportFromDisk(id);
            result.put("message", "删除成功！");
        } catch (Exception e) {
            logger.error("Exception in delete", e);
            result.put("message", "系统异常！");
        }
    }

    //..
    private void deleteWeeklyReportFromDisk(String id) {
        if (StringUtils.isBlank(id)) {
            logger.warn("id is blank");
            return;
        }
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringUploadPosition", "purchasedPartsWeeklyReport").getValue();
        // 处理下载目录的删除
        String fileFullPath = filePathBase + File.separator + id + ".pdf";
        File file = new File(fileFullPath);
        file.delete();
    }

    //..
    public void saveWeeklyReport(JSONObject result, HttpServletRequest request) {
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
            constructWeeklyReportParam(parameters, objBody);
            addOrUpdateWeeklyReport(objBody, fileObj);
            //钉钉-begin
            JSONObject jsonObject = new JSONObject(objBody);
            String receiverNoString = sysDicManager.getBySysTreeKeyAndDicKey(
                    "serviceEngineeringGroups", "purchasedPartsWeeklyReport").getValue();
            String[] receiverNos = receiverNoString.split(",");
            StringBuilder stringBuilder = new StringBuilder();
            if (receiverNos.length > 0) {
                for (String userNo : receiverNos) {
                    stringBuilder.append(osUserManager.getByUserName(userNo).getUserId());
                    stringBuilder.append(",");
                }
            }
            stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
            sendDingDing(jsonObject, stringBuilder.toString());
            //钉钉-end
            result.put("message", "保存成功！");
            result.put("id", objBody.get("id"));
            result.put("success", true);
        } catch (Exception e) {
            logger.error("Exception in savePurchasedPartsWeeklyReport", e);
            result.put("message", "系统异常！");
            result.put("success", false);
        }
    }

    //..
    private void constructWeeklyReportParam(Map<String, String[]> parameters, Map<String, Object> objBody) {
        if (parameters.get("id") != null && parameters.get("id").length != 0
                && StringUtils.isNotBlank(parameters.get("id")[0])) {
            objBody.put("id", parameters.get("id")[0]);
        }
        if (parameters.get("signYear") != null && parameters.get("signYear").length != 0
                && StringUtils.isNotBlank(parameters.get("signYear")[0])) {
            objBody.put("signYear", parameters.get("signYear")[0]);
        }
        if (parameters.get("signMonth") != null && parameters.get("signMonth").length != 0
                && StringUtils.isNotBlank(parameters.get("signMonth")[0])) {
            objBody.put("signMonth", parameters.get("signMonth")[0]);
        }
        if (parameters.get("description") != null && parameters.get("description").length != 0
                && StringUtils.isNotBlank(parameters.get("description")[0])) {
            objBody.put("description", parameters.get("description")[0]);
        }
        if (parameters.get("releaseTime") != null && parameters.get("releaseTime").length != 0
                && StringUtils.isNotBlank(parameters.get("releaseTime")[0])) {
            objBody.put("releaseTime", parameters.get("releaseTime")[0]);
        }
    }

    //..
    private void addOrUpdateWeeklyReport(Map<String, Object> objBody, MultipartFile fileObj) throws IOException {
        String id = objBody.get("id") == null ? "" : objBody.get("id").toString();
        if (StringUtils.isBlank(id)) {
            // 新增文件
            String newId = IdUtil.getId();
            if (fileObj != null) {
                updateWeeklyReportFile2Disk(newId, fileObj);
            }
            objBody.put("id", newId);
            objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            objBody.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            purchasedPartsDao.insertWeeklyReport(objBody);
        } else {
            if (fileObj != null) {
                updateWeeklyReportFile2Disk(id, fileObj);
            }
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            purchasedPartsDao.updateWeeklyReport(objBody);
        }
    }

    //..
    private void updateWeeklyReportFile2Disk(String id, MultipartFile fileObj) throws IOException {
        if (StringUtils.isBlank(id) || fileObj == null) {
            logger.warn("no id or fileObj");
            return;
        }
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringUploadPosition", "purchasedPartsWeeklyReport").getValue();
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

    //..p
    public void sendDingDing(JSONObject jsonObject, String userIds) {
        JSONObject noticeObj = new JSONObject();
        StringBuilder stringBuilder = new StringBuilder("【外购件图册收集及制作周报表】下面的:");
        stringBuilder.append(jsonObject.getString("description"));
        stringBuilder.append("，日期为");
        stringBuilder.append(jsonObject.getString("signYear") + "-" + jsonObject.getString("signMonth"));
        stringBuilder.append("已发布。").append(XcmgProjectUtil.getNowLocalDateStr(DateUtil.DATE_FORMAT_FULL));
        noticeObj.put("content", stringBuilder.toString());
        sendDDNoticeManager.sendNoticeForCommon(userIds, noticeObj);
    }
}
