package com.redxun.strategicPlanning.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.OfficeDocPreview;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.standardManager.core.manager.StandardConfigManager;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.strategicPlanning.core.dao.MonthlyDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class MonthlyService {
    private static Logger logger = LoggerFactory.getLogger(StandardConfigManager.class);
    @Autowired
    private MonthlyDao monthlyDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;

    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;

    public JsonPageResult<?> getMonthlyList(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        JsonPageResult result = new JsonPageResult();
        String docName = RequestUtil.getString(request, "docName", "");
        String type = RequestUtil.getString(request, "type", "");
        Map<String, Object> param = new HashMap<>();
        if (StringUtils.isNotBlank(docName)) {
            param.put("docName", docName);
        }
        if (StringUtils.isNotBlank(type)) {
            param.put("type", type);
        }
        rdmZhglUtil.addPage(request, param);
        rdmZhglUtil.addOrder(request, param, "CREATE_TIME_", "desc");
        List<JSONObject> gjllFileList = monthlyDao.queryMonthlyList(param);
        if (gjllFileList != null && !gjllFileList.isEmpty()) {
            for (JSONObject oneFile : gjllFileList) {
                if (oneFile.getDate("CREATE_TIME_") != null) {
                    oneFile.put("CREATE_TIME_",
                            DateUtil.formatDate(oneFile.getDate("CREATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
                }
            }
        }
        result.setData(gjllFileList);
        int count = monthlyDao.countMonthlyFileList(param);
        result.setTotal(count);
        return result;
    }



    public void deleteOneMonthlyFile(String fileId, String fileName) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, "", WebAppUtil.getProperty("zlghMonthlyFile"));
        Map<String, Object> param = new HashMap<>();
        param.put("Id", fileId);
        monthlyDao.deleteMonthlyFile(param);
    }

    // 保存文件到后台和数据库
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
        String filePathBase = WebAppUtil.getProperty("zlghMonthlyFile");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find zlghMonthlyPathBase");
            return;
        }
        try {
            String type=RequestUtil.getString(request,"type","");
            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));

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
            // 记录成功写入磁盘的文件信息
            Map<String, Object> fileInfo = new HashMap<>();
            String t= parameters.get("ykTime")[0];
            fileInfo.put("Id", id);
            fileInfo.put("fileName", fileName);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("yk_time",parseTime(t));
            fileInfo.put("description", parameters.get("description")[0]);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            fileInfo.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            fileInfo.put("type",type);
            //保存到数据库
            monthlyDao.createMonthlyFile(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveStandardTemplate", e);
        }
    }

    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }

    public  String parseTime(String dateStr) {
        if (StringUtils.isNotBlank(dateStr)) {
            String parseDate;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            Date date = new Date(dateStr);
            parseDate= sdf.format(date);
            return parseDate;
        }
        return null;
    }

    //文件的下载或者是pdf文件的预览
    public ResponseEntity<byte[]> pdfFilePreview(String fileName, String fileId, String formId,
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
}
