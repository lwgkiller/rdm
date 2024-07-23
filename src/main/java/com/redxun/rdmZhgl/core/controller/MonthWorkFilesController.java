
package com.redxun.rdmZhgl.core.controller;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.rdmZhgl.core.service.MonthWorkFilesService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.manager.MybatisBaseManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.controller.MybatisListController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.sys.core.util.SysPropertiesUtil;

/**
 *月度工作证明材料
 * @author zz
 */
@Controller
@RequestMapping("/rdmZhgl/core/monthWork/files/")
public class MonthWorkFilesController extends MybatisListController {
    @Resource
    private MonthWorkFilesService monthWorkFilesService;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Override
    public MybatisBaseManager getBaseManager() {
        return null;
    }
    @RequestMapping("fileUploadWindow")
    public ModelAndView fileUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/monthWorkFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("applyId", RequestUtil.getString(request, "applyId", ""));
        return mv;
    }
    @RequestMapping(value = "fileUpload")
    @ResponseBody
    public Map<String, Object> fileUpload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件，成功后再写入数据库，前台是一个一个文件的调用
            monthWorkFilesService.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }
    @RequestMapping("fileList")
    @ResponseBody
    public List<JSONObject> getFileList(HttpServletRequest request, HttpServletResponse response) {
        return monthWorkFilesService.getFileListByApplyId(request);
    }
    @RequestMapping("delFile")
    public void delFile(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "id");
        String applyId = RequestUtil.getString(request, "applyId");
        monthWorkFilesService.deleteOneSaleFile(fileId, fileName, applyId);
    }
    @RequestMapping("pdfPreview")
    public ResponseEntity<byte[]> pdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String applyId = RequestUtil.getString(request, "formId");
        String fileBasePath = SysPropertiesUtil.getGlobalProperty("monthWorkFileUrl");
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, applyId, fileBasePath);
    }

    //..
    @RequestMapping("officePreview")
    @ResponseBody
    public void officePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String applyId = RequestUtil.getString(request, "formId");
        String fileBasePath = SysPropertiesUtil.getGlobalProperty("monthWorkFileUrl");
        rdmZhglFileManager.officeFilePreview(fileName, fileId, applyId, fileBasePath, response);
    }

    //..
    @RequestMapping("imagePreview")
    @ResponseBody
    public void imagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String applyId = RequestUtil.getString(request, "formId");
        String fileBasePath = SysPropertiesUtil.getGlobalProperty("monthWorkFileUrl");
        rdmZhglFileManager.imageFilePreview(fileName, fileId, applyId, fileBasePath, response);
    }
    @RequestMapping("fileDownload")
    public ResponseEntity<byte[]> fileDownload(HttpServletRequest request, HttpServletResponse response) {
        try {
            String fileName = RequestUtil.getString(request, "fileName");
            if (StringUtils.isBlank(fileName)) {
                logger.error("操作失败，文件名为空！");
                return null;
            }
            // 预览还是下载，取的根路径不一样
            String action = RequestUtil.getString(request, "action");
            if (StringUtils.isBlank(action)) {
                logger.error("操作类型为空");
                return null;
            }
            String fileId = RequestUtil.getString(request, "fileId");
            if (StringUtils.isBlank(fileId)) {
                logger.error("操作失败，文件主键为空！");
                return null;
            }
            String applyId = RequestUtil.getString(request, "applyId");
            String fileBasePath = SysPropertiesUtil.getGlobalProperty("monthWorkFileUrl");
            if (StringUtils.isBlank(fileBasePath)) {
                logger.error("操作失败，找不到存储根路径");
                return null;
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String relativeFilePath = "";
            if (StringUtils.isNotBlank(applyId)) {
                relativeFilePath = File.separator + applyId;
            }
            String realFileName = fileId + "." + suffix;
            String fullFilePath = fileBasePath + relativeFilePath + File.separator + realFileName;
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
