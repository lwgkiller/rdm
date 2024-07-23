package com.redxun.strategicPlanning.core.controller;

import com.alibaba.fastjson.JSONArray;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.strategicPlanning.core.domain.ZlghZljc;
import com.redxun.strategicPlanning.core.domain.ZlghZlkt;
import com.redxun.strategicPlanning.core.domain.ZlghZyhd;
import com.redxun.strategicPlanning.core.domain.dto.ZlghZlktDto;
import com.redxun.strategicPlanning.core.domain.dto.ZlghZyhdDto;
import com.redxun.strategicPlanning.core.domain.vo.ParamsVo;
import com.redxun.strategicPlanning.core.service.ZlghDataService;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 战略规划关于战略举措，战略课题，主要活动的 控制层
 * @author douhongli
 */
@RestController
@RequestMapping("/strategicPlanning/core/zlghData/")
public class ZlghDataController {
    private static final Logger logger = LoggerFactory.getLogger(ZlghDataController.class);

    @Resource
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Resource
    private RdmZhglUtil rdmZhglUtil;
    @Resource
    private ZlghDataService zlghDataService;

    /**
     * 获取战略规划-战略举措页面
     * @param request 请求
     * @param response 相应
     * @return ModelAndView
     */
    @RequestMapping("zljc/page")
    public ModelAndView zljcPage(HttpServletRequest request, HttpServletResponse response){
        return getModelAndView("strategicPlanning/core/zlghZljc.jsp");
    }

    /**
     * 获取战略规划-战略课题页面
     * @param request 请求
     * @param response 相应
     * @return ModelAndView
     */
    @RequestMapping("zlkt/page")
    public ModelAndView zlktPage(HttpServletRequest request, HttpServletResponse response){
        return getModelAndView("strategicPlanning/core/zlghZlkt.jsp");
    }

    /**
     * 获取战略规划-战略课题-举措弹出页面
     * @param request 请求
     * @param response 相应
     * @return ModelAndView
     */
    @RequestMapping("zlkt/jcPage")
    public ModelAndView zlktJcPage(HttpServletRequest request, HttpServletResponse response){
        return getModelAndView("strategicPlanning/core/zlghZlkt-jc.jsp");
    }

    /**
     * 获取战略规划-战略课题-新增/编辑弹出页面
     * @param request 请求
     * @param response 相应
     * @return ModelAndView
     */
    @RequestMapping("zlkt/formPage")
    public ModelAndView zlktFormPage(HttpServletRequest request, HttpServletResponse response){
        return getModelAndView("strategicPlanning/core/zlghZlkt-form.jsp");
    }

    /**
     * 获取战略规划-主要活动页面
     * @param request 请求
     * @param response 相应
     * @return ModelAndView
     */
    @RequestMapping("zyhd/page")
    public ModelAndView zyhdPage(HttpServletRequest request, HttpServletResponse response){
        return getModelAndView("strategicPlanning/core/zlghZyhd.jsp");
    }

    /**
     * 获取战略规划-主要活动-课题弹出页面
     * @param request 请求
     * @param response 相应
     * @return ModelAndView
     */
    @RequestMapping("zyhd/ktPage")
    public ModelAndView zYhdKtPage(HttpServletRequest request, HttpServletResponse response){
        return getModelAndView("strategicPlanning/core/zlghZyhd-kt.jsp");
    }

    /**
     * 获取战略规划-主要活动-课题弹出页面
     * @param request 请求
     * @param response 相应
     * @return ModelAndView
     */
    @RequestMapping("zyhd/formPage")
    public ModelAndView zyhdFormPage(HttpServletRequest request, HttpServletResponse response){
        return getModelAndView("strategicPlanning/core/zlghZyhd-form.jsp");
    }

    /**
     * 根据jsp路径返回ModelAndView
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
//        List<JSONObject> kpiObject= zlghKPIDao.queryKPIList(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
//        mv.addObject("kpiObject",kpiObject);
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
     * 获取战略规划-战略举措列表数据
     * @param paramsVo 参数对象
     * @param request 请求
     * @return JsonPageResult
     */
    @RequestMapping("zljc/list")
    @ResponseBody
    public JsonPageResult<T> zljcList(ParamsVo paramsVo, HttpServletRequest request){
        return zlghDataService.selectZljcList(paramsVo, request, true);
    }

    /**
     * 战略举措列表数据全部
     * @return JsonPageResult
     * @author douhongli
     * @date 2021年5月26日13:55:35
     */
    @RequestMapping("zljc/listText")
    @ResponseBody
    public List<ZlghZljc> listZljcText(){
        return zlghDataService.listZljcText();
    }

    /**
     * 战略规划-批量保存、更新或删除战略举措
     * @param zlghZljcList 参数列表
     * @return JsonResult
     */
    @RequestMapping(value = "zljc/batchOptions")
    @ResponseBody
    public JsonResult zljcBatchOptions(@RequestBody List<ZlghZljc> zlghZljcList){
        return zlghDataService.zljcBatchOptions(zlghZljcList);
    }

    /**
     * 获取战略规划-战略课题列表数据
     * @param paramsVo 参数对象
     * @param request 请求
     * @return JsonPageResult
     */
    @RequestMapping("zlkt/list")
    @ResponseBody
    public JsonPageResult<T> zlktList(ParamsVo paramsVo, HttpServletRequest request){
        return zlghDataService.selectZlktList(paramsVo, request, true);
    }

    /**
     * 根据战略举措id查询战略课题列表数据
     * @return List
     * @author douhongli
     * @date 2021年5月26日13:55:35
     */
    @RequestMapping("zlkt/listText")
    @ResponseBody
    public List<ZlghZlkt> listZlktText(String zljcId){
        return zlghDataService.listZlktByZljcId(zljcId);
    }

    /**
     * 战略规划-批量保存、更新或删除战略课题
     * @param ZlghZlktList 参数列表
     * @return JsonResult
     */
    @RequestMapping(value = "zlkt/batchOptions")
    @ResponseBody
    public JsonResult zlktBatchOptions(@RequestBody List<ZlghZlktDto> ZlghZlktList){
        return zlghDataService.zlktBatchOptions(ZlghZlktList);
    }

    /**
     * 获取战略规划-战略课题列表数据
     * @param paramsVo 参数对象
     * @param request 请求
     * @return JsonPageResult
     */
    @RequestMapping("zyhd/list")
    @ResponseBody
    public JsonPageResult<T> zyhdList(ParamsVo paramsVo, HttpServletRequest request){
        return zlghDataService.selectZyhdList(paramsVo, request, true);
    }

    /**
     * 战略规划-批量保存、更新或删除战略课题
     * @param zlghZyhdList 参数列表
     * @return JsonResult
     */
    @RequestMapping(value = "zyhd/batchOptions")
    @ResponseBody
    public JsonResult zyhdBatchOptions(@RequestBody List<ZlghZyhdDto> zlghZyhdList){
        return zlghDataService.zyhdBatchOptions(zlghZyhdList);
    }

}
