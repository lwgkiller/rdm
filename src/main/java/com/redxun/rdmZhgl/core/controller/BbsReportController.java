
package com.redxun.rdmZhgl.core.controller;


import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.StringUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.rdmZhgl.core.service.BbsReportService;
import com.redxun.saweb.context.ContextUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 论坛报表
 *
 * @author zz
 */
@Controller
@RequestMapping("/rdmZhgl/core/bbs/report/")
public class BbsReportController {
    @Resource
    BbsReportService bbsReportService;
    /**
     * 获取帖子分类统计
     * */
    @RequestMapping("bbsTypeReport")
    @ResponseBody
    public JSONObject getBbsTypeReport(HttpServletRequest request, HttpServletResponse response) {
        return bbsReportService.getBbsTypeReport(request);
    }
    /**
     * 获取改进提案关键节点数据
     * */
    @RequestMapping("bbsDataReport")
    @ResponseBody
    public JSONObject getBbsDataReport(HttpServletRequest request, HttpServletResponse response) {
        return bbsReportService.getBbsDataReport(request);
    }
    /**
     * 获取改进提案关键节点数据
     * */
    @RequestMapping("bbsGjtaRankReport")
    @ResponseBody
    public JSONObject getBbsGjtaRankReport(HttpServletRequest request, HttpServletResponse response) {
        return bbsReportService.getBbsGjtaRankReport(request);
    }
    /**
     * 发帖数/人（TOP10）
     * */
    @RequestMapping("bbsPostRankReport")
    @ResponseBody
    public JSONObject getBbsPostRankReport(HttpServletRequest request, HttpServletResponse response) {
        return bbsReportService.getBbsPostRankReport(request);
    }
    /**
     * 列表
     */
    @RequestMapping("listPage")
    public ModelAndView getListAllPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/report/bbsListPage.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        JSONObject paramJson = new JSONObject();
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        String startTime = request.getParameter("bbs_startTime");
        String endTime = request.getParameter("bbs_endTime");
        String reportType = request.getParameter("reportType");
        String barName = request.getParameter("barName");
        String seriesName = request.getParameter("seriesName");
        if(StringUtil.isNotEmpty(startTime)){
            startTime += " 00:00:00";
        }
        if(StringUtil.isNotEmpty(endTime)){
            endTime += " 23:59:59";
        }
        paramJson.put("reportType",reportType);
        paramJson.put("barName",barName);
        paramJson.put("startTime",startTime);
        paramJson.put("endTime",endTime);
        paramJson.put("seriesName",seriesName);
        mv.addObject("paramJson", paramJson);
        return mv;
    }

    @RequestMapping(value = "bbsList")
    @ResponseBody
    public JsonPageResult<?> getBBsList(HttpServletRequest request, HttpServletResponse response) {
        return bbsReportService.getBbsList(request);
    }

}
