package com.redxun.bjjszc.core.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bjjszc.core.service.JsxxService;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Controller
@RequestMapping("/rdm/core/Jsxx/")
public class JsxxController extends GenericController {
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private JsxxService jsxxService;

    @RequestMapping("jsxxListPage")
    public ModelAndView msgManagement(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "bjjszc/core/jsxxList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        String mainGroupName = ContextUtil.getCurrentUser().getMainGroupName();
        boolean isFwgc = false;
        if (mainGroupName.equals("服务工程技术研究所")) {
            isFwgc = true;
        }
        mv.addObject("isFwgc", isFwgc);
        return mv;
    }

    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "bjjszc/core/jsxxEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String jsxxId = RequestUtil.getString(request, "jsxxId");
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
            mv.addObject("jsxxId", jsxxId).addObject("action", action).addObject("status", status);
        // 取出节点变量，返回到页面

        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "BJKFJSXXQD", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
//        JSONObject currentUserDepInfo = xcmgProjectManager.isCurrentUserDepRespman();
//        JSONObject dept = commonInfoManager.queryDeptNameById(currentUserDepInfo.getString("currentUserMainDepId"));
//        String deptName = dept.getString("deptname");
//        mv.addObject("deptName", deptName);
//        mv.addObject("currentUserMainDepId", currentUserDepInfo.getString("currentUserMainDepId"));




        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("queryJsxx")
    @ResponseBody
    public JsonPageResult<?> queryJstb(HttpServletRequest request, HttpServletResponse response) {
        return jsxxService.queryJsxx(request, true);
    }

    @RequestMapping("deleteJsxx")
    @ResponseBody
    public JsonResult deleteJstb(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String instIdStr = RequestUtil.getString(request, "instIds");
            String[] ids = uIdStr.split(",");
            String[] instIds = instIdStr.split(",");
            return jsxxService.deleteJsxx(ids, instIds);
        } catch (Exception e) {
            logger.error("Exception in delete Jsxxbase", e);
            return new JsonResult(false, e.getMessage());
        }
    }


    @RequestMapping("getJsxxDetail")
    @ResponseBody
    public JSONObject getJsxxDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject jsxxObj = new JSONObject();
        String jsxxId = RequestUtil.getString(request, "jsxxId");
        if (StringUtils.isNotBlank(jsxxId)) {
            jsxxObj = jsxxService.getJsxxDetail(jsxxId);
        }
        return jsxxObj;
    }

    @RequestMapping("getDetailList")
    @ResponseBody
    public List<JSONObject> getCnList(HttpServletRequest request, HttpServletResponse response) {
        String belongId = RequestUtil.getString(request, "belongId");
        return jsxxService.getDetailList(belongId);
    }

    @RequestMapping("deleteDetail")
    @ResponseBody
    public JsonResult deleteDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String ydjxIds = RequestUtil.getString(request, "ids");
            String[] ydjxIdsArr = ydjxIds.split(",", -1);
            return jsxxService.deleteDetail(ydjxIdsArr);
        } catch (Exception e) {
            logger.error("Exception in deleteYdjx", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("importTemplateDownload")
    public ResponseEntity<byte[]> importTemplateDownload(HttpServletRequest request, HttpServletResponse response) {
        return jsxxService.importTemplateDownload();
    }

    /**
     * 批量导入
     */
    @RequestMapping("importExcel")
    @ResponseBody
    public JSONObject importExcel(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        jsxxService.importJsxx(result, request);
        return result;
    }

    @RequestMapping("exportJsxxDetail")
    public void exportJsxxDetail(HttpServletRequest request, HttpServletResponse response) {
        jsxxService.exportJsxxDetail(request, response);
    }

    @RequestMapping("exportJsxxList")
    public void exportJsxxList(HttpServletRequest request, HttpServletResponse response) {
        jsxxService.exportJsxxList(request, response);
    }
}
