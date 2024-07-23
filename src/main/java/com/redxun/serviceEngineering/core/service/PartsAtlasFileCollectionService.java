package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.OfficeDocPreview;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.serviceEngineering.core.dao.PartsAtlasFileCollectionDao;
import com.redxun.sys.core.manager.SysDicManager;
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

import static com.redxun.rdmCommon.core.util.RdmCommonUtil.toGetParamVal;

@Service
public class PartsAtlasFileCollectionService {
    private static Logger logger = LoggerFactory.getLogger(PartsAtlasFileCollectionService.class);
    @Autowired
    private PartsAtlasFileCollectionDao partsAtlasFileCollectionDao;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;

    // ..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);
        List<JSONObject> businessList = null;
        int businessListCount;

        businessList = partsAtlasFileCollectionDao.dataListQuery(params);
        // 加文件存在状态及时间转换
        if (businessList.size() > 0) {
            for (JSONObject jsonObject : businessList) {
                String id = jsonObject.getString("id");
                String fileName = jsonObject.getString("fileName");
                boolean existFile = checkFileExist(id, fileName);
                jsonObject.put("existFile", existFile);
                if (jsonObject.get("CREATE_TIME_") != null) {
                    jsonObject.put("CREATE_TIME_",
                            DateUtil.formatDate((Date) jsonObject.get("CREATE_TIME_"), "yyyy-MM-dd"));
                }
                if (jsonObject.get("UPDATE_TIME_") != null) {
                    jsonObject.put("UPDATE_TIME_",
                            DateUtil.formatDate((Date) jsonObject.get("UPDATE_TIME_"), "yyyy-MM-dd"));
                }
            }
        }
        businessListCount = partsAtlasFileCollectionDao.countDataListQuery(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    // ..
    private boolean checkFileExist(String id, String fileName) {
        File file = null;
        file = findFile(id, fileName);
        if (file == null || !file.exists()) {
            return false;
        }
        return true;
    }

    // ..
    private File findFile(String id, String fileName) {
        if (StringUtils.isBlank(id)) {
            logger.warn("id is blank");
            return null;
        }
        if (StringUtils.isBlank(fileName)) {
            logger.warn("fileName is blank");
            return null;
        }
        String filePathBase =
                sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringUploadPosition", "partsAtlasArchive").getValue();
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find filePathBase");
            return null;
        }
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        String fileFullPath = filePathBase + File.separator + id + suffix;
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

    // PDF预览
    public void Preview(HttpServletRequest request, HttpServletResponse response) {
        String id = RequestUtil.getString(request, "id");
        String fileName = RequestUtil.getString(request, "fileName");
        File file = findFile(id, fileName);
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

    // Office文件预览
    public void previewOffice(HttpServletRequest request, HttpServletResponse response) {
        String id = RequestUtil.getString(request, "id");
        String fileName = RequestUtil.getString(request, "fileName");
        if (StringUtils.isBlank(fileName)) {
            logger.error("操作失败，文件名为空！");
            return;
        }
        if (StringUtils.isBlank(id)) {
            logger.error("操作失败，文件Id为空！");
            return;
        }
        String filePathBase =
                sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringUploadPosition", "partsAtlasArchive").getValue();
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("操作失败，文件路径不存在");
            return;
        }
        String suffix = CommonFuns.toGetFileSuffix(fileName);
        String originalFilePath = filePathBase + File.separator + id + "." + suffix;
        String convertPdfDir = WebAppUtil.getProperty("convertPdfDir");
        String convertPdfPath = filePathBase + File.separator + convertPdfDir + File.separator + id + ".pdf";
        ;
        OfficeDocPreview.previewOfficeDoc(originalFilePath, convertPdfPath, response);
    }

    // ..
    public ResponseEntity<byte[]> Download(HttpServletRequest request, String id, String description) {
        try {
            if (StringUtils.isBlank(id)) {
                logger.error("id is blank");
                return null;
            }
            if (StringUtils.isBlank(description)) {
                logger.error("description is blank");
                return null;
            }

            String filePathBase = sysDicManager
                    .getBySysTreeKeyAndDicKey("serviceEngineeringUploadPosition", "partsAtlasArchive").getValue();
            if (StringUtils.isBlank(filePathBase)) {
                logger.error("can't find filePathBase");
                return null;
            }
            String fileName = id + description.substring(description.lastIndexOf("."));
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
        JSONObject jsonObject = partsAtlasFileCollectionDao.queryDataById(id);
        return jsonObject;
    }

    // ..
    public void deleteBusiness(JSONObject result, String id) {
        try {
            JSONObject jsonObject = this.queryDataById(id);
            partsAtlasFileCollectionDao.deleteBusiness(id);
            deleteFileFromDisk(jsonObject);
            result.put("message", "删除成功！");
        } catch (Exception e) {
            logger.error("Exception in delete", e);
            result.put("message", "系统异常！");
        }
    }

    // ..
    private void deleteFileFromDisk(JSONObject jsonObject) {
        String filePathBase =
                sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringUploadPosition", "partsAtlasArchive").getValue();
        // 处理下载目录的删除
        String fileFullPath = filePathBase + File.separator + jsonObject.getString("id")
                + "." + jsonObject.getString("fileName").split("\\.")[1];
        File file = new File(fileFullPath);
        file.delete();
    }

    // ..
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

    public void saveUploadFiles(JSONObject result, HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            logger.warn("没有找到上传的参数");
            result.put("message", "没有找到上传的参数！");
            result.put("success", false);
            return;
        }
        // 多附件上传需要用到的MultipartHttpServletRequest
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap == null || fileMap.isEmpty()) {
            logger.warn("没有找到上传的文件");
            result.put("message", "没有找到上传的文件！");
            result.put("success", false);
            return;
        }
        String filePathBase =
                sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringUploadPosition", "partsAtlasArchive").getValue();
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find serviceEngineeringUploadPosition");
            result.put("message", "没有找到上传的serviceEngineeringUploadPosition！");
            result.put("success", false);
            return;
        }

        try {
            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String partsAtlasName = fileName.substring(0, fileName.lastIndexOf("."));
            String fileType = toGetParamVal(parameters.get("fileType"));
            String vinCode = "";
            String language = "";
            int index = partsAtlasName.indexOf("-");
            if (index != -1) {
                // 零件图册语言编码根据文件名识别，保养件清单、易损件清单默认为中文
                vinCode = partsAtlasName.substring(0, index);
                if ("零件图册".equals(fileType)) {
                    language = partsAtlasName.substring(index + 1);
                } else {
                    language = "zh-cn";
                }
            } else {
                logger.warn("零件图册：文件名不是 整机编码-语言 规则！或保养件、易损件清单：文件名不是 整机编码/物料号-文件类型 规则！");
                result.put("message", "零件图册：文件名不是 整机编码-语言 规则！或保养件、易损件清单：文件名不是 整机编码/物料号-文件类型 规则！");
                result.put("success", false);
                return;
            }

            String languageType = sysDicManager.getBySysTreeKeyAndDicKey("LJSCYY", language).getValue();
            if (StringUtils.isBlank(languageType)) {
                logger.error("can't find LJSCYY" + language);
                result.put("message", "can't find 零件图册语言对应" + language);
                result.put("success", false);
                return;
            }

            List<JSONObject> businessList = null;
            Map<String, Object> params = new HashMap<>();
            params.put("vinCode", vinCode);
            params.put("languageType", languageType);
            params.put("fileType", fileType);
            // 按照vinCode判断文件是否存在已无法满足最新的一个vinCode对应三类文件的情况，这里修改为按照文件名称、文件类型、语言判断
            businessList = partsAtlasFileCollectionDao.dataListQuery(params);
            if (!businessList.isEmpty()) {
                result.put("message", "系统已有" + vinCode + languageType + "版的" + fileType);
                result.put("success", false);
                return;
            }

            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();

            // 向下载目录中写入文件
            String filePath = filePathBase;
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
            // fileInfo.put("setId", setId);
            fileInfo.put("fileName", fileName);
            fileInfo.put("partsAtlasName", partsAtlasName);
            fileInfo.put("fileType", fileType);
            fileInfo.put("vinCode", vinCode);
            fileInfo.put("languageType", languageType);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("creatorName", ContextUtil.getCurrentUser().getFullname());
            fileInfo.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            fileInfo.put("CREATE_TIME_", new Date());
            partsAtlasFileCollectionDao.addFileInfos(fileInfo);
            result.put("success", true);
            return;
        } catch (Exception e) {
            logger.error("Exception in savepartsAtlasArchiveUploadFiles", e);
            result.put("message", "系统没匹配到语言种类");
            result.put("success", false);
        }
    }

    // ..
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
        if (parameters.get("vinCode") != null && parameters.get("vinCode").length != 0
                && StringUtils.isNotBlank(parameters.get("vinCode")[0])) {
            objBody.put("vinCode", parameters.get("vinCode")[0]);
        }
        if (parameters.get("partsAtlasName") != null && parameters.get("partsAtlasName").length != 0
                && StringUtils.isNotBlank(parameters.get("partsAtlasName")[0])) {
            objBody.put("partsAtlasName", parameters.get("partsAtlasName")[0]);
        }
        if (parameters.get("languageType") != null && parameters.get("languageType").length != 0
                && StringUtils.isNotBlank(parameters.get("languageType")[0])) {
            objBody.put("languageType", parameters.get("languageType")[0]);
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
                objBody.put("partsAtlasName", fileObj.getOriginalFilename().replace(".pdf", ""));
            }
            objBody.put("id", newId);
            objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("creatorName", ContextUtil.getCurrentUser().getFullname());
            objBody.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            objBody.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            // objBody.put("manualStatus", "编辑中");
            partsAtlasFileCollectionDao.insertBusiness(objBody);
        } else {
            if (fileObj != null) {
                updateFile2Disk(id, fileObj);
                objBody.put("fileName", fileObj.getOriginalFilename());
                objBody.put("partsAtlasName", fileObj.getOriginalFilename().replace(".pdf", ""));

            }
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            partsAtlasFileCollectionDao.updateBusiness(objBody);
        }
    }

    // ..
    private void updateFile2Disk(String id, MultipartFile fileObj) throws IOException {
        if (StringUtils.isBlank(id) || fileObj == null) {
            logger.warn("no id or fileObj");
            return;
        }
        String filePathBase =
                sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringUploadPosition", "partsAtlasArchive").getValue();
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

}
