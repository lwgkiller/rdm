package com.redxun.productDataManagement.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.StringUtil;
import com.redxun.productDataManagement.core.manager.AttachedtoolsSpectrumItemModelApplyService;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/productDataManagement/core/attachedtoolsSpectrumItemModelApply/")

public class AttachedtoolsSpectrumItemModelApplyController {
    private static final Logger logger = LoggerFactory.getLogger(AttachedtoolsSpectrumItemModelApplyController.class);
    @Autowired
    private AttachedtoolsSpectrumItemModelApplyService attachedtoolsSpectrumItemModelApplyService;
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private BpmInstManager bpmInstManager;

    //..
    @RequestMapping("dataListPage")
    public ModelAndView dataListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "productDataManagement/core/attachedtoolsSpectrumItemModelApplyList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        List<JSONObject> nodeSetListWithName =
                commonBpmManager.getNodeSetListWithName("attachedtoolsSpectrumItemModelApply", "userTask", "endEvent");
        mv.addObject("nodeSetListWithName", nodeSetListWithName);
        return mv;
    }

    //..
    @RequestMapping("dataListQuery")
    @ResponseBody
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        return attachedtoolsSpectrumItemModelApplyService.dataListQuery(request, response);
    }

    //..
    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "productDataManagement/core/attachedtoolsSpectrumItemModelApplyEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
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
                    commonBpmManager.queryNodeVarsByParam(nodeId, "attachedtoolsSpectrumItemModelApply", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
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
            return attachedtoolsSpectrumItemModelApplyService.deleteBusiness(ids, instIds);
        } catch (Exception e) {
            logger.error("Exception in deleteBusiness", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    //..
    @RequestMapping("getDataById")
    @ResponseBody
    public JSONObject getDataById(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = RequestUtil.getString(request, "businessId");
        return attachedtoolsSpectrumItemModelApplyService.getDataById(id);
    }

    //..
    @RequestMapping("saveBusiness")
    @ResponseBody
    public JsonResult saveBusiness(HttpServletRequest request, @RequestBody String formDataStr,
                                   HttpServletResponse response) {
        JSONObject jsonObject = JSONObject.parseObject(formDataStr);
        if (jsonObject == null || jsonObject.isEmpty()) {
            return new JsonResult(false, "表单内容为空，操作失败！");
        }
        try {
            return attachedtoolsSpectrumItemModelApplyService.saveBusiness(jsonObject);
        } catch (Exception e) {
            logger.error("Exception in saveBusiness", e);
            return new JsonResult(false, e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
        }
    }

    //..
    @RequestMapping("checkDesignModelValid")
    @ResponseBody
    public JsonResult checkDesignModelValid(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JsonResult result = new JsonResult(true);
        String id = RequestUtil.getString(request, "id", "");
        String designModel = RequestUtil.getString(request, "designModel", "");
        JSONObject param = new JSONObject();
        param.put("designModel", designModel);
        if (StringUtil.isNotEmpty(id)) {
            param.put("id", id);
        }
        attachedtoolsSpectrumItemModelApplyService.checkDesignModelValid(param, result);
        return result;
    }
}
