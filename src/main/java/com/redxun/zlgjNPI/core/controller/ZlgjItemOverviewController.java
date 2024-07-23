package com.redxun.zlgjNPI.core.controller;

import com.alibaba.fastjson.JSONArray;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import com.redxun.zlgjNPI.core.manager.ZlgjItemOverviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 质量改进项目总览-报表
 *
 * @author douhongli
 * @since 2021年6月4日14:06:59
 */
@RestController
@RequestMapping("/zlgj/core/report/")
public class ZlgjItemOverviewController {

    private static final Logger logger = LoggerFactory.getLogger(ZlgjItemOverviewController.class);
    @Resource
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Resource
    private RdmZhglUtil rdmZhglUtil;

    @Resource
    private ZlgjItemOverviewService zlgjItemOverviewService;

    /**
     * 获取质量改进报表页面
     *
     * @param request  请求
     * @param response 响应
     * @return ModelAndView
     * @author douhongli
     * @since 2021年6月4日14:29:38
     */
    @RequestMapping("overview/page")
    public ModelAndView overviewPage(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = getModelAndView("/zlgjNPI/report/zlgjItemOverview.jsp");
        modelAndView.addObject("lastMonth",
                XcmgProjectUtil.getPointMonthDateStr(XcmgProjectUtil.getNowLocalDateStr("yyyy-MM"), -1));
        return modelAndView;
    }

    /**
     * 根据jsp路径返回ModelAndView
     *
     * @param jspPath jsp相对路径
     * @return ModelAndView
     */
    private ModelAndView getModelAndView(String jspPath) {
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 返回当前登录人角色信息
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        // 返回当前用户是否是技术管理部负责人
        boolean isJsglbRespUser = rdmZhglUtil.judgeUserIsJsglbRespUser(ContextUtil.getCurrentUserId());
        mv.addObject("isJsglbRespUser", isJsglbRespUser);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    /**
     * 各维度查询问题改进完成情况柱状图
     *
     * @return JsonResult
     * @author douhongli
     * @since 2021年6月5日15:22:41
     */
    @RequestMapping(value = "WTWidgetChart")
    @ResponseBody
    public JsonResult WTWidgetChart(HttpServletRequest request, @RequestBody String zlgjStr,
                                    HttpServletResponse response) {
        return zlgjItemOverviewService.queryChartData(zlgjStr);
    }

    @RequestMapping(value = "responsibleDept")
    @ResponseBody
    public JsonResult responsibleDeptChart(HttpServletRequest request, @RequestBody String zlgjStr,
                                           HttpServletResponse response) {
        return zlgjItemOverviewService.queryChartDataByDept(zlgjStr);
    }

    @RequestMapping(value = "typeDataDept")
    @ResponseBody
    public JsonResult typeDataDept(HttpServletRequest request, @RequestBody String zlgjStr,
                                   HttpServletResponse response) {
        return zlgjItemOverviewService.queryTypeDataByDept(zlgjStr);
    }

    @RequestMapping(value = "typeDataMonth")
    @ResponseBody
    public JsonResult typeDataMonth(HttpServletRequest request, @RequestBody String zlgjStr,
                                    HttpServletResponse response) {
        return zlgjItemOverviewService.queryDataByMonth(zlgjStr);
    }

    /**
     * 完成率仪表盘图
     *
     * @param zlgjStr 参数
     * @return JsonResult
     * @author douhongli
     * @since 2021年6月8日15:30:57
     */
    @RequestMapping(value = "percentage")
    @ResponseBody
    public JsonResult gaugeChart(@RequestBody String zlgjStr) {
        return zlgjItemOverviewService.queryGaugePercentage(zlgjStr);
    }

    @RequestMapping("queryZrrTime")
    @ResponseBody
    public JsonResult queryZrrTime(HttpServletRequest request) {
        return zlgjItemOverviewService.queryZrrTime(request);
    }


    //2022-10-27新增
    @RequestMapping(value = "perDayChart")
    @ResponseBody
    public JsonResult perDayChart(HttpServletRequest request, @RequestBody String zlgjStr,
                                    HttpServletResponse response) {
        return zlgjItemOverviewService.queryPerDayChart(zlgjStr);
    }
}
