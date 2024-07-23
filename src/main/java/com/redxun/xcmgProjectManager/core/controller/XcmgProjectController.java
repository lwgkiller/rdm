
package com.redxun.xcmgProjectManager.core.controller;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.org.api.model.IUser;
import com.redxun.rdmCommon.core.dao.PdmApiDao;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.sys.log.LogEnt;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectScheduler;
import com.redxun.xcmgProjectManager.core.util.ConstantUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import com.redxun.xcmgProjectManager.report.manager.XcmgProjectReportManager;

/**
 * project_baseInfo控制器
 *
 * @author x
 */
@Controller
@RequestMapping("/xcmgProjectManager/core/xcmgProject/")
public class XcmgProjectController {
    private Logger logger = LogManager.getLogger(XcmgProjectController.class);
    @Resource
    private XcmgProjectManager xcmgProjectManager;
    @Resource
    private XcmgProjectReportManager xcmgProjectReportManager;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private XcmgProjectScheduler xcmgProjectScheduler;
    @Resource
    private CommonInfoManager commonInfoManager;
    @Autowired
    private PdmApiDao pdmApiDao;
    @Autowired
    private CommonBpmManager commonBpmManager;

    // ..@lwgkiller:获取所有的项目基本信息
    @RequestMapping("allProjectListQuery")
    @ResponseBody
    public JsonPageResult<?> allProjectListQuery(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        return xcmgProjectManager.allProjectListQuery(request, response, true);
    }

    // 获取编辑页面要用到的一些信息（如下拉，对应规则）
    @RequestMapping("getProjectEditRule")
    @ResponseBody
    public Map<String, Object> getProjectEditRule(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        return xcmgProjectManager.getProjectEditRule();
    }

    // 当前来源、类别、级别条件下的必选交付物
    @RequestMapping("getProjectDeliverys")
    @ResponseBody
    public List<JSONObject> queryProjectDeliverys(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        return xcmgProjectManager.queryProjectDeliverys(request);
    }

    @RequestMapping("queryMemberDeliveryAssign")
    @ResponseBody
    public List<JSONObject> queryMemberDeliveryAssign(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        return xcmgProjectManager.queryMemberDeliveryAssign(request);
    }

    @RequestMapping("checkDeliveryAssign")
    @ResponseBody
    public JsonResult checkDeliveryAssign(HttpServletRequest request, HttpServletResponse response,
                                          @RequestBody String requestBody) throws Exception {
        return xcmgProjectManager.checkDeliveryAssign(requestBody);
    }

    @RequestMapping("checkProductGlUserAssign")
    @ResponseBody
    public JsonResult checkProductGlUserAssign(HttpServletRequest request, HttpServletResponse response,
                                               @RequestBody String requestBody) throws Exception {
        return xcmgProjectManager.checkProductGlUserAssign(requestBody);
    }

    @RequestMapping("list")
    public ModelAndView getListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "xcmgProjectManager/core/xcmgProjectList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUserNo", currentUser.getUserNo());
        Map<String, Object> params = new HashMap<>();
        params.put("userId", currentUser.getUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 返回当前登录人角色信息
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        mv.addObject("pointCategoryName", RequestUtil.getString(request, "pointCategoryName", ""));
        mv.addObject("projectStartTime", RequestUtil.getString(request, "projectStartTime", ""));
        mv.addObject("projectEndTime", RequestUtil.getString(request, "projectEndTime", ""));
        return mv;
    }

    @RequestMapping("getProjectList")
    @ResponseBody
    public JsonPageResult<?> getProjectList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return xcmgProjectReportManager.progressReportList(request, response, true, "getProjectList");
    }

    // 新增或编辑、明细、处理任务
    @RequestMapping("edit")
    public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String projectId = RequestUtil.getString(request, "projectId");
        String nodeId = RequestUtil.getString(request, "nodeId");
        String action = RequestUtil.getString(request, "action");
        String status = RequestUtil.getString(request, "status");
        String detailHideRoleRatio = RequestUtil.getString(request, "detailHideRoleRatio", "false");
        String currentUserProjectRoleName = RequestUtil.getString(request, "currentUserProjectRoleName", "");
        if (StringUtils.isBlank(currentUserProjectRoleName) && StringUtils.isNotBlank(projectId)) {
            JSONObject memJudgeResult = xcmgProjectManager.judgeUserIsMem(ContextUtil.getCurrentUserId(), projectId);
            if (memJudgeResult.getBooleanValue("result")) {
                currentUserProjectRoleName = memJudgeResult.getString("roleName");
            }
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
        ModelAndView mv;
        Boolean exDeptFlag = false;
        String projectCategory = RequestUtil.getString(request, "projectCategory", "");
        if (StringUtils.isNotBlank(projectId)) {
            JSONObject xcmgProject = xcmgProjectManager.getXcmgProject(projectId);
            String currentDeptId = ContextUtil.getCurrentUser().getMainGroupId();
            // 判断不是挖掘机械研究院的，并且主负责部门不是当前部门的
            boolean flag = commonInfoManager.judgeIsTechDept(currentDeptId);
            if (!flag && !currentDeptId.equals(xcmgProject.getString("mainDepId"))) {
                exDeptFlag = true;
            }
            projectCategory = xcmgProject.getString("categoryId");
        }

        if (ConstantUtil.PRODUCT_CATEGORY.equals(projectCategory)) {
            String jspPath = "xcmgProjectManager/core/projectProductEdit.jsp";
            mv = new ModelAndView(jspPath);
        } else {
            String jspPath = "xcmgProjectManager/core/xcmgProjectEdit.jsp";
            mv = new ModelAndView(jspPath);
        }
        mv.addObject("exDeptFlag", exDeptFlag);
        mv.addObject("projectId", projectId).addObject("action", action).addObject("status", status);
        mv.addObject("currentUserProjectRoleName", currentUserProjectRoleName);
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            String solKey = RequestUtil.getString(request, "solKey");
            if (StringUtils.isBlank(solKey)) {
                solKey = "KJXMGL";
            }
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, solKey, null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        IUser currentUser = ContextUtil.getCurrentUser();
        // 返回当前登录人所属或者所负责的所有部门信息
        Map<String, Object> params = new HashMap<>();
        params.put("userId", currentUser.getUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> currentUserDeps = xcmgProjectOtherDao.queryUserDeps(params);
        JSONArray jsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserDeps);
        mv.addObject("currentUserDeps", jsonArray);
        // 返回当前登录人角色信息
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("currentUserMainDeptId", ContextUtil.getCurrentUser().getMainGroupId());
        mv.addObject("currentUser", currentUser);
        mv.addObject("projectCategory", projectCategory);
        mv.addObject("detailHideRoleRatio", detailHideRoleRatio);

        return mv;
    }

    @RequestMapping("del")
    @ResponseBody
    @LogEnt(action = "del", module = "xcmgProjectManager", submodule = "project_baseInfo")
    public JsonResult del(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String instIdStr = RequestUtil.getString(request, "instIds");
            if (StringUtils.isNotEmpty(uIdStr) && StringUtils.isNotEmpty(instIdStr)) {
                String[] ids = uIdStr.split(",");
                String[] instIds = instIdStr.split(",");
                return xcmgProjectManager.deleteProject(ids, instIds);
            }
        } catch (Exception e) {
            return new JsonResult(false, e.getMessage());
        }
        return new JsonResult(true, "成功删除!");
    }

    /**
     * 有子表的情况下编辑明细的json
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @RequestMapping("getJson")
    @ResponseBody
    public JSONObject getJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject xcmgProject = null;
        String uId = RequestUtil.getString(request, "ids");
        if (StringUtils.isNotBlank(uId)) {
            xcmgProject = xcmgProjectManager.getXcmgProject(uId);
            if (StringUtils.isNotBlank(xcmgProject.getString("levelId"))
                    && StringUtils.isBlank(xcmgProject.getString("beginLevelId"))) {
                xcmgProject.put("beginLevelId", xcmgProject.getString("levelId"));
            }

        }
        return xcmgProject;
    }

    @RequestMapping("{userId}/getUserInfoById")
    @ResponseBody
    public Map<String, Object> getUserInfoById(@PathVariable("userId") String userId, HttpServletRequest request,
                                               HttpServletResponse response) throws Exception {
        Map<String, Object> result = new HashMap<>();
        if (StringUtils.isNotBlank(userId)) {
            xcmgProjectManager.getUserInfoById(userId, result);
            Map<String, Object> params = new HashMap<>();
            params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            params.put("userId", userId);
            List<Map<String, Object>> currentUserDeps = xcmgProjectOtherDao.queryUserDeps(params);
            JSONArray jsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserDeps);
            result.put("userDeps", jsonArray);
            // 返回当前登录人角色信息
            List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
            JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
            result.put("userRoles", userRolesJsonArray);
        }
        return result;
    }

    @RequestMapping("getNewPlan")
    @ResponseBody
    public JSONObject getNewPlan(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String categoryId = RequestUtil.getString(request, "categoryId");
        String levelId = RequestUtil.getString(request, "levelId");
        String sourceId = RequestUtil.getString(request, "sourceId");
        if (StringUtils.isBlank(categoryId)) {
            return new JSONObject();
        }
        JSONObject result = xcmgProjectManager.getNewPlan(categoryId, levelId, sourceId);
        return result;
    }

    @RequestMapping("getPassStage")
    @ResponseBody
    public List<Map<String, String>> getPassStage(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String categoryId = RequestUtil.getString(request, "categoryId");
        String stageId = RequestUtil.getString(request, "stageId", "");
        List<Map<String, String>> result = xcmgProjectManager.getPassStage(categoryId, stageId);
        return result;
    }

    @RequestMapping("getDeliveryNamesByCategoryLevelSource")
    @ResponseBody
    public Map<String, String> getStageId2DeliveryNames(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String categoryId = RequestUtil.getString(request, "categoryId");
        String levelId = RequestUtil.getString(request, "levelId");
        String sourceId = RequestUtil.getString(request, "sourceId");
        if (StringUtils.isBlank(categoryId)) {
            return Collections.emptyMap();
        }
        Map<String, String> result = new HashMap<>();
        JSONObject plans = xcmgProjectManager.getNewPlan(categoryId, levelId, sourceId);
        List<JSONObject> projectPlans = new ArrayList<>();
        if (plans.containsKey("newPlan")) {
            projectPlans = plans.getObject("newPlan", List.class);
        }
        for (JSONObject xcmgProjectPlan : projectPlans) {
            result.put(xcmgProjectPlan.getString("stageId"), xcmgProjectPlan.getString("deliveryName"));
        }
        return result;
    }

    // 查询某个特定阶段下的交付物类型
    @RequestMapping(value = "getDeliverys", method = RequestMethod.POST)
    @ResponseBody
    public List<Map<String, Object>> getDeliveryId2Name(HttpServletRequest request, HttpServletResponse response,
                                                        @RequestBody String requestBody) throws Exception {
        Map<String, String> queryParams = new HashMap<>();
        if (StringUtils.isNotBlank(requestBody)) {
            String[] arrs = requestBody.split("&", -1);
            if (arrs != null && arrs.length != 0) {
                for (String oneParam : arrs) {
                    String[] innerArr = oneParam.split("=", -1);
                    queryParams.put(innerArr[0], innerArr[1]);
                }
            }
        }
        // 是否包含公共的交付物（如变更评审材料仅作为前台上传交付物的一个选项，）老功能已废弃 by liangchuanjiang 2020-1-15
        List<Map<String, Object>> result =
                xcmgProjectManager.getDeliveryId2Name(queryParams.getOrDefault("stageId", ""),
                        queryParams.getOrDefault("levelPid", ""), queryParams.getOrDefault("sourceId", ""),
                        queryParams.getOrDefault("queryPublic", ""), queryParams.getOrDefault("projectId", ""));
        return result;
    }

    @RequestMapping(value = "save", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult save(HttpServletRequest request, @RequestBody String xcmgProjectStr,
                           HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(xcmgProjectStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        try {
            JSONObject xcmgProject = JSONObject.parseObject(xcmgProjectStr);
            if (xcmgProject == null || xcmgProject.isEmpty()) {
                logger.warn("xcmgProject is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(xcmgProject.getString("projectId"))) {
                xcmgProjectManager.create(xcmgProject);
            } else {
                xcmgProjectManager.update(xcmgProject);
                // 调整了项目负责人，需要把这个项目，原负责人的待办都更改为当前负责人
                String changedRespManId = RequestUtil.getString(request, "changedRespManId", "");
                String currentRespManId = RequestUtil.getString(request, "currentRespManId", "");
                if (StringUtils.isNotBlank(changedRespManId) && StringUtils.isNotBlank(currentRespManId)
                        && !changedRespManId.equalsIgnoreCase(currentRespManId)) {
                    xcmgProjectManager.changeRespTask(xcmgProject.getString("projectId"), changedRespManId,
                            currentRespManId);
                }
            }
        } catch (Exception e) {
            logger.error("Exception in save project");
            result.setSuccess(false);
            result.setMessage("Exception in save project");
            return result;
        }
        return result;
    }

    /**
     * 查看明细
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    /*    @RequestMapping("get")
    public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String pkId = RequestUtil.getString(request, "pkId");
        JSONObject xcmgProject = new JSONObject();
        if (StringUtils.isNotEmpty(pkId)) {
            xcmgProject = xcmgProjectManager.getXcmgProject(pkId);
        }
        return getPathView(request).addObject("xcmgProject", xcmgProject);
    }*/
    @RequestMapping("checkStageDelivery")
    @ResponseBody
    public JSONObject checkStageDelivery(HttpServletRequest request, HttpServletResponse response) {
        String projectId = RequestUtil.getString(request, "projectId");
        String stageId = RequestUtil.getString(request, "stageId");
        String levelId = RequestUtil.getString(request, "levelId");
        String sourceId = RequestUtil.getString(request, "sourceId");
        String pointDeliveryName = RequestUtil.getString(request, "pointDeliveryName");
        String categoryName = RequestUtil.getString(request, "categoryName");
        JSONObject resultJson = xcmgProjectManager.checkStageDelivery(projectId, stageId, levelId, sourceId,
                pointDeliveryName, categoryName);

        return resultJson;
    }

    // 获取角色与系数的对应范围
    @RequestMapping("getRoleRatioList")
    @ResponseBody
    public List<Map<String, Object>> getRoleRatioList(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        return xcmgProjectOtherDao.getRoleRatioList(params);
    }

    // 获取流程实例上一个审批节点的结果
    @RequestMapping("getPreHandleResult")
    @ResponseBody
    public Map<String, String> getPreHandleResult(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String actInstId = RequestUtil.getString(request, "actInstId");
        if (StringUtils.isBlank(actInstId)) {
            return Collections.emptyMap();
        }
        return xcmgProjectManager.getPreHandleResult(actInstId);
    }

    @RequestMapping("editRuleShow")
    public ModelAndView editRuleShow(HttpServletRequest request, HttpServletResponse response) {
        String scene = RequestUtil.getString(request, "scene");
        String jspPath = "xcmgProjectManager/core/xcmgProjectEditRuleShow.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("scene", scene);
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        JSONArray jsonArray = new JSONArray();
        if (scene.equals("buildScoreRule")) {
            List<Map<String, Object>> gridData = xcmgProjectOtherDao.queryProjectLevelDivide(params);
            jsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(gridData);
        } else if (scene.equals("knotScoreRule")) {
            List<Map<String, Object>> tempData = xcmgProjectOtherDao.queryProjectKnot2Rating(params);
            if (tempData != null && !tempData.isEmpty()) {
                for (Map<String, Object> oneData : tempData) {
                    String knotScoreRange =
                            oneData.get("minScore").toString() + "~" + oneData.get("maxScore").toString();
                    String ratioRange = oneData.get("minRatio").toString() + "~" + oneData.get("maxRatio").toString();
                    oneData.put("knotScoreRange", knotScoreRange);
                    oneData.put("ratioRange", ratioRange);
                }
                jsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(tempData);
            }
        } else if (scene.equals("roleRatioRule")) {
            List<Map<String, String>> gridData = xcmgProjectOtherDao.queryProjectMemRole(params);
            jsonArray = XcmgProjectUtil.convertListMap2JsonArrString(gridData);
        }
        mv.addObject("gridData", jsonArray);
        return mv;
    }

    /**
     * 获取流程变更信息
     */
    @RequestMapping(value = "changeInfo", method = RequestMethod.POST)
    @ResponseBody
    public JSONArray getChangeInfo(HttpServletRequest request, @RequestBody String postData,
                                   HttpServletResponse response) throws Exception {
        JSONObject postDataJson = new JSONObject();
        if (StringUtils.isNotBlank(postData)) {
            postDataJson = JSONObject.parseObject(postData);
        }
        return xcmgProjectManager.getChangeInfo(postDataJson, response, request);
    }

    @RequestMapping("exportProjectExcel")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        xcmgProjectManager.exportProjectExcel(request, response);
    }

    /**
     * 获取流程变更信息
     */
    @RequestMapping(value = "getSysProperties", method = RequestMethod.POST)
    @ResponseBody
    public String getSysProperties(HttpServletRequest request, @RequestBody String postData,
                                   HttpServletResponse response) throws Exception {
        JSONObject postDataJson = new JSONObject();
        String value = "";
        if (StringUtils.isNotBlank(postData)) {
            postDataJson = JSONObject.parseObject(postData);
            value = WebAppUtil.getProperty(postDataJson.getString("key"));
        }
        return value;
    }

    /**
     * 将项目批量设置为进度追赶状态或者取消进度追赶状态
     *
     * @param request
     * @param requestBody
     * @param response
     * @return
     */
    @RequestMapping("changeProgressRunStatus")
    @ResponseBody
    public String changeProgressRunStatus(HttpServletRequest request, @RequestBody String requestBody,
                                          HttpServletResponse response) {
        if (StringUtils.isBlank(requestBody)) {
            logger.error("requestBody is blank");
            return "操作失败！";
        }
        JSONObject requestObject = JSONObject.parseObject(requestBody);
        if (requestObject == null || requestObject.isEmpty()) {
            logger.error("requestObject is empty");
            return "操作失败！";
        }

        return xcmgProjectManager.changeProgressRunStatus(requestObject);
    }

    /**
     * 检查当前阶段已上传的所有交付物，需要审批的是否已经审批完成
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("checkDeliveryApproval")
    @ResponseBody
    public JSONObject checkDeliveryApproval(HttpServletRequest request, HttpServletResponse response) {
        String projectId = RequestUtil.getString(request, "projectId");
        String stageId = RequestUtil.getString(request, "stageId");

        return xcmgProjectManager.checkDeliveryApproval(projectId, stageId);
    }


    /**
     * 后台启动交付物审批流程
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("startDeliveryApproval")
    @ResponseBody
    public JSONObject startDeliveryApproval(HttpServletRequest request, HttpServletResponse response) {
        return xcmgProjectManager.startDeliveryApproval(request, response);
    }

    @RequestMapping("checkDeliveryProduct")
    @ResponseBody
    public JSONObject checkDeliveryProduct(HttpServletRequest request, HttpServletResponse response) {
        String projectId = RequestUtil.getString(request, "projectId");
        return xcmgProjectManager.checkDeliveryProduct(projectId);
    }

    /**
     * 判断当前登录人是否是项目的成员
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("judgeProjectMember")
    @ResponseBody
    public JSONObject judgeProjectMember(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return xcmgProjectManager.judgeUserIsMem(ContextUtil.getCurrentUserId(),
                RequestUtil.getString(request, "projectId"));
    }

    @RequestMapping("scheduleTest")
    @ResponseBody
    public JSONObject scheduleTest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        xcmgProjectScheduler.projectDelayNotice();
        return null;
    }

    @RequestMapping("replaceMember")
    @ResponseBody
    public JSONObject replaceMember(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String projectId = RequestUtil.getString(request, "projectId", "");
        String originalUserId = RequestUtil.getString(request, "originalUserId", "");
        String currentUserId = RequestUtil.getString(request, "currentUserId", "");
        String memberId = RequestUtil.getString(request, "memberId", "");
        return xcmgProjectManager.replaceMember(projectId, originalUserId, currentUserId, memberId);
    }

    @RequestMapping("queryMembersByPid")
    @ResponseBody
    public List<JSONObject> queryMembersByPid(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String projectId = RequestUtil.getString(request, "projectId", "");
        return xcmgProjectManager.queryMembersByPid(projectId);
    }

    @RequestMapping("respProjectDelay5")
    @ResponseBody
    public int respProjectDelay5(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String userId = RequestUtil.getString(request, "userId", "");
        if (StringUtils.isBlank(userId)) {
            return 0;
        }
        return xcmgProjectManager.respProjectDelay5(userId);
    }

    @RequestMapping("queryInputList")
    @ResponseBody
    public List<JSONObject> queryInputList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String projectId = RequestUtil.getString(request, "projectId", "");
        return xcmgProjectManager.queryInputList(projectId);
    }

    @RequestMapping("inputEditPage")
    public ModelAndView inputEditPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "xcmgProjectManager/core/xcmgProjectInputEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    @RequestMapping("saveInputEdit")
    @ResponseBody
    public JsonResult saveInputEdit(HttpServletRequest request, HttpServletResponse response,
                                    @RequestBody String requestBody) throws Exception {
        return xcmgProjectManager.saveInputEdit(requestBody);
    }

    @RequestMapping("deleteInput")
    @ResponseBody
    public JsonResult deleteInput(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            if (StringUtils.isNotEmpty(uIdStr)) {
                String[] ids = uIdStr.split(",");
                return xcmgProjectManager.deleteInput(ids);
            }
        } catch (Exception e) {
            return new JsonResult(false, e.getMessage());
        }
        return new JsonResult(true, "成功删除!");
    }

    @RequestMapping("outListPage")
    public ModelAndView outListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "xcmgProjectManager/core/xcmgProjectOutList.jsp";
        String outPlanId = RequestUtil.getString(request, "outPlanId", "");
        String projectId = RequestUtil.getString(request, "projectId", "");
        String canOperateFile = RequestUtil.getString(request, "canOperateFile", "");
        String typeName = RequestUtil.getString(request, "typeName", "");
        String belongSubSysKey = RequestUtil.getString(request, "belongSubSysKey", "");
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("outPlanId", outPlanId);
        mv.addObject("projectId", projectId);
        mv.addObject("canOperateFile", canOperateFile);
        mv.addObject("typeName", typeName);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("belongSubSysKey", belongSubSysKey);
        return mv;
    }

    @RequestMapping("queryOutList")
    @ResponseBody
    public List<JSONObject> queryOutList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String outPlanId = RequestUtil.getString(request, "outPlanId", "");
        String typeName = RequestUtil.getString(request, "typeName", "");
        return xcmgProjectManager.queryOutList(outPlanId, typeName);
    }

    @RequestMapping(value = "saveOutList", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveOutList(HttpServletRequest request, @RequestBody String changeGridDataStr,
                                  HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(changeGridDataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("请求参数为空");
            return result;
        }
        xcmgProjectManager.saveOutList(result, changeGridDataStr);
        return result;
    }

    @RequestMapping("delayListPage")
    public ModelAndView getDelayListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "xcmgProjectManager/core/projectDelay.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUserNo", currentUser.getUserNo());
        Map<String, Object> params = new HashMap<>();
        params.put("userId", currentUser.getUserId());
        JSONObject resultJson = commonInfoManager.hasPermission("JSZXXMGLRY");
        mv.addObject("permission",
                resultJson.getBoolean("JSZXXMGLRY") || resultJson.getBoolean(RdmConst.ALLDATA_QUERY_KEY)
                        || "admin".equals(ContextUtil.getCurrentUser().getUserNo()));
        return mv;
    }

    @RequestMapping("queryParticipateProject")
    @ResponseBody
    public List<JSONObject> queryParticipateProject(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> param = new HashMap<>();
        String userId = RequestUtil.getString(request, "queryProjectUserId", "");
        if (StringUtils.isBlank(userId)) {
            return Collections.emptyList();
        }
        param.put("userId", userId);
        param.put("status", "RUNNING");
        List<JSONObject> result = pdmApiDao.pdmQueryProjectList(param);
        return result;
    }

    // 新增或编辑、明细、处理任务
    @RequestMapping("productEditPage")
    public ModelAndView getProductEditPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String projectId = RequestUtil.getString(request, "projectId");
        String nodeId = RequestUtil.getString(request, "nodeId");
        String action = RequestUtil.getString(request, "action");
        String status = RequestUtil.getString(request, "status");
        String detailHideRoleRatio = RequestUtil.getString(request, "detailHideRoleRatio", "false");
        String currentUserProjectRoleName = RequestUtil.getString(request, "currentUserProjectRoleName", "");
        if (StringUtils.isBlank(currentUserProjectRoleName) && StringUtils.isNotBlank(projectId)) {
            JSONObject memJudgeResult = xcmgProjectManager.judgeUserIsMem(ContextUtil.getCurrentUserId(), projectId);
            if (memJudgeResult.getBooleanValue("result")) {
                currentUserProjectRoleName = memJudgeResult.getString("roleName");
            }
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
        String jspPath = "xcmgProjectManager/core/projectProductEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("projectId", projectId).addObject("action", action).addObject("status", status);
        mv.addObject("currentUserProjectRoleName", currentUserProjectRoleName);
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            String solKey = RequestUtil.getString(request, "solKey");
            if (StringUtils.isBlank(solKey)) {
                solKey = "KJXMGL";
            }
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, solKey, null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        IUser currentUser = ContextUtil.getCurrentUser();
        // 返回当前登录人所属或者所负责的所有部门信息
        Map<String, Object> params = new HashMap<>();
        params.put("userId", currentUser.getUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> currentUserDeps = xcmgProjectOtherDao.queryUserDeps(params);
        JSONArray jsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserDeps);
        mv.addObject("currentUserDeps", jsonArray);
        // 返回当前登录人角色信息
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("currentUserMainDeptId", ContextUtil.getCurrentUser().getMainGroupId());
        mv.addObject("currentUser", currentUser);
        String projectCategory = RequestUtil.getString(request, "projectCategory", "");
        mv.addObject("projectCategory", projectCategory);
        mv.addObject("detailHideRoleRatio", detailHideRoleRatio);
        Boolean exDeptFlag = false;
        if (StringUtils.isNotBlank(projectId)) {
            JSONObject xcmgProject = xcmgProjectManager.getXcmgProject(projectId);
            String currentDeptId = ContextUtil.getCurrentUser().getMainGroupId();
            // 判断不是挖掘机械研究院的，并且主负责部门不是当前部门的
            boolean flag = commonInfoManager.judgeIsTechDept(currentDeptId);
            if (!flag && !currentDeptId.equals(xcmgProject.getString("mainDepId"))) {
                exDeptFlag = true;
            }
        }
        mv.addObject("exDeptFlag", exDeptFlag);
        return mv;
    }

    /**
     * 获取产品列表
     */
    @RequestMapping("productList")
    @ResponseBody
    public List<JSONObject> getProductList(HttpServletRequest request, HttpServletResponse response) {
        return xcmgProjectManager.getProductList(request);
    }

    @RequestMapping("deliveryPage")
    public ModelAndView getDeliveryPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "xcmgProjectManager/core/projectMemberDelivery.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String projectId = request.getParameter("projectId");
        String userId = request.getParameter("userId");
        String editable = request.getParameter("editable");
        mv.addObject("projectId", projectId);
        mv.addObject("userId", userId);
        mv.addObject("editable", editable);
        return mv;
    }

    @RequestMapping("listDeliveryData")
    @ResponseBody
    public List<JSONObject> getDeliveryData(HttpServletRequest request, HttpServletResponse response) {
        return xcmgProjectManager.getDeliveryData(request);
    }

    /**
     * 获取产品列表
     */
    @RequestMapping("projectProductList")
    @ResponseBody
    public List<JSONObject> getProjectProductList(HttpServletRequest request, HttpServletResponse response) {
        return xcmgProjectManager.getProjectProductList(request);
    }

    @RequestMapping(value = "saveProjectDeliveryProduct", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject saveProjectDeliveryProduct(HttpServletRequest request, @RequestBody String changeGridDataStr,
                                                 HttpServletResponse response) {
        return xcmgProjectManager.saveProjectDeliveryProduct(changeGridDataStr, request);
    }

    @RequestMapping(value = "projectUserDelivery", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject getProjectUserDelivery(HttpServletRequest request, @RequestBody String postData,
                                             HttpServletResponse response) {
        JSONObject postDataJson = new JSONObject();
        JSONObject resultJson = new JSONObject();
        if (StringUtils.isNotBlank(postData)) {
            postDataJson = JSONObject.parseObject(postData);
        } else {
            return ResultUtil.result(false, "请求参数不能为空", null);

        }
        return xcmgProjectManager.getProjectUserDelivery(postDataJson);
    }

    /**
     * 测试用，勿动
     *
     * @param request
     * @param changeGridDataStr
     * @param response
     * @throws Exception
     */
    @RequestMapping("testScore")
    @ResponseBody
    public void testScore(HttpServletRequest request, @RequestBody String changeGridDataStr,
                          HttpServletResponse response) throws Exception {
        /*JSONObject param = new JSONObject();
        List<JSONObject> allProjects = xcmgProjectOtherDao.testScore1(param);
        File testOut3 = new File("E:/testOut5.txt");
        PrintWriter printWriter = new PrintWriter(testOut3);
        for (JSONObject oneProject : allProjects) {
            if ("SUCCESS_END".equalsIgnoreCase(oneProject.getString("STATUS_"))) {
                param.put("projectId", oneProject.getString("projectId"));
                param.put("stageId", oneProject.getString("maxStageId"));
                JSONObject scoreNum = xcmgProjectOtherDao.testScore2(param);
                if (scoreNum.getIntValue("scoreNum") == 0) {
                    printWriter.print("*******最后阶段积分没有：" + oneProject.getString("projectId") + "，" + oneProject.getString("projectName") + "\n");
                }
            }
            */
        /*param.put("projectId", oneProject.getString("projectId"));
        List<JSONObject> notInMemdata = xcmgProjectOtherDao.testScore3(param);
        if (notInMemdata.size() > 0) {
          printWriter.print("*******积分中有成员没有：" + param.getString("projectId") + "，项目状态："
              + oneProject.getString("STATUS_") + "\n");
        }*//*
                     }
                     printWriter.close();*/

    }

    @RequestMapping(value = "splitUserDelivery", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject splitUserDelivery(HttpServletRequest request, @RequestBody String postData,
                                        HttpServletResponse response) {
        JSONObject postDataJson = new JSONObject();
        JSONObject resultJson = new JSONObject();
        if (StringUtils.isNotBlank(postData)) {
            postDataJson = JSONObject.parseObject(postData);
        } else {
            return ResultUtil.result(false, "请求参数不能为空", null);

        }
        return xcmgProjectManager.genProductUserDelivery(postDataJson);
    }

    /**
     * 判断部门是否是挖掘机械研究院下的部门
     */
    @RequestMapping(value = "isTechDept", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject isTechDept(HttpServletRequest request, @RequestBody String postData,
                                 HttpServletResponse response) {
        JSONObject postDataJson = new JSONObject();
        JSONObject resultJson = new JSONObject();
        if (StringUtils.isNotBlank(postData)) {
            postDataJson = JSONObject.parseObject(postData);
        } else {
            return ResultUtil.result(false, "请求参数不能为空", null);
        }
        boolean flag = commonInfoManager.judgeIsTechDept(postDataJson.getString("deptId"));
        return ResultUtil.result(true, "请求成功", flag);
    }

    /**
     * 产品补录
     */
    @RequestMapping("editProduct")
    @ResponseBody
    public JSONObject editProduct(HttpServletRequest request, HttpServletResponse response) {
        return xcmgProjectManager.editProduct(request);
    }

    @RequestMapping("queryStageEvaluateList")
    @ResponseBody
    public List<JSONObject> queryStageEvaluateList(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        return xcmgProjectManager.queryStageEvaluateList(request);
    }

    @RequestMapping(value = "saveStageEvaluate", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveStageEvaluate(HttpServletRequest request, @RequestBody String postDataStr,
                                        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(postDataStr)) {
            logger.warn("postDataStr is blank");
            result.setSuccess(false);
            result.setMessage("postDataStr is blank");
            return result;
        }
        xcmgProjectManager.saveStageEvaluate(result, postDataStr);
        return result;
    }

    @RequestMapping(value = "checkEvaluate", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult checkEvaluate(HttpServletRequest request, @RequestBody String postDataStr,
                                    HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(postDataStr)) {
            logger.warn("postDataStr is blank");
            result.setSuccess(false);
            result.setMessage("postDataStr is blank");
            return result;
        }
        xcmgProjectManager.checkEvaluate(result, postDataStr);
        return result;
    }

    @RequestMapping("getDutyStatus")
    @ResponseBody
    public JSONObject getDutyStatus(HttpServletRequest request, HttpServletResponse response) {
        String userId = RequestUtil.getString(request, "userId", "");
        if (StringUtils.isBlank(userId)) {
            return null;
        }
        JSONObject param = new JSONObject();
        param.put("userIds", Arrays.asList(userId));
        List<JSONObject> userInfo = xcmgProjectOtherDao.queryUserByIds(param);
        return userInfo.isEmpty() ? null : userInfo.get(0);
    }

}
