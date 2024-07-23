
package com.redxun.rdmZhgl.core.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.rdmCommon.core.manager.CommonFuns;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.manager.MybatisBaseManager;
import com.redxun.org.api.model.IUser;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmZhgl.core.dao.DelayApplyDao;
import com.redxun.rdmZhgl.core.service.DelayApplyService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.MybatisListController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;

/**
 * 加班申请审批流程
 * 
 * @author zz
 */
@Controller
@RequestMapping("/rdmZhgl/core/delayApply/")
public class DelayApplyController extends MybatisListController {
    @Resource
    private DelayApplyService delayApplyService;
    @Resource
    private DelayApplyDao delayApplyDao;
    @Resource
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Resource
    private CommonInfoManager commonInfoManager;
    @Autowired
    private CommonBpmManager commonBpmManager;

    @SuppressWarnings("rawtypes")
    @Override
    public MybatisBaseManager getBaseManager() {
        return null;
    }

    /**
     * 审批列表页面
     */
    @RequestMapping("listPage")
    public ModelAndView getListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/delayApplyList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        Map<String, Object> params = new HashMap<>();
        params.put("userId", currentUser.getUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        List<String> roleNames = currentUserRoles.stream()
            .map(stringObjectMap -> stringObjectMap.get("NAME_").toString()).collect(Collectors.toList());
        mv.addObject("currentUser", currentUser);
        JSONObject resultJson = commonInfoManager.hasPermission("BMRZZY");
        JSONObject techJson = commonInfoManager.hasPermission("JSZXRZZY");
        mv.addObject("permission", resultJson.getBoolean("BMRZZY"));
        mv.addObject("techAdmin", techJson.getBoolean("JSZXRZZY"));
        mv.addObject("currentMainGroupId", currentUser.getMainGroupId());
        return mv;
    }

    /**
     * 获取审批页面
     */
    @RequestMapping("editPage")
    public ModelAndView getEditPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/delayApplyEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String taskId_ = RequestUtil.getString(request, "taskId_");
        String nodeId = RequestUtil.getString(request, "nodeId");
        String id = RequestUtil.getString(request, "id");
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
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "JBSQFlow", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        mv.addObject("action", action);
        Map<String, Object> planApplyObj = null;
        JSONObject applyObj = new JSONObject();
        if (StringUtils.isNotBlank(id)) {
            planApplyObj = delayApplyDao.getObjectById(id);
            applyObj = CommonFuns.convertMap2JsonObject(planApplyObj);
            applyObj.put("mainDepId", applyObj.getString("deptId"));
        } else {
            String currentDeptId = ContextUtil.getCurrentUser().getMainGroupId();
            applyObj.put("mainDepId", currentDeptId);
            applyObj.put("deptId", currentDeptId);
            applyObj.put("deptName", ContextUtil.getCurrentUser().getMainGroupName());
        }
        mv.addObject("applyObj", applyObj);
        JSONObject resultJson = commonInfoManager.hasPermission("BMRZZY");
        JSONObject techJson = commonInfoManager.hasPermission("JSZXRZZY");
        mv.addObject("permission", resultJson.getBoolean("BMRZZY"));
        mv.addObject("techAdmin", techJson.getBoolean("JSZXRZZY"));
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        return mv;
    }

    /**
     * 获取list
     */
    @RequestMapping("queryList")
    @ResponseBody
    public JsonPageResult<?> queryList(HttpServletRequest request, HttpServletResponse response) {
        return delayApplyService.queryList(request, response);
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
                return delayApplyService.delete(ids, instIds);
            }
        } catch (Exception e) {
            return new JsonResult(false, e.getMessage());
        }
        return new JsonResult(true, "成功删除!");
    }

    /**
     * 明细：查看审批信息
     */
    @RequestMapping("edit")
    public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = RequestUtil.getString(request, "id");
        String action = RequestUtil.getString(request, "action");
        String status = RequestUtil.getString(request, "status");
        String jspPath = "rdmZhgl/core/delayApplyEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("id", id).addObject("action", action).addObject("status", status);
        Map<String, Object> abolishApplyObj = null;
        JSONObject applyObj = new JSONObject();
        if (StringUtils.isNotBlank(id)) {
            abolishApplyObj = delayApplyDao.getObjectById(id);
            applyObj = CommonFuns.convertMap2JsonObject(abolishApplyObj);
        }
        mv.addObject("action", action);
        mv.addObject("applyObj", applyObj);
        JSONObject resultJson = commonInfoManager.hasPermission("BMRZZY");
        JSONObject techJson = commonInfoManager.hasPermission("JSZXRZZY");
        mv.addObject("permission", resultJson.getBoolean("BMRZZY"));
        mv.addObject("techAdmin", techJson.getBoolean("JSZXRZZY"));
        return mv;
    }

    @RequestMapping("saveDelayApply")
    @ResponseBody
    public JsonResult saveDelayApply(HttpServletRequest request, @RequestBody String formData,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(formData)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        try {
            JSONObject formDataJson = JSONObject.parseObject(formData);
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("id"))) {
                delayApplyService.add(formDataJson);
            } else {
                delayApplyService.update(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save delayApply");
            result.setSuccess(false);
            result.setMessage("Exception in save delayApply");
            return result;
        }
        return result;
    }

    // 判断流程是否已启动
    @RequestMapping("judgeApplyStart")
    @ResponseBody
    public JsonResult judgeApplyStart(HttpServletRequest request, HttpServletResponse response) {
        JsonResult result = new JsonResult(false, "");
        String instId = RequestUtil.getString(request, "instId", "");
        if (StringUtils.isBlank(instId)) {
            return result;
        }
        try {
            List<JSONObject> instObjList = delayApplyDao.findInstById(instId);
            if (instObjList == null || instObjList.isEmpty()) {
                return result;
            }
            String status = instObjList.get(0).getString("STATUS_");
            if (StringUtils.isNotBlank(status) && "RUNNING".equalsIgnoreCase(status)) {
                result.setSuccess(true);
            }
        } catch (Exception e) {
            logger.error("Exception in judgeApplyStart");
            result.setSuccess(true);
            result.setMessage("系统异常");
        }
        return result;
    }
}
