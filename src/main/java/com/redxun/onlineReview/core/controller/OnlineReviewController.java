package com.redxun.onlineReview.core.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.onlineReview.core.dao.OnlineReviewDao;
import com.redxun.onlineReview.core.service.OnlineReviewService;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/onlineReview/core/")
public class OnlineReviewController extends GenericController {
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private OnlineReviewService onlineReviewService;
    @Autowired
    private OnlineReviewDao onlineReviewDao;
    @Resource
    private CommonInfoManager commonInfoManager;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;

    @RequestMapping("baseListPage")
    public ModelAndView msgManagement(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "onlineReview/core/onlineReviewBaseList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    @RequestMapping("baseEditPage")
    public ModelAndView baseEditPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "onlineReview/core/onlineReviewBaseEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String id = RequestUtil.getString(request, "id");
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
        mv.addObject("id", id).addObject("action", action).addObject("status", status);
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "CKCPXSPS", null);
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
        // 返回当前登录人角色信息
        params.put("userId", ContextUtil.getCurrentUserId());
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        JSONArray userRoleKeys = new JSONArray();
        for (int index = 0; index < userRolesJsonArray.size(); index++) {
            JSONObject oneRole = (JSONObject) userRolesJsonArray.get(index);
            userRoleKeys.add(oneRole.getString("KEY_"));
        }
        mv.addObject("currentUserRoles", userRoleKeys);
        return mv;
    }

    @RequestMapping("modelEditPage")
    public ModelAndView modelEditPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "onlineReview/core/onlineReviewDetailEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String id = RequestUtil.getString(request, "id");
        String belongId = RequestUtil.getString(request, "belongId");
        String action = RequestUtil.getString(request, "action");
        String status = RequestUtil.getString(request, "status");
        String stageName = RequestUtil.getString(request, "stageName");
        mv.addObject("id", id).addObject("action", action)
                .addObject("status", status).addObject("stageName", stageName)
                .addObject("belongId", belongId);
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
        // 返回当前登录人角色信息
        params.put("userId", ContextUtil.getCurrentUserId());
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        JSONArray userRoleKeys = new JSONArray();
        for (int index = 0; index < userRolesJsonArray.size(); index++) {
            JSONObject oneRole = (JSONObject) userRolesJsonArray.get(index);
            userRoleKeys.add(oneRole.getString("KEY_"));
        }
        mv.addObject("currentUserRoles", userRoleKeys);
        return mv;
    }

    @RequestMapping("queryOnlineReviewBase")
    @ResponseBody
    public JsonPageResult<?> queryOnlineReviewBase(HttpServletRequest request, HttpServletResponse response) {
        return onlineReviewService.queryOnlineReviewBase(request, true);
    }

    @RequestMapping("getTimeDetailList")
    @ResponseBody
    public List<JSONObject> queryTime(HttpServletRequest request, HttpServletResponse response) {
        String belongId = RequestUtil.getString(request, "belongId");
        return onlineReviewService.queryTime(belongId);
    }

    @RequestMapping("getConfigDetailList")
    @ResponseBody
    public List<JSONObject> queryConfig(HttpServletRequest request, HttpServletResponse response) {
        String belongId = RequestUtil.getString(request, "belongId");
        return onlineReviewService.queryConfig(belongId);
    }

    @RequestMapping("getModelDetailList")
    @ResponseBody
    public List<JSONObject> queryModel(HttpServletRequest request, HttpServletResponse response) {
        String belongId = RequestUtil.getString(request, "belongId");
        return onlineReviewService.queryModel(belongId);
    }

    @RequestMapping("deleteOnlineReview")
    @ResponseBody
    public JsonResult deleteOnlineReview(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String id = RequestUtil.getString(request, "id");
            return onlineReviewService.deleteOnlineReview(id);
        } catch (Exception e) {
            logger.error("Exception in deleteOnlineReview", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("saveOnlineReview")
    @ResponseBody
    public JsonResult saveOnlineReview(HttpServletRequest request, @RequestBody String xcmgProjectStr,
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
                onlineReviewService.createOnlineReview(formDataJson);
            } else {
                onlineReviewService.updateOnlineReview(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save onlineReview");
            result.setSuccess(false);
            result.setMessage("Exception in save onlineReview");
            return result;
        }

        return result;
    }

    @RequestMapping("saveModel")
    @ResponseBody
    public JsonResult saveModel(HttpServletRequest request, @RequestBody String xcmgProjectStr,
                                HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(xcmgProjectStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        String belongId = RequestUtil.getString(request, "belongId");
        try {
            JSONObject formDataJson = JSONObject.parseObject(xcmgProjectStr);
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("id"))) {
                formDataJson.put("belongId", belongId);
                onlineReviewService.createModel(formDataJson);
            } else {
                onlineReviewService.updateModel(formDataJson);
            }
            result.setData(formDataJson.getString("id"));
        } catch (Exception e) {
            logger.error("Exception in save onlineReview");
            result.setSuccess(false);
            result.setMessage("Exception in save onlineReview");
            return result;
        }
        return result;
    }


    @RequestMapping("deleteModel")
    @ResponseBody
    public JsonResult deleteModel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String id = RequestUtil.getString(request, "id");
            return onlineReviewService.deleteModel(id);
        } catch (Exception e) {
            logger.error("Exception in deleteOnlineReview", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("getOnlineReviewBase")
    @ResponseBody
    public JSONObject getOnlineReviewBase(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject onlineReviewObj = new JSONObject();
        String id = RequestUtil.getString(request, "id");
        if (StringUtils.isNotBlank(id)) {
            onlineReviewObj = onlineReviewService.getOnlineReviewBase(id);
        }
        return onlineReviewObj;
    }

    @RequestMapping("getModelDetail")
    @ResponseBody
    public JSONObject getModelDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject onlineReviewObj = new JSONObject();
        String id = RequestUtil.getString(request, "id");
        if (StringUtils.isNotBlank(id)) {
            onlineReviewObj = onlineReviewService.queryModelDetailById(id);
        }
        return onlineReviewObj;
    }

    @RequestMapping("getModel")
    @ResponseBody
    public Map<String, Object> getModel(HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {
        Map<String, Object> result = new HashMap<>();
        {
            String materielCode = RequestUtil.getString(request, "materielCode");
            if (StringUtils.isNotBlank(materielCode)) {
                Map<String, Object> params = new HashMap<>();
                params.put("materialCode", materielCode);
                List<Map<String, Object>> modelList = onlineReviewDao.getModel(params);
                result.putAll(modelList.get(0));
            }
        }
        return result;
    }

    // 需求条目附件列表
    @RequestMapping("approveValid")
    @ResponseBody
    public JSONObject approveValid(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject approveValid = new JSONObject();
        approveValid.put("success", true);
        approveValid.put("message", "");
        String id = RequestUtil.getString(request, "id", "");
        String stageName = RequestUtil.getString(request, "stageName", "");
        String action = RequestUtil.getString(request, "action", "");
        if (StringUtils.isBlank(action) && StringUtils.isBlank(stageName)) {
            approveValid.put("success", false);
            approveValid.put("message", "节点变量或流程状态不能为空！");
            return approveValid;
        }
        approveValid = onlineReviewService.textValid(approveValid, id, stageName, action);
        return approveValid;
    }

    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "onlineReview/core/onlineReviewFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("id", RequestUtil.getString(request, "id", ""));
        mv.addObject("fileType", RequestUtil.getString(request, "fileType", ""));
        return mv;
    }

    @RequestMapping(value = "upload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            onlineReviewService.saveOnlineReviewUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }


    @RequestMapping("getOnlineReviewFileList")
    @ResponseBody
    public List<JSONObject> getOnlineReviewFileList(HttpServletRequest request, HttpServletResponse response) {
        List<String> idList = Arrays.asList(RequestUtil.getString(request, "id", ""));
        String fileType = RequestUtil.getString(request, "fileType");
        return onlineReviewService.getOnlineReviewFileList(idList, fileType);
    }

    @RequestMapping("deleteOnlineReviewFile")
    public void deleteOnlineReviewFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String id = postBodyObj.getString("formId");
        onlineReviewService.deleteOneOnlineReviewFile(fileId, fileName, id);
    }

    @RequestMapping("onlineReviewPdfPreview")
    public ResponseEntity<byte[]> onlineReviewPdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = SysPropertiesUtil.getGlobalProperty("onlineReviewFilePathBase");
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, fileBasePath);
    }

    @RequestMapping("onlineReviewOfficePreview")
    @ResponseBody
    public void onlineReviewOfficePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = SysPropertiesUtil.getGlobalProperty("onlineReviewFilePathBase");
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("onlineReviewImagePreview")
    @ResponseBody
    public void onlineReviewImagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = SysPropertiesUtil.getGlobalProperty("onlineReviewFilePathBase");
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, fileBasePath, response);
    }
}
