package com.redxun.rdmZhgl.core.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmZhgl.core.service.CcbgService;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * 应用模块名称
 * <p>
 * 代码描述
 * <p>
 * Copyright: Copyright (C) 2021 XXX, Inc. All rights reserved.
 * <p>
 * Company: 徐工挖掘机械有限公司
 * <p>
 *
 * @author liwenguang
 * @since 2021/2/23 10:43
 */
@RestController
@RequestMapping("/zhgl/core/ccbg/")
public class CcbgController {
    private Logger logger = LogManager.getLogger(CcbgController.class);
    @Resource
    private CcbgService ccbgService;
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;

    // ..
    @RequestMapping("listPage")
    public ModelAndView listPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/ccbgList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }

    // ..
    @RequestMapping("getCcbgList")
    @ResponseBody
    public JsonPageResult<?> getCcgbList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return ccbgService.getCcgbList(request, response, true);
    }

    // ..
    @RequestMapping("deleteCcbg")
    @ResponseBody
    public JsonResult deleteCcbg(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String instIdStr = RequestUtil.getString(request, "instIds");
            String[] ids = uIdStr.split(",");
            String[] instIds = instIdStr.split(",");
            return ccbgService.deleteCcbg(ids, instIds);
        } catch (Exception e) {
            logger.error("Exception in deleteCcbg", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    // ..
    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/ccbgEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String ccbgId = RequestUtil.getString(request, "ccbgId");
        String nodeId = RequestUtil.getString(request, "nodeId");
        String action = RequestUtil.getString(request, "action");
        String status = RequestUtil.getString(request, "status");
        if (StringUtils.isBlank(action)) {
            // 新增或编辑的时候没有nodeId
            if (StringUtils.isBlank(nodeId) || nodeId.contains("PROCESS")) {
                action = "edit";
            } else {
                // 处理任务的时候有nodeId
                action = "task";
            }
        }
        mv.addObject("ccbgId", ccbgId).addObject("action", action).addObject("status", status);
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "CCBG", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentUserMainGroupId", ContextUtil.getCurrentUser().getMainGroupId());
        mv.addObject("currentUserMainGroupName", ContextUtil.getCurrentUser().getMainGroupName());
        return mv;
    }

    // ..
    @RequestMapping("getCcbgDetail")
    @ResponseBody
    public JSONObject getCcbgDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject ccbgObj = new JSONObject();
        String ccbgId = RequestUtil.getString(request, "ccbgId");
        if (StringUtils.isNotBlank(ccbgId)) {
            ccbgObj = ccbgService.getCcbgDetail(ccbgId);
        }
        return ccbgObj;
    }

    // ..
    @RequestMapping("saveCcbg")
    @ResponseBody
    public JsonResult saveCcbg(HttpServletRequest request, @RequestBody String formDataStr,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(formDataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        try {
            JSONObject formDataJson = JSONObject.parseObject(formDataStr);
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("id"))) {
                ccbgService.createCcbg(formDataJson);
            } else {
                ccbgService.updateCcbg(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save ccbg");
            result.setSuccess(false);
            result.setMessage("Exception in save ccbg");
            return result;
        }
        return result;
    }

    // ..
    @RequestMapping("getCcbgFileList")
    @ResponseBody
    public List<JSONObject> getCcbgFileList(HttpServletRequest request, HttpServletResponse response) {
        List<String> ccbgIdList = Arrays.asList(RequestUtil.getString(request, "ccbgId", ""));
        return ccbgService.getCcbgFileList(ccbgIdList);
    }

    // ..
    @RequestMapping("fileUploadWindow")
    public ModelAndView fileUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/ccbgFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("ccbgId", RequestUtil.getString(request, "ccbgId", ""));
        return mv;
    }

    // ..
    @RequestMapping(value = "fileUpload")
    @ResponseBody
    public Map<String, Object> fileUpload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件，成功后再写入数据库，前台是一个一个文件的调用
            ccbgService.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    // ..
    @RequestMapping("delCcbgFile")
    public void delCcbgFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String ccbgId = postBodyObj.getString("formId");
        ccbgService.deleteOneCcbgFile(fileId, fileName, ccbgId);
    }

    // ..
    @RequestMapping("ccbgPdfPreview")
    public ResponseEntity<byte[]> ccbgPdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("ccbgFilePathBase");
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, fileBasePath);
    }

    // ..
    @RequestMapping("ccbgOfficePreview")
    @ResponseBody
    public void ccbgOfficePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("ccbgFilePathBase");
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    // ..
    @RequestMapping("ccbgImagePreview")
    @ResponseBody
    public void ccbgImagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("ccbgFilePathBase");
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    /*@RequestMapping("userUpdate")
    @ResponseBody
    public JsonResult userUpdate(HttpServletRequest request, HttpServletResponse response) {
        JsonResult result=new JsonResult();
        List<JSONObject> dataList=ccbgDao.userUpdateQuery();
        for(JSONObject oneData:dataList) {
            ccbgDao.userUpdate(oneData);
        }
        result.setMessage("更新完成");
        return result;
    }*/
}
