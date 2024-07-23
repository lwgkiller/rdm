package com.redxun.serviceEngineering.core.controller;

import java.io.File;
import java.util.ArrayList;
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
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.OfficeDocPreview;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.serviceEngineering.core.service.JxcshcService;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Controller
@RequestMapping("/serviceEngineering/core/jxcshc")
public class JxcshcController {
    private static final Logger logger = LoggerFactory.getLogger(JxcshcController.class);
    @Autowired
    private JxcshcService jxcshcService;
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Resource
    private BpmInstManager bpmInstManager;

    // ..
    @RequestMapping("jxcshcListPage")
    public ModelAndView dataListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/jxcshcList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("mainGroupName", ContextUtil.getCurrentUser().getMainGroupName());
        return mv;
    }

    // ..
    @RequestMapping("jxcshcListQuery")
    @ResponseBody
    public JsonPageResult<?> jxcshcListQuery(HttpServletRequest request, HttpServletResponse response) {
        return jxcshcService.jxcshcListQuery(request, response, true);
    }

    // ..
    @RequestMapping("deleteJxcshc")
    @ResponseBody
    public JsonResult deleteJxcshc(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String instIdStr = RequestUtil.getString(request, "instIds");
            if (StringUtils.isNotBlank(instIdStr)) {
                String[] instIds = instIdStr.split(",");
                for (int index = 0; index < instIds.length; index++) {
                    bpmInstManager.deleteCascade(instIds[index], "");
                }
            }
            String[] ids = uIdStr.split(",");
            return jxcshcService.deleteJxcshc(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteData", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("jxcshcEditPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/jxcshcEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String jxcshcId = RequestUtil.getString(request, "jxcshcId");
        String nodeId = RequestUtil.getString(request, "nodeId");
        String action = RequestUtil.getString(request, "action");
        String taskStatus = RequestUtil.getString(request, "taskStatus");
        if (StringUtils.isBlank(action)) {
            // 新增或编辑的时候没有nodeId
            if (StringUtils.isBlank(nodeId) || nodeId.contains("PROCESS")) {
                action = "edit";
            } else {
                // 处理任务的时候有nodeId
                action = "task";
            }
        }
        mv.addObject("jxcshcId", jxcshcId).addObject("action", action).addObject("taskStatus", taskStatus);
        String isTest = "no";
        String mainGroupName = ContextUtil.getCurrentUser().getMainGroupName();
        if (mainGroupName.equals("测试研究所")) {
            isTest = "yes";
        }
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "JXCSHC", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        mv.addObject("isTest", isTest);
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("getJxcshcDetail")
    @ResponseBody
    public JSONObject getJxcshcDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject obj = new JSONObject();
        String jxcshcId = RequestUtil.getString(request, "jxcshcId");
        if (StringUtils.isNotBlank(jxcshcId)) {
            obj = jxcshcService.getJxcshcDetail(jxcshcId);
        }
        return obj;
    }

    @RequestMapping("saveJxcshc")
    @ResponseBody
    public JsonResult saveJxcshc(HttpServletRequest request, @RequestBody String formData,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(formData)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        try {
            JSONObject formDataJson = JSONObject.parseObject(formData);
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("id"))) {
                jxcshcService.createJxcshc(formDataJson);
            } else {
                jxcshcService.updateJxcshc(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save jxcshc");
            result.setSuccess(false);
            result.setMessage("Exception in save jxcshc");
            return result;
        }
        return result;
    }

    @RequestMapping("openJxcshcUploadWindow")
    public ModelAndView fileUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/jxcshcFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("masterId", RequestUtil.getString(request, "masterId"));
        return mv;
    }

    @RequestMapping(value = "jxcshcUpload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            jxcshcService.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    @RequestMapping("queryFileList")
    @ResponseBody
    public List<JSONObject> fileList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jxcshcId = RequestUtil.getString(request, "jxcshcId");
        if (StringUtils.isBlank(jxcshcId)) {
            logger.error("jxcshcId is blank");
            return null;
        }
        Map<String, Object> params = new HashMap<>();
        List<String> jxcshcIds = new ArrayList<>();
        jxcshcIds.add(jxcshcId);
        params.put("masterIds", jxcshcIds);
        List<JSONObject> fileInfos = jxcshcService.queryFileList(params);
        return fileInfos;
    }

    // 删除某个文件（从文件表和磁盘上都删除）
    @RequestMapping("delUploadFile")
    public void delete(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String id = postBodyObj.getString("id");
        String masterId = postBodyObj.getString("formId");
        jxcshcService.delUploadFile(id, fileName, masterId);
    }

    // 文档的下载（预览pdf也调用这里）
    @RequestMapping("jxcshcDownload")
    public ResponseEntity<byte[]> fileDownload(HttpServletRequest request, HttpServletResponse response) {
        try {
            String fileName = RequestUtil.getString(request, "fileName");
            if (StringUtils.isBlank(fileName)) {
                logger.error("操作失败，文件名为空！");
                return null;
            }
            String fileId = RequestUtil.getString(request, "fileId");
            if (StringUtils.isBlank(fileId)) {
                logger.error("操作失败，文件Id为空！");
                return null;
            }
            String masterId = RequestUtil.getString(request, "formId");
            if (StringUtils.isBlank(masterId)) {
                logger.error("操作失败，主表Id为空！");
                return null;
            }
            // 预览还是下载
            // String action = RequestUtil.getString(request, "action");
            // if (StringUtils.isBlank(action)) {
            // logger.error("操作类型为空");
            // return null;
            // }
            String fileBasePath = WebAppUtil.getProperty("jxbzzbFilePathBase");
            if (StringUtils.isBlank(fileBasePath)) {
                logger.error("操作失败，找不到存储路径");
                return null;
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fullFilePath = fileBasePath + File.separator + masterId + File.separator + fileId + "." + suffix;
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

    @RequestMapping("jxcshcOfficePreview")
    @ResponseBody
    public void officePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        if (StringUtils.isBlank(fileName)) {
            logger.error("操作失败，文件名为空！");
            return;
        }
        String fileId = RequestUtil.getString(request, "fileId");
        if (StringUtils.isBlank(fileId)) {
            logger.error("操作失败，文件Id为空！");
            return;
        }
        String masterId = RequestUtil.getString(request, "formId");
        if (StringUtils.isBlank(masterId)) {
            logger.error("操作失败，错件整改Id为空！");
            return;
        }

        String fileBasePath = WebAppUtil.getProperty("jxbzzbFilePathBase");
        if (StringUtils.isBlank(fileBasePath)) {
            logger.error("操作失败，找不到存储路径");
            return;
        }
        String suffix = CommonFuns.toGetFileSuffix(fileName);
        String originalFilePath = fileBasePath + File.separator + masterId + File.separator + fileId + "." + suffix;
        String convertPdfDir = WebAppUtil.getProperty("convertPdfDir");
        String convertPdfPath = fileBasePath + File.separator + masterId + File.separator + convertPdfDir
            + File.separator + fileId + ".pdf";;
        OfficeDocPreview.previewOfficeDoc(originalFilePath, convertPdfPath, response);
    }

    @RequestMapping("jxcshcImagePreview")
    @ResponseBody
    public void imagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        if (StringUtils.isBlank(fileName)) {
            logger.error("操作失败，文件名为空！");
            return;
        }
        String fileId = RequestUtil.getString(request, "fileId");
        if (StringUtils.isBlank(fileId)) {
            logger.error("操作失败，文件Id为空！");
            return;
        }
        String masterId = RequestUtil.getString(request, "formId");
        if (StringUtils.isBlank(masterId)) {
            logger.error("操作失败，组件Id为空！");
            return;
        }

        String fileBasePath = WebAppUtil.getProperty("jxbzzbFilePathBase");
        if (StringUtils.isBlank(fileBasePath)) {
            logger.error("操作失败，找不到存储路径");
            return;
        }
        String suffix = CommonFuns.toGetFileSuffix(fileName);
        String originalFilePath = fileBasePath + File.separator + masterId + File.separator + fileId + "." + suffix;
        OfficeDocPreview.imagePreview(originalFilePath, response);
    }
}
