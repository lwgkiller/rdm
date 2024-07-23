package com.redxun.serviceEngineering.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.service.PurchasedPartsService;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/serviceEngineering/core/purchasedParts")
public class PurchasedPartsController {
    private static final Logger logger = LoggerFactory.getLogger(PurchasedPartsController.class);
    @Autowired
    private PurchasedPartsService purchasedPartsService;

    //..
    @RequestMapping("dataListPage")
    public ModelAndView dataListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/purchasedPartsList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    //..
    @RequestMapping("dataListQuery")
    @ResponseBody
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        return purchasedPartsService.dataListQuery(request, response);
    }

    //..
    @RequestMapping("deleteData")
    @ResponseBody
    public JsonResult deleteData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return purchasedPartsService.deleteData(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteData", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    //..
    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/purchasedPartsEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String businessId = RequestUtil.getString(request, "businessId");
        String action = RequestUtil.getString(request, "action");
        mv.addObject("businessId", businessId).addObject("action", action);
        return mv;
    }

    //..
    @RequestMapping(value = "saveData", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveData(HttpServletRequest request, @RequestBody String DataStr,
                               HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
        if (StringUtils.isBlank(DataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("表单为空，保存失败！");
            return result;
        }
        purchasedPartsService.saveData(result, DataStr);
        return result;
    }

    //..
    @RequestMapping("queryDataById")
    @ResponseBody
    public JSONObject queryDataById(HttpServletRequest request, HttpServletResponse response) {
        String businessId = RequestUtil.getString(request, "businessId");
        if (StringUtils.isBlank(businessId)) {
            logger.error("Id is blank");
            return null;
        }
        return purchasedPartsService.queryDataById(businessId);
    }

    //..
    @RequestMapping("weeklyReportListPage")
    public ModelAndView weeklyReportListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "serviceEngineering/core/purchasedPartsWeeklyReportList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }

    //..
    @RequestMapping("weeklyReportEditPage")
    public ModelAndView weeklyReportEditPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "serviceEngineering/core/purchasedPartsWeeklyReportEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String id = RequestUtil.getString(request, "id");
        String action = RequestUtil.getString(request, "action");
        JSONObject obj = new JSONObject();
        if (StringUtils.isNotBlank(id)) {
            obj = purchasedPartsService.queryWeeklyReportById(id);
        }
        mv.addObject("obj", obj);
        mv.addObject("action", action);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }

    //..
    @RequestMapping("weeklyReportListQuery")
    @ResponseBody
    public JsonPageResult<?> weeklyReportListQuery(HttpServletRequest request, HttpServletResponse response) {
        return purchasedPartsService.weeklyReportListQuery(request, response);
    }

    //..
    @RequestMapping("weeklyReportPreview")
    public void weeklyReportPreview(HttpServletRequest request, HttpServletResponse response) {
        purchasedPartsService.weeklyReportPreview(request, response);
    }

    //..
    @RequestMapping("weeklyReportDownload")
    public ResponseEntity<byte[]> weeklyReportDownload(HttpServletRequest request, HttpServletResponse response) {
        String id = RequestUtil.getString(request, "id");
        String description = RequestUtil.getString(request, "description");
        return purchasedPartsService.weeklyReportDownload(request, id, description);
    }

    //..
    @RequestMapping("deleteWeeklyReport")
    @ResponseBody
    public JSONObject deleteWeeklyReport(HttpServletRequest request, @RequestBody String requestBody,
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
        purchasedPartsService.deleteWeeklyReport(result, id);
        return result;
    }

    //..
    @RequestMapping("saveWeeklyReport")
    @ResponseBody
    public JSONObject saveWeeklyReport(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        purchasedPartsService.saveWeeklyReport(result, request);
        return result;
    }
}
