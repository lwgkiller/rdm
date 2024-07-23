package com.redxun.serviceEngineering.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.identity.service.impl.GroupCalServiceImpl;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.org.api.model.IGroup;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.service.ReplacementRelationshipService;
import com.redxun.sys.org.manager.GroupServiceImpl;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/serviceEngineering/core/replacementRelationship")
public class ReplacementRelationshipController {
    private static final Logger logger = LoggerFactory.getLogger(ReplacementRelationshipController.class);
    @Autowired
    private ReplacementRelationshipService replacementRelationshipService;
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private CommonInfoManager commonInfoManager;
    @Autowired
    private GroupServiceImpl groupService;

    //..
    @RequestMapping("dataListPage")
    public ModelAndView dataListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/replacementRelationshipList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        List<JSONObject> nodeSetListWithName =
                commonBpmManager.getNodeSetListWithName("replacementRelationship", "userTask", "endEvent");
        mv.addObject("nodeSetListWithName", nodeSetListWithName);
        return mv;
    }

    //..
    @RequestMapping("masterdataListPage")
    public ModelAndView masterdataListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/replacementRelationshipMasterdataList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        String whatIsTheRole = "browse";
        List<IGroup> groupsByUserId = groupService.getGroupsByUserId(ContextUtil.getCurrentUserId());
        HashSet<String> groupKeys = new HashSet<>();
        for (IGroup group : groupsByUserId) {
            groupKeys.add(group.getKey());
        }
        if (ContextUtil.getCurrentUserId().equalsIgnoreCase("1") || groupKeys.contains("materialInfoAdmin")) {
            whatIsTheRole = "sa";
        } else if (false) {
            whatIsTheRole = "apply";
        } else {
            whatIsTheRole = "browse";
        }
        mv.addObject("whatIsTheRole", whatIsTheRole);
        return mv;
    }

    //..
    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/replacementRelationshipEdit.jsp";
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
                // 处理任务的时候有nodeId
                action = "task";
            }
        }
        mv.addObject("businessId", businessId).addObject("action", action).addObject("status", status);
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars =
                    commonBpmManager.queryNodeVarsByParam(nodeId, "replacementRelationship", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }

    //..
    @RequestMapping("dataListQuery")
    @ResponseBody
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        return replacementRelationshipService.dataListQuery(request, response);
    }

    //..
    @RequestMapping("masterdataListQuery")
    @ResponseBody
    public JsonPageResult<?> masterdataListQuery(HttpServletRequest request, HttpServletResponse response) {
        return replacementRelationshipService.masterdataListQuery(request, response);
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
            return replacementRelationshipService.deleteBusiness(ids, instIds);
        } catch (Exception e) {
            logger.error("Exception in delete", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    //..
    @RequestMapping("deleteBusinessMasterdata")
    @ResponseBody
    public JsonResult deleteBusinessMasterdata(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return replacementRelationshipService.deleteBusinessMasterdata(ids);
        } catch (Exception e) {
            logger.error("Exception in delete", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    //..
    @RequestMapping("getDetail")
    @ResponseBody
    public JSONObject getDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject jsonObject = new JSONObject();
        String businessId = RequestUtil.getString(request, "businessId");
        jsonObject = replacementRelationshipService.getDetail(businessId);
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
                replacementRelationshipService.createBusiness(formDataJson);
            } else {
                replacementRelationshipService.updateBusiness(formDataJson);
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
    @RequestMapping("exportListMasterdata")
    public void exportListMasterdata(HttpServletRequest request, HttpServletResponse response) {
        replacementRelationshipService.exportListMasterdata(request, response);
    }

    //..
    @RequestMapping("importMasterdataTemplateDownload")
    public ResponseEntity<byte[]> importMasterdataTemplateDownload(HttpServletRequest request, HttpServletResponse response) {
        return replacementRelationshipService.importMasterdataTemplateDownload();
    }

    //..
    @RequestMapping("importItemdataTemplateDownload")
    public ResponseEntity<byte[]> importItemdataTemplateDownload(HttpServletRequest request, HttpServletResponse response) {
        return replacementRelationshipService.importItemdataTemplateDownload();
    }

    //..
    @RequestMapping("importMasterdata")
    @ResponseBody
    public JSONObject importMasterdata(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        replacementRelationshipService.importMasterdata(result, request);
        return result;
    }

    //..
    @RequestMapping("importItemdata")
    @ResponseBody
    public JSONObject importItemdata(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        replacementRelationshipService.importItemdata(result, request);
        return result;
    }
}
