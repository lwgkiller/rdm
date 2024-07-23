package com.redxun.rdmZhgl.core.controller;

import com.alibaba.fastjson.JSONArray;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.domain.ZhglJsjdsPlan;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.rdmZhgl.core.service.ZhglJsjdsPlanService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.strategicPlanning.core.domain.dto.ZlghDto;
import com.redxun.strategicPlanning.core.domain.vo.ParamsVo;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
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
import java.util.Objects;

/**
 * 技术交底书计划展示的控制层
 * @author douhongli
 * @since 2021年6月9日15:44:53
 */
@RestController
@RequestMapping("/zhgl/core/jsjds/plan/")
public class ZhglJsjdsPlanController {
    private static final Logger logger = LoggerFactory.getLogger(ZhglJsjdsPlanController.class);

    @Resource
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Resource
    private RdmZhglUtil rdmZhglUtil;
    @Resource
    private ZhglJsjdsPlanService zhglJsjdsPlanService;

    /**
     * 获取技术交底书计划页面
     * @param request 请求
     * @param response 相应
     * @return ModelAndView
     * @author douhongli
     * @since 2021年6月9日17:33:03
     */
    @RequestMapping("page")
    public ModelAndView zljcPage(HttpServletRequest request, HttpServletResponse response){
        return getModelAndView("rdmZhgl/core/ZhglJsjdsPlan.jsp");
    }

    /**
     * 根据jsp路径返回ModelAndView
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
     * 获取技术交底书计划列表数据
     * @param paramsVo 参数对象
     * @param request 请求
     * @return JsonPageResult
     * @author douhongli
     * @date 2021年6月9日17:36:20
     */
    @RequestMapping("list")
    @ResponseBody
    public JsonPageResult<T> list(ParamsVo paramsVo, HttpServletRequest request){
        return zhglJsjdsPlanService.selecList(paramsVo, request, true);
    }

    /**
     * 技术交底书计划-批量保存、更新或删除战略课题
     * @param zhglJsjdsPlans 参数列表
     * @return JsonResult
     * @author douhongli
     * @date 2021年6月9日17:56:09
     */
    @RequestMapping(value = "batchOptions")
    @ResponseBody
    public JsonResult batchOptions(@Validated @RequestBody List<ZhglJsjdsPlan> zhglJsjdsPlans){
            return zhglJsjdsPlanService.batchOptions(zhglJsjdsPlans);
    }
}
