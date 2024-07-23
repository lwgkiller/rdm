package com.redxun.portrait.core.controller;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.portrait.core.manager.PortraitBbsManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.portrait.core.manager.PortraitPatentManager;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.context.ContextUtil;

import java.util.List;
import java.util.Map;

/**
 * @author zhangzhen
 */
@RestController
@RequestMapping("/portrait/bbs/")
public class PortraitBbsController {
    @Resource
    PortraitBbsManager portraitBbsManager;
    @Resource
    CommonInfoManager commonInfoManager;
    /**
     * 获取列表页面
     * */
    @RequestMapping("listPage")
    public ModelAndView getListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "portrait/bbs/portraitBbsList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        JSONObject resultJson = commonInfoManager.hasPermission("bbsAdmin");
        mv.addObject("permission",resultJson.getBoolean("HX-GLY")||resultJson.getBoolean("bbsAdmin")||"admin".equals(ContextUtil.getCurrentUser().getUserNo()));
        return mv;
    }
    /**
     * 获取列表数据
     * */
    @RequestMapping("listData")
    @ResponseBody
    public JsonPageResult<?> getListData(HttpServletRequest request, HttpServletResponse response) {
        return portraitBbsManager.query(request);
    }
    @RequestMapping("asyncBbs")
    @ResponseBody
    public JSONObject asyncBbs(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = portraitBbsManager.asyncBbs();
        return resultJSON;
    }
    /**
     * 获取列表数据
     * */
    @RequestMapping("bbsPersonList")
    @ResponseBody
    public List<JSONObject> getBbsPersonList(HttpServletRequest request, HttpServletResponse response) {
        String userId = request.getParameter("userId");
        String reportYear = request.getParameter("reportYear");
        JSONObject paramJson = new JSONObject();
        paramJson.put("userId",userId);
        paramJson.put("reportYear",reportYear);
        return portraitBbsManager.getPersonBbsList(paramJson);
    }
}
