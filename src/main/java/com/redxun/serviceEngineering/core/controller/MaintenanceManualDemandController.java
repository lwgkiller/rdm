package com.redxun.serviceEngineering.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.entity.BpmSolution;
import com.redxun.bpm.core.manager.BpmDefManager;
import com.redxun.bpm.core.manager.BpmNodeSetManager;
import com.redxun.bpm.core.manager.BpmSolutionManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.dao.MaintenanceManualDemandDao;
import com.redxun.serviceEngineering.core.service.MaintenanceManualDemandService;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.db.manager.SysSqlCustomQueryUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@RequestMapping("/serviceEngineering/core/maintenanceManualDemand")
public class MaintenanceManualDemandController {
    private static final Logger logger = LoggerFactory.getLogger(MaintenanceManualDemandController.class);
    @Autowired
    MaintenanceManualDemandService maintenanceManualDemandService;
    @Autowired
    MaintenanceManualDemandDao maintenanceManualDemandDao;
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    SysDicManager sysDicManager;
    @Autowired
    RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    BpmNodeSetManager bpmNodeSetManager;
    @Autowired
    BpmSolutionManager bpmSolutionManager;
    @Autowired
    BpmDefManager bpmDefManager;

    //..
    @RequestMapping("dataListPage")
    public ModelAndView dataListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/maintenanceManualDemandList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        String maintenanceManualAdmin = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringGroups", "maintenanceManualAdmin").getValue();
        mv.addObject("maintenanceManualAdmin", maintenanceManualAdmin);
        List<JSONObject> nodeSetListWithName =
                commonBpmManager.getNodeSetListWithName("maintenanceManualDemand", "userTask", "endEvent");
        mv.addObject("nodeSetListWithName", nodeSetListWithName);
        return mv;
    }

    //..
    @RequestMapping("dataListQuery")
    @ResponseBody
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        return maintenanceManualDemandService.dataListQuery(request, response);
    }

    //..
    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/maintenanceManualDemandEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        //新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String businessId = RequestUtil.getString(request, "businessId");
        String nodeId = RequestUtil.getString(request, "nodeId");
        String action = RequestUtil.getString(request, "action");
        String status = RequestUtil.getString(request, "status");
        if (StringUtils.isBlank(action)) {
            // 新增或编辑的时候没有nodeId
            if (StringUtils.isBlank(nodeId) || nodeId.contains("PROCESS")) {
                action = "edit";
            } else {
                //处理任务的时候有nodeId
                action = "task";
            }
        }
        mv.addObject("businessId", businessId).addObject("action", action).addObject("status", status);
        //取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "maintenanceManualDemand", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("applyTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd"));
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentUserMainGroupId", ContextUtil.getCurrentUser().getMainGroupId());
        mv.addObject("currentUserMainGroupName", ContextUtil.getCurrentUser().getMainGroupName());
        String maintenanceManualAdmin = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringGroups", "maintenanceManualAdmin").getValue();
        mv.addObject("maintenanceManualAdmin", maintenanceManualAdmin);
        return mv;
    }

    //..
    @RequestMapping("deleteBusiness")
    @ResponseBody
    public JsonResult deleteBusiness(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String instIdStr = RequestUtil.getString(request, "instIds");
            String[] ids = uIdStr.split(",");
            String[] instIds = instIdStr.split(",");
            return maintenanceManualDemandService.deleteBusiness(ids, instIds);
        } catch (Exception e) {
            logger.error("Exception in deleteCcbg", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    //..
    @RequestMapping("amHandle")
    @ResponseBody
    public JsonResult amHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return maintenanceManualDemandService.amHandle(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteCcbg", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    //..
    @RequestMapping("getDetail")
    @ResponseBody
    public JSONObject getDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject jsonObject = new JSONObject();
        String businessId = RequestUtil.getString(request, "businessId");
        if (StringUtils.isNotBlank(businessId)) {
            jsonObject = maintenanceManualDemandService.getDetail(businessId);
        }
        return jsonObject;
    }

    //..
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
                maintenanceManualDemandService.createBusiness(formDataJson);
            } else {
                maintenanceManualDemandService.updateBusiness(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save");
            result.setSuccess(false);
            result.setMessage("Exception in save");
            return result;
        }
        return result;
    }

    //..
    @RequestMapping("fileUploadWindow")
    public ModelAndView fileUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/maintenanceManualDemandFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("businessId", RequestUtil.getString(request, "businessId", ""));
        return mv;
    }

    //..
    @RequestMapping(value = "fileUpload")
    @ResponseBody
    public Map<String, Object> fileUpload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件，成功后再写入数据库，前台是一个一个文件的调用
            maintenanceManualDemandService.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    //..
    @RequestMapping("getFileList")
    @ResponseBody
    public List<JSONObject> getFileList(HttpServletRequest request, HttpServletResponse response) {
        List<String> businessIdList = Arrays.asList(RequestUtil.getString(request, "businessId", ""));
        return maintenanceManualDemandService.getFileList(businessIdList);
    }

    //..
    @RequestMapping("delFile")
    public void delFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String businessId = postBodyObj.getString("formId");
        maintenanceManualDemandService.deleteOneBusinessFile(fileId, fileName, businessId);
    }

    //..
    @RequestMapping("PdfPreview")
    public ResponseEntity<byte[]> PdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringUploadPosition", "maintenanceManualDemand").getValue();
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, filePathBase);
    }

    //..
    @RequestMapping("OfficePreview")
    @ResponseBody
    public void OfficePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringUploadPosition", "maintenanceManualDemand").getValue();
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, filePathBase, response);
    }

    //..
    @RequestMapping("ImagePreview")
    @ResponseBody
    public void ImagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringUploadPosition", "maintenanceManualDemand").getValue();
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, filePathBase, response);
    }

    //..
    @RequestMapping("PdfPreviewAndAllDownload")
    public ResponseEntity<byte[]> PdfPreviewAndAllDownload(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringUploadPosition", "maintenanceManualDemand").getValue();
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, filePathBase);
    }

    //..
    @RequestMapping("exportList")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        maintenanceManualDemandService.exportList(request, response);
    }

    //..
    @RequestMapping("saveCollect")
    @ResponseBody
    public JsonResult saveCollect(HttpServletRequest request, HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "收藏成功");
        try {
            JSONObject formDataJson = new JSONObject();
            String userId = RequestUtil.getString(request, "userId");
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            formDataJson.put("collectorId", userId);
            List<JSONObject> collects = maintenanceManualDemandDao.queryCollect(formDataJson);
            for (String businessId : ids) {
                boolean link = true;
                for (JSONObject collect : collects) {
                    if (collect.getString("businessId").equals(businessId)) {
                        link = false;
                        break;
                    }
                }
                if (link) {
                    formDataJson.put("businessId", businessId);
                    maintenanceManualDemandService.createCollect(formDataJson);
                }

            }

        } catch (Exception e) {
            logger.error("Exception in save saveCollect");
            result.setSuccess(false);
            result.setMessage("Exception in save saveCollect");
            return result;
        }
        return result;
    }

    //..
    @RequestMapping("deleteCollect")
    @ResponseBody
    public JsonResult deleteCollect(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String userId = RequestUtil.getString(request, "userId");
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return maintenanceManualDemandService.deleteCollect(ids, userId);
        } catch (Exception e) {
            logger.error("Exception in deleteCollect", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    //..
    @RequestMapping("copyBusiness")
    @ResponseBody
    public JsonResult copyBusiness(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String businessId = RequestUtil.getString(request, "businessId");
            return maintenanceManualDemandService.copyBusiness(businessId);
        } catch (Exception e) {
            logger.error("Exception in copyBusiness", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    //..获取匹配明细
    @RequestMapping("getManualMatchList")
    @ResponseBody
    public List<JSONObject> getManualMatchList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String businessId = RequestUtil.getString(request, "businessId", "");
        JSONObject params = new JSONObject();
        params.put("businessId", businessId);
        List<JSONObject> manualMatchList = maintenanceManualDemandService.getManualMatchList(params);
        return manualMatchList;
    }
}
