package com.redxun.productDataManagement.report.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.redxun.productDataManagement.report.manager.PdmReportManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.sys.core.manager.SysDicManager;

@Controller
@RequestMapping("/pmd/report/reports")
public class PdmReportController {
    private static final Logger logger = LoggerFactory.getLogger(PdmReportController.class);
    @Autowired
    private PdmReportManager pdmReportManager;
    @Autowired
    private SysDicManager sysDicManager;

//    @RequestMapping("getPdmReport1")
//    @ResponseBody
//    public JsonResult getPdmReport1(HttpServletRequest request, HttpServletResponse response) {
//        try {
//            JSONObject param = new JSONObject();
//            param.put("startTime",request.getParameter("startTime"));
//            param.put("endTime",request.getParameter("endTime"));
//            param.put("departName",request.getParameter("departName"));
//
//            return pdmReportManager.getPdmReport1(param);
//        } catch (Exception e) {
//            logger.error("SeGeneralKanBanNewController的refreshCacheGenSit出现运行时错误:" + e.getMessage());
//            return new JsonResult(false, e.getMessage());
//        }
//    }

    @RequestMapping("getPdmReportChartOne")
    @ResponseBody
    public List<JSONObject> getPdmReportChartOne(HttpServletRequest request, HttpServletResponse response,
                                                    @RequestBody String postDataStr) {
        return pdmReportManager.getPdmReportChartOne(request, response, postDataStr);
    }

    @RequestMapping("getPdmReportChartTwoJstz")
    @ResponseBody
    public List<JSONObject> getPdmReportChartTwoJstz(HttpServletRequest request, HttpServletResponse response,
                                                 @RequestBody String postDataStr) {
        return pdmReportManager.getPdmReportChartTwo(request, response, postDataStr);
    }

    @RequestMapping("getPdmReportChartTwo")
    @ResponseBody
    public List<JSONObject> getPdmReportChartTwo(HttpServletRequest request, HttpServletResponse response,
                                                 @RequestBody String postDataStr) {
        return pdmReportManager.getPdmReportChartTwo(request, response, postDataStr);
    }

    @RequestMapping("pdmKanban")
    public ModelAndView seGeneralKanbanPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "productDataManagement/report/pdmKanban.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String seKanbanAdmin =
                sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringGroups", "seKanbanAdmin").getValue();
        mv.addObject("seKanbanAdmin", seKanbanAdmin);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }




}
