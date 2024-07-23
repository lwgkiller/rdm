package com.redxun.serviceEngineering.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.service.SeGeneralKanbanCompletenessService;
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
@RequestMapping("/serviceEngineering/core/seGeneralKanbanCompleteness")
public class SeGeneralKanbanCompletenessController {
    private static final Logger logger = LoggerFactory.getLogger(SeGeneralKanbanCompletenessController.class);
    @Autowired
    SeGeneralKanbanCompletenessService seGeneralKanbanCompletenessService;

    //..
    @RequestMapping("dataListPage")
    public ModelAndView dataListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/seGeneralKanbanCompleteness.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    //..
    @RequestMapping("dataListQuery")
    @ResponseBody
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        return seGeneralKanbanCompletenessService.dataListQuery(request, response);
    }

    //..
    @RequestMapping("deleteData")
    @ResponseBody
    public JsonResult deleteData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return seGeneralKanbanCompletenessService.deleteData(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteData", e);
            return new JsonResult(false, e.getMessage());
        }
    }


    //..
    @RequestMapping(value = "saveBusiness", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveBusiness(HttpServletRequest request, @RequestBody String businessDataStr,
                                   HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
        seGeneralKanbanCompletenessService.saveBusiness(result, businessDataStr);
        return result;
    }

    //..
    @RequestMapping("exportList")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        seGeneralKanbanCompletenessService.exportList(request, response);
    }

    //..
    @RequestMapping("importTemplateDownload")
    public ResponseEntity<byte[]> importTemplateDownload(HttpServletRequest request, HttpServletResponse response) {
        return seGeneralKanbanCompletenessService.importTemplateDownload();
    }

    //..
    @RequestMapping("importExcel")
    @ResponseBody
    public JSONObject importExcel(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        seGeneralKanbanCompletenessService.importExcel(result, request);
        return result;
    }
}
