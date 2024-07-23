package com.redxun.zlgjNPI.core.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import com.redxun.zlgjNPI.core.dao.NewItemFxpgDao;
import com.redxun.zlgjNPI.core.manager.NewItemFxpgService;

@RestController
@RequestMapping("/zhgl/core/fxpg/")
public class NewItemFxpgController {
    private Logger logger = LogManager.getLogger(NewItemFxpgController.class);
    @Resource
    private NewItemFxpgService newItemFxpgService;
    @Autowired
    private NewItemFxpgDao newItemFxpgDao;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Resource
    private BpmInstManager bpmInstManager;
    @Autowired
    private CommonBpmManager commonBpmManager;

    @RequestMapping("listPage")
    public ModelAndView listPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "zlgjNPI/newItemFxpgList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 返回当前登录人角色信息
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        // 返回当前用户是否是技术管理部负责人
        boolean isJsglbRespUser = rdmZhglUtil.judgeUserIsJsglbRespUser(ContextUtil.getCurrentUserId());
        mv.addObject("isJsglbRespUser", isJsglbRespUser);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    @RequestMapping("getXpsxList")
    @ResponseBody
    public JsonPageResult<?> getXpsxList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return newItemFxpgService.getXpsxList(request, response, true);
    }

    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "zlgjNPI/newItemFxpgEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String xpsxId = RequestUtil.getString(request, "xpsxId");
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
        mv.addObject("xpsxId", xpsxId).addObject("action", action).addObject("status", status);
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "XPSXFXPG", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("getXpsxDetail")
    @ResponseBody
    public JSONObject getXpsxDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject xpsxObj = new JSONObject();
        String xpsxId = RequestUtil.getString(request, "xpsxId");
        if (StringUtils.isNotBlank(xpsxId)) {
            xpsxObj = newItemFxpgService.getXpsxDetail(xpsxId);
        }
        return xpsxObj;
    }

    @RequestMapping("{userId}/getUserInfo")
    @ResponseBody
    public Map<String, Object> getUserInfo(@PathVariable("userId") String userId, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Map<String, Object> result = new HashMap<>();
        if (StringUtils.isNotBlank(userId)) {
            Map<String, Object> params = new HashMap<>();
            params.put("userId", userId);
            List<Map<String, Object>> currentUser = newItemFxpgDao.queryUser(params);
            JSONArray jsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUser);
            result.put("userDeps", jsonArray);
        }
        return result;
    }

    @RequestMapping("getFxpgList")
    @ResponseBody
    public List<JSONObject> getFxpgList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String xpsxId = RequestUtil.getString(request, "ids");
        return newItemFxpgService.getFxpgList(xpsxId);
    }

    @RequestMapping("deleteXpsx")
    @ResponseBody
    public JsonResult deleteXpsx(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String instIdStr = RequestUtil.getString(request, "instIds");
            if (StringUtils.isNotBlank(instIdStr)) {
                String[] instIds = instIdStr.split(",");
                for (int index = 0; index < instIds.length; index++) {
                    bpmInstManager.deleteCascade(instIds[index], "");
                }
            }
            String[] ids = uIdStr.split(",");
            return newItemFxpgService.deleteXpsx(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteXpsx", e);
            return new JsonResult(false, e.getMessage());
        }
    }
}
