package com.redxun.standardManager.core.controller;

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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.org.api.model.IUser;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.standardManager.core.manager.StandardDemandManager;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;

@Controller
@RequestMapping("/standardManager/core/standardDemand/")
public class StandardDemandController {
    private Logger logger = LoggerFactory.getLogger(StandardDemandController.class);
    @Autowired
    private StandardDemandManager standardDemandManager;
    @Autowired
    private CommonBpmManager commonBpmManager;

    @RequestMapping("jsDemandListPage")
    public ModelAndView listPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "standardManager/core/jsStandardDemandList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        return mv;
    }

    @RequestMapping("jsDemandList")
    @ResponseBody
    public JsonPageResult<?> demandList(HttpServletRequest request, HttpServletResponse response) {
        return standardDemandManager.standardApplyList(request, response);
    }

    @RequestMapping("jsDemandDelete")
    @ResponseBody
    public JsonResult demandDelete(HttpServletRequest request, HttpServletResponse response) {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String instIdStr = RequestUtil.getString(request, "instIds");
            if (StringUtils.isNotEmpty(uIdStr) && StringUtils.isNotEmpty(instIdStr)) {
                String[] ids = uIdStr.split(",");
                String[] instIds = instIdStr.split(",");
                return standardDemandManager.deleteApply(ids, instIds);
            }
        } catch (Exception e) {
            logger.error("Exception in demandDelete", e);
            return new JsonResult(false, e.getMessage());
        }
        return new JsonResult(true, "成功删除!");
    }

    @RequestMapping("jsDemandEdit")
    public ModelAndView jsDemandEdit(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "standardManager/core/jsStandardDemandEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String formId = RequestUtil.getString(request, "formId");
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
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId,"JSBZXQFK",null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }

        mv.addObject("action", action);
        JSONObject formObj = new JSONObject();
        if (StringUtils.isNotBlank(formId)) {
            formObj = standardDemandManager.queryDemandDetail(formId);
            formObj.put("problemStandardNumber",formObj.getString("standardNumber")+"("+formObj.getString("standardName")+")");
        } else {
            IUser user = ContextUtil.getCurrentUser();
            formObj.put("applyUserId", user.getUserId());
            formObj.put("applyUserName", user.getFullname());
            formObj.put("applyUserPhone", user.getMobile());
            formObj.put("applyUserDeptName", user.getMainGroupName());
            formObj.put("feedbackType", "need");
        }
        mv.addObject("standardDemandObj", formObj);
        mv.addObject("status", status);

        return mv;
    }

}
