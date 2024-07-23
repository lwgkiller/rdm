package com.redxun.xcmgjssjk.core.service;

import com.redxun.core.util.OfficeDocPreview;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.File;

@Service
public class JssjkFileManager {
    private static Logger logger = LoggerFactory.getLogger(RdmZhglFileManager.class);
    //文件的下载或者是pdf文件的预览
    public ResponseEntity<byte[]> pdfPreviewOrDownLoad(String fileName, String fileId, String formId,
                                                       String fileBasePath) {
        try {
            if (StringUtils.isBlank(fileName)) {
                logger.error("操作失败，文件名为空！");
                return null;
            }
            if (StringUtils.isBlank(fileId)) {
                logger.error("操作失败，文件Id为空！");
                return null;
            }
            if (StringUtils.isBlank(fileBasePath)) {
                logger.error("操作失败，找不到存储路径");
                return null;
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fullFilePath = fileBasePath + (StringUtils.isBlank(formId) ? "" : (File.separator + formId))
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
    public void officeFilePreview(String fileName, String fileId, String formId, String fileBasePath,
                                  HttpServletResponse response) {
        if (StringUtils.isBlank(fileName)) {
            logger.error("操作失败，文件名为空！");
            return;
        }
        if (StringUtils.isBlank(fileId)) {
            logger.error("操作失败，文件Id为空！");
            return;
        }
        if (StringUtils.isBlank(fileBasePath)) {
            logger.error("操作失败，找不到存储路径");
            return;
        }
        String suffix = CommonFuns.toGetFileSuffix(fileName);
        String originalFilePath = fileBasePath + (StringUtils.isBlank(formId) ? "" : (File.separator + formId))
                + File.separator + fileId + "." + suffix;
        String convertPdfDir = WebAppUtil.getProperty("convertPdfDir");
        String convertPdfPath = fileBasePath + (StringUtils.isBlank(formId) ? "" : (File.separator + formId))
                + File.separator + convertPdfDir + File.separator + fileId + ".pdf";;
        OfficeDocPreview.previewOfficeDoc(originalFilePath, convertPdfPath, response);
    }
    public void imageFilePreview(String fileName, String fileId, String formId, String fileBasePath,
                                 HttpServletResponse response) {
        if (StringUtils.isBlank(fileName)) {
            logger.error("操作失败，文件名为空！");
            return;
        }
        if (StringUtils.isBlank(fileId)) {
            logger.error("操作失败，文件Id为空！");
            return;
        }
        if (StringUtils.isBlank(fileBasePath)) {
            logger.error("操作失败，找不到存储路径");
            return;
        }
        String suffix = CommonFuns.toGetFileSuffix(fileName);
        String originalFilePath = fileBasePath + (StringUtils.isBlank(formId) ? "" : (File.separator + formId))
                + File.separator + fileId + "." + suffix;
        OfficeDocPreview.imagePreview(originalFilePath, response);
    }
    /**
     * 从磁盘中删除文件（下载目录以及tmp中的pdf）
     */
    public void deleteOneFileFromDisk(String fileId, String fileName, String formId, String filePathBase) {
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find filePathBase");
            return;
        }
        try {
            // 删除下载目录中文件
            String filePath = filePathBase;
            if (StringUtils.isNotBlank(formId)) {
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
    public void deleteDirFromDisk(String formId, String filePathBase) {
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find jsjlFilePathBase");
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
