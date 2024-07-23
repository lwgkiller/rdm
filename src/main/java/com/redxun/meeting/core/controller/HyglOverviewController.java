package com.redxun.meeting.core.controller;

import com.redxun.core.json.JsonResult;
import com.redxun.meeting.core.service.HyglOverviewService;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.saweb.context.ContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/meeting/core/overview/")
public class HyglOverviewController {
    private static final Logger logger = LoggerFactory.getLogger(HyglOverviewController.class);
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private HyglOverviewService hyglOverviewService;

    @RequestMapping("hyglOverviewOverview")
    public ModelAndView grgztListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "meeting/core/hyglOverview.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    // ..
    @RequestMapping("queryMeetingType")
    @ResponseBody
    public JsonResult queryMeetingType(HttpServletRequest request) {
        return hyglOverviewService.queryMeetingType(request);
    }

    @RequestMapping("queryMeetingDelay")
    @ResponseBody
    public JsonResult queryMeetingDelay(HttpServletRequest request) {
        return hyglOverviewService.queryMeetingDelay(request);
    }

    @RequestMapping("queryRwfjFinish")
    @ResponseBody
    public JsonResult queryRwfjFinish(HttpServletRequest request) {
        return hyglOverviewService.queryRwfjFinish(request);
    }

    @RequestMapping("queryZRMeetingDelay")
    @ResponseBody
    public JsonResult queryZRMeetingDelay(HttpServletRequest request) {
        return hyglOverviewService.queryZRMeetingDelay(request);
    }

    @RequestMapping("queryZRRwfjFinish")
    @ResponseBody
    public JsonResult queryZRRwfjFinish(HttpServletRequest request) {
        return hyglOverviewService.queryZRRwfjFinish(request);
    }

}
