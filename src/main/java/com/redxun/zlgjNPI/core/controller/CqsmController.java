package com.redxun.zlgjNPI.core.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
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
import com.redxun.zlgjNPI.core.manager.CqsmService;

@Controller
@RequestMapping("/zlgj/Cqsm/")
public class CqsmController extends GenericController {
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private CqsmService cqsmService;
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;

    @RequestMapping("CqsmListPage")
    public ModelAndView msgManagement(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "zlgjNPI/cqsmList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        Map<String, Object> params2 = new HashMap<>();
        params2.put("userId", ContextUtil.getCurrentUserId());
        params2.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params2);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        // 返回当前用户是否是技术管理部负责人
        boolean isJsglbRespUser = rdmZhglUtil.judgeUserIsJsglbRespUser(ContextUtil.getCurrentUserId());
        mv.addObject("isJsglbRespUser", isJsglbRespUser);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "zlgjNPI/cqsmEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String cqsmId = RequestUtil.getString(request, "cqsmId");
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
        mv.addObject("cqsmId", cqsmId).addObject("action", action).addObject("status", status);
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "WTCQYYSM", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("queryCqsm")
    @ResponseBody
    public JsonPageResult<?> queryCqsm(HttpServletRequest request, HttpServletResponse response) {
        return cqsmService.queryCqsm(request, true);
    }

    @RequestMapping("deleteCqsm")
    @ResponseBody
    public JsonResult deleteCqsm(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return cqsmService.deleteCqsm(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteCqsm", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("saveCqsm")
    @ResponseBody
    public JsonResult saveCqsm(HttpServletRequest request, @RequestBody String xcmgProjectStr,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(xcmgProjectStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        try {
            JSONObject formDataJson = JSONObject.parseObject(xcmgProjectStr);
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("cqsmId"))) {
                cqsmService.createCqsm(formDataJson);
            } else {
                cqsmService.updateCqsm(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save cqsm");
            result.setSuccess(false);
            result.setMessage("Exception in save cqsm");
            return result;
        }
        return result;
    }

    @RequestMapping("getCqsmDetail")
    @ResponseBody
    public JSONObject getCqsmDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject cqsmObj = new JSONObject();
        String cqsmId = RequestUtil.getString(request, "cqsmId");
        if (StringUtils.isNotBlank(cqsmId)) {
            cqsmObj = cqsmService.getCqsmDetail(cqsmId);
        }
        return cqsmObj;
    }

}
