package com.redxun.rdmZhgl.core.controller;

import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmZhgl.core.service.ZljdOverviewService;
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
@RequestMapping("/zljd/core/overview/")
public class ZljdOverviewController {
    private static final Logger logger = LoggerFactory.getLogger(ZljdOverviewController.class);
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private ZljdOverviewService zljdOverviewService;

    @RequestMapping("zljdOverviewOverview")
    public ModelAndView grgztListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/report/zljdOverview.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    // ..
    @RequestMapping("queryValue")
    @ResponseBody
    public JsonResult queryValue(HttpServletRequest request) {
        return zljdOverviewService.queryValue(request);
    }

    @RequestMapping("queryNum")
    @ResponseBody
    public JsonResult queryNum(HttpServletRequest request) {
        return zljdOverviewService.queryNum(request);
    }

    @RequestMapping("queryReTotalNum")
    @ResponseBody
    public JsonResult queryReTotalNum(HttpServletRequest request) {
        return zljdOverviewService.queryReTotalNum(request);
    }
    @RequestMapping("queryZljdNumByuser")
    @ResponseBody
    public JsonResult queryZljdNumByuser(HttpServletRequest request) {
        return zljdOverviewService.queryZljdNumByuser(request);
    }
    @RequestMapping("queryReNumByuser")
    @ResponseBody
    public JsonResult queryReNumByuser(HttpServletRequest request) {
        return zljdOverviewService.queryReNumByuser(request);
    }
    @RequestMapping("queryApplyNum")
    @ResponseBody
    public JsonResult queryApplyNum(HttpServletRequest request) {
        return zljdOverviewService.queryApplyNum(request);
    }

    @RequestMapping("queryNewApplyNum")
    @ResponseBody
    public JsonPageResult<?> queryNewApplyNum(HttpServletRequest request, HttpServletResponse response) {
        return zljdOverviewService.queryNewApplyNum(request);
    }

    @RequestMapping("queryIPCNum")
    @ResponseBody
    public JsonPageResult<?> queryIPCNum(HttpServletRequest request, HttpServletResponse response) {
        return zljdOverviewService.queryIPCNum(request, false);
    }
}
