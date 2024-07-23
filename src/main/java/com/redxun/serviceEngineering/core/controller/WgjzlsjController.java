package com.redxun.serviceEngineering.core.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.org.api.model.IUser;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.service.WgjzlsjService;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Controller
@RequestMapping("/serviceEngineering/core/wgjzlsj")
public class WgjzlsjController {
    private static final Logger logger = LoggerFactory.getLogger(WgjzlsjController.class);
    @Autowired
    private WgjzlsjService wgjzlsjService;
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Resource
    private BpmInstManager bpmInstManager;
    @Autowired
    private CommonInfoManager commonInfoManager;

    //..
    @RequestMapping("wgjzlsjListPage")
    public ModelAndView wgjzlsjListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/wgjzlsjList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("isSa", commonInfoManager.judgeUserIsPointRole("外购件流程负责人", ContextUtil.getCurrentUserId()));
        return mv;
    }

    //..
    @RequestMapping("wgjzlsjListQuery")
    @ResponseBody
    public JsonPageResult<?> wgjzlsjListQuery(HttpServletRequest request, HttpServletResponse response) {
        return wgjzlsjService.dataListQuery(request, response, true);
    }


    //..
    @RequestMapping("wgjzlsjEditPage")
    public ModelAndView wgjzlsjEditPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/wgjzlsjEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String wgjzlsjId = RequestUtil.getString(request, "wgjzlsjId");
        String nodeId = RequestUtil.getString(request, "nodeId");
        String action = RequestUtil.getString(request, "action");
        String taskStatus = RequestUtil.getString(request, "taskStatus");
        String mainGroupName = "";
        String mainGroupId = "";
        IUser iUser = ContextUtil.getCurrentUser();
        if (iUser != null) {
            mainGroupName = ContextUtil.getCurrentUser().getMainGroupName();
            mainGroupId = ContextUtil.getCurrentUser().getMainGroupId();
        }
        String isFwgc = "no";
        if (mainGroupName.equals("服务工程技术研究所")) {
            isFwgc = "yes";
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
        mv.addObject("wgjzlsjId", wgjzlsjId).addObject("action", action).addObject("taskStatus", taskStatus);
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "WGJZLSJ", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        mv.addObject("currentUserName", iUser == null ? "" : ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("isFwgc", isFwgc);
        mv.addObject("mainGroupName", mainGroupName);
        mv.addObject("mainGroupId", mainGroupId);
        mv.addObject("currentUserId", iUser == null ? "" : ContextUtil.getCurrentUserId());
        return mv;
    }

    //..
    @RequestMapping("getWgjzlsjDetail")
    @ResponseBody
    public JSONObject getWgjzlsjDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject obj = new JSONObject();
        String wgjzlsjId = RequestUtil.getString(request, "wgjzlsjId");
        if (StringUtils.isNotBlank(wgjzlsjId)) {
            obj = wgjzlsjService.getWgjzlsjDetail(wgjzlsjId);
        }
        return obj;
    }

    //..
    @RequestMapping("saveWgjzlsj")
    @ResponseBody
    public JsonResult saveWgjzlsj(HttpServletRequest request, @RequestBody String formData,
                                  HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
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
                wgjzlsjService.createWgjzlsj(formDataJson);
            } else {
                wgjzlsjService.updateWgjzlsj(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save wgjzlsj");
            result.setSuccess(false);
            result.setMessage("Exception in save wgjzlsj");
            return result;
        }
        return result;
    }

    //..
    @RequestMapping("deleteWgjzlsj")
    @ResponseBody
    public JsonResult deleteWgjzlsj(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
            return wgjzlsjService.deleteData(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteData", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    //..
    @RequestMapping("validReptition")
    @ResponseBody
    public JsonResult validReptition(HttpServletRequest request, @RequestBody String formData,
                                     HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(formData)) {
            logger.warn("requestBody is blank");
            result.setMessage("requestBody is blank");
            result.setSuccess(false);
            return result;
        }
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson == null || formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        JSONObject wgjzlsj = wgjzlsjService.queryByMaterialCodeAndSupplier(formDataJson);
        if (wgjzlsj != null && !wgjzlsj.isEmpty()) {
            // 判断是否有重复物料
            if (!formDataJson.getString("id").equals(wgjzlsj.getString("id"))) {
                result.setSuccess(false);
                result.setMessage("物料已存在");
            }
        }
        return result;
    }

    //李文光大改未涉及
    @RequestMapping("departmentReportPage")
    public ModelAndView departmentReportPage(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String jspPath = "serviceEngineering/core/wgjzlsjDepReport.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    //李文光大改未涉及
    @RequestMapping("departmentReportListQuery")
    @ResponseBody
    public JsonPageResult<?> departmentReportListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult jsonPageResult = new JsonPageResult();
        return wgjzlsjService.queryDepartmentReport(request, response, true);
    }

    //李文光大改未涉及
    @RequestMapping("supplierReportPage")
    public ModelAndView supplierReportPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/wgjzlsjSupplierReport.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    //李文光大改未涉及
    @RequestMapping("supplierReportListQuery")
    @ResponseBody
    public JsonPageResult<?> querySupplierReport(HttpServletRequest request, HttpServletResponse response) {
        return wgjzlsjService.querySupplierReport(request, response, true);
    }

    //李文光大改未涉及
    @RequestMapping("queryWgjzlsjChart")
    @ResponseBody
    public JSONObject queryWgjzlsjChart(HttpServletRequest request, HttpServletResponse response) {
        return wgjzlsjService.queryWgjzlsjChart();
    }

    //..
    @RequestMapping("exportWgj")
    public void exportWgj(HttpServletRequest request, HttpServletResponse response) {
        wgjzlsjService.exportWgj(request, response);
    }

    //..
    @RequestMapping("importTemplateDownload")
    public ResponseEntity<byte[]> importTemplateDownload(HttpServletRequest request, HttpServletResponse response) {
        return wgjzlsjService.importTemplateDownload();
    }

    //..
    @RequestMapping("importExcel")
    @ResponseBody
    public JSONObject importExcel(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        wgjzlsjService.importExcel(result, request);
        return result;
    }
}
