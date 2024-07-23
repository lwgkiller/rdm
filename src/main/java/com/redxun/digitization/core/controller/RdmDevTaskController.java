package com.redxun.digitization.core.controller;

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
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.digitization.core.service.RdmDevTaskService;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
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
 * @author liangchuanjiang
 * @since 2021/2/23 10:43
 */
@RestController
@RequestMapping("/devTask/core/")
public class RdmDevTaskController {
    private Logger logger = LogManager.getLogger(RdmDevTaskController.class);
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private RdmDevTaskService rdmDevTaskService;
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Resource
    private BpmInstManager bpmInstManager;

    @RequestMapping("devListPage")
    public ModelAndView devListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "digitization/core/RdmDevTaskList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    @RequestMapping("getDevList")
    @ResponseBody
    public JsonPageResult<?> getDevList(HttpServletRequest request, HttpServletResponse response) {
        return rdmDevTaskService.getDevList(request, true);
    }

    @RequestMapping("devEditPage")
    public ModelAndView szhEditPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "digitization/core/RdmDevTaskEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String applyId = RequestUtil.getString(request, "applyId", "");
        String action = RequestUtil.getString(request, "action", "");
        String status = RequestUtil.getString(request, "status");
        String nodeId = RequestUtil.getString(request, "nodeId");
        if (StringUtils.isBlank(action)) {
            // 新增或编辑的时候没有nodeId
            if (StringUtils.isBlank(nodeId) || nodeId.contains("PROCESS")) {
                action = "edit";
            } else {
                // 处理任务的时候有nodeId
                action = "task";
            }
        }
        mv.addObject("action", action).addObject("status", status);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "RDMGNKFSQ", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        mv.addObject("applyId", applyId);
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("getDevInfo")
    @ResponseBody
    public JSONObject getDevInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject devInfo = new JSONObject();
        String applyId = RequestUtil.getString(request, "applyId");
        if (StringUtils.isNotBlank(applyId)) {
            devInfo = rdmDevTaskService.getDevInfoById(applyId);
        }
        return devInfo;
    }

    @RequestMapping("saveDevInfo")
    @ResponseBody
    public JsonResult saveDevInfo(HttpServletRequest request, @RequestBody JSONObject formDataJson,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "成功");
        try {
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("applyId"))) {
                rdmDevTaskService.createDevInfo(formDataJson);
            } else {
                rdmDevTaskService.updateDevInfo(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in saveDevInfo");
            result.setSuccess(false);
            result.setMessage("Exception in saveDevInfo");
            return result;
        }
        result.setData(formDataJson.getString("id"));
        return result;
    }

    @RequestMapping("getDevFileList")
    @ResponseBody
    public List<JSONObject> getDevFileList(HttpServletRequest request, HttpServletResponse response) {
        return rdmDevTaskService.getDevFileList(RequestUtil.getString(request, "applyId", ""));
    }

    @RequestMapping("deleteDevTask")
    @ResponseBody
    public JsonResult deleteDevTask(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
            return rdmDevTaskService.deleteDevTask(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteDevTask", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("fileUploadWindow")
    public ModelAndView fileUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "digitization/core/RdmDevTaskFileUpload.jsp";
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
            rdmDevTaskService.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    @RequestMapping("delDevTaskFile")
    public void delDevTaskFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String applyId = postBodyObj.getString("formId");
        rdmDevTaskService.delDevTaskFile(fileId, fileName, applyId);
    }

    @RequestMapping("devPdfPreview")
    public ResponseEntity<byte[]> devPdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("rdmDevTaskFilePathBase");
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, fileBasePath);
    }

    @RequestMapping("devOfficePreview")
    @ResponseBody
    public void devOfficePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("rdmDevTaskFilePathBase");
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("devImagePreview")
    @ResponseBody
    public void devImagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("rdmDevTaskFilePathBase");
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, fileBasePath, response);
    }
}
