package com.redxun.environment.core.controller;

import com.redxun.core.json.JsonResult;
import com.redxun.environment.core.service.GsClhbOverviewService;
import com.redxun.saweb.controller.GenericController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 国四环保信息公开报表
 */
@Controller
@RequestMapping("/environment/core/ClhbOverview/")
public class GsClhbOverViewController extends GenericController {
    @Autowired
    private GsClhbOverviewService gsClhbOverviewService;

    @RequestMapping("gsClhbOverview")
    public ModelAndView reviseOverviewPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "rap/core/gsClhbOverview.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    @RequestMapping("queryNumByDept")
    @ResponseBody
    public JsonResult queryReviseOverviewByDept(HttpServletRequest request) {
        return gsClhbOverviewService.queryNumByDept(request);
    }

    @RequestMapping("queryDelayByDept")
    @ResponseBody
    public JsonResult queryDelayByDept() {
        return gsClhbOverviewService.queryDelayByDept();
    }

    @RequestMapping("queryYearNum")
    @ResponseBody
    public JsonResult queryYearNum() {
        return gsClhbOverviewService.queryYearNum();
    }

    @RequestMapping("queryMonthNum")
    @ResponseBody
    public JsonResult queryMonthNum(HttpServletRequest request) {
        return gsClhbOverviewService.queryMonthNum(request);
    }

    @RequestMapping("queryNumByBrand")
    @ResponseBody
    public JsonResult queryNumByBrand() {
        return gsClhbOverviewService.queryNumByBrand();
    }
}
