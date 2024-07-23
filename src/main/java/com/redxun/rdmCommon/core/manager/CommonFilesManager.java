package com.redxun.rdmCommon.core.manager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.dao.CommonFilesDao;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.sys.core.util.SysPropertiesUtil;

/**
 * @author zhangzhen
 */
@Service
public class CommonFilesManager {
    private static final Logger logger = LoggerFactory.getLogger(CommonFilesManager.class);
    @Resource
    private CommonFilesDao commonFilesDao;
    @Resource
    private RdmZhglFileManager rdmZhglFileManager;
    @Resource
    private LoginRecordManager loginRecordManager;

    public JsonPageResult<?> query(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            String mainId = request.getParameter("mainId");
            String tableName = request.getParameter("tableName");
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            params = CommonFuns.getSearchParam(params, request, true);
            params.put("mainId", mainId);
            params.put("tableName", tableName);
            list = commonFilesDao.getFileList(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            params.put("mainId", mainId);
            params.put("tableName", tableName);
            totalList = commonFilesDao.getFileList(params);
            convertDate(list);
            // 返回结果
            result.setData(list);
            result.setTotal(totalList.size());
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
            result.setSuccess(false);
            result.setMessage("系统异常");
        }
        return result;
    }
    public void saveUploadFiles(HttpServletRequest request) {
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
        String filePathBase = SysPropertiesUtil.getGlobalProperty("commonFileUrl");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find commonFileUrl");
            return;
        }
        try {
            String mainId = CommonFuns.toGetParamVal(parameters.get("mainId"));
            String id = IdUtil.getId();
            String fileName = CommonFuns.toGetParamVal(parameters.get("fileName"));
            String fileSize = CommonFuns.toGetParamVal(parameters.get("fileSize"));
            String fileDesc = CommonFuns.toGetParamVal(parameters.get("fileDesc"));
            String tableName = CommonFuns.toGetParamVal(parameters.get("tableName"));
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();

            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator+tableName+ File.separator + mainId;
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
            fileInfo.put("mainId", mainId);
            fileInfo.put("tableName", tableName);
            fileInfo.put("fileDesc", fileDesc);
            fileInfo.put("filePath", fileFullPath);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            commonFilesDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public void deleteOneFile(String fileId, String fileName, String mainId,String fileUrl,String tableName) {
        deleteOneFileFromDisk(fileId, fileName, mainId,SysPropertiesUtil.getGlobalProperty(fileUrl),tableName);
        Map<String, Object> param = new HashMap<>(16);
        commonFilesDao.delFileById(fileId);
    }
    /**
     * 从磁盘中删除文件（下载目录以及tmp中的pdf）
     */
    public void deleteOneFileFromDisk(String fileId, String fileName, String formId, String filePathBase,String tableName) {
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find filePathBase");
            return;
        }
        try {
            // 删除下载目录中文件
            String filePath = filePathBase+(StringUtils.isBlank(tableName) ? "" : (File.separator + tableName));
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

    public JSONObject getFileList(JSONObject paramJson){
        List<JSONObject> fileList = new ArrayList<>();
        try {
            fileList = commonFilesDao.getFileListByMainId(paramJson.getString("mainId"));
        }catch (Exception e){
            logger.error("Exception in saveUploadFiles", e);
        }
        return ResultUtil.result(true,"获取成功",fileList);
    }
    public JSONObject getFileListByParam(JSONObject paramJson){
        List<JSONObject> fileList = new ArrayList<>();
        try {
            fileList = commonFilesDao.getFileListByParam(paramJson);
        }catch (Exception e){
            logger.error("Exception in getFileListByParam", e);
        }
        return ResultUtil.result(true,"获取成功",fileList);
    }
    public  void fileToZip(String filePath, ZipOutputStream zipOutputStream,String fileName) throws IOException{
        //需要压缩的文件
        File file = new File(filePath);
        FileInputStream fileInputStream = new FileInputStream(filePath);
        //缓冲
        byte[] bufferArea = new byte[1024*10];
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream,1024*10);
        //将当前文件作为一个zip实体写入压缩流，fileName代表压缩文件中的文件名称
        zipOutputStream.putNextEntry(new ZipEntry(fileName));
        int length = 0;
        while ((length=bufferedInputStream.read(bufferArea,0,1024*10)) != -1){
            zipOutputStream.write(bufferArea,0,length);
        }
        //关闭流
        fileInputStream.close();
        bufferedInputStream.close();
    }
    public static void convertDate(List<Map<String, Object>> list) {
        if (list != null && !list.isEmpty()) {
            for (Map<String, Object> oneApply : list) {
                for (String key : oneApply.keySet()) {
                    if (key.endsWith("_TIME_") || key.endsWith("_time") || key.endsWith("_date")) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd  HH:mm:ss"));
                        }
                    }
                    if ("reportDate".equals(key)) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd"));
                        }
                    }
                    if ("reportValidity".equals(key)) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd"));
                        }
                    }
                    if ("closeDate".equals(key)) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd HH:mm:ss"));
                        }
                    }
                }
            }
        }
    }
}
