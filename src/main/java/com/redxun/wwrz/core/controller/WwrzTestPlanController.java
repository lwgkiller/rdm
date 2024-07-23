package com.redxun.wwrz.core.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.org.api.model.IUser;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.wwrz.core.dao.WwrzTestPlanDao;
import com.redxun.wwrz.core.service.WwrzTestPlanService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.StringUtil;
import com.redxun.gkgf.core.dao.GkgfPlateDao;
import com.redxun.gkgf.core.manager.GkgfPlateManager;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.context.ContextUtil;

import java.util.List;

/**
 * @author zz

 * */
@Controller
@RequestMapping("/wwrz/core/testPlan/")
public class WwrzTestPlanController {
    @Resource
    WwrzTestPlanService wwrzTestPlanService;
    @Resource
    WwrzTestPlanDao wwrzTestPlanDao;
    @Resource
    CommonInfoManager commonInfoManager;
    /**
     * 获取列表页面
     * */
    @RequestMapping("listPage")
    public ModelAndView getListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "wwrz/core/wwrzTestPlanList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        JSONObject adminJson = commonInfoManager.hasPermission("WWRZ-GLY");
        mv.addObject("admin".equals(ContextUtil.getCurrentUser().getUserNo()));
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        return mv;
    }
    @RequestMapping("editPage")
    public ModelAndView getEditPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "wwrz/core/wwrzTestPlanEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String action = request.getParameter("action");
        String id = request.getParameter("id");
        JSONObject applyObj = new JSONObject();
        if(StringUtil.isNotEmpty(id)){
            applyObj = wwrzTestPlanDao.getObjectById(id);
        }else{
            applyObj.put("charger",ContextUtil.getCurrentUserId());
            applyObj.put("chargerName",ContextUtil.getCurrentUser().getFullname());
            applyObj.put("deptId",ContextUtil.getCurrentUser().getMainGroupId());
            applyObj.put("deptName",ContextUtil.getCurrentUser().getMainGroupName());
        }
        mv.addObject("action",action);
        mv.addObject("applyObj", applyObj);
        return mv;
    }
    @RequestMapping("listData")
    @ResponseBody
    public JsonPageResult<?> getListData(HttpServletRequest request, HttpServletResponse response) {
        return wwrzTestPlanService.query(request);
    }
    @RequestMapping("getItemObject")
    @ResponseBody
    public JSONObject getItemObject(HttpServletRequest request, HttpServletResponse response) {
        return wwrzTestPlanService.getItemObject(request);
    }
    @RequestMapping("save")
    @ResponseBody
    public JSONObject save(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        JSONObject resultJSON = null;
        if(StringUtil.isEmpty(id)){
            resultJSON = wwrzTestPlanService.add(request);
        }else{
            resultJSON = wwrzTestPlanService.update(request);
        }
        return resultJSON;
    }
    @RequestMapping("remove")
    @ResponseBody
    public JSONObject remove(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = new JSONObject();
        resultJSON = wwrzTestPlanService.remove(request);
        return resultJSON;
    }
    @RequestMapping("exportExcel")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        wwrzTestPlanService.exportBaseInfoExcel(request, response);
    }
    @RequestMapping("planList")
    @ResponseBody
    public List<JSONObject> getPlanList(HttpServletRequest request, HttpServletResponse response) {
        return wwrzTestPlanService.getPlanList(request);
    }

}
