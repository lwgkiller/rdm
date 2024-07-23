package com.redxun.serviceEngineering.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.StringUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.serviceEngineering.core.service.MaintenanceManualfileService;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/serviceEngineering/core/maintenanceManualfile")
public class MaintenanceManualfileController {
    private static final Logger logger = LoggerFactory.getLogger(MaintenanceManualfileController.class);
    @Autowired
    MaintenanceManualfileService maintenanceManualfileService;
    @Autowired
    SysDicManager sysDicManager;

    //..
    @RequestMapping("dataListPage")
    public ModelAndView dataListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/maintenanceManualfileList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        //..第三方页面打开时传入便捷查询参数
        String salesModel = RequestUtil.getString(request, "salesModel");
        String designModel = RequestUtil.getString(request, "designModel");
        String materialCode = RequestUtil.getString(request, "materialCode");
        String manualLanguage = RequestUtil.getString(request, "manualLanguage");
        String isCE = RequestUtil.getString(request, "isCE");
        String maintenanceManualAdmin = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringGroups", "maintenanceManualAdmin").getValue();
        mv.addObject("salesModel", salesModel).addObject("designModel", designModel).addObject("materialCode", materialCode)
                .addObject("manualLanguage", manualLanguage).addObject("isCE", isCE)
                .addObject("maintenanceManualAdmin", maintenanceManualAdmin);
        return mv;
    }

    //..
    @RequestMapping("dataListQuery")
    @ResponseBody
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        return maintenanceManualfileService.dataListQuery(request, response);
    }

    //..
    @RequestMapping("Preview")
    public void Preview(HttpServletRequest request, HttpServletResponse response) {
        maintenanceManualfileService.Preview(request, response);
    }

    //..
    @RequestMapping("Download")
    public ResponseEntity<byte[]> Download(HttpServletRequest request, HttpServletResponse response) {
        String id = RequestUtil.getString(request, "id");
        String description = RequestUtil.getString(request, "description");
        return maintenanceManualfileService.Download(request, id, description);
    }

    //..
    @RequestMapping("EditPage")
    public ModelAndView EditPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "serviceEngineering/core/maintenanceManualfileEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String id = RequestUtil.getString(request, "id");
        String action = RequestUtil.getString(request, "action");
        JSONObject obj = new JSONObject();
        if (StringUtils.isNotBlank(id)) {
            obj = maintenanceManualfileService.queryDataById(id);
        }
        mv.addObject("obj", obj).addObject("businessId", obj.getString("id"));
        mv.addObject("action", action);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        String maintenanceManualAdmin = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringGroups", "maintenanceManualAdmin").getValue();
        mv.addObject("maintenanceManualAdmin", maintenanceManualAdmin);
        return mv;
    }

    //..
    @RequestMapping("deleteBusiness")
    @ResponseBody
    public JSONObject deleteBusiness(HttpServletRequest request, @RequestBody String requestBody,
                                     HttpServletResponse response) {
        JSONObject result = new JSONObject();
        JSONObject requestBodyObj = JSONObject.parseObject(requestBody);
        String id = requestBodyObj.getString("id");
        maintenanceManualfileService.deleteBusiness(result, id);
        return result;
    }

    //..
    @RequestMapping("releaseBusiness")
    @ResponseBody
    public JSONObject releaseBusiness(HttpServletRequest request, @RequestBody String requestBody,
                                      HttpServletResponse response) {
        JSONObject result = new JSONObject();
        JSONObject requestBodyObj = JSONObject.parseObject(requestBody);
        String id = requestBodyObj.getString("id");
        maintenanceManualfileService.releaseBusiness(result, id);
        return result;
    }

    //..
    @RequestMapping("refreshBusiness")
    @ResponseBody
    public JSONObject refreshBusiness(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        maintenanceManualfileService.refreshBusiness(result);
        return result;
    }

    //..
    @RequestMapping("saveBusiness")
    @ResponseBody
    public JSONObject saveBusiness(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        maintenanceManualfileService.saveBusiness(result, request);
        return result;
    }

    //..
    @RequestMapping("getLogList")
    @ResponseBody
    public List<JSONObject> getLogList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String businessId = RequestUtil.getString(request, "businessId", "");
        return maintenanceManualfileService.getLogList(businessId);
    }

    //..
    @RequestMapping("exportList")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        maintenanceManualfileService.exportList(request, response);
    }
}
