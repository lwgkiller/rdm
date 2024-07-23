package com.redxun.bjjszc.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bjjszc.core.service.KfsqService;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/rdm/core/Kfsq/")
public class KfsqController extends GenericController {
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private KfsqService kfsqService;
    @Autowired
    private CommonInfoManager commonInfoManager;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;

    @RequestMapping("editGcs")
    public ModelAndView editGcs(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "bjjszc/core/gcsEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    @RequestMapping("kfsqListPage")
    public ModelAndView msgManagement(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "bjjszc/core/kfsqList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "bjjszc/core/kfsqEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String kfsqId = RequestUtil.getString(request, "kfsqId");
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
        mv.addObject("kfsqId", kfsqId).addObject("action", action).addObject("status", status);
        // 取出节点变量，返回到页面

        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId,"KFSQ",null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        JSONObject currentUserDepInfo = xcmgProjectManager.isCurrentUserDepRespman();
        JSONObject dept = commonInfoManager.queryDeptNameById(currentUserDepInfo.getString("currentUserMainDepId"));
        String deptName = dept.getString("deptname");
        mv.addObject("deptName", deptName);
        mv.addObject("currentUserMainDepId", currentUserDepInfo.getString("currentUserMainDepId"));
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("queryKfsq")
    @ResponseBody
    public JsonPageResult<?> queryJstb(HttpServletRequest request, HttpServletResponse response) {
        return kfsqService.queryKfsq(request, true);
    }

    @RequestMapping("deleteKfsq")
    @ResponseBody
    public JsonResult deleteJstb(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String instIdStr = RequestUtil.getString(request, "instIds");
            String[] ids = uIdStr.split(",");
            String[] instIds = instIdStr.split(",");
            return kfsqService.deleteKfsq(ids, instIds);
        } catch (Exception e) {
            logger.error("Exception in deleteSecret", e);
            return new JsonResult(false, e.getMessage());
        }
    }
    @RequestMapping("queryGcs")
    @ResponseBody
    public JsonPageResult<?> queryGcs() {
        return kfsqService.queryGcs();
    }


    @RequestMapping("saveKfsq")
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
            if (StringUtils.isBlank(formDataJson.getString("kfsqId"))) {
                kfsqService.createKfsq(formDataJson);
            } else {
                kfsqService.updateKfsq(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save kfsq");
            result.setSuccess(false);
            result.setMessage("Exception in save kfsq");
            return result;
        }
        return result;
    }
    @RequestMapping(value = "saveGcs", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveGcs(HttpServletRequest request, @RequestBody String businessDataStr,
                              HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
        kfsqService.saveGcs(result, businessDataStr);
        return result;
    }

    @RequestMapping("getUserInfoByBj")
    @ResponseBody
    public JSONObject getUserInfoByBj(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject kfsqObj = new JSONObject();
        String bjType = RequestUtil.getString(request, "bjType");
        if (StringUtils.isNotBlank(bjType)) {
            kfsqObj = kfsqService.getUserInfoByBj(bjType);
        }
        return kfsqObj;
    }

    @RequestMapping("getKfsqDetail")
    @ResponseBody
    public JSONObject getKfsqDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject kfsqObj = new JSONObject();
        String kfsqId = RequestUtil.getString(request, "kfsqId");
        if (StringUtils.isNotBlank(kfsqId)) {
            kfsqObj = kfsqService.getKfsqDetail(kfsqId);
        }
        return kfsqObj;
    }

    @RequestMapping("getDetailList")
    @ResponseBody
    public List<JSONObject> getCnList(HttpServletRequest request, HttpServletResponse response) {
        String belongId = RequestUtil.getString(request, "belongId");
        return kfsqService.getDetailList(belongId);
    }

    @RequestMapping("deleteDetail")
    @ResponseBody
    public JsonResult deleteDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String ydjxIds = RequestUtil.getString(request, "ids");
            String[] ydjxIdsArr = ydjxIds.split(",", -1);
            return kfsqService.deleteDetail(ydjxIdsArr);
        } catch (Exception e) {
            logger.error("Exception in deleteYdjx", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("importTemplateDownload")
    public ResponseEntity<byte[]> importTemplateDownload(HttpServletRequest request, HttpServletResponse response) {
        return kfsqService.importTemplateDownload();
    }

    /**
     * 批量导入
     */
    @RequestMapping("importExcel")
    @ResponseBody
    public JSONObject importExcel(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        kfsqService.importKfsq(result, request);
        return result;
    }

    @RequestMapping("exportKfsqList")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        kfsqService.exportKfsqList(request, response);
    }
}
