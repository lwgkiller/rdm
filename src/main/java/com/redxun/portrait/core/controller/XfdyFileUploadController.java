package com.redxun.portrait.core.controller;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.redxun.core.util.DateUtil;
import com.redxun.core.util.OfficeDocPreview;
import com.redxun.portrait.core.manager.XfdyFileUploadManager;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Controller
@RequestMapping("/xfdy/core/file/")
public class XfdyFileUploadController {
    private Logger logger = LoggerFactory.getLogger(XfdyFileUploadController.class);
    @Resource
    private XfdyFileUploadManager fileUploadManager;

    @Resource
    private RdmZhglUtil rdmZhglUtil;
    @RequestMapping("listPage")
    public ModelAndView listPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "portrait/userInfo/xfdyFileList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        boolean isXfdy = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), "先锋党员专员");
        mv.addObject("isXfdy", isXfdy);
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }
    @RequestMapping("xfdyFileList")
    @ResponseBody
    public List<Map<String, Object>> xfdyFileList(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String docName = RequestUtil.getString(request, "docName");
        Map<String, Object> params = new HashMap<>();
        if (StringUtils.isNotBlank(docName)) {
            params.put("fileName", docName);
        }
        List<Map<String, Object>> fileInfos = fileUploadManager.queryXfdyFileList(params);
        // 格式化时间
        if (fileInfos != null && !fileInfos.isEmpty()) {
            for (Map<String, Object> oneFile : fileInfos) {
                if (oneFile.get("CREATE_TIME_") != null) {
                    oneFile.put("CREATE_TIME_",
                            DateUtil.formatDate((Date)oneFile.get("CREATE_TIME_"), "yyyy-MM-dd"));
                }
            }
        }
        return fileInfos;
    }
    @RequestMapping("xfdyUploadWindow")
    public ModelAndView xfdyUploadWindow(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String jspPath = "portrait/userInfo/xfdyFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        return mv;
    }
    @RequestMapping(value = "xfdyUpload")
    @ResponseBody
    public Map<String, Object> xfdyUpload(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件，成功后再写入数据库，前台是一个一个文件的调用
            fileUploadManager.saveXfdyUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }
    // 项目管理文档的下载（预览pdf也调用这里）
    @RequestMapping("fileDownload")
    public ResponseEntity<byte[]> fileDownload(HttpServletRequest request, HttpServletResponse response) {
        try {
            String fileName = RequestUtil.getString(request, "fileName");
            if (StringUtils.isBlank(fileName)) {
                logger.error("操作失败，文件名为空！");
                return null;
            }
            // id
            String fileId = RequestUtil.getString(request, "fileId");
            if (StringUtils.isBlank(fileId)) {
                logger.error("操作失败，文件主键为空！");
                return null;
            }
            // 预览还是下载
            String action = RequestUtil.getString(request, "action");
            if (StringUtils.isBlank(action)) {
                logger.error("操作类型为空");
                return null;
            }
            String fileBasePath = "";
            fileBasePath = WebAppUtil.getProperty("xfdyFilePathBase");
            if (StringUtils.isBlank(fileBasePath)) {
                logger.error("操作失败，找不到存储路径");
                return null;
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String realFileName = fileId + "." + suffix;

            String fullFilePath = fileBasePath  + File.separator + realFileName;
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
    @RequestMapping("imagePreview")
    @ResponseBody
    public void imagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        if (StringUtils.isBlank(fileName)) {
            logger.error("图片预览失败，文件名为空！");
            return;
        }
        String fileId = RequestUtil.getString(request, "fileId");
        String fileBasePath = WebAppUtil.getProperty("xfdyFilePathBase");
        if (StringUtils.isBlank(fileBasePath)) {
            logger.error("图片预览失败，找不到存储路径");
            return;
        }
        String suffix = CommonFuns.toGetFileSuffix(fileName);
        String realFileName = fileId + "." + suffix;
        String originalFilePath = fileBasePath  + File.separator + realFileName;
        OfficeDocPreview.imagePreview(originalFilePath, response);
    }
    @RequestMapping("officePreview")
    public void officePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        if (StringUtils.isBlank(fileName)) {
            logger.error("文档预览失败，文件名为空！");
            return;
        }
        String fileId = RequestUtil.getString(request, "fileId");
        String fileBasePath = WebAppUtil.getProperty("xfdyFilePathBase");
        if (StringUtils.isBlank(fileBasePath)) {
            logger.error("文档预览失败，找不到存储路径");
            return;
        }
        String suffix = CommonFuns.toGetFileSuffix(fileName);
        String realFileName = fileId + "." + suffix;
        String originalFilePath = fileBasePath  + File.separator + realFileName;
        String convertPdfDir = WebAppUtil.getProperty("convertPdfDir");
        String convertPdfPath = fileBasePath  + File.separator + convertPdfDir + File.separator
                + OfficeDocPreview.generateConvertPdfFileName(realFileName);
        OfficeDocPreview.previewOfficeDoc(originalFilePath, convertPdfPath, response);
    }
    // 删除某个文件（从文件表和磁盘上都删除）
    @RequestMapping("fileDelete")
    public void fileDelete(HttpServletRequest request, HttpServletResponse response) {
        String id = RequestUtil.getString(request, "id");
        String fileName = RequestUtil.getString(request, "fileName");
        fileUploadManager.deleteXfdyOneFile( id,  fileName);
    }
}
