
package com.redxun.xcmgProjectManager.core.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.org.api.model.IUser;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectChangeDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectChangeManager;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * @author zz
 */
@Controller
@RequestMapping("/xcmgProjectManager/core/xcmgProjectChange/")
public class XcmgProjectChangeController {
    @Autowired
    private XcmgProjectChangeDao xcmgProjectChangeDao;
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Resource
    private XcmgProjectChangeManager xcmgProjectChangeManager;

    /**
     * 项目变更申请流程列表
     */
    @RequestMapping("getListPage")
    public ModelAndView getListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "xcmgProjectManager/core/projectChangeApplyList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        return mv;
    }

    /**
     * 获取变更流程页面
     */
    @RequestMapping("getEditPage")
    public ModelAndView getEditPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "xcmgProjectManager/core/projectChangeApplyEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String projectId = RequestUtil.getString(request, "projectId");
        String projectName = RequestUtil.getString(request, "projectName");
        String taskId_ = RequestUtil.getString(request, "taskId_");
        String nodeId = RequestUtil.getString(request, "nodeId");
        String id = RequestUtil.getString(request, "id");
        mv.addObject("projectId", projectId).addObject("projectName", projectName).addObject("taskId_", taskId_);
        // 新增或编辑的时候没有nodeId
        String action = "";
        if (StringUtils.isBlank(nodeId) || nodeId.contains("PROCESS")) {
            action = "edit";
        } else {
            // 处理任务的时候有nodeId
            action = "task";
        }
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "XMBGSQ", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        mv.addObject("action", action);
        Map<String, Object> changeApplyObj = null;
        JSONObject applyObj = new JSONObject();
        if (StringUtils.isNotBlank(id)) {
            Map<String, Object> params = new HashMap<>(16);
            params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            params.put("id", id);
            changeApplyObj = xcmgProjectChangeDao.queryProjectChangeById(params);
            applyObj = XcmgProjectUtil.convertMap2JsonObject(changeApplyObj);
        } else {
            Map<String, Object> params = new HashMap<>(16);
            params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            params.put("projectId", projectId);
            List<Map<String, Object>> projectInfoList = xcmgProjectChangeDao.getProjectBaseInfo(params);
            if (projectInfoList != null && projectInfoList.size() > 0) {
                JSONObject jsonObject = XcmgProjectUtil.convertMap2JsonObject(projectInfoList.get(0));
                applyObj.put("projectName", jsonObject.getString("projectName"));
                applyObj.put("projectLevel", jsonObject.getString("projectLevel"));
                applyObj.put("mainResUserName", jsonObject.getString("mainResUserName"));
                applyObj.put("projectType", jsonObject.getString("projectType"));
                applyObj.put("mainDeptName", jsonObject.getString("mainDeptName"));
                applyObj.put("number", jsonObject.getString("number"));
                applyObj.put("currentStage", jsonObject.getString("currentStage"));
            }
            applyObj.put("projectId", projectId);
            applyObj.put("taskId_", taskId_);
        }
        mv.addObject("changeApplyObj", applyObj);
        return mv;
    }

    /**
     * 获取项目变更list
     */
    @RequestMapping("queryList")
    @ResponseBody
    public JsonPageResult<?> queryList(HttpServletRequest request, HttpServletResponse response) {
        return xcmgProjectChangeManager.queryChangeApplyList(request, response,true);
    }

    /**
     * 项目变更表单和流程删除
     */
    @RequestMapping("changeApplyDel")
    @ResponseBody
    public JsonResult changeApplyDel(HttpServletRequest request, HttpServletResponse response) {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String instIdStr = RequestUtil.getString(request, "instIds");
            if (StringUtils.isNotEmpty(uIdStr) && StringUtils.isNotEmpty(instIdStr)) {
                String[] ids = uIdStr.split(",");
                String[] instIds = instIdStr.split(",");
                return xcmgProjectChangeManager.deleteChangeApplyById(ids, instIds);
            }
        } catch (Exception e) {
            return new JsonResult(false, e.getMessage());
        }
        return new JsonResult(true, "成功删除!");
    }

    @RequestMapping("edit")
    public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = RequestUtil.getString(request, "id");
        String action = RequestUtil.getString(request, "action");
        String status = RequestUtil.getString(request, "status");
        String jspPath = "xcmgProjectManager/core/projectChangeApplyEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("id", id).addObject("action", action).addObject("status", status);
        Map<String, Object> changeApplyObj = null;
        JSONObject applyObj = new JSONObject();
        if (StringUtils.isNotBlank(id)) {
            Map<String, Object> params = new HashMap<>(16);
            params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            params.put("id", id);
            changeApplyObj = xcmgProjectChangeDao.queryProjectChangeById(params);
            applyObj = XcmgProjectUtil.convertMap2JsonObject(changeApplyObj);
        }
        mv.addObject("changeApplyObj", applyObj);
        return mv;
    }

    @RequestMapping("exportProjectChange")
    public void exportBjqdList(HttpServletRequest request, HttpServletResponse response) {
        xcmgProjectChangeManager.exportProjectChange(request, response);
    }

}
