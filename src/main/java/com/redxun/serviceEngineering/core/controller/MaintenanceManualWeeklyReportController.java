package com.redxun.serviceEngineering.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.service.MaintenanceManualWeeklyReportService;
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

@Controller
@RequestMapping("/serviceEngineering/core/maintenanceManualWeeklyReport")
public class MaintenanceManualWeeklyReportController {
    private static final Logger logger = LoggerFactory.getLogger(MaintenanceManualWeeklyReportController.class);
    @Autowired
    private MaintenanceManualWeeklyReportService maintenanceManualWeeklyReportService;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;

    //..
    @RequestMapping("dataListPage")
    public ModelAndView dataListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "serviceEngineering/core/maintenanceManualWeeklyReportList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }


    //..
    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "serviceEngineering/core/maintenanceManualWeeklyReportEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String id = RequestUtil.getString(request, "id");
        String action = RequestUtil.getString(request, "action");
        JSONObject obj = new JSONObject();
        if (StringUtils.isNotBlank(id)) {
            obj = maintenanceManualWeeklyReportService.queryWeeklyReportById(id);
        }
        mv.addObject("obj", obj);
        mv.addObject("action", action);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }

    //..
    @RequestMapping("dataListQuery")
    @ResponseBody
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        return maintenanceManualWeeklyReportService.dataListQuery(request, response);
    }

    //..
    @RequestMapping("preview")
    public void preview(HttpServletRequest request, HttpServletResponse response) {
        maintenanceManualWeeklyReportService.preview(request, response);
    }

    //..
    @RequestMapping("download")
    public ResponseEntity<byte[]> download(HttpServletRequest request, HttpServletResponse response) {
        String id = RequestUtil.getString(request, "id");
        String description = RequestUtil.getString(request, "description");
        return maintenanceManualWeeklyReportService.download(request, id, description);
    }

    //..
    @RequestMapping("delete")
    @ResponseBody
    public JSONObject delete(HttpServletRequest request, @RequestBody String requestBody,
                                            HttpServletResponse response) {
        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(requestBody)) {
            logger.error("requestBody is blank");
            result.put("message", "删除失败，消息体为空！");
            return result;
        }
        JSONObject requestBodyObj = JSONObject.parseObject(requestBody);
        if (StringUtils.isBlank(requestBodyObj.getString("id"))) {
            logger.error("id is blank");
            result.put("message", "删除失败，主键为空！");
            return result;
        }
        String id = requestBodyObj.getString("id");
        maintenanceManualWeeklyReportService.delete(result, id);
        return result;
    }

    //..
    @RequestMapping("save")
    @ResponseBody
    public JSONObject save(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        maintenanceManualWeeklyReportService.save(result, request);
        return result;
    }

    //..
    // ..
    @RequestMapping("OfficePreview")
    @ResponseBody
    public void OfficePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringUploadPosition", "maintenanceManualWeeklyReport").getValue();
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, filePathBase, response);
    }
}
