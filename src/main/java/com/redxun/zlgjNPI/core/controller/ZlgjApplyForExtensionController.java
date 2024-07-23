package com.redxun.zlgjNPI.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import com.redxun.zlgjNPI.core.manager.ZlgjApplyForExtensionService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/zlgj/applyForExtension/")
public class ZlgjApplyForExtensionController extends GenericController {
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private ZlgjApplyForExtensionService zlgjApplyForExtensionService;
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;

    //..
    @RequestMapping("applyListPage")
    public ModelAndView applyListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "zlgjNPI/zlgjApplyForExtensionList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
//        Map<String, Object> params2 = new HashMap<>();
//        params2.put("userId", ContextUtil.getCurrentUserId());
//        params2.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
//        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params2);
//        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
//        mv.addObject("currentUserRoles", userRolesJsonArray);
//        //返回当前用户是否是技术管理部负责人
//        boolean isJsglbRespUser = rdmZhglUtil.judgeUserIsJsglbRespUser(ContextUtil.getCurrentUserId());
//        mv.addObject("isJsglbRespUser", isJsglbRespUser);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    //..
    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "zlgjNPI/zlgjApplyForExtensionEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        //新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
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
        //取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "ZlgjApplyForExtension", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    //..
    @RequestMapping("queryApply")
    @ResponseBody
    public JsonPageResult<?> queryApply(HttpServletRequest request, HttpServletResponse response) {
        return zlgjApplyForExtensionService.queryApply(request, true);
    }

    //..
    @RequestMapping("deleteApply")
    @ResponseBody
    public JsonResult deleteApply(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            String instIdStr = RequestUtil.getString(request, "instIds");
            String[] instIds = instIdStr.split(",");
            return zlgjApplyForExtensionService.deleteApply(ids, instIds);
        } catch (Exception e) {
            logger.error("Exception in deleteApply", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    //..
    @RequestMapping("getApplyDetail")
    @ResponseBody
    public JSONObject getApplyDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject applyDetail = new JSONObject();
        String businessId = RequestUtil.getString(request, "businessId");
        if (StringUtils.isNotBlank(businessId)) {
            applyDetail = zlgjApplyForExtensionService.getApplyDetail(businessId);
        }
        return applyDetail;
    }
}
