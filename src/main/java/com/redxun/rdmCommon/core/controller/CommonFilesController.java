package com.redxun.rdmCommon.core.controller;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.rdmCommon.core.manager.CommonBigFileUploadManager;
import com.redxun.rdmCommon.core.manager.CommonFilesManager;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.sys.core.util.SysPropertiesUtil;

/**
 * @author zhangzhen
 */
@RestController
@RequestMapping("/common/core/file/")
public class CommonFilesController {
    protected Logger logger = LogManager.getLogger(CommonFilesController.class);
    @Resource
    CommonFilesManager commonFilesManager;
    @Autowired
    CommonBigFileUploadManager commonBigFileUploadManager;

    @RequestMapping("fileWindow")
    public ModelAndView getFileWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "common/core/commonFileList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String mainId = RequestUtil.getString(request, "mainId");
        String tableName = RequestUtil.getString(request, "tableName");
        Boolean editable = RequestUtil.getBoolean(request, "editable");
        mv.addObject("mainId", mainId);
        mv.addObject("tableName", tableName);
        mv.addObject("editable", editable);
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        return mv;
    }

    /**
     * 获取列表数据
     */
    @RequestMapping("files")
    @ResponseBody
    public JsonPageResult<?> getFiles(HttpServletRequest request, HttpServletResponse response) {
        return commonFilesManager.query(request);
    }

    @RequestMapping("fileUploadWindow")
    public ModelAndView fileUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "common/core/commonFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("mainId", RequestUtil.getString(request, "mainId", ""));
        mv.addObject("tableName", RequestUtil.getString(request, "tableName", ""));
        return mv;
    }

    @RequestMapping(value = "fileUpload")
    @ResponseBody
    public Map<String, Object> fileUpload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件，成功后再写入数据库，前台是一个一个文件的调用
            commonFilesManager.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    @RequestMapping("delFile")
    public void delFile(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "id");
        String mainId = RequestUtil.getString(request, "mainId");
        String fileUrl = RequestUtil.getString(request, "fileUrl");
        String tableName = RequestUtil.getString(request, "tableName");
        commonFilesManager.deleteOneFile(fileId, fileName, mainId, fileUrl, tableName);
    }

    @RequestMapping(value = "fileList", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject getFileList(HttpServletRequest request, @RequestBody String postData,
        HttpServletResponse response) throws Exception {
        JSONObject postDataJson = new JSONObject();
        JSONObject resultJson = new JSONObject();
        if (StringUtils.isNotBlank(postData)) {
            postDataJson = JSONObject.parseObject(postData);
        } else {
            return ResultUtil.result(false, "请求参数不能为空", null);

        }
        resultJson = commonFilesManager.getFileList(postDataJson);
        return resultJson;
    }

    @RequestMapping(value = "fileListByParam", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject getFileListByParam(HttpServletRequest request, @RequestBody String postData,
        HttpServletResponse response) throws Exception {
        JSONObject postDataJson = new JSONObject();
        JSONObject resultJson = new JSONObject();
        if (StringUtils.isNotBlank(postData)) {
            postDataJson = JSONObject.parseObject(postData);
        } else {
            return ResultUtil.result(false, "请求参数不能为空", null);
        }
        resultJson = commonFilesManager.getFileListByParam(postDataJson);
        return resultJson;
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
            String formId = RequestUtil.getString(request, "formId");
            String fileUrl = RequestUtil.getString(request, "fileUrl");
            String tableName = RequestUtil.getString(request, "tableName");
            String fileBasePath = SysPropertiesUtil.getGlobalProperty(fileUrl);
            if (StringUtils.isBlank(fileBasePath)) {
                logger.error("操作失败，找不到存储根路径");
                return null;
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String relativeFilePath = "";
            if (StringUtils.isNotBlank(formId)) {
                relativeFilePath = File.separator + formId;
            }
            String realFileName = fileId + "." + suffix;
            String fullFilePath = fileBasePath + (StringUtils.isBlank(tableName) ? "" : (File.separator + tableName))
                + relativeFilePath + File.separator + realFileName;
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
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers,
                org.springframework.http.HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception in fileDownload", e);
            return null;
        }
    }

    @RequestMapping("bigFileUploadWindow")
    public ModelAndView h5UploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmCommon/bigFileUploadTable.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("uploadUrl", RequestUtil.getString(request, "uploadUrl", ""));
        return mv;
    }

    /**
     * 样例，各自上传controller可以参照这个去调用文件上传，并自行添加文件信息保存数据库
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "bigUploadH5")
    @ResponseBody
    public Map<String, Object> xcmgDocMgrUploadH5(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件，成功后再写入数据库，前台是一个一个文件的调用
            JSONObject fileUploadResult = commonBigFileUploadManager.saveCommonUploadFilesH5(request,
                "E:/devManagement/common", Arrays.asList("otherParam"));
            if (StringUtils.isNotBlank(fileUploadResult.getString("fileId"))) {
                // 信息保存到mysql数据库
                System.out.println(
                    "文件id:" + fileUploadResult.getString("fileId") + "，文件名称:" + fileUploadResult.getString("fileName"));
            }
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }
}
