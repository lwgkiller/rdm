package com.redxun.wwrz.core.controller;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.core.util.DateUtil;
import com.redxun.wwrz.core.dao.WwrzMoneyDao;
import com.redxun.wwrz.core.service.WwrzMoneyService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.StringUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.context.ContextUtil;

import java.util.Date;

/**
 * @author zz

 * */
@Controller
@RequestMapping("/wwrz/core/money/")
public class WwrzMoneyController {
    @Resource
    WwrzMoneyService wwrzMoneyService;
    @Resource
    WwrzMoneyDao wwrzMoneyDao;
    @Resource
    CommonInfoManager commonInfoManager;
    /**
     * 获取列表页面
     * */
    @RequestMapping("listPage")
    public ModelAndView getListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "wwrz/core/wwrzMoneyList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        JSONObject adminJson = commonInfoManager.hasPermission("WWRZ-CJGLY");
        mv.addObject("permission",adminJson.getBoolean("WWRZ-CJGLY"));
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        return mv;
    }
    @RequestMapping("editPage")
    public ModelAndView getEditPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "wwrz/core/wwrzMoneyEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String action = request.getParameter("action");
        String id = request.getParameter("id");
        JSONObject applyObj = new JSONObject();
        if(StringUtil.isNotEmpty(id)){
            applyObj = wwrzMoneyDao.getObjectById(id);
            if (applyObj.get("paymentDate") != null) {
                applyObj.put("paymentDate",
                        DateUtil.formatDate((Date)applyObj.get("paymentDate"), "yyyy-MM-dd"));
            }
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
        return wwrzMoneyService.query(request);
    }
    @RequestMapping("getItemObject")
    @ResponseBody
    public JSONObject getItemObject(HttpServletRequest request, HttpServletResponse response) {
        return wwrzMoneyService.getItemObject(request);
    }
    @RequestMapping("save")
    @ResponseBody
    public JSONObject save(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        JSONObject resultJSON = null;
        if(StringUtil.isEmpty(id)){
            resultJSON = wwrzMoneyService.add(request);
        }else{
            resultJSON = wwrzMoneyService.update(request);
        }
        return resultJSON;
    }
    @RequestMapping("remove")
    @ResponseBody
    public JSONObject remove(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = new JSONObject();
        resultJSON = wwrzMoneyService.remove(request);
        return resultJSON;
    }
    @RequestMapping("exportExcel")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        wwrzMoneyService.exportBaseInfoExcel(request, response);
    }
    @RequestMapping("finish")
    @ResponseBody
    public JSONObject finish(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = new JSONObject();
        resultJSON = wwrzMoneyService.finish(request);
        return resultJSON;
    }
    @RequestMapping(value = "isEnd", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject isEnd(HttpServletRequest request, @RequestBody String postData, HttpServletResponse response)
            throws Exception {
        JSONObject postDataJson = new JSONObject();
        if (StringUtils.isNotBlank(postData)) {
            postDataJson = JSONObject.parseObject(postData);
        }
        return wwrzMoneyService.isEnd(postDataJson);
    }
}
