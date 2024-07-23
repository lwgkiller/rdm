package com.redxun.wwrz.core.controller;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.redxun.wwrz.core.dao.WwrzStandardMngDao;
import com.redxun.wwrz.core.service.WwrzStandardMngService;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.StringUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.wwrz.core.dao.WwrzCompanyDao;
import com.redxun.wwrz.core.service.WwrzCompanyService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zz

 * */
@Controller
@RequestMapping("/wwrz/core/standardMng/")
public class WwrzStandardMngController {
    @Resource
    WwrzStandardMngService wwrzStandardMngService;
    @Resource
    WwrzStandardMngDao wwrzStandardMngDao;
    @Resource
    CommonInfoManager commonInfoManager;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    /**
     * 获取列表页面
     * */
    @RequestMapping("listPage")
    public ModelAndView getListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "wwrz/core/wwrzStandardMngList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        JSONObject adminJson = commonInfoManager.hasPermission("WWRZ-GLY");
        mv.addObject("permission",adminJson.getBoolean("WWRZ-GLY")||"admin".equals(ContextUtil.getCurrentUser().getUserNo()));
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        String standardType = request.getParameter("standardType");
        mv.addObject("standardType", standardType);
        Map<String, Object> params = new HashMap<>();
        params.put("userId", currentUser.getUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 角色信息
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        List<Map<String, Object>> currentUserZJ = xcmgProjectOtherDao.queryUserZJ(params);
        JSONArray userZJJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserZJ);
        mv.addObject("currentUserZJ", userZJJsonArray);
        mv.addObject("tabName", "JS");
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }
    @RequestMapping("editPage")
    public ModelAndView getEditPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "wwrz/core/wwrzStandardMngEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String action = request.getParameter("action");
        String id = request.getParameter("id");
        String standardType = request.getParameter("standardType");
        JSONObject applyObj = new JSONObject();
        if(StringUtil.isNotEmpty(id)){
            applyObj = wwrzStandardMngDao.getObjectById(id);
        }else{
            applyObj.put("standardType",standardType);
        }
        mv.addObject("action",action);
        mv.addObject("applyObj", applyObj);
        return mv;
    }
    @RequestMapping("listData")
    @ResponseBody
    public JsonPageResult<?> getListData(HttpServletRequest request, HttpServletResponse response) {
        return wwrzStandardMngService.query(request);
    }
    @RequestMapping("save")
    @ResponseBody
    public JSONObject save(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        JSONObject resultJSON = null;
        if(StringUtil.isEmpty(id)){
            resultJSON = wwrzStandardMngService.add(request);
        }else{
            resultJSON = wwrzStandardMngService.update(request);
        }
        return resultJSON;
    }
    @RequestMapping("remove")
    @ResponseBody
    public JSONObject remove(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = new JSONObject();
        resultJSON = wwrzStandardMngService.remove(request);
        return resultJSON;
    }
}
