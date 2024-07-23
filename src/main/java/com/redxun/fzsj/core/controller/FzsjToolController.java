package com.redxun.fzsj.core.controller;

import com.redxun.core.json.JsonResult;
import com.redxun.fzsj.core.service.FzsjToolService;
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
@RequestMapping("/fzsj/core/tool")
public class FzsjToolController {
    private static final Logger logger = LoggerFactory.getLogger(FzsjToolController.class);
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private FzsjToolService fzsjToolService;

    @RequestMapping("/fzsjToolOverview")
    public ModelAndView grgztListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "fzsj/core/fzsjToolOverview.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    // ..
    @RequestMapping("/monthData")
    @ResponseBody
    public JsonResult monthData(HttpServletRequest request) {
        return fzsjToolService.jdbcQueryMonthInfo(request);
    }

    @RequestMapping("/typeData")
    @ResponseBody
    public JsonResult typeData(HttpServletRequest request) {
        return fzsjToolService.jdbcQueryTypeInfo(request);
    }

    @RequestMapping("/deptData")
    @ResponseBody
    public JsonResult deptData(HttpServletRequest request) {
        return fzsjToolService.jdbcQueryDeptInfo(request);
    }
}
