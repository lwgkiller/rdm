package com.redxun.bjjszc.core.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bjjszc.core.service.GjbjService;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Controller
@RequestMapping("/rdm/core/Gjbj/")
public class GjbjController extends GenericController {
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private GjbjService gjbjService;


    @RequestMapping("gjbjListPage")
    public ModelAndView msgManagement(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "bjjszc/core/gjbjList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "bjjszc/core/gjbjEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String gjbjId = RequestUtil.getString(request, "gjbjId");
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
        mv.addObject("gjbjId", gjbjId).addObject("action", action).addObject("status", status);
        // 取出节点变量，返回到页面

        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "GJBJQD", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        // JSONObject currentUserDepInfo = xcmgProjectManager.isCurrentUserDepRespman();
        // JSONObject dept = commonInfoManager.queryDeptNameById(currentUserDepInfo.getString("currentUserMainDepId"));
        // String deptName = dept.getString("deptname");
        // mv.addObject("deptName", deptName);
        // mv.addObject("currentUserMainDepId", currentUserDepInfo.getString("currentUserMainDepId"));
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("queryGjbj")
    @ResponseBody
    public JsonPageResult<?> queryJstb(HttpServletRequest request, HttpServletResponse response) {
        return gjbjService.queryGjbj(request, true);
    }

    @RequestMapping("deleteGjbj")
    @ResponseBody
    public JsonResult deleteJstb(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String instIdStr = RequestUtil.getString(request, "instIds");
            String[] ids = uIdStr.split(",");
            String[] instIds = instIdStr.split(",");
            return gjbjService.deleteGjbj(ids, instIds);
        } catch (Exception e) {
            logger.error("Exception in delete Gjbjbase", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("saveGjbj")
    @ResponseBody
    public JsonResult saveJstb(HttpServletRequest request, @RequestBody String xcmgProjectStr,
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
            if (StringUtils.isBlank(formDataJson.getString("gjbjId"))) {
                gjbjService.createGjbj(formDataJson);
            } else {
                gjbjService.updateGjbj(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save gjbj");
            result.setSuccess(false);
            result.setMessage("Exception in save gjbj");
            return result;
        }
        return result;
    }

    @RequestMapping("getGjbjDetail")
    @ResponseBody
    public JSONObject getGjbjDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject gjbjObj = new JSONObject();
        String gjbjId = RequestUtil.getString(request, "gjbjId");
        if (StringUtils.isNotBlank(gjbjId)) {
            gjbjObj = gjbjService.getGjbjDetail(gjbjId);
        }
        return gjbjObj;
    }

    @RequestMapping("getDetailList")
    @ResponseBody
    public List<JSONObject> getCnList(HttpServletRequest request, HttpServletResponse response) {
        String belongId = RequestUtil.getString(request, "belongId");
        return gjbjService.getDetailList(belongId);
    }

    @RequestMapping("deleteDetail")
    @ResponseBody
    public JsonResult deleteDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String gjbjIds = RequestUtil.getString(request, "ids");
            String[] gjbjIdsArr = gjbjIds.split(",", -1);
            return gjbjService.deleteDetail(gjbjIdsArr);
        } catch (Exception e) {
            logger.error("Exception in deleteGjbj", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("importTemplateDownload")
    public ResponseEntity<byte[]> importTemplateDownload(HttpServletRequest request, HttpServletResponse response) {
        return gjbjService.importTemplateDownload();
    }

    /**
     * 批量导入
     */
    @RequestMapping("importExcel")
    @ResponseBody
    public JSONObject importExcel(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        gjbjService.importGjbj(result, request);
        return result;
    }

    @RequestMapping("exportGjbjDetail")
    public void exportGjbjDetail(HttpServletRequest request, HttpServletResponse response) {
        gjbjService.exportGjbjDetail(request, response);
    }

    @RequestMapping("exportGjbjList")
    public void exportGjbjList(HttpServletRequest request, HttpServletResponse response) {
        gjbjService.exportGjbjList(request, response);
    }
}
