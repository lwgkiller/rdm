package com.redxun.serviceEngineering.core.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.service.MaintenanceManualMctService;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Controller
@RequestMapping("/serviceEngineering/core/maintenanceManualMct")
public class MaintenanceManualMctController {
    private static final Logger logger = LoggerFactory.getLogger(MaintenanceManualMctController.class);
    @Autowired
    MaintenanceManualMctService maintenanceManualMctService;
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    SysDicManager sysDicManager;
    @Autowired
    RdmZhglFileManager rdmZhglFileManager;

    // ..
    @RequestMapping("dataListPage")
    public ModelAndView dataListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/maintenanceManualMctList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        String maintenanceManualAdmin =
                sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringGroups", "maintenanceManualAdmin").getValue();
        mv.addObject("maintenanceManualAdmin", maintenanceManualAdmin);
        List<JSONObject> nodeSetListWithName =
                commonBpmManager.getNodeSetListWithName("maintenanceManualMct", "userTask", "endEvent");
        mv.addObject("nodeSetListWithName", nodeSetListWithName);
        return mv;
    }

    // ..
    @RequestMapping("dataListQuery")
    @ResponseBody
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        return maintenanceManualMctService.dataListQuery(request, response);
    }

    // ..
    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/maintenanceManualMctEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String businessId = RequestUtil.getString(request, "businessId");
        String nodeId = RequestUtil.getString(request, "nodeId");
        String action = RequestUtil.getString(request, "action");
        String status = RequestUtil.getString(request, "status");
        // @lwgkiller阿诺多罗:由需求申请流程自动发起Mct流程时会有这个需求id传进来 todo:mark！！！
        String demandId = RequestUtil.getString(request, "demandId");
        if (StringUtils.isBlank(action)) {
            // 新增或编辑的时候没有nodeId
            if (StringUtils.isBlank(nodeId) || nodeId.contains("PROCESS")) {
                action = "edit";
            } else {
                // 处理任务的时候有nodeId
                action = "task";
            }
        }
        mv.addObject("businessId", businessId).addObject("action", action).addObject("status", status)
                .addObject("demandId", demandId);
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "maintenanceManualMct", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        mv.addObject("applyTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd"));
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentUserMainGroupId", ContextUtil.getCurrentUser().getMainGroupId());
        mv.addObject("currentUserMainGroupName", ContextUtil.getCurrentUser().getMainGroupName());
        return mv;
    }

    // ..
    @RequestMapping("deleteBusiness")
    @ResponseBody
    public JsonResult deleteBusiness(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String instIdStr = RequestUtil.getString(request, "instIds");
            String[] ids = uIdStr.split(",");
            String[] instIds = instIdStr.split(",");
            return maintenanceManualMctService.deleteBusiness(ids, instIds);
        } catch (Exception e) {
            logger.error("Exception in delete", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    // ..
    @RequestMapping("getDetail")
    @ResponseBody
    public JSONObject getDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject jsonObject = new JSONObject();
        String businessId = RequestUtil.getString(request, "businessId");
        if (StringUtils.isNotBlank(businessId)) {
            jsonObject = maintenanceManualMctService.getDetail(businessId);
        }
        return jsonObject;
    }

    // ..
    @RequestMapping("saveBusiness")
    @ResponseBody
    public JsonResult saveBusiness(HttpServletRequest request, @RequestBody String formDataStr,
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
                maintenanceManualMctService.createBusiness(formDataJson);
            } else {
                maintenanceManualMctService.updateBusiness(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save");
            result.setSuccess(false);
            result.setMessage("Exception in save");
            return result;
        }
        return result;
    }

    // ..
    @RequestMapping("fileUploadWindow")
    public ModelAndView fileUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/maintenanceManualMctFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("businessId", RequestUtil.getString(request, "businessId", ""));
        return mv;
    }

    // ..
    @RequestMapping(value = "fileUpload")
    @ResponseBody
    public Map<String, Object> fileUpload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件，成功后再写入数据库，前台是一个一个文件的调用
            maintenanceManualMctService.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    // ..
    @RequestMapping("getFileList")
    @ResponseBody
    public List<JSONObject> getFileList(HttpServletRequest request, HttpServletResponse response) {
        List<String> businessIdList = Arrays.asList(RequestUtil.getString(request, "businessId", ""));
        return maintenanceManualMctService.getFileList(businessIdList);
    }

    // ..
    @RequestMapping("delFile")
    public void delFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String businessId = postBodyObj.getString("formId");
        maintenanceManualMctService.deleteOneBusinessFile(fileId, fileName, businessId);
    }

    // ..
    @RequestMapping("PdfPreview")
    public ResponseEntity<byte[]> PdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String filePathBase = sysDicManager
                .getBySysTreeKeyAndDicKey("serviceEngineeringUploadPosition", "maintenanceManualMct").getValue();
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, filePathBase);
    }

    // ..
    @RequestMapping("OfficePreview")
    @ResponseBody
    public void OfficePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String filePathBase = sysDicManager
                .getBySysTreeKeyAndDicKey("serviceEngineeringUploadPosition", "maintenanceManualMct").getValue();
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, filePathBase, response);
    }

    // ..
    @RequestMapping("ImagePreview")
    @ResponseBody
    public void ImagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String filePathBase = sysDicManager
                .getBySysTreeKeyAndDicKey("serviceEngineeringUploadPosition", "maintenanceManualMct").getValue();
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, filePathBase, response);
    }

    // ..
    @RequestMapping("templateDownload")
    public ResponseEntity<byte[]> templateDownload(HttpServletRequest request, HttpServletResponse response) {
        String type = RequestUtil.getString(request, "type");
        return maintenanceManualMctService.templateDownload(type);
    }

    // ..
    @RequestMapping("PdfPreviewAndAllDownload")
    public ResponseEntity<byte[]> PdfPreviewAndAllDownload(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String filePathBase = sysDicManager
                .getBySysTreeKeyAndDicKey("serviceEngineeringUploadPosition", "maintenanceManualMct").getValue();
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, filePathBase);
    }

    // ..
    @RequestMapping("exportList")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        maintenanceManualMctService.exportList(request, response);
    }
}
