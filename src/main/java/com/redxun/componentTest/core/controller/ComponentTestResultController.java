package com.redxun.componentTest.core.controller;

import com.redxun.componentTest.core.service.ComponentTestResultService;
import com.redxun.core.json.JsonPageResult;
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
@RequestMapping("/componentTest/core/result")
public class ComponentTestResultController {
    private static final Logger logger = LoggerFactory.getLogger(ComponentTestResultController.class);
    @Autowired
    private ComponentTestResultService componentTestResultService;

    //..
    @RequestMapping("dataListPage")
    public ModelAndView dataListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "componentTest/core/componentTestResultList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }

    //..
    @RequestMapping("dataListQuery")
    @ResponseBody
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        return componentTestResultService.dataListQuery(request, response);
    }

    //..
    @RequestMapping("dataPassListPage")
    public ModelAndView dataPassListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "componentTest/core/componentTestResultPassList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }

    //..
    @RequestMapping("dataPassListQuery")
    @ResponseBody
    public JsonPageResult<?> dataPassListQuery(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return componentTestResultService.dataPassListQuery(request, response);
    }

    //..
    @RequestMapping("exportPass")
    public void exportPass(HttpServletRequest request, HttpServletResponse response) throws Exception {
        componentTestResultService.exportPass(request, response);
    }

    //..
    @RequestMapping("dataNotPassListPage")
    public ModelAndView dataNotPassListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "componentTest/core/componentTestResultNotPassList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }

    //..
    @RequestMapping("dataNotPassListQuery")
    @ResponseBody
    public JsonPageResult<?> dataNotPassListQuery(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return componentTestResultService.dataNotPassListQuery(request, response);
    }

    //..
    @RequestMapping("exportNotPass")
    public void exportNotPass(HttpServletRequest request, HttpServletResponse response) throws Exception {
        componentTestResultService.exportNotPass(request, response);
    }
}
