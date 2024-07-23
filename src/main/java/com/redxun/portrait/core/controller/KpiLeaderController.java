package com.redxun.portrait.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.portrait.core.manager.KpiLeaderManager;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import com.redxun.xcmgbudget.core.dao.BudgetMonthUserDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
@RequestMapping("/kpiLeader/core/")
public class KpiLeaderController {
    private static final Logger logger = LoggerFactory.getLogger(KpiLeaderController.class);

    @Resource
    private KpiLeaderManager kpiLeaderManager;
    @Resource
    private CommonBpmManager commonBpmManager;
    @Autowired
    private BudgetMonthUserDao budgetMonthUserDao;

    /**
     * 所长绩效年度KPI列表页面
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("kpiLeaderYearListPage")
    public ModelAndView kpiLeaderYearListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "portrait/kpiLeader/kpiLeaderYearListPage.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUserNo", currentUser.getUserNo());
        mv.addObject("currentUserId", currentUser.getUserId());
        return mv;
    }

    /**
     * 被考核人员基本信息管理
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/users")
    public ModelAndView kpiLeaderUsers(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "portrait/kpiLeader/kpiLeaderMonthUsers.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    /**
     * 被考核人员基本信息维护接口（增删改）
     * @param request
     * @param changeGridDataStr
     * @param response
     * @return
     */
    @RequestMapping(value = "saveUsersList", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveUsersList(HttpServletRequest request, @RequestBody String changeGridDataStr,
                                       HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(changeGridDataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("请求体为空");
            return result;
        }
        kpiLeaderManager.saveUsersList(result, changeGridDataStr);
        return result;
    }

    /**
     * 被考核人员基本信息列表查询
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("getUsersList")
    @ResponseBody
    public JsonPageResult<?> getUsersList(HttpServletRequest request, HttpServletResponse response){
        return kpiLeaderManager.getUsersList(request,response);
    }

    /**
     * 新增、编辑、办理、明细、变更的处理器。 其中新增、编辑、办理是由流程引擎跳转过来的（根据URL表单跳转，并替换url中的参数），明细和变更是直接跳转
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("edit")
    public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "portrait/kpiLeader/kpiLeaderYearEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String action = RequestUtil.getString(request, "action");
        String scene = RequestUtil.getString(request, "scene");
        String selectYear = RequestUtil.getString(request, "selectYear");
        mv.addObject("action", action);
        mv.addObject("scene", scene);
        mv.addObject("selectYear", selectYear);
        mv.addObject("kpiId", RequestUtil.getString(request, "id"));
        return mv;
    }

    /**
     * 项目列表数据
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("getKpiYearList")
    @ResponseBody
    public JsonPageResult<?> getKpiYearList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JsonPageResult result = kpiLeaderManager.getKpiYearList(request, response);
        return result;
    }

    /**
     * 根据projectId查询项目的详细信息
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("getKpiYearJson")
    @ResponseBody
    public JSONObject getKpiYearJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject kpiYear = new JSONObject();
        String kpiId = RequestUtil.getString(request, "kpiId");
        if (StringUtils.isNotBlank(kpiId)) {
            kpiYear = kpiLeaderManager.getKpiYearJson(kpiId);
        }
        return kpiYear;
    }


    /**
     * 保存或者更新年度KPI
     *
     * @param request
     * @param kpiYearStr
     * @param response
     * @return
     */
    @RequestMapping(value = "saveKpi", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveKpiYear(HttpServletRequest request, @RequestBody String kpiYearStr,
                                  HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(kpiYearStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("表单内容为空！");
            return result;
        }
        try {
            JSONObject formDataJson = JSONObject.parseObject(kpiYearStr);
            kpiLeaderManager.addOrUpdateKpiYear(formDataJson, result);
        } catch (Exception e) {
            logger.error("Exception in saveKpiYear", e);
            result.setSuccess(false);
            result.setMessage("系统异常！");
            return result;
        }
        return result;
    }

    /**
     * 删除KPI及关联的所有信息
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("delKpi")
    @ResponseBody
    public JsonResult delKpi(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String idStr = RequestUtil.getString(request, "ids");
            if (StringUtils.isNotEmpty(idStr)) {
                List<String> ids = Arrays.asList(idStr.split(","));
                return kpiLeaderManager.deleteKpiYearList(ids);
            }
        } catch (Exception e) {
            logger.error("Exception in delKpi", e);
            return new JsonResult(false, "系统异常");
        }
        return new JsonResult(true, "成功删除!");
    }

    /**
     * 月度KPI列表页面
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("kpiLeaderMonthListPage")
    public ModelAndView kpiLeaderMonthListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "portrait/kpiLeader/kpiLeaderMonthListPage.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        List<JSONObject> roleKeys = budgetMonthUserDao.queryRelInstRoles(ContextUtil.getCurrentUserId());
        // 所长绩效管理人员
        Boolean szjxManager = false;
        if (roleKeys != null && !roleKeys.isEmpty()) {
            for (JSONObject temp : roleKeys) {
                String roleKey = temp.getString("roleKey");
                if ("SZYDJXSH".equalsIgnoreCase(roleKey)) {
                    szjxManager = true;
                }

            }
        }
        mv.addObject("szjxManager", szjxManager);
        mv.addObject("currentUserNo", currentUser.getUserNo());
        mv.addObject("currentUserId", currentUser.getUserId());
        mv.addObject("yearMonth", DateFormatUtil.format(DateUtil.addMonth(new Date(), -1), "yyyy-MM"));
        return mv;
    }

    /**
     * 月度KPI列表页面
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("kpiLeaderMonthReportListPage")
    public ModelAndView kpiLeaderMonthReportListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "portrait/kpiLeader/kpiLeaderMonthListReport.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUserNo", currentUser.getUserNo());
        mv.addObject("currentUserId", currentUser.getUserId());
        mv.addObject("yearMonth", DateFormatUtil.format(DateUtil.addMonth(new Date(), -1), "yyyy-MM"));
        return mv;
    }

    /**
     * 新增、编辑、办理、明细、变更的处理器。 其中新增、编辑、办理是由流程引擎跳转过来的（根据URL表单跳转，并替换url中的参数），明细和变更是直接跳转
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("editMonthPage")
    public ModelAndView editMonthPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "portrait/kpiLeader/kpiLeaderMonthEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
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
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "SZYDJX", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }

    @RequestMapping("editMonthPageReport")
    public ModelAndView editMonthPageReport(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "portrait/kpiLeader/kpiLeaderMonthEditReport.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
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
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "SZYDJX", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }

    @RequestMapping("/getJson")
    @ResponseBody
    public JSONObject getKpiLeaderJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject kpiLeaderList = new JSONObject();
        String id = RequestUtil.getString(request, "id");
        if (StringUtils.isNotBlank(id)) {
            kpiLeaderList = kpiLeaderManager.getDetailJson(id);
        }
        return kpiLeaderList;
    }

    @RequestMapping("/getJsonReport")
    @ResponseBody
    public JSONObject getKpiLeaderJsonReport(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject kpiLeaderList = new JSONObject();
        String id = RequestUtil.getString(request, "id");
        if (StringUtils.isNotBlank(id)) {
            kpiLeaderList = kpiLeaderManager.getDetailJsonReport(id);
        }
        return kpiLeaderList;
    }

    //流程创建入口
    @RequestMapping("createAllProcess")
    @ResponseBody
    public JsonResult createAllProcess(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return kpiLeaderManager.createAllProcess(request);
    }

    //附加分保存
    @RequestMapping("saveFjScore")
    @ResponseBody
    public JsonResult saveFjScore(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return kpiLeaderManager.saveFjScore(request);
    }

    //流程列表查询接口
    @RequestMapping("queryList")
    @ResponseBody
    public JsonPageResult<?> queryList(HttpServletRequest request, HttpServletResponse response) {
        return kpiLeaderManager.kpiLeaderFlowList(request,response);
    }

    //流程报表查询接口
    @RequestMapping("queryListReport")
    @ResponseBody
    public JsonPageResult<?> queryListReport(HttpServletRequest request, HttpServletResponse response) {
        return kpiLeaderManager.kpiLeaderFlowReportList(request,response);
    }

    @RequestMapping("delKpiLeaderMonthFlow")
    @ResponseBody
    public JsonResult delKpiLeaderMonthFlow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String instIdStr = RequestUtil.getString(request, "instIds");
            if (StringUtils.isNotEmpty(uIdStr) && StringUtils.isNotEmpty(instIdStr)) {
                String[] ids = uIdStr.split(",");
                String[] instIds = instIdStr.split(",");
                return kpiLeaderManager.deleteKpiLeaderMonthFlow(ids,instIds);
            }
        } catch (Exception e) {
            logger.error("Exception in delKpiLeaderMonthFlow", e);
            return new JsonResult(false, "系统异常！");
        }
        return new JsonResult(true, "成功删除!");
    }

    //保存表单（更新）
    @RequestMapping(value = "saveDetailList", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveDetailList(HttpServletRequest request, @RequestBody String changeGridDataStr,
                                       HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(changeGridDataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("请求体为空");
            return result;
        }
        kpiLeaderManager.saveDetailList(result,changeGridDataStr);

        return result;
    }

    //导出月度绩效报表
    @RequestMapping("exportKpiLeaderReport")
    @ResponseBody
    public void exportPlan(HttpServletRequest request, HttpServletResponse response) {
        kpiLeaderManager.exportKpiLeaderReport(request, response);
    }

}
