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

import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
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
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.DateUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.dao.ZzzFileDao;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Service
public class ZzzFileService {
    private static Logger logger = LoggerFactory.getLogger(ZzzFileService.class);
    @Autowired
    private ZzzFileDao zzzFileDao;
    @Autowired
    private SysDicManager sysDicManager;

    // ..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        String menuType = RequestUtil.getString(request, "menuType");
        if (StringUtil.isNotEmpty(menuType)) {
            params.put("menuType", menuType);
        }
        getListParams(params, request);
        List<JSONObject> businessList = null;
        int businessListCount;

        businessList = zzzFileDao.dataListQuery(params);
        // 加文件存在状态及时间转换
        if (businessList.size() > 0) {
            for (JSONObject jsonObject : businessList) {
                boolean existFile = checkFileExist(jsonObject.getString("id"), jsonObject.getString("fileName"));
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
        businessListCount = zzzFileDao.countDataListQuery(params);
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
            sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringUploadPosition", "zzzFile").getValue();
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find filePathBase");
            return null;
        }
        String fileFullPath = filePathBase + File.separator + id + "." + suffix;
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
                sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringUploadPosition", "zzzFile").getValue();
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
        JSONObject jsonObject = zzzFileDao.queryDataById(id);
        return jsonObject;
    }

    // ..
    public void deleteBusiness(JSONObject result, String id, String fileName) {
        try {
            deleteFileFromDisk(id, fileName);
            zzzFileDao.deleteBusiness(id);
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
            sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringUploadPosition", "zzzFile").getValue();
        // 处理下载目录的删除
        String fileFullPath = filePathBase + File.separator + id + "." + CommonFuns.toGetFileSuffix(fileName);
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
        if (parameters.get("menuType") != null && parameters.get("menuType").length != 0
            && StringUtils.isNotBlank(parameters.get("menuType")[0])) {
            objBody.put("menuType", parameters.get("menuType")[0]);
        }
        if (parameters.get("partsType") != null && parameters.get("partsType").length != 0
            && StringUtils.isNotBlank(parameters.get("partsType")[0])) {
            objBody.put("partsType", parameters.get("partsType")[0]);
        }
        if (parameters.get("partsModel") != null && parameters.get("partsModel").length != 0
            && StringUtils.isNotBlank(parameters.get("partsModel")[0])) {
            objBody.put("partsModel", parameters.get("partsModel")[0]);
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
            zzzFileDao.insertBusiness(objBody);
        } else {
            if (fileObj != null) {
                updateFile2Disk(id, fileObj);
                objBody.put("fileName", fileObj.getOriginalFilename());
            } else {
                // 前台清除文件后，删除磁盘中的文件
                JSONObject jsonObject = zzzFileDao.queryDataById(id);
                deleteFileFromDisk(id, jsonObject.getString("fileName"));
            }
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            zzzFileDao.updateBusiness(objBody);
        }
    }

    // ..
    private void updateFile2Disk(String id, MultipartFile fileObj) throws IOException {
        if (StringUtils.isBlank(id) || fileObj == null) {
            logger.warn("no id or fileObj");
            return;
        }
        String filePathBase =
            sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringUploadPosition", "zzzFile").getValue();
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
        String fileName = fileObj.getOriginalFilename();
        String suffix = CommonFuns.toGetFileSuffix(fileName);

        String fileFullPath = filePathBase + File.separator + id + "." + suffix;
        File file = new File(fileFullPath);
        // 文件存在则更新掉
        if (file.exists()) {
            logger.warn("File " + fileFullPath + " will be deleted");
            file.delete();
        }
        // 已存在不同类型的文件则删除
        JSONObject jsonObject = zzzFileDao.queryDataById(id);
        if (jsonObject != null) {
            String orgFileName = jsonObject.getString("fileName");
            if (CommonFuns.toGetFileSuffix(orgFileName) != suffix) {
                deleteFileFromDisk(id, jsonObject.getString("fileName"));
            }

        }

        FileCopyUtils.copy(fileObj.getBytes(), file);
    }
}
