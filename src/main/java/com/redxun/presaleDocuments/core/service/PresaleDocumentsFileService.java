package com.redxun.presaleDocuments.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.OfficeDocPreview;
import com.redxun.core.util.StringUtil;
import com.redxun.presaleDocuments.core.dao.PresaleDocumentsFileDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.util.RdmCommonUtil;
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

@Service
public class PresaleDocumentsFileService {
    private static Logger logger = LoggerFactory.getLogger(PresaleDocumentsFileService.class);
    @Autowired
    private PresaleDocumentsFileDao presaleDocumentsFileDao;
    @Autowired
    private SysDicManager sysDicManager;

    //..从磁盘中删除文件（下载目录以及tmp中的pdf）
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

    //..从磁盘中删除某一个表单文件夹（包括预览文件夹），前提是已经将目录下的文件都删除完了
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

    //表单绑定文件添加
    public void updateFile2Disk(String id, MultipartFile fileObj, String filePathBase) throws Exception {
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

    //..文件的下载或者是pdf文件的预览
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
        ;
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
    public JsonResult saveFiles(HttpServletRequest request, String filePathBase) throws Exception {
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
        try {
            String businessId = RdmCommonUtil.toGetParamVal(parameters.get("businessId"));
            String businessType = RdmCommonUtil.toGetParamVal(parameters.get("businessType"));
            String id = IdUtil.getId();
            String fileName = RdmCommonUtil.toGetParamVal(parameters.get("fileName"));
            String fileSize = RdmCommonUtil.toGetParamVal(parameters.get("fileSize"));
            String fileDesc = RdmCommonUtil.toGetParamVal(parameters.get("fileDesc"));
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
            presaleDocumentsFileDao.insertFileInfos(fileInfo);
            return new JsonResult(true, "上传成功");
        } catch (Exception e) {
            logger.error("Exception in saveFiles", e);
            throw e;
        }
    }

    //..
    public void deleteFile(String fileId, String fileName, String businessId, String businessType, String filePathBase) throws Exception {
        this.deleteOneFileFromDisk(fileId, fileName, businessId, filePathBase);
        JSONObject params = new JSONObject();
        params.put("id", fileId);
        presaleDocumentsFileDao.deleteFileInfos(params);
    }

    //..
    public List<JSONObject> getFileListInfos(JSONObject params) {
        List<JSONObject> fileList = new ArrayList<>();
        fileList = presaleDocumentsFileDao.getFileListInfos(params);
        return fileList;
    }

    //..
    public JsonResult insertFileInfos(JSONObject jsonObject) throws Exception {
        presaleDocumentsFileDao.insertFileInfos(jsonObject);
        return new JsonResult(true, "添加文件信息成功");
    }

    //..
    public JsonResult deleteFileInfos(JSONObject jsonObject) throws Exception {
        presaleDocumentsFileDao.deleteFileInfos(jsonObject);
        return new JsonResult(true, "删除文件信息成功");
    }
}
