package com.redxun.standardManager.report.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.standardManager.core.util.StandardConstant;
import com.redxun.standardManager.core.util.StandardManagerUtil;
import com.redxun.standardManager.report.manager.StandardReportManager;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/standardManager/report/standard/")
public class StandardReportController extends GenericController {
    @Autowired
    private StandardReportManager standardReportManager;

    @RequestMapping("overview")
    public ModelAndView overview(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = getPathView(request);
        String webappName=WebAppUtil.getProperty("webappName","rdm");
        String systemCategoryValue=StandardConstant.SYSTEMCATEGORY_GL;
        if ("rdm".equalsIgnoreCase(webappName)&& !StandardManagerUtil.judgeGLNetwork(request)) {
            systemCategoryValue = StandardConstant.SYSTEMCATEGORY_JS;
        }
        mv.addObject("systemCategoryValue",systemCategoryValue);
        return mv;
    }

    @RequestMapping(value = "queryPublishChart", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject queryPublishChart(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String publishTimeFrom = RequestUtil.getString(request, "publishTimeFrom");
        String publishTimeTo = RequestUtil.getString(request, "publishTimeTo");
        String systemCategoryId = RequestUtil.getString(request, "systemCategoryId");
        return standardReportManager.queryPublishChart(publishTimeFrom, publishTimeTo, systemCategoryId);
    }

    @RequestMapping(value = "queryCategoryChart", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject queryCategoryChart(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String systemCategoryId = RequestUtil.getString(request, "systemCategoryId");
        return standardReportManager.queryCategoryChart(systemCategoryId);
    }

    @RequestMapping(value = "queryStandardCheckChart", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject queryPreviewChart(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String systemCategoryId = RequestUtil.getString(request, "systemCategoryId");
        String timeFrom = RequestUtil.getString(request, "timeFrom");
        String timeTo = RequestUtil.getString(request, "timeTo");
        String checkCategoryId = RequestUtil.getString(request, "checkCategoryId");

        return standardReportManager.queryStandardCheckChart(systemCategoryId, timeFrom, timeTo, checkCategoryId);
    }

    @RequestMapping(value = "queryDepCheckStandardChart", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject queryDepCheckStandardChart(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String systemCategoryId = RequestUtil.getString(request, "systemCategoryId");
        String timeFrom = RequestUtil.getString(request, "timeFrom");
        String timeTo = RequestUtil.getString(request, "timeTo");
        String checkCategoryId = RequestUtil.getString(request, "checkCategoryId");

        return standardReportManager.queryDepCheckStandardChart(systemCategoryId, timeFrom, timeTo, checkCategoryId);
    }

    @RequestMapping("groupByPublish")
    public ModelAndView groupByPublishPage(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = getPathView(request);
        String webappName=WebAppUtil.getProperty("webappName","rdm");
        String systemCategoryValue=StandardConstant.SYSTEMCATEGORY_GL;
        if ("rdm".equalsIgnoreCase(webappName)&& !StandardManagerUtil.judgeGLNetwork(request)) {
            systemCategoryValue = StandardConstant.SYSTEMCATEGORY_JS;
        }
        mv.addObject("systemCategoryValue",systemCategoryValue);
        return mv;
    }

    // 标准按发布日期统计
    @RequestMapping("groupByPublishQuery")
    @ResponseBody
    public JsonPageResult<?> groupByPublishQuery(HttpServletRequest request, HttpServletResponse response) {
        return standardReportManager.groupByPublish(request);
    }

    // 标准按发布日期统计导出
    @RequestMapping("exportGroupByPublish")
    public void exportGroupByPublish(HttpServletRequest request, HttpServletResponse response) {
        standardReportManager.exportGroupByPublish(request, response);
    }


    @RequestMapping("groupBySystem")
    public ModelAndView groupBySystemPage(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = getPathView(request);
        String webappName=WebAppUtil.getProperty("webappName","rdm");
        String systemCategoryValue=StandardConstant.SYSTEMCATEGORY_GL;
        if ("rdm".equalsIgnoreCase(webappName)&& !StandardManagerUtil.judgeGLNetwork(request)) {
            systemCategoryValue = StandardConstant.SYSTEMCATEGORY_JS;
        }
        mv.addObject("systemCategoryValue",systemCategoryValue);
        return mv;
    }

    // 标准按体系统计
    @RequestMapping("groupBySystemQuery")
    @ResponseBody
    public JsonPageResult<?> groupBySystemQuery(HttpServletRequest request, HttpServletResponse response) {
        return standardReportManager.groupBySystem(request);
    }

    // 标准按体系统计导出
    @RequestMapping("exportGroupBySystem")
    public void exportGroupBySystem(HttpServletRequest request, HttpServletResponse response) {
        standardReportManager.exportGroupBySystem(request, response);
    }

    @RequestMapping("groupByProject")
    public ModelAndView groupByProject(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "/standardManager/report/standardByProject.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String webappName=WebAppUtil.getProperty("webappName","rdm");
        String systemCategoryValue=StandardConstant.SYSTEMCATEGORY_GL;
        if ("rdm".equalsIgnoreCase(webappName)&& !StandardManagerUtil.judgeGLNetwork(request)) {
            systemCategoryValue = StandardConstant.SYSTEMCATEGORY_JS;
        }
        mv.addObject("systemCategoryValue",systemCategoryValue);
        return mv;
    }
    // 标准按项目统计
    @RequestMapping("groupByProjectQuery")
    @ResponseBody
    public JsonPageResult<?> groupByProjectQuery(HttpServletRequest request, HttpServletResponse response) {
        return standardReportManager.groupByProject(request);
    }

    // 标准按项目导出
    @RequestMapping("exportGroupByProject")
    public void exportGroupByProject(HttpServletRequest request, HttpServletResponse response) {
        standardReportManager.exportGroupByProject(request, response);
    }
}
