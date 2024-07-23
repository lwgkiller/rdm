package com.redxun.strategicPlanning.core.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.strategicPlanning.core.dao.ZlghKPIDao;
import com.redxun.strategicPlanning.core.domain.ZlghKpi;
import com.redxun.strategicPlanning.core.domain.vo.ParamsVo;
import com.redxun.strategicPlanning.core.service.ZlghKPIService;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
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
 * @author pandong
 */
@RestController
@RequestMapping("/strategicplanning/core/kpilist/")
public class KPIListController {

    @Resource
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Resource
    private RdmZhglUtil rdmZhglUtil;
    @Resource
    private ZlghKPIService zlghKPIService;
    @Resource
    private ZlghKPIDao zlghKPIDao;


    /**
     * 获取列表页面
     */
    @RequestMapping("indexListPage")
    public ModelAndView getIndexListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "zlghKPI/core/zlghKPIList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 返回当前登录人角色信息
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        List<JSONObject> kpiObject= zlghKPIDao.queryKPIList(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        mv.addObject("kpiObject",kpiObject);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        // 返回当前用户是否是技术管理部负责人
        boolean isJsglbRespUser = rdmZhglUtil.judgeUserIsJsglbRespUser(ContextUtil.getCurrentUserId());
        mv.addObject("isJsglbRespUser", isJsglbRespUser);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        //是否是战略规划专员
        boolean isZLGHZY = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), "战略规划专员");
        mv.addObject("isZLGHZY",isZLGHZY);
        return mv;
    }

    /**
     * 获取列表数据
     */
    @RequestMapping("indexList")
    @ResponseBody
    public JsonPageResult<?> indexList(ParamsVo paramsVo, HttpServletRequest request) {
        return zlghKPIService.getKPIList(request, paramsVo,true);
    }

    @RequestMapping(value = "saveKpi")
    @ResponseBody
    public JsonResult saveKpi(@RequestBody List<ZlghKpi> zlghKpiList) {
        return zlghKPIService.batchSaveOrUpdateKpiData(zlghKpiList);
    }


    /*==============================================我是美丽的分割线==============================================*/
    //关键绩效指标名称管理
    /**
     * 获取列表页面
     */
    @RequestMapping("kpiNameListPage")
    public ModelAndView getKpiNameListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "zlghKPI/core/zlghKPINameList.jsp";
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

        //是否是战略规划专员
        boolean isZLGHZY = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), "战略规划专员");
        mv.addObject("isZLGHZY",isZLGHZY);
        return mv;
    }

    /**
     * 获取列表数据
     */
    @RequestMapping("kpiNameList")
    @ResponseBody
    public JsonPageResult<?> kpiNameList(HttpServletRequest request, HttpServletResponse response) {
        return zlghKPIService.getKpiNameList(request,response,true);
    }

    /**
     *
     * @param changeGridDataStr
     * @return
     */
    @RequestMapping(value = "saveKpiName")
    @ResponseBody
    public JsonResult saveKpiName(@RequestBody String changeGridDataStr) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(changeGridDataStr)) {
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        zlghKPIService.saveKpiName(result, changeGridDataStr);
        return result;
    }
}
