package com.redxun.wwrz.report.controller;


import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.core.json.JsonPageResult;
import com.redxun.wwrz.report.service.WwrzReportService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.org.api.model.IUser;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.context.ContextUtil;

/**
 * @author zz

 * */
@Controller
@RequestMapping("/wwrz/core/report/")
public class WwrzReportController {
    @Resource
    CommonInfoManager commonInfoManager;
    @Resource
    WwrzReportService wwrzReportService;
    /**
     * 获取列表页面
     * */
    @RequestMapping("overviewPage")
    public ModelAndView getOverviewPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "wwrz/report/wwrzOverview.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        JSONObject adminJson = commonInfoManager.hasPermission("WWRZ-CJGLY");
        mv.addObject("permission",adminJson.getBoolean("WWRZ-CJGLY"));
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        return mv;
    }
    @RequestMapping("reportData")
    @ResponseBody
    public JSONObject getReportData(HttpServletRequest request, HttpServletResponse response) {
        return wwrzReportService.getReportData(request);
    }
    @RequestMapping("reportTypeData")
    @ResponseBody
    public JSONObject getReportTypeData(HttpServletRequest request, HttpServletResponse response) {
        return wwrzReportService.getReportTypeData(request);
    }
    /**
     * 获取list
     * */
    @RequestMapping("queryApplyList")
    @ResponseBody
    public JsonPageResult<?> queryApplyList(HttpServletRequest request, HttpServletResponse response) {
        return wwrzReportService.queryList(request, response,true);
    }
    @RequestMapping("reportDeptProject")
    @ResponseBody
    public JSONObject getDeptProjectData(HttpServletRequest request, HttpServletResponse response) {
        return wwrzReportService.getDeptProjectData(request);
    }

    /**
     * 认证费用报表
     * */
    @RequestMapping("moneyReportPage")
    public ModelAndView getMoneyReportPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "wwrz/report/wwrzMoneyOverview.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        JSONObject adminJson = commonInfoManager.hasPermission("WWRZ-CJGLY");
        mv.addObject("permission",adminJson.getBoolean("WWRZ-CJGLY"));
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        return mv;
    }
    @RequestMapping("reportRzProjectData")
    @ResponseBody
    public JSONObject getReportRzProjectData(HttpServletRequest request, HttpServletResponse response) {
        return wwrzReportService.getWwrzProjectData(request);
    }
    @RequestMapping("reportMoneyData")
    @ResponseBody
    public JSONObject getReportMoneyData(HttpServletRequest request, HttpServletResponse response) {
        return wwrzReportService.getWwrzMoneyData(request);
    }
    @RequestMapping("reportMoneyViewData")
    @ResponseBody
    public JSONObject getWwrzMoneyViewData(HttpServletRequest request, HttpServletResponse response) {
        return wwrzReportService.getWwrzMoneyViewData(request);
    }

    /**
     * 认证计划报表
     * */
    @RequestMapping("planReportPage")
    public ModelAndView getPlanReportPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "wwrz/report/wwrzPlanOverview.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        JSONObject adminJson = commonInfoManager.hasPermission("WWRZ-CJGLY");
        mv.addObject("permission",adminJson.getBoolean("WWRZ-CJGLY"));
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        return mv;
    }

    @RequestMapping("reportPlanData")
    @ResponseBody
    public JSONObject getReportPlanData(HttpServletRequest request, HttpServletResponse response) {
        return wwrzReportService.getPlanData(request);
    }
    @RequestMapping("reportDeptPlanData")
    @ResponseBody
    public JSONObject getReportDeptPlanData(HttpServletRequest request, HttpServletResponse response) {
        return wwrzReportService.getDeptPlanData(request);
    }
    /**
     * 认证计划报表
     * */
    @RequestMapping("projectProcessPage")
    public ModelAndView getpProjectProcessPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "wwrz/report/wwrzProjectProcessList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        JSONObject adminJson = commonInfoManager.hasPermission("WWRZ-CJGLY");
        mv.addObject("permission",adminJson.getBoolean("WWRZ-CJGLY"));
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        return mv;
    }
}
