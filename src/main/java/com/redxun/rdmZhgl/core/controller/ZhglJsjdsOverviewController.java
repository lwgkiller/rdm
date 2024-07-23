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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.rdmZhgl.core.service.ZhglJsjdsOverviewService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * 专利和发明-报表控制层
 *
 * @author douhongli
 * @since 2021年6月16日17:27:30
 */
@RestController
@RequestMapping("/zhgl/core/jsjds/report/")
public class ZhglJsjdsOverviewController {

    private static final Logger logger = LoggerFactory.getLogger(ZhglJsjdsOverviewController.class);
    @Resource
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Resource
    private RdmZhglUtil rdmZhglUtil;

    @Resource
    private ZhglJsjdsOverviewService zhglJsjdsOverviewService;

    /**
     * 获取技术交底书报表页面
     *
     * @param request  请求
     * @param response 响应
     * @return ModelAndView
     * @author douhongli
     * @since 2021年6月4日14:29:38
     */
    @RequestMapping("overview/page")
    public ModelAndView overviewPage(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = getModelAndView("/rdmZhgl/report/jsjdsOverview.jsp");
        modelAndView.addObject("lastMonth",
                XcmgProjectUtil.getPointMonthDateStr(XcmgProjectUtil.getNowLocalDateStr("yyyy-MM"), -1));
        modelAndView.addObject("maxYear", DateUtil.getCurYear());
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
        //是否是专利工程师
        boolean isOperator = rdmZhglUtil.judgeIsPointRoleUser(currentUserId, RdmConst.ZLGCS);
        // 超级管理员
        boolean isAdmin = Objects.equals(currentUserId, "1");
        mv.addObject("isOperator",isOperator || isAdmin);
        return mv;
    }

    /**
     * 各部门提交技术交底书数量
     * @param postStr 参数
     * @return JsonResult
     * @author douhongli
     * @since 2021年6月16日20:00:48
     */
    @RequestMapping(value = "queryJsjdsByDept")
    @ResponseBody
    public JsonResult queryJsjdsByDept(@RequestBody String postStr) {
        JSONObject queryParam=JSONObject.parseObject(postStr);
        return zhglJsjdsOverviewService.queryJsjdsByDept(queryParam);
    }

    /**
     * 各部门提交技术发明类交底书数量
     * @param postStr 参数
     * @return JsonResult
     * @author douhongli
     * @since 2021年6月17日14:37:06
     */
    @RequestMapping(value = "queryInventJsjdsByDept")
    @ResponseBody
    public JsonResult queryInventJsjdsByDept(@RequestBody String postStr) {
        JSONObject queryParam=JSONObject.parseObject(postStr);
        return zhglJsjdsOverviewService.queryInventJsjdsByDept(queryParam);
    }

    /**
     * 技术交底书计划数报表
     * @param param 参数
     * @return JsonResult
     * @author douhongli
     * @since 2021年6月17日15:17:07
     */
    @RequestMapping(value = "queryJsjdsPlanBarChart")
    @ResponseBody
    public JsonResult queryJsjdsPlanBarBarChart(@Validated @RequestBody JSONObject param) {
        return zhglJsjdsOverviewService.queryJsjdsPlanBarBarChart(param);
    }

    /**
     * 发明类交底书计划数报表
     * @param param 参数
     * @return JsonResult
     * @author zwt
     * @since
     */
    @RequestMapping(value = "queryInventJsjdsPlanBarChart")
    @ResponseBody
    public JsonResult queryInventJsjdsPlanBarChart(@Validated @RequestBody JSONObject param) {
        return zhglJsjdsOverviewService.queryInventJsjdsPlanBarChart(param);
    }

}
