package com.redxun.portrait.core.manager;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.redxun.rdmCommon.core.manager.CommonFuns;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.util.OfficeDocPreview;
import com.redxun.portrait.core.dao.XfdyPersonDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Service
public class XfdyFileUploadManager {
    private Logger logger = LoggerFactory.getLogger(XfdyFileUploadManager.class);
    @Autowired
    private XfdyPersonDao xfdyPersonDao;
    // 从数据库中查询文档信息
    public List<Map<String, Object>> queryXfdyFileList(Map<String, Object> params) {
        try {
            return xfdyPersonDao.queryXfdyFileList(params);
        } catch (Exception e) {
            logger.error("Exception in queryXfdyFileList", e);
            return null;
        }
    }
    // 保存文件到后台和数据库
    public void saveXfdyUploadFiles(HttpServletRequest request) {
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
        String filePathBase = WebAppUtil.getProperty("xfdyFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find xfdyFilePathBase");
            return;
        }
        try {
            // 记录成功写入磁盘的文件信息
            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String fileDesc = toGetParamVal(parameters.get("fileDesc"));
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
            fileInfo.put("fileName", fileName);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("fileDesc", fileDesc);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            xfdyPersonDao.addXfdyFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in getFilesFromRequest", e);
        }
        return;
    }
    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }
    // 删除一个文件
    public void deleteXfdyOneFile( String id, String fileName) {
        try {
            String fullFilePath = "";
            if (StringUtils.isBlank(fileName)) {
                logger.error("fileName is blank");
                return;
            }
            String filePathBase = WebAppUtil.getProperty("xfdyFilePathBase");
            if (StringUtils.isBlank(filePathBase)) {
                logger.error("can't find filePathBase");
                return;
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String realFileName = id + "." + suffix;
            fullFilePath = filePathBase + File.separator + realFileName;
            // 先删除磁盘中的文件
            File file = new File(fullFilePath);
            boolean deleteResult = true;
            if (file.exists()) {
                logger.info("File " + fullFilePath + " will be deleted!");
                deleteResult = file.delete();
            }
            if (!deleteResult) {
                logger.error("File " + fullFilePath + " delete failed!");
                return;
            }
            // 删除预览文件夹中生成的临时pdf文件
            String convertPdfDir = WebAppUtil.getProperty("convertPdfDir");
            String convertPdfPath = filePathBase + File.separator + convertPdfDir + File.separator
                    + OfficeDocPreview.generateConvertPdfFileName(realFileName);
            File tmpPdfFile = new File(convertPdfPath);
            tmpPdfFile.delete();
            // 删除数据库中的数据，只需删除一遍
            Map<String, Object> params = new HashMap<>();
            params.put("id", id);
            xfdyPersonDao.deleteXfdyFileInfos(params);

        } catch (Exception e) {
            logger.error("Exception in deleteXfdyOneFile", e);
        }
    }
}
