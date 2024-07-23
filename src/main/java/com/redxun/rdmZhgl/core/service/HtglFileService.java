package com.redxun.rdmZhgl.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.rdmZhgl.core.dao.CxyProjectFileDao;
import com.redxun.rdmZhgl.core.dao.HtglFileDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class HtglFileService {
    private static Logger logger = LoggerFactory.getLogger(HtglFileService.class);
    @Autowired
    private HtglFileDao htglFileDao;

    //..
    public void saveContractUploadFiles(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            logger.warn("没有找到上传的参数");
            return;
        }
        // 多附件上传需要用到的MultipartHttpServletRequest
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap == null || fileMap.isEmpty()) {
            logger.warn("没有找到上传的文件");
            return;
        }
        String filePathBase = WebAppUtil.getProperty("htglFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find htglFilePathBase");
            return;
        }
        String filePathBasePreview = WebAppUtil.getProperty("htglFilePathBase_preview");
        if (StringUtils.isBlank(filePathBasePreview)) {
            logger.error("can't find htglFilePathBase_preview");
            return;
        }
        try {
            String contractId = toGetParamVal(parameters.get("contractId"));
            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String fileDesc = toGetParamVal(parameters.get("fileDesc"));
            String fileType = toGetParamVal(parameters.get("fileType"));
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();

            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + contractId;
            File pathFile = new File(filePath);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fileFullPath = filePath + File.separator + id + "." + suffix;
            File file = new File(fileFullPath);
            FileCopyUtils.copy(mf.getBytes(), file);

            // 向预览目录中写入文件
            String filePathPreview = filePathBasePreview + File.separator + contractId;
            File pathFilePreview = new File(filePathPreview);
            if (!pathFilePreview.exists()) {
                pathFilePreview.mkdirs();
            }
            String fileFullPathPreview = filePathPreview + File.separator + id + "." + suffix;
            File previewFile = new File(fileFullPathPreview);
            FileCopyUtils.copy(mf.getBytes(), previewFile);

            // 写入数据库
            JSONObject fileInfo = new JSONObject();
            fileInfo.put("id", id);
            fileInfo.put("fileName", fileName);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("contractId", contractId);
            fileInfo.put("fileDesc", fileDesc);
            fileInfo.put("fileType", fileType);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            htglFileDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveContractUploadFiles", e);
        }
    }

    //..
    public List<JSONObject> queryContractFileList(Map<String, Object> params) {
        return htglFileDao.queryFilesByContractIds(params);
    }

    //..
    public void deleteOneFile(String id, String fileName, String contractId) {
        deleteOneFileFromDisk(id, fileName, contractId);
        htglFileDao.deleteFileById(id);
    }

    //..
    public void deleteOneFileFromDisk(String id, String fileName, String contractId) {
        String filePathBase = WebAppUtil.getProperty("htglFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find htglFilePathBase");
            return;
        }
        String filePathBasePreview = WebAppUtil.getProperty("htglFilePathBase_preview");
        if (StringUtils.isBlank(filePathBasePreview)) {
            logger.error("can't find htglFilePathBase_preview");
            return;
        }
        try {
            // 删除下载目录中文件
            String filePath = filePathBase + File.separator + contractId;
            File pathFile = new File(filePath);
            if (!pathFile.exists()) {
                return;
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fileFullPath = filePath + File.separator + id + "." + suffix;
            File file = new File(fileFullPath);
            file.delete();

            // 删除预览目录中文件
            String filePathPreview = filePathBasePreview + File.separator + contractId;
            File pathFilePreview = new File(filePathPreview);
            if (!pathFilePreview.exists()) {
                return;
            }
            String fileFullPathPreview = filePathPreview + File.separator + id + "." + suffix;
            File previewFile = new File(fileFullPathPreview);
            previewFile.delete();

            // 删除预览目录中pdf文件
            String convertPdfDir = WebAppUtil.getProperty("convertPdfDir");
            String filePathPreviewPdf = filePathBasePreview + File.separator + contractId + File.separator + convertPdfDir;
            File pathFilePreviewPdf = new File(filePathPreviewPdf);
            if (!pathFilePreviewPdf.exists()) {
                return;
            }
            String fileFullPathPreviewPdf = filePathPreviewPdf + File.separator + id + ".pdf";
            File previewFilePdf = new File(fileFullPathPreviewPdf);
            previewFilePdf.delete();
        } catch (Exception e) {
            logger.error("Exception in deleteOneFileFromDisk", e);
        }
    }

    //..
    public void deleteContractDirFromDisk(String contractId) {
        String filePathBase = WebAppUtil.getProperty("htglFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find htglFilePathBase");
            return;
        }
        String filePathBasePreview = WebAppUtil.getProperty("htglFilePathBase_preview");
        if (StringUtils.isBlank(filePathBasePreview)) {
            logger.error("can't find htglFilePathBase_preview");
            return;
        }
        try {
            // 删除下载目录
            String filePath = filePathBase + File.separator + contractId;
            File pathFile = new File(filePath);
            pathFile.delete();

            // 删除预览目录下的tmp文件夹
            String convertPdfDir = WebAppUtil.getProperty("convertPdfDir");
            String filePathPreviewPdf = filePathBasePreview + File.separator + contractId + File.separator + convertPdfDir;
            File pathFilePreviewPdf = new File(filePathPreviewPdf);
            pathFilePreviewPdf.delete();

            // 删除预览目录
            String filePathPreview = filePathBasePreview + File.separator + contractId;
            File pathFilePreview = new File(filePathPreview);
            pathFilePreview.delete();
        } catch (Exception e) {
            logger.error("Exception in deleteContractDirFromDisk", e);
        }
    }

    //..
    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }
}
