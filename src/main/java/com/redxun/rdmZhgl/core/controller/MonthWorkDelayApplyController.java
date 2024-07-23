
package com.redxun.rdmZhgl.core.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.org.api.model.IUser;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmZhgl.core.dao.MonthWorkDelayApplyDao;
import com.redxun.rdmZhgl.core.service.MonthWorkDelayApplyService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectChangeDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * 月度工作延期审批流程
 * 
 * @author zz
 */
@Controller
@RequestMapping("/rdmZhgl/core/delay/")
public class MonthWorkDelayApplyController {
    @Resource
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private MonthWorkDelayApplyDao monthWorkDelayApplyDao;
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Resource
    MonthWorkDelayApplyService monthWorkDelayApplyService;
    @Autowired
    XcmgProjectChangeDao xcmgProjectChangeDao;

    /**
     * 审批列表页面
     */
    @RequestMapping("getListPage")
    public ModelAndView getListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/monthWorkDelayApplyList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        return mv;
    }

    /**
     * 获取审批页面
     */
    @RequestMapping("getEditPage")
    public ModelAndView getEditPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/monthWorkDelayApplyEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String taskId_ = RequestUtil.getString(request, "taskId_");
        String nodeId = RequestUtil.getString(request, "nodeId");
        String id = RequestUtil.getString(request, "id");
        String planIds = RequestUtil.getString(request, "planIds");
        String unPlanIds = RequestUtil.getString(request, "unPlanIds");
        String unPlanTaskIds = RequestUtil.getString(request, "unPlanTaskIds");
        mv.addObject("taskId_", taskId_);
        // 新增或编辑的时候没有nodeId
        String action = "";
        if (StringUtils.isBlank(nodeId) || nodeId.contains("PROCESS")) {
            action = "edit";
        } else {
            action = "task";
        }
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "YDJHYQSP", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        mv.addObject("action", action);
        Map<String, Object> planApplyObj = null;
        JSONObject applyObj = new JSONObject();
        if (StringUtils.isNotBlank(id)) {
            planApplyObj = monthWorkDelayApplyDao.getObjectById(id);
            applyObj = XcmgProjectUtil.convertMap2JsonObject(planApplyObj);
        } else {
            applyObj.put("deptName", ContextUtil.getCurrentUser().getMainGroupName());
            applyObj.put("deptId", ContextUtil.getCurrentUser().getMainGroupId());
            applyObj.put("taskId_", taskId_);
            applyObj.put("planIds", planIds);
            applyObj.put("unPlanIds", unPlanIds);
            applyObj.put("unPlanTaskIds", unPlanTaskIds);
        }
        mv.addObject("applyObj", applyObj);
        return mv;
    }

    /**
     * 获取list
     */
    @RequestMapping("queryList")
    @ResponseBody
    public JsonPageResult<?> queryList(HttpServletRequest request, HttpServletResponse response) {
        return monthWorkDelayApplyService.queryList(request, response);
    }

    /**
     * 项目作废表单和流程删除
     */
    @RequestMapping("delete")
    @ResponseBody
    public JsonResult delete(HttpServletRequest request, HttpServletResponse response) {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String instIdStr = RequestUtil.getString(request, "instIds");
            if (StringUtils.isNotEmpty(uIdStr) && StringUtils.isNotEmpty(instIdStr)) {
                String[] ids = uIdStr.split(",");
                String[] instIds = instIdStr.split(",");
                return monthWorkDelayApplyService.delete(ids, instIds);
            }
        } catch (Exception e) {
            return new JsonResult(false, e.getMessage());
        }
        return new JsonResult(true, "成功删除!");
    }

    /**
     * 更加solid获取key
     */
    @RequestMapping(value = "getBpmSolution", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject deskHomeProjectType(HttpServletRequest request, @RequestBody String postData,
        HttpServletResponse response) throws Exception {
        Map<String, Object> param = new HashMap<>(16);
        if (StringUtils.isNotBlank(postData)) {
            JSONObject jsonObject = JSONObject.parseObject(postData);
            param.put("solId", jsonObject.getString("solId"));
        }
        return monthWorkDelayApplyService.getBpmSolution(param);
    }

    /**
     * 获取流程作废信息
     */
    @RequestMapping(value = "abolishInfo", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject getAbolishInfo(HttpServletRequest request, @RequestBody String postData,
        HttpServletResponse response) throws Exception {
        JSONObject postDataJson = new JSONObject();
        if (StringUtils.isNotBlank(postData)) {
            postDataJson = JSONObject.parseObject(postData);
        }
        return monthWorkDelayApplyService.getAbolishInfo(postDataJson, response, request);
    }

    /**
     * 明细：查看审批信息
     */
    @RequestMapping("edit")
    public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = RequestUtil.getString(request, "id");
        String action = RequestUtil.getString(request, "action");
        String status = RequestUtil.getString(request, "status");
        String jspPath = "rdmZhgl/core/monthWorkDelayApplyEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("id", id).addObject("action", action).addObject("status", status);
        Map<String, Object> abolishApplyObj = null;
        JSONObject applyObj = new JSONObject();
        if (StringUtils.isNotBlank(id)) {
            abolishApplyObj = monthWorkDelayApplyDao.getObjectById(id);
            applyObj = XcmgProjectUtil.convertMap2JsonObject(abolishApplyObj);
        }
        mv.addObject("applyObj", applyObj);
        return mv;
    }

    @RequestMapping(value = "isApply", method = RequestMethod.POST)
    @ResponseBody
    public List<JSONObject> judyeApply(HttpServletRequest request, @RequestBody String postData,
        HttpServletResponse response) throws Exception {
        JSONObject postDataJson = new JSONObject();
        if (StringUtils.isNotBlank(postData)) {
            postDataJson = JSONObject.parseObject(postData);
        }
        return monthWorkDelayApplyService.getApplyListByParam(postDataJson);
    }

    @RequestMapping(value = "isRunning", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject isRunning(HttpServletRequest request, @RequestBody String postData, HttpServletResponse response)
        throws Exception {
        JSONObject postDataJson = new JSONObject();
        if (StringUtils.isNotBlank(postData)) {
            postDataJson = JSONObject.parseObject(postData);
        }
        return monthWorkDelayApplyService.getActTaskById(postDataJson);
    }
}
