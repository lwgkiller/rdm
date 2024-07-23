package com.redxun.componentTest.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.OfficeDocPreview;
import com.redxun.core.util.StringUtil;
import com.redxun.componentTest.core.dao.ComponentResourcesPlatformDataDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.componentTest.core.dao.ComponentResourcesPlatformDataDao;
import com.redxun.sys.core.entity.SysDic;
import com.redxun.sys.core.manager.SysDicManager;

import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.sys.org.entity.OsGroup;
import com.redxun.componentTest.core.service.ComponentResourcesPlatformDataService;
import com.redxun.sys.org.manager.OsGroupManager;
import org.apache.commons.collections.map.HashedMap;
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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static com.redxun.rdmCommon.core.util.RdmCommonUtil.toGetParamVal;

@Service
public class ComponentResourcesPlatformDataService {
    private static Logger logger = LoggerFactory.getLogger(ComponentResourcesPlatformDataService.class);
    @Autowired
    private ComponentResourcesPlatformDataDao componentResourcesPlatformDataDao;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private BpmInstManager bpmInstManager;
    @Resource
    OsGroupManager osGroupManager;

    //..
    public JsonPageResult<?> masterDataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        JSONObject params = new JSONObject();
        CommonFuns.getSearchParam(params, request, true);
        List<JSONObject> businessList = componentResourcesPlatformDataDao.masterDataListQuery(params);
        int businessListCount = componentResourcesPlatformDataDao.countMasterDataListQuery(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    //..
    public JSONObject getMasterDataById(String id) throws Exception {
        JSONObject model = componentResourcesPlatformDataDao.getMasterDataById(id);
        if (model.get("CREATE_TIME_") != null) {
            model.put("CREATE_TIME_", DateUtil.formatDate((Date) model.get("CREATE_TIME_"), "yyyy-MM-dd"));
        }
        return model;
    }

    //..
    public JsonResult saveMasterData(HttpServletRequest request) throws Exception {
        JsonResult result = new JsonResult(true, "操作成功！");
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, String[]> parameters = multipartRequest.getParameterMap();
        MultipartFile fileObj = multipartRequest.getFile("businessFile");
        if (parameters == null || parameters.isEmpty()) {
            result.setMessage("操作失败，表单内容为空！");
            result.setSuccess(false);
            return result;
        }
        Map<String, Object> objBody = new HashMap<>();
        this.constructBusinessParam(parameters, objBody);
        this.addOrUpdateBusiness(objBody, fileObj);
        result.setData(objBody.get("id").toString());
        return result;
    }




    //..
    private void constructBusinessParam(Map<String, String[]> parameters, Map<String, Object> objBody) throws Exception {
        if (parameters.get("id") != null && parameters.get("id").length != 0
                && StringUtils.isNotBlank(parameters.get("id")[0])) {
            objBody.put("id", parameters.get("id")[0]);
        }
        if (parameters.get("orderNo") != null && parameters.get("orderNo").length != 0
                && StringUtils.isNotBlank(parameters.get("orderNo")[0])) {
            objBody.put("orderNo", parameters.get("orderNo")[0]);
        }
        if (parameters.get("platformName") != null && parameters.get("platformName").length != 0
                && StringUtils.isNotBlank(parameters.get("platformName")[0])) {
            objBody.put("platformName", parameters.get("platformName")[0]);
        }
        if (parameters.get("monthlyUtiRate") != null && parameters.get("monthlyUtiRate").length != 0
                && StringUtils.isNotBlank(parameters.get("monthlyUtiRate")[0])) {
            objBody.put("monthlyUtiRate", parameters.get("monthlyUtiRate")[0]);
        }
        if (parameters.get("remarks") != null && parameters.get("remarks").length != 0
                && StringUtils.isNotBlank(parameters.get("remarks")[0])) {
            objBody.put("remarks", parameters.get("remarks")[0]);
        }
        if (parameters.get("fileName") != null && parameters.get("fileName").length != 0
                && StringUtils.isNotBlank(parameters.get("fileName")[0])) {
            objBody.put("fileName", parameters.get("fileName")[0]);
        }
    }

    //..
    private void addOrUpdateBusiness(Map<String, Object> objBody, MultipartFile fileObj) throws Exception {
        String id = objBody.get("id") == null ? "" : objBody.get("id").toString();

        if (StringUtil.isEmpty(id)) {
            //新增文件
            String newId = IdUtil.getId();
            if (fileObj != null) {
                this.updateFile2Disk(newId, fileObj);
            }
            objBody.put("id", newId);
            objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("CREATE_TIME_", DateFormatUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            componentResourcesPlatformDataDao.insertMasterData(new JSONObject(objBody));
        } else {
            if (fileObj != null) {
                this.deleteFileFromDisk(id);
                this.updateFile2Disk(id, fileObj);
            }
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", DateFormatUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            componentResourcesPlatformDataDao.updateMasterData(new JSONObject(objBody));
        }
    }

    //..
    private void updateFile2Disk(String id, MultipartFile fileObj) throws Exception {
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("resourcesPlatformUpload",
                "componentResourcesPlatformPic").getValue();
        if (StringUtils.isBlank(filePathBase)) {
            throw new RuntimeException("找不到文件路径");
        }
        // 处理下载目录的更新
        File pathFile = new File(filePathBase);
        // 目录不存在则创建
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        String fileFullPath = filePathBase + File.separator + id + "." + fileObj.getOriginalFilename().split("\\.")[1];
        File file = new File(fileFullPath);
        // 文件存在则更新掉
        if (file.exists()) {
            file.delete();
        }
        FileCopyUtils.copy(fileObj.getBytes(), file);
    }

    //..
    public JsonResult deleteMasterData(String[] ids) throws Exception {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> idList = Arrays.asList(ids);
        JSONObject params = new JSONObject();
        params.clear();
        params.put("ids", idList);
        List<JSONObject> masterDataList = componentResourcesPlatformDataDao.masterDataListQuery(params);
        componentResourcesPlatformDataDao.deleteMasterData(params);
        //删除主数据相关文件
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("resourcesPlatformUpload",
                "componentResourcesPlatformPic").getValue();
        for (JSONObject masterData : masterDataList) {
            this.deleteOneFileFromDisk(masterData.get("id").toString(), masterData.get("fileName").toString(),
                    null, filePathBase);
        }
        return result;
    }

    //..删除机型预览图片用
    private void deleteFileFromDisk(String id) throws IOException {
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("resourcesPlatformUpload",
                "componentResourcesPlatformPic").getValue();
        if (StringUtils.isBlank(filePathBase)) {
            throw new RuntimeException("找不到文件路径");
        }
        File directory = new File(filePathBase);
        if (!directory.exists()) {
            return;
        }
        //获取目录中所有文件名
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        //遍历文件数组，删除与指定文件名相同的文件
        for (File file : files) {
            if (file.isFile() && file.getName().split("\\.")[0].equals(id)) {
                Files.delete(Paths.get(file.toURI()));
            }
        }
    }

    //..
    private void deleteOneFileFromDisk(String fileId, String fileName, String formId, String filePathBase) {
        if (StringUtil.isEmpty(filePathBase)) {
            logger.error("can't find filePathBase");
            return;
        }
        try {
            // 删除下载目录中文件
            String filePath = filePathBase;
            if (StringUtil.isNotEmpty(formId)) {
                filePath += File.separator + formId;
            }
            File pathFile = new File(filePath);
            if (!pathFile.exists()) {
                return;
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fileFullPath = filePath + File.separator + fileId + "." + suffix;
            File file = new File(fileFullPath);
            file.delete();

            // 删除预览目录中pdf文件
            String convertPdfDir = WebAppUtil.getProperty("convertPdfDir");
            String filePathPreviewPdf = filePath + File.separator + convertPdfDir;
            File pathFilePreviewPdf = new File(filePathPreviewPdf);
            if (!pathFilePreviewPdf.exists()) {
                return;
            }
            String fileFullPathPreviewPdf = filePathPreviewPdf + File.separator + fileId + ".pdf";
            File previewFilePdf = new File(fileFullPathPreviewPdf);
            previewFilePdf.delete();
        } catch (Exception e) {
            logger.error("Exception in deleteOneFileFromDisk", e);
        }
    }

    //..
    public ResponseEntity<byte[]> pdfPreviewOrDownLoad(String fileName, String fileId, String formId, String fileBasePath) {
        try {
            if (StringUtil.isEmpty(fileName)) {
                logger.error("操作失败，文件名为空！");
                return null;
            }
            if (StringUtil.isEmpty(fileId)) {
                logger.error("操作失败，文件Id为空！");
                return null;
            }
            if (StringUtil.isEmpty(fileBasePath)) {
                logger.error("操作失败，找不到存储路径");
                return null;
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fullFilePath = fileBasePath + (StringUtil.isEmpty(formId) ? "" : (File.separator + formId))
                    + File.separator + fileId + "." + suffix;
            // 创建文件实例
            File file = new File(fullFilePath);
            // 修改文件名的编码格式
            String downloadFileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
            // 设置httpHeaders,使浏览器响应下载
            HttpHeaders headers = new HttpHeaders();
            // 告诉浏览器执行下载的操作，“attachment”告诉了浏览器进行下载,下载的文件 文件名为 downloadFileName
            headers.setContentDispositionFormData("attachment", downloadFileName);
            // 设置响应方式为二进制，以二进制流传输
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception in pdfPreviewOrDownLoad", e);
            return null;
        }
    }

    //..
    public void officeFilePreview(String fileName, String fileId, String formId, String fileBasePath,
                                  HttpServletResponse response) {
        if (StringUtil.isEmpty(fileName)) {
            logger.error("操作失败，文件名为空！");
            return;
        }
        if (StringUtil.isEmpty(fileId)) {
            logger.error("操作失败，文件Id为空！");
            return;
        }
        if (StringUtil.isEmpty(fileBasePath)) {
            logger.error("操作失败，找不到存储路径");
            return;
        }
        String suffix = CommonFuns.toGetFileSuffix(fileName);
        String originalFilePath = fileBasePath + (StringUtil.isEmpty(formId) ? "" : (File.separator + formId))
                + File.separator + fileId + "." + suffix;
        String convertPdfDir = WebAppUtil.getProperty("convertPdfDir");
        String convertPdfPath = fileBasePath + (StringUtil.isEmpty(formId) ? "" : (File.separator + formId))
                + File.separator + convertPdfDir + File.separator + fileId + ".pdf";
        OfficeDocPreview.previewOfficeDoc(originalFilePath, convertPdfPath, response);
    }

    //..
    public void imageFilePreview(String fileName, String fileId, String formId, String fileBasePath,
                                 HttpServletResponse response) {
        if (StringUtil.isEmpty(fileName)) {
            logger.error("操作失败，文件名为空！");
            return;
        }
        if (StringUtil.isEmpty(fileId)) {
            logger.error("操作失败，文件Id为空！");
            return;
        }
        if (StringUtil.isEmpty(fileBasePath)) {
            logger.error("操作失败，找不到存储路径");
            return;
        }
        String suffix = CommonFuns.toGetFileSuffix(fileName);
        String originalFilePath = fileBasePath + (StringUtil.isEmpty(formId) ? "" : (File.separator + formId))
                + File.separator + fileId + "." + suffix;
        OfficeDocPreview.imagePreview(originalFilePath, response);
    }


    //..
    private void deleteDirFromDisk(String formId, String filePathBase) {
        if (StringUtil.isEmpty(filePathBase)) {
            logger.error("can't find FilePathBase");
            return;
        }
        try {
            // 删除目录下的tmp文件夹
            String convertPdfDir = WebAppUtil.getProperty("convertPdfDir");
            String previewPdfPath = filePathBase + File.separator + formId + File.separator + convertPdfDir;
            File pathFilePreviewPdf = new File(previewPdfPath);
            pathFilePreviewPdf.delete();

            // 删除目录
            String filePathDir = filePathBase + File.separator + formId;
            File pathFile = new File(filePathDir);
            pathFile.delete();
        } catch (Exception e) {
            logger.error("Exception in deleteDirFromDisk", e);
        }
    }
}
