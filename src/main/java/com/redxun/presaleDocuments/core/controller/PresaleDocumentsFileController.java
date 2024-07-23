package com.redxun.presaleDocuments.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.StringUtil;
import com.redxun.presaleDocuments.core.service.PresaleDocumentsFileService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.sys.core.manager.SysDicManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/presaleDocuments/core/file")
public class PresaleDocumentsFileController {
    private static final Logger logger = LoggerFactory.getLogger(PresaleDocumentsFileController.class);
    @Autowired
    private PresaleDocumentsFileService presaleDocumentsFileService;
    @Autowired
    private SysDicManager sysDicManager;

    //..
    @RequestMapping("pdfPreview")
    public ResponseEntity<byte[]> pdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String businessId = RequestUtil.getString(request, "businessId");
        String businessType = RequestUtil.getString(request, "businessType");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("PresaleDocumentUploadPosition", "主数据文件").getValue();
        return presaleDocumentsFileService.pdfPreviewOrDownLoad(fileName, fileId, businessId, filePathBase);
    }

    //..
    @RequestMapping("applypdfPreview")
    public ResponseEntity<byte[]> applypdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String businessId = RequestUtil.getString(request, "businessId");
        String businessType = RequestUtil.getString(request, "businessType");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("PresaleDocumentUploadPosition", "申请流程文件").getValue();
        return presaleDocumentsFileService.pdfPreviewOrDownLoad(fileName, fileId, businessId, filePathBase);
    }

    //..
    @RequestMapping("officePreview")
    @ResponseBody
    public void officePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String businessId = RequestUtil.getString(request, "businessId");
        String businessType = RequestUtil.getString(request, "businessType");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("PresaleDocumentUploadPosition", "主数据文件").getValue();
        presaleDocumentsFileService.officeFilePreview(fileName, fileId, businessId, filePathBase, response);
    }

    //..
    @RequestMapping("applyofficePreview")
    @ResponseBody
    public void applyofficePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String businessId = RequestUtil.getString(request, "businessId");
        String businessType = RequestUtil.getString(request, "businessType");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("PresaleDocumentUploadPosition", "申请流程文件").getValue();
        presaleDocumentsFileService.officeFilePreview(fileName, fileId, businessId, filePathBase, response);
    }

    //..
    @RequestMapping("imagePreview")
    @ResponseBody
    public void imagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String businessId = RequestUtil.getString(request, "businessId");
        String businessType = RequestUtil.getString(request, "businessType");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("PresaleDocumentUploadPosition", "主数据文件").getValue();
        presaleDocumentsFileService.imageFilePreview(fileName, fileId, businessId, filePathBase, response);
    }

    //..
    @RequestMapping("applyimagePreview")
    @ResponseBody
    public void applyimagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String businessId = RequestUtil.getString(request, "businessId");
        String businessType = RequestUtil.getString(request, "businessType");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("PresaleDocumentUploadPosition", "申请流程文件").getValue();
        presaleDocumentsFileService.imageFilePreview(fileName, fileId, businessId, filePathBase, response);
    }

    //
    @RequestMapping("openNewProductTrialProduceFileWindow")
    public ModelAndView openNewProductTrialProduceFileWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "newProductTrialProduce/core/newProductTrialProduceFileWindow.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String businessId = RequestUtil.getString(request, "businessId");
        String businessType = RequestUtil.getString(request, "businessType");
        String action = RequestUtil.getString(request, "action");
        String coverContent = RequestUtil.getString(request, "coverContent");
        String isSingle = RequestUtil.getString(request, "isSingle");
        mv.addObject("businessId", businessId).addObject("businessType", businessType).
                addObject("action", action).addObject("coverContent", coverContent).addObject("isSingle", isSingle);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        return mv;
    }

    //..
    @RequestMapping("openFileUploadWindow")
    public ModelAndView openFileUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "presaleDocuments/core/presaleDocumentsFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("businessId", RequestUtil.getString(request, "businessId", ""));
        mv.addObject("businessType", RequestUtil.getString(request, "businessType", ""));
        mv.addObject("urlCut", RequestUtil.getString(request, "urlCut", ""));
        mv.addObject("fileType", RequestUtil.getString(request, "fileType", ""));
        return mv;
    }

    //..
    @RequestMapping("fileUpload")
    @ResponseBody
    public JsonResult fileUpload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("PresaleDocumentUploadPosition", "主数据文件").getValue();
            return presaleDocumentsFileService.saveFiles(request, filePathBase);
        } catch (Exception e) {
            logger.error("Exception in fileUpload", e);
            return new JsonResult(false, e.getCause().toString());
        }
    }

    //..
    @RequestMapping("applyfileUpload")
    @ResponseBody
    public JsonResult applyfileUpload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("PresaleDocumentUploadPosition", "申请流程文件").getValue();
            return presaleDocumentsFileService.saveFiles(request, filePathBase);
        } catch (Exception e) {
            logger.error("Exception in fileUpload", e);
            return new JsonResult(false, e.getCause().toString());
        }
    }

    //..
    @RequestMapping("deleteFile")
    public void deleteFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String businessId = postBodyObj.getString("businessId");
        String businessType = postBodyObj.getString("businessType");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("PresaleDocumentUploadPosition", "主数据文件").getValue();
        try {
            presaleDocumentsFileService.deleteFile(fileId, fileName, businessId, businessType, filePathBase);
        } catch (Exception e) {
            logger.error("Exception in deleteFile", e);
        }
    }

    //..
    @RequestMapping("applydeleteFile")
    public void applydeleteFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String businessId = postBodyObj.getString("businessId");
        String businessType = postBodyObj.getString("businessType");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("PresaleDocumentUploadPosition", "申请流程文件").getValue();
        try {
            presaleDocumentsFileService.deleteFile(fileId, fileName, businessId, businessType, filePathBase);
        } catch (Exception e) {
            logger.error("Exception in applydeleteFile", e);
        }
    }

    //..
    @RequestMapping("getFileListInfos")
    @ResponseBody
    public List<JSONObject> getFileListInfos(HttpServletRequest request, HttpServletResponse response) {
        List<String> businessIdList = Arrays.asList(RequestUtil.getString(request, "businessId", ""));
        List<String> businessTypeList = new ArrayList<>();//有时只给第一个参数，第二个就要屏蔽掉，否则会有干扰
        if (StringUtil.isNotEmpty(RequestUtil.getString(request, "businessType", ""))) {
            businessTypeList = Arrays.asList(RequestUtil.getString(request, "businessType", ""));
        }
        JSONObject params = new JSONObject();
        params.put("businessIds", businessIdList);
        params.put("businessTypes", businessTypeList);
        return presaleDocumentsFileService.getFileListInfos(params);
    }
}
