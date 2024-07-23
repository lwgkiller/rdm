package com.redxun.rdMaterial.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.StringUtil;
import com.redxun.rdMaterial.core.service.RdMaterialFileService;
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
@RequestMapping("/rdMaterialCommon/core/file")
public class RdMaterialFileController {
    private static final Logger logger = LoggerFactory.getLogger(RdMaterialFileController.class);
    @Autowired
    private RdMaterialFileService rdMaterialFileService;
    @Autowired
    private SysDicManager sysDicManager;

    //..
    @RequestMapping("pdfPreview")
    public ResponseEntity<byte[]> pdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String businessId = RequestUtil.getString(request, "businessId");
        String businessType = RequestUtil.getString(request, "businessType");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("rdMaterialUploadPosition", businessType).getValue();
        return rdMaterialFileService.pdfPreviewOrDownLoad(fileName, fileId, businessId, filePathBase);
    }

    //..
    @RequestMapping("officePreview")
    @ResponseBody
    public void officePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String businessId = RequestUtil.getString(request, "businessId");
        String businessType = RequestUtil.getString(request, "businessType");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("rdMaterialUploadPosition", businessType).getValue();
        rdMaterialFileService.officeFilePreview(fileName, fileId, businessId, filePathBase, response);
    }

    //..
    @RequestMapping("imagePreview")
    @ResponseBody
    public void imagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String businessId = RequestUtil.getString(request, "businessId");
        String businessType = RequestUtil.getString(request, "businessType");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("rdMaterialUploadPosition", businessType).getValue();
        rdMaterialFileService.imageFilePreview(fileName, fileId, businessId, filePathBase, response);
    }

    //..打开文件列表窗口
    @RequestMapping("openFileWindow")
    public ModelAndView openFileWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdMaterial/core/rdMaterialFileWindow.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String businessId = RequestUtil.getString(request, "businessId");
        String businessType = RequestUtil.getString(request, "businessType");
        String action = RequestUtil.getString(request, "action");
        String coverContent = RequestUtil.getString(request, "coverContent");
        mv.addObject("businessId", businessId).addObject("businessType", businessType).
                addObject("action", action).addObject("coverContent", coverContent);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        return mv;
    }

    //..打开新品试制模块文件上传窗口
    @RequestMapping("openFileUploadWindow")
    public ModelAndView openFileUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdMaterial/core/rdMaterialFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("businessId", RequestUtil.getString(request, "businessId", ""));
        mv.addObject("businessType", RequestUtil.getString(request, "businessType", ""));
        mv.addObject("urlCut", RequestUtil.getString(request, "urlCut", ""));
        mv.addObject("fileType", RequestUtil.getString(request, "fileType", ""));
        return mv;
    }

    //..文件上传命令
    @RequestMapping("fileUpload")
    @ResponseBody
    public JsonResult fileUpload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            return rdMaterialFileService.saveFiles(request);
        } catch (Exception e) {
            logger.error("Exception in saveFiles", e);
            return new JsonResult(false, e.getCause().toString());
        }
    }

    //..新品试制文件删除命令
    @RequestMapping("deleteFile")
    public void deleteFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String businessId = postBodyObj.getString("businessId");
        String businessType = postBodyObj.getString("businessType");
        try {
            rdMaterialFileService.deleteFile(fileId, fileName, businessId, businessType);
        } catch (Exception e) {
            logger.error("Exception in deleteFile", e);
        }
    }

    //..获取文件信息
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
        return rdMaterialFileService.getFileListInfos(params);
    }

    //..检查文件是否存在
    @RequestMapping("checkFile")
    @ResponseBody
    public JsonResult checkFile(HttpServletRequest request, HttpServletResponse response) {
        String businessId = RequestUtil.getString(request, "businessId");
        String businessType = RequestUtil.getString(request, "businessType");
        String businessDes = RequestUtil.getString(request, "businessDes");
        return rdMaterialFileService.checkFile(businessId, businessType, businessDes);
    }
}
