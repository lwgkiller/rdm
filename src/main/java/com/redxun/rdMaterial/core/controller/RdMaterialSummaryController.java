package com.redxun.rdMaterial.core.controller;

import com.redxun.core.json.JsonPageResult;
import com.redxun.rdMaterial.core.service.RdMaterialSummaryService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;

@Controller
@RequestMapping("/rdMaterial/core/summary/")
public class RdMaterialSummaryController {
    private static final Logger logger = LoggerFactory.getLogger(RdMaterialSummaryController.class);
    @Autowired
    private RdMaterialSummaryService rdMaterialSummaryService;

    //..
    @RequestMapping("summaryListPage")
    public ModelAndView summaryListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdMaterial/core/rdMaterialSummaryList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }

    //..
    @RequestMapping("summaryListQuery")
    @ResponseBody
    public JsonPageResult<?> summaryListQuery(HttpServletRequest request, HttpServletResponse response) {
        return rdMaterialSummaryService.summaryListQuery(request, response);
    }

    //..
    @RequestMapping("itemListPage")
    public ModelAndView itemListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdMaterial/core/rdMaterialItemList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("materialCode", RequestUtil.getString(request, "materialCode", ""));
        mv.addObject("materialDescription", RequestUtil.getString(request, "materialDescription", ""));
        mv.addObject("untreatedTimespanBegin", RequestUtil.getString(request, "untreatedTimespanBegin", ""));
        mv.addObject("untreatedTimespanEnd", RequestUtil.getString(request, "untreatedTimespanEnd", ""));
        mv.addObject("theYear", RequestUtil.getString(request, "theYear", ""));
        mv.addObject("untreatedQuantityNotEqual", RequestUtil.getString(request, "untreatedQuantityNotEqual", ""));
        mv.addObject("responsibleDep", RequestUtil.getString(request, "responsibleDep", ""));
        return mv;
    }

    //..
    @RequestMapping("itemListQuery")
    @ResponseBody
    public JsonPageResult<?> itemListQuery(HttpServletRequest request, HttpServletResponse response) throws ParseException {
        return rdMaterialSummaryService.itemListQuery(request, response);
    }

    //..
    @RequestMapping("exportItemList")
    public void exportItemList(HttpServletRequest request, HttpServletResponse response) throws ParseException {
        rdMaterialSummaryService.exportItemList(request, response);
    }
}
