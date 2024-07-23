package com.redxun.rdmZhgl.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.rdmZhgl.core.dao.CxyProjectFileDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.xcmgJsjl.core.dao.XcmgJsjlFileDao;
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
public class CxyProjectFileService {
    private static Logger logger = LoggerFactory.getLogger(CxyProjectFileService.class);
    @Autowired
    private CxyProjectFileDao cxyProjectFileDao;

    //保存文件到后台和数据库
    public void saveCxyProjectUploadFiles(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            logger.warn("没有找到上传的参数");
            return;
        }
        // 多附件上传需要用到的MultipartHttpServletRequest
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap == null || fileMap.isEmpty()) {
            logger.warn("没有找到上传的文件");
            return;
        }
        String filePathBase = WebAppUtil.getProperty("cxyProjectFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find cxyProjectFilePathBase");
            return;
        }
        String filePathBasePreview = WebAppUtil.getProperty("cxyProjectFilePathBase_preview");
        if (StringUtils.isBlank(filePathBasePreview)) {
            logger.error("can't find cxyProjectFilePathBase_preview");
            return;
        }
        try {
            String cxyProjectId = toGetParamVal(parameters.get("cxyProjectId"));
            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String fileDesc = toGetParamVal(parameters.get("fileDesc"));

            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();

            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + cxyProjectId;
            File pathFile = new File(filePath);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fileFullPath = filePath + File.separator + id + "." + suffix;
            File file = new File(fileFullPath);
            FileCopyUtils.copy(mf.getBytes(), file);

            // 向预览目录中写入文件
            String filePathPreview = filePathBasePreview + File.separator + cxyProjectId;
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
            fileInfo.put("cxyProjectId", cxyProjectId);
            fileInfo.put("fileDesc", fileDesc);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            cxyProjectFileDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveCxyProjectUploadFiles", e);
        }
    }

    //查询某个产学研项目下的文件列表
    public List<JSONObject> queryCxyProjectFileList(Map<String, Object> params) {
        return cxyProjectFileDao.queryFilesByCxyProjectIds(params);
    }

    //删除某个文件
    public void deleteOneFile(String id, String fileName, String cxyProjectId) {
        deleteOneFileFromDisk(id, fileName, cxyProjectId);
        cxyProjectFileDao.deleteFileById(id);
    }

    //从磁盘中删除文件（包括预览目录中的）
    public void deleteOneFileFromDisk(String id, String fileName, String CxyProjectId) {
        String filePathBase = WebAppUtil.getProperty("cxyProjectFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find cxyProjectFilePathBase");
            return;
        }
        String filePathBasePreview = WebAppUtil.getProperty("cxyProjectFilePathBase_preview");
        if (StringUtils.isBlank(filePathBasePreview)) {
            logger.error("can't find cxyProjectFilePathBase_preview");
            return;
        }
        try {
            // 删除下载目录中文件
            String filePath = filePathBase + File.separator + CxyProjectId;
            File pathFile = new File(filePath);
            if (!pathFile.exists()) {
                return;
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fileFullPath = filePath + File.separator + id + "." + suffix;
            File file = new File(fileFullPath);
            file.delete();

            // 删除预览目录中文件
            String filePathPreview = filePathBasePreview + File.separator + CxyProjectId;
            File pathFilePreview = new File(filePathPreview);
            if (!pathFilePreview.exists()) {
                return;
            }
            String fileFullPathPreview = filePathPreview + File.separator + id + "." + suffix;
            File previewFile = new File(fileFullPathPreview);
            previewFile.delete();

            // 删除预览目录中pdf文件
            String convertPdfDir = WebAppUtil.getProperty("convertPdfDir");
            String filePathPreviewPdf = filePathBasePreview + File.separator + CxyProjectId + File.separator + convertPdfDir;
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

    //从磁盘中删除技术交流文件夹（包括预览文件夹），前提是已经将文件删除完
    public void deleteCxyProjectDirFromDisk(String cxyProjectId) {
        String filePathBase = WebAppUtil.getProperty("cxyProjectFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find cxyProjectFilePathBase");
            return;
        }
        String filePathBasePreview = WebAppUtil.getProperty("cxyProjectFilePathBase_preview");
        if (StringUtils.isBlank(filePathBasePreview)) {
            logger.error("can't find cxyProjectFilePathBase_preview");
            return;
        }
        try {
            // 删除下载目录
            String filePath = filePathBase + File.separator + cxyProjectId;
            File pathFile = new File(filePath);
            pathFile.delete();

            // 删除预览目录下的tmp文件夹
            String convertPdfDir = WebAppUtil.getProperty("convertPdfDir");
            String filePathPreviewPdf = filePathBasePreview + File.separator + cxyProjectId + File.separator + convertPdfDir;
            File pathFilePreviewPdf = new File(filePathPreviewPdf);
            pathFilePreviewPdf.delete();

            // 删除预览目录
            String filePathPreview = filePathBasePreview + File.separator + cxyProjectId;
            File pathFilePreview = new File(filePathPreview);
            pathFilePreview.delete();
        } catch (Exception e) {
            logger.error("Exception in deleteCxyProjectDirFromDisk", e);
        }
    }

    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }
}
