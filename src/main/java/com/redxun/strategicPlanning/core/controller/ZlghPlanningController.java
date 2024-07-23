package com.redxun.strategicPlanning.core.controller;

import com.alibaba.fastjson.JSONArray;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.strategicPlanning.core.dao.ZlghPlanningDao;
import com.redxun.strategicPlanning.core.domain.dto.ZlghDto;
import com.redxun.strategicPlanning.core.domain.dto.ZlghZyhdDto;
import com.redxun.strategicPlanning.core.domain.vo.ParamsVo;
import com.redxun.strategicPlanning.core.service.ZlghPlanningService;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.poi.ss.formula.functions.T;
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
 * 战略规划整体展示的控制层
 * @author douhongli
 */
@RestController
@RequestMapping("/strategicPlanning/core/zlghPlanning/")
public class ZlghPlanningController {
    private static final Logger logger = LoggerFactory.getLogger(ZlghPlanningController.class);

    @Resource
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Resource
    private RdmZhglUtil rdmZhglUtil;
    @Resource
    private ZlghPlanningService zlghPlanningService;

    /**
     * 获取战略规划页面
     * @param request 请求
     * @param response 相应
     * @return ModelAndView
     */
    @RequestMapping("zlgh/page")
    public ModelAndView zljcPage(HttpServletRequest request, HttpServletResponse response){
        return getModelAndView("strategicPlanning/core/zlghHdnf.jsp");
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
     * 获取战略规划-战略课题列表数据
     * @param paramsVo 参数对象
     * @param request 请求
     * @return JsonPageResult
     * @author douhongli
     * @date 2021年5月27日15:14:55
     */
    @RequestMapping("zlgh/list")
    @ResponseBody
    public JsonPageResult<T> zlghList(ParamsVo paramsVo, HttpServletRequest request){
        return zlghPlanningService.selecZlghList(paramsVo, request, true);
    }

    /**
     * 战略规划-批量保存、更新或删除战略课题
     * @param zlghList 参数列表
     * @return JsonResult
     * @author douhongli
     * @date 2021年5月27日15:15:09
     */
    @RequestMapping(value = "zlgh/batchOptions")
    @ResponseBody
    public JsonResult zlghBatchOptions(@RequestBody List<ZlghDto> zlghList){
            return zlghPlanningService.zlghBatchOptions(zlghList);
    }
}
