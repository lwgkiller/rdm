package com.redxun.rdmZhgl.core.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.rdmCommon.core.util.RdmConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.rdmZhgl.core.service.ZLAndFWOverviewService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * 专利和发明-报表控制层
 *
 * @author douhongli
 * @since 2021年6月4日14:06:59
 */
@RestController
@RequestMapping("/zhgl/core/ZLAndFW/report/")
public class ZLAndFMOverviewController {

    private static final Logger logger = LoggerFactory.getLogger(ZLAndFMOverviewController.class);
    @Resource
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Resource
    private RdmZhglUtil rdmZhglUtil;

    @Resource
    private ZLAndFWOverviewService zlAndFWOverviewService;

    /**
     * 获取专利和发明报表页面
     *
     * @param request
     *            请求
     * @param response
     *            响应
     * @return ModelAndView
     * @author douhongli
     * @since 2021年6月4日14:29:38
     */
    @RequestMapping("overview/page")
    public ModelAndView overviewPage(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = getModelAndView("/rdmZhgl/report/ZLAndFMOverview.jsp");
        modelAndView.addObject("lastMonth",
            XcmgProjectUtil.getPointMonthDateStr(XcmgProjectUtil.getNowLocalDateStr("yyyy-MM"), -1));
        modelAndView.addObject("maxYear", DateUtil.getCurYear());
        return modelAndView;
    }

    /**
     * 根据jsp路径返回ModelAndView
     *
     * @param jspPath
     *            jsp相对路径
     * @return ModelAndView
     */
    private ModelAndView getModelAndView(String jspPath) {
        ModelAndView mv = new ModelAndView(jspPath);
        String currentUserId = ContextUtil.getCurrentUserId();
        mv.addObject("currentUserId", currentUserId);
        Map<String, Object> params = new HashMap<>();
        params.put("userId", currentUserId);
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 返回当前登录人角色信息
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        // 返回当前用户是否是技术管理部负责人
        boolean isJsglbRespUser = rdmZhglUtil.judgeUserIsJsglbRespUser(currentUserId);
        mv.addObject("isJsglbRespUser", isJsglbRespUser);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        // 是否是专利工程师
        boolean isOperator = rdmZhglUtil.judgeIsPointRoleUser(currentUserId, RdmConst.ZLGCS);
        // 超级管理员
        boolean isAdmin = Objects.equals(currentUserId, "1");
        mv.addObject("isOperator", isOperator || isAdmin);
        return mv;
    }

    /**
     * 有效授权中国专利量
     * 
     * @param queryParam
     *            参数
     * @return JsonResult
     * @author douhongli
     * @since 2021年6月8日15:30:57
     */
    @RequestMapping(value = "authorizedPieChart")
    @ResponseBody
    public JsonResult authorizedPieChart(@RequestBody String queryParam) {
        return zlAndFWOverviewService.queryEffectiveAuthorizedPieChart(queryParam);
    }

    /**
     * 有效授权中国专利量-bar
     * 
     * @param queryParam
     *            参数
     * @return JsonResult
     * @author douhongli
     * @since 2021年6月15日11:13:18
     */
    @RequestMapping(value = "authorizedBarChart")
    @ResponseBody
    public JsonResult authorizedBarChart(@RequestBody String queryParam) {
        return zlAndFWOverviewService.queryEffectiveAuthorizedBarChart(queryParam);
    }

    /**
     * 中国专利申请情况
     * 
     * @param queryParam
     *            参数
     * @return JsonResult
     * @author douhongli
     * @since 2021年6月8日15:30:57
     */
    @RequestMapping(value = "patenApplyPieChart")
    @ResponseBody
    public JsonResult patenApplyPieChart(@RequestBody String queryParam) {
        return zlAndFWOverviewService.queryPatenApplyPieChart(queryParam);
    }

    /**
     * 中国专利申请情况-bar
     * 
     * @param queryParam
     *            参数
     * @return JsonResult
     * @author douhongli
     * @since 2021年6月15日16:14:39
     */
    @RequestMapping(value = "patenApplyBarChart")
    @ResponseBody
    public JsonResult patenApplyBarChart(@RequestBody String queryParam) {
        return zlAndFWOverviewService.queryPatenApplyBarChart(queryParam);
    }

    /**
     * 中国发明专利申请情况
     * 
     * @param queryParam
     *            参数
     * @return JsonResult
     * @author douhongli
     * @since 2021年6月8日15:30:57
     */
    @RequestMapping(value = "inventPatentApplyPieChart")
    @ResponseBody
    public JsonResult inventPatentApplyPieChart(@RequestBody String queryParam) {
        return zlAndFWOverviewService.queryInventAuthorizedApplyPieChart(queryParam);
    }
}
