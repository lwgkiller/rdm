package com.redxun.rdMaterial.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.OfficeDocPreview;
import com.redxun.core.util.StringUtil;
import com.redxun.rdMaterial.core.dao.RdMaterialFileDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.sys.core.manager.SysDicManager;
import org.apache.commons.io.FileUtils;
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
import java.text.MessageFormat;
import java.util.*;

import static com.redxun.rdmCommon.core.util.RdmCommonUtil.toGetParamVal;

@Service
public class RdMaterialFileService {
    private static Logger logger = LoggerFactory.getLogger(RdMaterialFileService.class);
    @Autowired
    private RdMaterialFileDao rdMaterialFileDao;
    @Autowired
    private SysDicManager sysDicManager;

    //从磁盘中删除文件（下载目录以及tmp中的pdf）
    public void deleteOneFileFromDisk(String fileId, String fileName, String formId, String filePathBase) {
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

    //从磁盘中删除某一个表单文件夹（包括预览文件夹），前提是已经将目录下的文件都删除完了
    public void deleteDirFromDisk(String formId, String filePathBase) {
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

    //从磁盘中递归删除一个文件夹及下面的文件
    public void deleteDirAndFileFromDisk(File dir) {
        if (dir == null || !dir.exists()) {
            logger.error("can't find filePathBase");
            return;
        }
        //获取目录下子文件
        File[] files = dir.listFiles();
        //遍历该目录下的文件对象
        for (File file : files) {
            //判断子目录是否存在子目录，如果是文件则删除
            if (file.isDirectory()) {
                //递归删除目录下面的文件
                deleteDirAndFileFromDisk(file);
            } else {
                //文件删除
                file.delete();
            }
        }
        //文件夹删除
        dir.delete();
    }

    //文件的下载或者是pdf文件的预览
    public ResponseEntity<byte[]> pdfPreviewOrDownLoad(String fileName, String fileId, String formId,
                                                       String fileBasePath) {
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
            logger.error("Exception in fileDownload", e);
            return null;
        }
    }

    //
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
        ;
        OfficeDocPreview.previewOfficeDoc(originalFilePath, convertPdfPath, response);
    }

    //
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

    //..文件上传命令
    public JsonResult saveFiles(HttpServletRequest request) throws Exception {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            logger.warn("没有找到上传的参数");
            return new JsonResult(false, "没有找到上传的参数");
        }
        //多附件上传需要用到的MultipartHttpServletRequest
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap == null || fileMap.isEmpty()) {
            logger.warn("没有找到上传的文件");
            return new JsonResult(false, "没有找到上传的文件");
        }
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("rdMaterialUploadPosition",
                toGetParamVal(parameters.get("businessType"))).getValue();
        if (StringUtil.isEmpty(filePathBase)) {
            logger.error("没有找到上传的路径");
            return new JsonResult(false, "没有找到上传的路径");
        }
        try {
            String businessId = toGetParamVal(parameters.get("businessId"));
            String businessType = toGetParamVal(parameters.get("businessType"));
            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String fileDesc = toGetParamVal(parameters.get("fileDesc"));
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();
            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + businessId;
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
            fileInfo.put("businessId", businessId);
            fileInfo.put("businessType", businessType);
            fileInfo.put("fileName", fileName);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("fileDesc", fileDesc);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            rdMaterialFileDao.insertFileInfos(fileInfo);
            return new JsonResult(true, "上传成功");
        } catch (Exception e) {
            logger.error("Exception in saveFiles", e);
            throw e;
        }
    }

    //..文件删除命令
    public void deleteFile(String fileId, String fileName, String businessId, String businessType) throws Exception {
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("rdMaterialUploadPosition", businessType).getValue();
        this.deleteOneFileFromDisk(fileId, fileName, businessId, filePathBase);
        JSONObject params = new JSONObject();
        params.put("id", fileId);
        rdMaterialFileDao.deleteFileInfos(params);
    }

    //..获取文件信息
    public List<JSONObject> getFileListInfos(JSONObject params) {
        List<JSONObject> fileList = new ArrayList<>();
        fileList = rdMaterialFileDao.getFileListInfos(params);
        return fileList;
    }

    //..检查文件是否存在
    public JsonResult checkFile(String businessId, String businessType, String businessDes) {
        JsonResult result = new JsonResult(false, "文件校验不通过");
        JSONObject params = new JSONObject();
        params.put("businessIds", Arrays.asList(businessId));
        params.put("businessTypes", Arrays.asList(businessType));
        List<JSONObject> fileListInfos = this.getFileListInfos(params);
        if (fileListInfos != null && !fileListInfos.isEmpty()) {
            result.setMessage("文件校验通过");
            result.setSuccess(true);
        } else {
            result.setMessage(MessageFormat.format("{0}!", businessDes));
            result.setSuccess(false);
        }
        return result;
    }

    //..文件信息删除
    public JsonResult deleteFileInfos(JSONObject params) throws Exception {
        rdMaterialFileDao.deleteFileInfos(params);
        return new JsonResult(true, "删除文件信息成功");
    }
}
