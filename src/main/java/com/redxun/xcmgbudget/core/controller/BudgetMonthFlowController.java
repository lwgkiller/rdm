package com.redxun.xcmgbudget.core.controller;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import com.redxun.xcmgbudget.core.dao.BudgetMonthFlowDao;
import com.redxun.xcmgbudget.core.dao.BudgetMonthUserDao;
import com.redxun.xcmgbudget.core.manager.BudgetMonthFlowManager;

@Controller
@RequestMapping("/xcmgBudget/core/budgetMonthFlow/")
public class BudgetMonthFlowController {
    private static final Logger logger = LoggerFactory.getLogger(BudgetMonthFlowController.class);

    @Autowired
    private BudgetMonthFlowDao budgetMonthFlowDao;
    @Autowired
    private BudgetMonthUserDao budgetMonthUserDao;
    @Autowired
    private BudgetMonthFlowManager budgetMonthFlowManager;

    @RequestMapping("listPage")
    public ModelAndView getListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "xcmgBudget/core/budgetMonthFlowList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String currentMonth = XcmgProjectUtil.getNowLocalDateStr("yyyy-MM");
        mv.addObject("currentMonth", currentMonth);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        JSONObject userRole = budgetMonthFlowManager.queryUserRoles(ContextUtil.getCurrentUserId());
        boolean isYsglry = budgetMonthFlowManager.judgeIsYsglry(ContextUtil.getCurrentUserId());
        mv.addObject("isYsglry", isYsglry);
        mv.addObject("currentUserRoles", userRole);
        mv.addObject("lastMonth", DateFormatUtil.format(DateUtil.addMonth(new Date(), 1), "yyyy-MM"));
        return mv;
    }

    @RequestMapping("tabPage")
    public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "xcmgBudget/core/budgetMonthFlowTabPage.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String budgetId = RequestUtil.getString(request, "id", "");
        String nodeId = RequestUtil.getString(request, "nodeId", "");
        String action = RequestUtil.getString(request, "action", "");
        String status = RequestUtil.getString(request, "status", "");
        // 新增、编辑、办理的时候前端没有传递action
        if (StringUtils.isBlank(action)) {
            // 新增或编辑的时候没有nodeId
            if (StringUtils.isBlank(nodeId) || nodeId.contains("PROCESS")) {
                action = "edit";
            } else {
                // 流程启动后（办理）才有nodeId
                action = "task";
            }
        }
        // 取出节点变量，返回到页面
        JSONObject varJSONObj = new JSONObject();
        if (StringUtils.isNotBlank(nodeId) && !nodeId.contains("PROCESS")) {
            Map<String, Object> params = new HashMap<>();
            params.put("SCOPE_", nodeId);
            params.put("SOL_KEY_", "YDYSSB");
            params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            List<Map<String, String>> nodeVars = budgetMonthUserDao.queryNodeVarsByNodeId(params);
            if (nodeVars == null || nodeVars.isEmpty()) {
                varJSONObj.put("vars", "null");
            }
            if (nodeVars != null && !nodeVars.isEmpty()) {
                for (Map<String, String> oneVar : nodeVars) {
                    varJSONObj.put(oneVar.get("KEY_"), oneVar.get("DEF_VAL_"));
                }
            }
        }
        // 定义标签页大类（挖掘机械研究院仅对应研发费用一类）
        List<JSONObject> bigTypeArray = new ArrayList<>();
        JSONObject oneBigType = new JSONObject();
        oneBigType.put("bigTypeName", "研发费用");
        oneBigType.put("bigTypeId", "003");
        oneBigType.put("type", "cg");
        bigTypeArray.add(oneBigType);

        JSONObject applyObject = null;
        if (StringUtils.isNotBlank(budgetId)) {
            applyObject = budgetMonthFlowManager.queryBudgetMonthFlowById(budgetId);
        }

        if (applyObject == null) {
            applyObject = new JSONObject();
            String nextMonth = DateFormatUtil.format(DateUtil.addMonth(new Date(), 1), "yyyy-MM");
            applyObject.put("yearMonth", nextMonth);
            applyObject.put("deptId", ContextUtil.getCurrentUser().getMainGroupId());
            applyObject.put("deptName", ContextUtil.getCurrentUser().getMainGroupName());
            applyObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            applyObject.put("userName", ContextUtil.getCurrentUser().getFullname());
        }

        JSONObject userRole = budgetMonthFlowManager.queryUserRoles(ContextUtil.getCurrentUserId());
        boolean sysManager = false;
        if (userRole.containsKey("ysManager") && userRole.getString("ysManager").equals("yes")) {
            sysManager = true;
        }
        mv.addObject("isysManager", sysManager);
        mv.addObject("bigTypeArray", bigTypeArray);
        mv.addObject("applyObject", applyObject);
        mv.addObject("nodeVars", varJSONObj);
        mv.addObject("action", action);
        mv.addObject("status", status);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    @RequestMapping("cgTabPage")
    public ModelAndView cgTabPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "xcmgBudget/core/budgetMonthFlowCgTab.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String yearMonth = RequestUtil.getString(request, "yearMonth", "");
        String deptId = RequestUtil.getString(request, "deptId", "");
        String deptName = RequestUtil.getString(request, "deptName", "");
        String bigTypeId = RequestUtil.getString(request, "bigTypeId", "");
        String bigTypeName = RequestUtil.getString(request, "bigTypeName", "");
        String budgetType = RequestUtil.getString(request, "budgetType", "");
        String budgetId = RequestUtil.getString(request, "budgetId", "");
        String action = RequestUtil.getString(request, "action", "");
        String projectId = RequestUtil.getString(request, "projectId", "");
        String epm_url = SysPropertiesUtil.getGlobalProperty("epm_url");

        boolean editBudget = Boolean.parseBoolean(RequestUtil.getString(request, "editBudget", "false"));

        mv.addObject("yearMonth", yearMonth);
        mv.addObject("deptId", deptId);
        mv.addObject("deptName", deptName);
        mv.addObject("bigTypeId", bigTypeId);
        mv.addObject("bigTypeName", bigTypeName);
        mv.addObject("editBudget", editBudget);
        mv.addObject("budgetType", budgetType);
        mv.addObject("budgetId", budgetId);
        mv.addObject("action", action);
        mv.addObject("projectId", projectId);
        // 返回当前登录人角色信息
        boolean isYsglry = budgetMonthFlowManager.judgeIsYsglry(ContextUtil.getCurrentUserId());
        mv.addObject("isYsglry", isYsglry);
        mv.addObject("currentUserDeptId", ContextUtil.getCurrentUser().getMainGroupId());
        mv.addObject("currentUserDeptName", ContextUtil.getCurrentUser().getMainGroupName());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("epm_url", epm_url);
        return mv;
    }

    @RequestMapping("sumPage")
    public ModelAndView sumPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "xcmgBudget/core/budgetMonthFlowSum.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String currentMonth = XcmgProjectUtil.getNowLocalDateStr("yyyy-MM");
        String epm_url = SysPropertiesUtil.getGlobalProperty("epm_url");
        mv.addObject("currentMonth", currentMonth);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        JSONObject userRole = budgetMonthFlowManager.queryUserRoles(ContextUtil.getCurrentUserId());
        mv.addObject("currentUserRoles", userRole);
        mv.addObject("yearMonth", DateFormatUtil.format(DateUtil.addMonth(new Date(), 1), "yyyy-MM"));
        mv.addObject("epm_url", epm_url);
        return mv;
    }

    @RequestMapping("deptPage")
    public ModelAndView deptPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "xcmgBudget/core/budgetMonthFlowDeptSum.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String currentMonth = XcmgProjectUtil.getNowLocalDateStr("yyyy-MM");
        mv.addObject("currentMonth", currentMonth);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        JSONObject userRole = budgetMonthFlowManager.queryUserRoles(ContextUtil.getCurrentUserId());
        mv.addObject("currentUserRoles", userRole);
        mv.addObject("yearMonth", DateFormatUtil.format(DateUtil.addMonth(new Date(), 1), "yyyy-MM"));
        return mv;
    }

    @RequestMapping("queryList")
    @ResponseBody
    public JsonPageResult<?> queryList(HttpServletRequest request, HttpServletResponse response) {
        return budgetMonthFlowManager.queryBudgetMonthFlowList(request, response);
    }

    /**
     * 删除预算信息
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("deleteBudgetMonthFlow")
    @ResponseBody
    public JsonResult deleteBudgetMonthFlow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String instIdStr = RequestUtil.getString(request, "instIds");
            if (StringUtils.isNotEmpty(uIdStr) && StringUtils.isNotEmpty(instIdStr)) {
                String[] ids = uIdStr.split(",");
                String[] instIds = instIdStr.split(",");
                return budgetMonthFlowManager.deleteBudgetMonthFlow(ids, instIds);
            }
        } catch (Exception e) {
            logger.error("Exception in deleteBudgetMonthFlow", e);
            return new JsonResult(false, "系统异常！");
        }
        return new JsonResult(true, "成功删除!");
    }

    @RequestMapping("updateMonthProject")
    @ResponseBody
    public JsonResult updateMonthProject(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String projectId = RequestUtil.getString(request, "projectId");
            String subjectId = RequestUtil.getString(request, "budgetId");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("projectId", projectId);
            jsonObject.put("budgetId", subjectId);
            budgetMonthFlowDao.updateProjectId(jsonObject);
            return new JsonResult(true, "执行成功!");

        } catch (Exception e) {
            logger.error("Exception in deleteBudgetMonthFlow", e);
            return new JsonResult(false, "系统异常！");
        }
    }

    @RequestMapping("updateMonthProjectType")
    @ResponseBody
    public JsonResult updateMonthProjectType(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        try {
            String budgetType = RequestUtil.getString(request, "budgetType");
            String budgetId = RequestUtil.getString(request, "budgetId");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("budgetType", budgetType);
            jsonObject.put("budgetId", budgetId);
            budgetMonthFlowDao.updateProjectType(jsonObject);
            jsonObject.put("projectId", "");
            budgetMonthFlowDao.updateProjectId(jsonObject);
            return new JsonResult(true, "执行成功!");

        } catch (Exception e) {
            logger.error("Exception in updateMonthProjectType", e);
            return new JsonResult(false, "系统异常！");
        }
    }

    @RequestMapping("deleteBudgetMonth")
    @ResponseBody
    public JsonResult deleteBudgetMonth(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String budgetId = RequestUtil.getString(request, "budgetId");
            if (budgetId.isEmpty() || budgetId == null) {
                return new JsonResult(true, "执行成功!");
            }
            budgetMonthFlowDao.deleteBudgetMonth(budgetId);
            return new JsonResult(true, "执行成功!");
        } catch (Exception e) {
            logger.error("Exception in deleteBudgetMonth", e);
            return new JsonResult(false, "系统异常！");
        }
    }

    @RequestMapping("deleteBudgetMonthById")
    @ResponseBody
    public JsonResult deleteBudgetMonthById(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String id = RequestUtil.getString(request, "id", "");
            if (id.isEmpty() || id == null || id == "") {
                return new JsonResult(false, "没有可删除数据!");
            }
            budgetMonthFlowDao.deleteBudgetMonthById(id);
            return new JsonResult(true, "删除成功!");
        } catch (Exception e) {
            logger.error("Exception in deleteBudgetMonthById", e);
            return new JsonResult(false, "系统异常！");
        }
    }

    @RequestMapping("judgeBudgetMonthStatus")
    @ResponseBody
    public JsonPageResult<?> judgeBudgetMonthStatus(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        String yearMonth = RequestUtil.getString(request, "yearMonth");
        return budgetMonthFlowManager.judgeBudgetMonthStatus(yearMonth);
    }
}
