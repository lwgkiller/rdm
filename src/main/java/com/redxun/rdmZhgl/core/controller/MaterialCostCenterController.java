package com.redxun.rdmZhgl.core.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.rdmZhgl.core.service.MaterialCostCenterService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;

/**
 * @author zz 资产相关类
 */
@Controller
@RequestMapping("/rdmZhgl/core/material/costCenter/")
public class MaterialCostCenterController {
    @Resource
    private MaterialCostCenterService materialCostCenterService;

    /**
     * 获取资产列表页
     */
    @RequestMapping("costCenterListPage")
    public ModelAndView getAssetsListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "rdmZhgl/core/MaterialCostCenterList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    @RequestMapping("costCenterList")
    @ResponseBody
    public JsonPageResult<?> assetCostCenterManager(HttpServletRequest request, HttpServletResponse response) {
        return materialCostCenterService.getCostCenterList(request, response, true);
    }
    /**
     * 同步成本中心
     **/
    @RequestMapping("asyncCostCenter")
    @ResponseBody
    public JsonResult asyncCostCenter(HttpServletRequest request, HttpServletResponse response){
        return materialCostCenterService.asyncCostCenter(request, response);
    }
    /**
     * 获取成本中心
     **/
    @RequestMapping("centerCostDic")
    @ResponseBody
    public JSONArray getCenterCostDic(HttpServletRequest request, HttpServletResponse response){
        return materialCostCenterService.getCostCenterDic(request, response);
    }
}
