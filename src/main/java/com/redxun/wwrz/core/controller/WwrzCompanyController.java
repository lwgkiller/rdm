package com.redxun.wwrz.core.controller;


import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.wwrz.core.dao.WwrzCompanyDao;
import com.redxun.wwrz.core.service.WwrzCompanyService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.context.ContextUtil;

/**
 * @author zz

 * */
@Controller
@RequestMapping("/wwrz/core/company/")
public class WwrzCompanyController {
    @Resource
    WwrzCompanyService wwrzCompanyService;
    @Resource
    WwrzCompanyDao wwrzCompanyDao;
    @Resource
    CommonInfoManager commonInfoManager;
    /**
     * 获取列表页面
     * */
    @RequestMapping("listPage")
    public ModelAndView getListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "wwrz/core/wwrzCompanyList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        JSONObject adminJson = commonInfoManager.hasPermission("WWRZ-CJGLY");
        mv.addObject("permission",adminJson.getBoolean("WWRZ-CJGLY"));
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        return mv;
    }
    @RequestMapping("editPage")
    public ModelAndView getEditPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "wwrz/core/wwrzCompanyEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String action = request.getParameter("action");
        String id = request.getParameter("id");
        JSONObject applyObj = new JSONObject();
        if(StringUtil.isNotEmpty(id)){
            applyObj = wwrzCompanyDao.getObjectById(id);
        }
        mv.addObject("action",action);
        mv.addObject("applyObj", applyObj);
        return mv;
    }
    @RequestMapping("listData")
    @ResponseBody
    public JsonPageResult<?> getListData(HttpServletRequest request, HttpServletResponse response) {
        return wwrzCompanyService.query(request);
    }
    @RequestMapping("save")
    @ResponseBody
    public JSONObject save(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        JSONObject resultJSON = null;
        if(StringUtil.isEmpty(id)){
            resultJSON = wwrzCompanyService.add(request);
        }else{
            resultJSON = wwrzCompanyService.update(request);
        }
        return resultJSON;
    }
    @RequestMapping("remove")
    @ResponseBody
    public JSONObject remove(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = new JSONObject();
        resultJSON = wwrzCompanyService.remove(request);
        return resultJSON;
    }
    @RequestMapping("listCompany")
    @ResponseBody
    public List<JSONObject> getListCompany(HttpServletRequest request, HttpServletResponse response) {
        return wwrzCompanyDao.getCompanyList();
    }
}
