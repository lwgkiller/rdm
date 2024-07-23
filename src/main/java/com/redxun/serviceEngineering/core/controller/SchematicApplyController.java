package com.redxun.serviceEngineering.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.dao.SchematicApplyDao;
import com.redxun.serviceEngineering.core.service.SchematicApplyService;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@RequestMapping("/serviceEngineer/core/SchematicApply/")
public class SchematicApplyController extends GenericController {
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private SchematicApplyService schematicApplyService;
    @Autowired
    private SchematicApplyDao schematicApplyDao;
    @Resource
    private CommonInfoManager commonInfoManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;


    
    @RequestMapping("schematicApplyListPage")
    public ModelAndView msgManagement(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "serviceEngineering/core/schematicApplyList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/schematicApplyEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String id = RequestUtil.getString(request, "id");
        String nodeId = RequestUtil.getString(request, "nodeId");
        String action = RequestUtil.getString(request, "action");
        String status = RequestUtil.getString(request, "status");
        String oldId = RequestUtil.getString(request, "oldId");
        if(StringUtils.isNotBlank(oldId)){
            JSONObject oldData = new JSONObject();
            JSONObject jsonObjects = schematicApplyService.getSchematicApplyDetail(oldId);
            oldData.put("machineNum",jsonObjects.getString("machineNum"));
            oldData.put("schematicType",jsonObjects.getString("schematicType"));
            oldData.put("modelId",jsonObjects.getString("modelId"));
            oldData.put("modelName",jsonObjects.getString("modelName"));
            oldData.put("saleModel",jsonObjects.getString("saleModel"));
            oldData.put("materialCode",jsonObjects.getString("materialCode"));
            oldData.put("saleArea",jsonObjects.getString("saleArea"));
            oldData.put("needTime",jsonObjects.getString("needTime"));
            mv.addObject("oldData", oldData);
        }
        if (StringUtils.isBlank(action)) {
            // 新增或编辑的时候没有nodeId
            if (StringUtils.isBlank(nodeId) || nodeId.contains("PROCESS")) {
                action = "edit";
            } else {
                // 处理任务的时候有nodeId
                action = "task";
            }
        }
        mv.addObject("id", id).addObject("action", action).addObject("status", status);
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId,"YLTXQSQ",null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        JSONObject currentUserDepInfo = xcmgProjectManager.isCurrentUserDepRespman();
        Map<String, Object> params = new HashMap<>();
        params.put("currentUserMainDepId", currentUserDepInfo.getString("currentUserMainDepId"));
        JSONObject dept = commonInfoManager.queryDeptNameById(currentUserDepInfo.getString("currentUserMainDepId"));
        String deptName = dept.getString("deptname");
        mv.addObject("deptName", deptName);
        mv.addObject("currentUserMainDepId", currentUserDepInfo.getString("currentUserMainDepId"));
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("querySchematicApply")
    @ResponseBody
    public JsonPageResult<?> queryJstb(HttpServletRequest request, HttpServletResponse response) {
        return schematicApplyService.querySchematicApply(request, response);
    }

    @RequestMapping("queryDetail")
    @ResponseBody
    public List<JSONObject> getCnList(HttpServletRequest request, HttpServletResponse response) {
        String belongId = RequestUtil.getString(request, "belongId");
        return schematicApplyService.queryDetail(belongId);
    }

    @RequestMapping("deleteSchematicApply")
    @ResponseBody
    public JsonResult deleteJstb(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String instIdStr = RequestUtil.getString(request, "instIds");
            String[] ids = uIdStr.split(",");
            return schematicApplyService.deleteSchematicApply(ids,instIdStr);
        } catch (Exception e) {
            logger.error("Exception in deleteSchematicApply", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("saveSchematicApply")
    @ResponseBody
    public JsonResult saveJstb(HttpServletRequest request, @RequestBody String xcmgProjectStr,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(xcmgProjectStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        try {
            JSONObject formDataJson = JSONObject.parseObject(xcmgProjectStr);
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("id"))) {
                schematicApplyService.createSchematicApply(formDataJson);
            } else {
                schematicApplyService.updateSchematicApply(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save schematicApply");
            result.setSuccess(false);
            result.setMessage("Exception in save schematicApply");
            return result;
        }
        return result;
    }

    @RequestMapping("getSchematicApplyDetail")
    @ResponseBody
    public JSONObject getSchematicApplyDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject schematicApplyObj = new JSONObject();
        String id = RequestUtil.getString(request, "id");
        if (StringUtils.isNotBlank(id)) {
            schematicApplyObj = schematicApplyService.getSchematicApplyDetail(id);
        }
        return schematicApplyObj;
    }

    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/schematicApplyFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("id", RequestUtil.getString(request, "id", ""));
        return mv;
    }

    @RequestMapping("fileList")
    public ModelAndView fileList(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "serviceEngineering/core/schematicApplyFileList.jsp";
        String id = RequestUtil.getString(request, "id");
        String action = RequestUtil.getString(request, "action");
        String stageName = RequestUtil.getString(request, "stageName");
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("id", id).addObject("action", action).addObject("stageName", stageName);
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        Boolean isFwgc = false;
        if(RdmConst.FWGCS_NAME.equals(ContextUtil.getCurrentUser().getMainGroupName())||"admin".equals(ContextUtil.getCurrentUser().getUserNo())){
            isFwgc = true;
        }
        mv.addObject("isFwgc", isFwgc);
        return mv;
    }

    @RequestMapping(value = "upload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            schematicApplyService.saveSchematicApplyUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }


    @RequestMapping("getSchematicApplyFileList")
    @ResponseBody
    public List<JSONObject> getSchematicApplyFileList(HttpServletRequest request, HttpServletResponse response) {
        List<String> idList = Arrays.asList(RequestUtil.getString(request, "id", ""));
        return schematicApplyService.getSchematicApplyFileList(idList);
    }

    @RequestMapping("deleteSchematicApplyFile")
    public void deleteSchematicApplyFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String id = postBodyObj.getString("formId");
        schematicApplyService.deleteOneSchematicApplyFile(fileId, fileName, id);
    }

    @RequestMapping("schematicApplyPdfPreview")
    public ResponseEntity<byte[]> schematicApplyPdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = SysPropertiesUtil.getGlobalProperty("schematicApplyFilePathBase");
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, fileBasePath);
    }

    @RequestMapping("schematicApplyOfficePreview")
    @ResponseBody
    public void schematicApplyOfficePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = SysPropertiesUtil.getGlobalProperty("schematicApplyFilePathBase");
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("schematicApplyImagePreview")
    @ResponseBody
    public void schematicApplyImagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = SysPropertiesUtil.getGlobalProperty("schematicApplyFilePathBase");
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("approveValid")
    @ResponseBody
    public JSONObject approveValid(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject approveValid = new JSONObject();
        approveValid.put("success", true);
        approveValid.put("message", "");
        String id = RequestUtil.getString(request, "id", "");
        if (StringUtils.isBlank(id)) {
            approveValid.put("success", false);
            approveValid.put("message", "主键ID为空！");
            return approveValid;
        }
        return schematicApplyService.checkUploadMds(id,approveValid);
    }

    @RequestMapping("exportSchematicApplyList")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        schematicApplyService.exportSchematicApplyList(request, response);
    }
}
