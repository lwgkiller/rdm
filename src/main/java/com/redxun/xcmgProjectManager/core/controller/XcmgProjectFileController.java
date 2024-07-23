
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.org.api.model.IUser;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectChangeDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectFileDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectFileManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * 项目交付物审批表
 *
 * @author zz
 */
@Controller
@RequestMapping("/xcmgProjectManager/core/xcmgProjectFile/")
public class XcmgProjectFileController {
    @Autowired
    private XcmgProjectFileDao xcmgProjectFileDao;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private XcmgProjectChangeDao xcmgProjectChangeDao;
    @Resource
    XcmgProjectFileManager xcmgProjectFileManager;
    @Autowired
    private CommonBpmManager commonBpmManager;

    /**
     * 项目 申请流程列表
     */
    @RequestMapping("getListPage")
    public ModelAndView getListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "xcmgProjectManager/core/projectFileApplyList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        mv.addObject("currentUserNo", currentUser.getUserNo());
        // 返回当前登录人是否是项目管理人员
        boolean isProjectManager = false;
        Map<String, Object> params = new HashMap<>();
        params.put("userId", currentUser.getUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        for (int index = 0; index < userRolesJsonArray.size(); index++) {
            JSONObject oneRole = userRolesJsonArray.getJSONObject(index);
            if (oneRole.getString("REL_TYPE_KEY_").equalsIgnoreCase("GROUP-USER-LEADER")
                || oneRole.getString("REL_TYPE_KEY_").equalsIgnoreCase("GROUP-USER-BELONG")) {
                if (oneRole.getString("NAME_").contains("项目管理人员")) {
                    isProjectManager = true;
                }
            }
        }
        mv.addObject("isProjectManager", isProjectManager);
        return mv;
    }

    /**
     * 交付物与流程实例绑定新增加的页面
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("fileListPage")
    public ModelAndView fileListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "xcmgProjectManager/core/projectFileList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String projectId = RequestUtil.getString(request, "projectId");
        String stageId = RequestUtil.getString(request, "stageId");
        String recordId = RequestUtil.getString(request, "recordId");
        mv.addObject("projectId", projectId);
        mv.addObject("stageId", stageId);
        mv.addObject("recordId", recordId);
        return mv;
    }

    /**
     * 获取 流程页面
     */
    @RequestMapping("getEditPage")
    public ModelAndView getEditPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "xcmgProjectManager/core/projectFileApplyEdit.jsp";

        ModelAndView mv = new ModelAndView(jspPath);
        String projectId = RequestUtil.getString(request, "projectId");
        String stageId = RequestUtil.getString(request, "stageId");
        String nodeId = RequestUtil.getString(request, "nodeId");
        String id = RequestUtil.getString(request, "id");
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
            if (StringUtils.isNotBlank(id)) {
                JSONObject solObj = commonBpmManager.querySolutionByBusKey(id);
                if (solObj != null) {
                    List<Map<String, String>> nodeVars =
                        commonBpmManager.queryNodeVarsByParam(nodeId, solObj.getString("KEY_"), null);
                    if (nodeVars != null && !nodeVars.isEmpty()) {
                        mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
                    }
                }
            }
        }
        Map<String, Object> fileApplyObj = null;
        JSONObject applyObj = new JSONObject();
        if (StringUtils.isNotBlank(id)) {
            Map<String, Object> params = new HashMap<>(16);
            params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            params.put("id", id);
            fileApplyObj = xcmgProjectFileDao.getObject(params);
            applyObj = XcmgProjectUtil.convertMap2JsonObject(fileApplyObj);
        } else {
            // 新增不允许添加交付物
            action = "";
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
                applyObj.put("mainDepId", jsonObject.getString("mainDepId"));
            }
            applyObj.put("projectId", projectId);
            applyObj.put("stageId", stageId);
        }
        mv.addObject("action", action);
        mv.addObject("applyObj", applyObj);
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }

    /**
     * 获取项目 list
     */
    @RequestMapping("queryList")
    @ResponseBody
    public JsonPageResult<?> queryList(HttpServletRequest request, HttpServletResponse response) {
        return xcmgProjectFileManager.queryList(request, response);
    }

    /**
     * 项目 表单和流程删除
     */
    @RequestMapping("delete")
    @ResponseBody
    public JsonResult delete(HttpServletRequest request, HttpServletResponse response) {
        try {
            String uIdStr = RequestUtil.getString(request, "ids", "");
            String instIdStr = RequestUtil.getString(request, "instIds", "");
            if (StringUtils.isNotEmpty(uIdStr) && StringUtils.isNotEmpty(instIdStr)) {
                String[] ids = uIdStr.split(",");
                String[] instIds = instIdStr.split(",");
                return xcmgProjectFileManager.delete(ids, instIds);
            }
        } catch (Exception e) {
            return new JsonResult(false, e.getMessage());
        }
        return new JsonResult(true, "成功删除!");
    }

    /**
     * 获取流程 信息
     */
    @RequestMapping(value = "abolishInfo", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject getAbolishInfo(HttpServletRequest request, @RequestBody String postData,
        HttpServletResponse response) throws Exception {
        JSONObject postDataJson = new JSONObject();
        if (StringUtils.isNotBlank(postData)) {
            postDataJson = JSONObject.parseObject(postData);
        }
        return null;
    }

    /**
     * 明细：查看审批信息
     */
    @RequestMapping("edit")
    public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = RequestUtil.getString(request, "id");
        String action = RequestUtil.getString(request, "action", "");
        String status = RequestUtil.getString(request, "status", "");
        String jspPath = "xcmgProjectManager/core/projectFileApplyEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("id", id).addObject("action", action).addObject("status", status);
        Map<String, Object> fileApplyObj = null;
        JSONObject applyObj = new JSONObject();
        if (StringUtils.isNotBlank(id)) {
            Map<String, Object> params = new HashMap<>(16);
            params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            params.put("id", id);
            fileApplyObj = xcmgProjectFileDao.getObject(params);
            applyObj = XcmgProjectUtil.convertMap2JsonObject(fileApplyObj);
        }
        mv.addObject("applyObj", applyObj);
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }

    /**
     * 从交付物审批单中移除文档
     */
    @RequestMapping("delFile")
    @ResponseBody
    public JsonResult delFile(HttpServletRequest request, HttpServletResponse response) {
        try {
            return xcmgProjectFileManager.delFile(request, response);
        } catch (Exception e) {
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("addFile")
    @ResponseBody
    public JsonResult addFile(HttpServletRequest request, HttpServletResponse response) {
        try {
            return xcmgProjectFileManager.addFile(request, response);
        } catch (Exception e) {
            return new JsonResult(false, e.getMessage());
        }
    }

    /**
     * 删除原始交付物文件
     */
    @RequestMapping("delOrgFiles")
    @ResponseBody
    public JsonResult delOrgFiles(HttpServletRequest request, HttpServletResponse response) {
        try {
            return xcmgProjectFileManager.delOrgFiles(request, response);
        } catch (Exception e) {
            return new JsonResult(false, e.getMessage());
        }
    }

    /**
     * 获取阶段项目交付物
     */
    @RequestMapping("getStageFileList")
    @ResponseBody
    public List getStageFileList(HttpServletRequest request, HttpServletResponse response) {
        return xcmgProjectFileManager.getStageFileList(request, response);
    }

    /**
     * 获取审批表单中交付物列表
     */
    @RequestMapping("getApprovalFileList")
    @ResponseBody
    public List getApprovalFileList(HttpServletRequest request, HttpServletResponse response) {
        return xcmgProjectFileManager.getApprovalFileList(request, response);
    }

    @RequestMapping("queryStageFileList")
    @ResponseBody
    public List<JSONObject> queryStageFileList(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> params = new HashMap<>();
        params.put("projectId", RequestUtil.getString(request, "projectId", ""));
        params.put("stageId", RequestUtil.getString(request, "stageId", ""));
        return xcmgProjectFileDao.getStageFileList(params);
    }
}
