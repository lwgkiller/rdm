package com.redxun.serviceEngineering.core.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.redxun.core.json.JsonResult;
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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.dao.HgxSafeArchiveDao;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Service
public class HgxSafeArchiveService {
    private static Logger logger = LoggerFactory.getLogger(HgxSafeArchiveService.class);
    @Autowired
    private HgxSafeArchiveDao hgxSafeArchiveDao;
    @Autowired
    private SysDicManager sysDicManager;

    // ..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);
        List<JSONObject> businessList = null;
        int businessListCount;

        businessList = hgxSafeArchiveDao.dataListQuery(params);
        // 加文件存在状态及时间转换
        if (businessList.size() > 0) {
            for (JSONObject jsonObject : businessList) {
                boolean existFile = false;
                existFile = checkFileExist(jsonObject.getString("id"), jsonObject.getString("fileName"));
                jsonObject.put("existFile", existFile);
                if (jsonObject.get("CREATE_TIME_") != null) {
                    jsonObject.put("CREATE_TIME_",
                        DateUtil.formatDate((Date)jsonObject.get("CREATE_TIME_"), "yyyy-MM-dd"));
                }
                if (jsonObject.get("UPDATE_TIME_") != null) {
                    jsonObject.put("UPDATE_TIME_",
                        DateUtil.formatDate((Date)jsonObject.get("UPDATE_TIME_"), "yyyy-MM-dd"));
                }
            }
        }
        businessListCount = hgxSafeArchiveDao.countDataListQuery(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    // ..
    private boolean checkFileExist(String id, String fileName) {
        File file = null;
        String suffix = CommonFuns.toGetFileSuffix(fileName);
        file = findFile(id, suffix);
        if (file == null || !file.exists()) {
            return false;
        }
        return true;
    }

    // ..
    private File findFile(String id, String suffix) {
        if (StringUtils.isBlank(id)) {
            logger.warn("id is blank");
            return null;
        }
        String filePathBase =
            sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringUploadPosition", "hgxSafeArchive").getValue();
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find filePathBase");
            return null;
        }
        // 加了个目录/id/id.doc
        String fileFullPath = filePathBase + File.separator + id + File.separator + id + "." + suffix;
        File file = new File(fileFullPath);
        if (!file.exists()) {
            return null;
        }
        return file;
    }

    // ..
    private void getListParams(Map<String, Object> params, HttpServletRequest request) {
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "CREATE_TIME_");
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
                    // if ("communicateStartTime".equalsIgnoreCase(name)) {
                    // value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8));
                    // }
                    // if ("communicateEndTime".equalsIgnoreCase(name)) {
                    // value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16));
                    // }
                    params.put(name, value);
                }
            }
        }

        // 增加分页条件
        // params.put("startIndex", 0);
        // params.put("pageSize", 20);
        String pageIndex = request.getParameter("pageIndex");
        String pageSize = request.getParameter("pageSize");
        if (StringUtils.isNotBlank(pageIndex) && StringUtils.isNotBlank(pageSize)) {
            params.put("startIndex", Integer.parseInt(pageSize) * Integer.parseInt(pageIndex));
            params.put("pageSize", Integer.parseInt(pageSize));
        }
    }

    // ..
    public void Preview(HttpServletRequest request, HttpServletResponse response) {
        String id = RequestUtil.getString(request, "id");
        String fileName = RequestUtil.getString(request, "fileName");
        String suffix = CommonFuns.toGetFileSuffix(fileName);
        File file = findFile(id, suffix);
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





    // ..
    public ResponseEntity<byte[]> Download(HttpServletRequest request, String id, String description) {
        try {
            if (StringUtils.isBlank(id)) {
                logger.error("id is blank");
                return null;
            }
            String filePathBase =
                sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringUploadPosition", "hgxSafeArchive").getValue();
            if (StringUtils.isBlank(filePathBase)) {
                logger.error("can't find filePathBase");
                return null;
            }
            String fileName = id + "." + CommonFuns.toGetFileSuffix(description);
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

    // ..
    public JSONObject queryDataById(String id) {
        JSONObject jsonObject = hgxSafeArchiveDao.queryDataById(id);
        return jsonObject;
    }

    // ..
    public void deleteBusiness(JSONObject result, String id, String fileName) {
        try {
            deleteFileFromDisk(id, fileName);
            hgxSafeArchiveDao.deleteBusiness(id);
            result.put("message", "删除成功！");
        } catch (Exception e) {
            logger.error("Exception in delete", e);
            result.put("message", "系统异常！");
        }
    }

    // ..
    private void deleteFileFromDisk(String id, String fileName) {
        if (StringUtils.isBlank(id)) {
            logger.warn("id is blank");
            return;
        }
        String filePathBase =
            sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringUploadPosition", "hgxSafeArchive").getValue();
        // 处理下载目录的删除
        String fileFullPath = filePathBase + File.separator + id + File.separator + id + "." + CommonFuns.toGetFileSuffix(fileName);
        File file = new File(fileFullPath);
        file.delete();
    }

    // ..
    public void saveBusiness(JSONObject result, HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
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

            //处理topic子表
            if (parameters.get("topicGrid") != null && parameters.get("topicGrid").length != 0) {
                topicRelProcess(objBody.get("id").toString(), JSONObject.parseArray(parameters.get("topicGrid")[0]));
            }
            //处理合规性规子表
            if (parameters.get("standardGrid") != null && parameters.get("standardGrid").length != 0) {
                standardRelProcess(objBody.get("id").toString(), JSONObject.parseArray(parameters.get("standardGrid")[0]));
            }

            result.put("message", "保存成功！");
            result.put("id", objBody.get("id"));
            result.put("success", true);
        } catch (Exception e) {
            logger.error("Exception in savePublicStandard", e);
            result.put("message", "系统异常！");
            result.put("success", false);
        }
    }

    // ..
    private void constructBusinessParam(Map<String, String[]> parameters, Map<String, Object> objBody) {
        if (parameters.get("id") != null && parameters.get("id").length != 0
            && StringUtils.isNotBlank(parameters.get("id")[0])) {
            objBody.put("id", parameters.get("id")[0]);
        }
        if (parameters.get("remark") != null && parameters.get("remark").length != 0
            && StringUtils.isNotBlank(parameters.get("remark")[0])) {
            objBody.put("remark", parameters.get("remark")[0]);
        }
        if (parameters.get("creatorName") != null && parameters.get("creatorName").length != 0
            && StringUtils.isNotBlank(parameters.get("creatorName")[0])) {
            objBody.put("creatorName", parameters.get("creatorName")[0]);
        }
        if (parameters.get("fileName") != null && parameters.get("fileName").length != 0
            && StringUtils.isNotBlank(parameters.get("fileName")[0])) {
            objBody.put("fileName", parameters.get("fileName")[0]);
        }

        if (parameters.get("safeCode") != null && parameters.get("safeCode").length != 0
                && StringUtils.isNotBlank(parameters.get("safeCode")[0])) {
            objBody.put("safeCode", parameters.get("safeCode")[0]);
        }if (parameters.get("safeType") != null && parameters.get("safeType").length != 0
                && StringUtils.isNotBlank(parameters.get("safeType")[0])) {
            objBody.put("safeType", parameters.get("safeType")[0]);
        }
        if (parameters.get("region") != null && parameters.get("region").length != 0
                && StringUtils.isNotBlank(parameters.get("region")[0])) {
            objBody.put("region", parameters.get("region")[0]);
        }
        if (parameters.get("version") != null && parameters.get("version").length != 0
                && StringUtils.isNotBlank(parameters.get("version")[0])) {
            objBody.put("version", parameters.get("version")[0]);
        }
        if (parameters.get("topicId") != null && parameters.get("topicId").length != 0
                && StringUtils.isNotBlank(parameters.get("topicId")[0])) {
            objBody.put("topicId", parameters.get("topicId")[0]);
        }if (parameters.get("topicName") != null && parameters.get("topicName").length != 0
                && StringUtils.isNotBlank(parameters.get("topicName")[0])) {
            objBody.put("topicName", parameters.get("topicName")[0]);
        }
        if (parameters.get("standardId") != null && parameters.get("standardId").length != 0
                && StringUtils.isNotBlank(parameters.get("standardId")[0])) {
            objBody.put("standardId", parameters.get("standardId")[0]);
        }if (parameters.get("standardName") != null && parameters.get("standardName").length != 0
                && StringUtils.isNotBlank(parameters.get("standardName")[0])) {
            objBody.put("standardName", parameters.get("standardName")[0]);
        }


    }

    // ..
    private void addOrUpdateBusiness(Map<String, Object> objBody, MultipartFile fileObj) throws IOException {
        String id = objBody.get("id") == null ? "" : objBody.get("id").toString();
        if (StringUtils.isBlank(id)) {
            // 新增文件
            String newId = IdUtil.getId();
            if (fileObj != null) {
                updateFile2Disk(newId, fileObj);
                objBody.put("fileName", fileObj.getOriginalFilename());
            }
            objBody.put("id", newId);
            objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("creatorName", ContextUtil.getCurrentUser().getFullname());
            objBody.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            objBody.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            objBody.put("versionStatus", "current");
            hgxSafeArchiveDao.insertBusiness(objBody);
        } else {
            if (fileObj != null) {
                updateFile2Disk(id, fileObj);
                objBody.put("fileName", fileObj.getOriginalFilename());
            } else {
                // 前台清除文件后，删除磁盘中的文件
                JSONObject jsonObject = hgxSafeArchiveDao.queryDataById(id);
                // todo 删pdf
                deleteFileFromDisk(id, jsonObject.getString("fileName"));
            }
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            hgxSafeArchiveDao.updateBusiness(objBody);
        }
    }

    // ..
    private void topicRelProcess(String applyId, JSONArray demandArr) {
        if (demandArr == null || demandArr.isEmpty()) {
            return;
        }
        JSONObject param = new JSONObject();
        param.put("ids", new JSONArray());
        for (int index = 0; index < demandArr.size(); index++) {
            JSONObject oneObject = demandArr.getJSONObject(index);
            String id = oneObject.getString("id");
            String state = oneObject.getString("_state");
            if ("added".equals(state) || StringUtils.isBlank(state) || StringUtils.isBlank(id)) {
                // 新增 复制时，会把主表id清空，并把子表的各id也清空，故都会走这里新增
                oneObject.put("id", IdUtil.getId());
                oneObject.put("applyId", applyId);
                oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("CREATE_TIME_", new Date());
                hgxSafeArchiveDao.insertTopicRel(oneObject);
            } else if ("modified".equals(state)) {
                oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("UPDATE_TIME_", new Date());
                hgxSafeArchiveDao.updateTopicRel(oneObject);
            } else if ("removed".equals(state)) {
                // 删除
                param.getJSONArray("ids").add(oneObject.getString("id"));
            }
        }
        if (!param.getJSONArray("ids").isEmpty()) {
            hgxSafeArchiveDao.deleteTopicRel(param);
        }
    }


    private void standardRelProcess(String applyId, JSONArray demandArr) {
        if (demandArr == null || demandArr.isEmpty()) {
            return;
        }
        JSONObject param = new JSONObject();
        param.put("ids", new JSONArray());
        for (int index = 0; index < demandArr.size(); index++) {
            JSONObject oneObject = demandArr.getJSONObject(index);
            String id = oneObject.getString("id");
            String state = oneObject.getString("_state");
            if ("added".equals(state) || StringUtils.isBlank(state) || StringUtils.isBlank(id)) {
                // 新增 复制时，会把主表id清空，并把子表的各id也清空，故都会走这里新增
                oneObject.put("id", IdUtil.getId());
                oneObject.put("applyId", applyId);
                oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("CREATE_TIME_", new Date());
                hgxSafeArchiveDao.insertStandardRel(oneObject);
            } else if ("modified".equals(state)) {
                oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("UPDATE_TIME_", new Date());
                hgxSafeArchiveDao.updateStandardRel(oneObject);
            } else if ("removed".equals(state)) {
                // 删除
                param.getJSONArray("ids").add(oneObject.getString("id"));
            }
        }
        if (!param.getJSONArray("ids").isEmpty()) {
            hgxSafeArchiveDao.deleteStandardRel(param);
        }
    }

    // ..
    private void updateFile2Disk(String id, MultipartFile fileObj) throws IOException {
        if (StringUtils.isBlank(id) || fileObj == null) {
            logger.warn("no id or fileObj");
            return;
        }
        String filePathBase =
            sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringUploadPosition", "hgxSafeArchive").getValue();
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find filePathBase");
            return;
        }

        // 处理下载目录的更新
        String filePath = filePathBase + File.separator + id;
        File pathFile = new File(filePath);
//        File pathFile = new File(filePathBase + File.separator + id);
        // 目录不存在则创建
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        String fileName = fileObj.getOriginalFilename();
        String suffix = CommonFuns.toGetFileSuffix(fileName);

        String fileFullPath = filePath  + File.separator + id + "." + suffix;
        File file = new File(fileFullPath);
        // 文件存在则更新掉
        if (file.exists()) {
            logger.warn("File " + fileFullPath + " will be deleted");
            file.delete();
        }
        // 已存在不同类型的文件则删除
        JSONObject jsonObject = hgxSafeArchiveDao.queryDataById(id);
        if (jsonObject != null) {
            String orgFileName = jsonObject.getString("fileName");
            if (CommonFuns.toGetFileSuffix(orgFileName) != suffix) {
                deleteFileFromDisk(id, jsonObject.getString("fileName"));
            }

        }

        FileCopyUtils.copy(fileObj.getBytes(), file);
    }

    public void exportData(HttpServletRequest request, HttpServletResponse response) {
        // 这个导出不分页


        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);
        List<JSONObject> businessList = null;
        businessList = hgxSafeArchiveDao.dataListQuery(params);

        // 加文件存在状态及时间转换
        if (businessList.size() > 0) {
            for (JSONObject jsonObject : businessList) {
                boolean existFile = checkFileExist(jsonObject.getString("id"), jsonObject.getString("fileName"));
                if (existFile) {
                    jsonObject.put("existFile", "是");
                }
                else{
                    jsonObject.put("existFile", "否");
                }

                if (jsonObject.get("CREATE_TIME_") != null) {
                    jsonObject.put("CREATE_TIME_",
                            DateUtil.formatDate((Date)jsonObject.get("CREATE_TIME_"), "yyyy-MM-dd"));
                }
                if (jsonObject.get("UPDATE_TIME_") != null) {
                    jsonObject.put("UPDATE_TIME_",
                            DateUtil.formatDate((Date)jsonObject.get("UPDATE_TIME_"), "yyyy-MM-dd"));
                }
            }
        }

        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "操保手册安全信息库";
        String excelName = nowDate + title;
        String[] fieldNames = {"编号", "文件名称","类别", "适用范围", "版本","版本状态",
                 "归档人", "归档时间","是否有文件",
                };
        String[] fieldCodes = {"safeCode", "fileName", "safeType", "region", "version","versionStatus",
                 "creatorName", "CREATE_TIME_","existFile"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(businessList, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);

    }

    public List<JSONObject> queryTopicList(JSONObject params) {
        List<JSONObject> demandList = hgxSafeArchiveDao.queryTopicRelList(params);
        for (JSONObject oneDemand : demandList) {
            if (oneDemand.get("CREATE_TIME_") != null) {
                oneDemand.put("CREATE_TIME_", DateUtil.formatDate((Date)oneDemand.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
            if (oneDemand.get("UPDATE_TIME_") != null) {
                oneDemand.put("UPDATE_TIME_", DateUtil.formatDate((Date)oneDemand.get("UPDATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        return demandList;
    }
    public List<JSONObject> queryStandardList(JSONObject params) {
        List<JSONObject> demandList = hgxSafeArchiveDao.queryStandardRelList(params);
        for (JSONObject oneDemand : demandList) {
            if (oneDemand.get("CREATE_TIME_") != null) {
                oneDemand.put("CREATE_TIME_", DateUtil.formatDate((Date)oneDemand.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
            if (oneDemand.get("UPDATE_TIME_") != null) {
                oneDemand.put("UPDATE_TIME_", DateUtil.formatDate((Date)oneDemand.get("UPDATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        return demandList;
    }

    public JsonResult updateVersion(String id) {

        JsonResult result = new JsonResult(true, "操作成功！");

        JSONObject param = new JSONObject();
        param.put("id", id);
        param.put("versionStatus", "history");
        hgxSafeArchiveDao.updateVersion(param);


        return result;
    }


}
