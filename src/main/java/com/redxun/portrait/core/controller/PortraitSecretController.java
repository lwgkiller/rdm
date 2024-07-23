package com.redxun.portrait.core.controller;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.portrait.core.manager.PortraitSecretManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.portrait.core.manager.PortraitBbsManager;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.context.ContextUtil;

import java.util.List;

/**
 * @author zhangzhen
 */
@RestController
@RequestMapping("/portrait/secret/")
public class PortraitSecretController {
    @Resource
    PortraitSecretManager portraitSecretManager;
    @Resource
    CommonInfoManager commonInfoManager;
    /**
     * 获取列表页面
     * */
    @RequestMapping("listPage")
    public ModelAndView getListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "portrait/secret/portraitSecretList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        JSONObject resultJson = commonInfoManager.hasPermission("secretAdmin");
        mv.addObject("permission",resultJson.getBoolean("HX-GLY")||resultJson.getBoolean("secretAdmin")||"admin".equals(ContextUtil.getCurrentUser().getUserNo()));
        return mv;
    }
    /**
     * 获取列表数据
     * */
    @RequestMapping("listData")
    @ResponseBody
    public JsonPageResult<?> getListData(HttpServletRequest request, HttpServletResponse response) {
        return portraitSecretManager.query(request);
    }
    @RequestMapping("asyncSecret")
    @ResponseBody
    public JSONObject asyncSecret(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = portraitSecretManager.asyncSecret();
        return resultJSON;
    }
    /**
     * 获取列表数据
     * */
    @RequestMapping("secretPersonList")
    @ResponseBody
    public List<JSONObject> getSecretPersonList(HttpServletRequest request, HttpServletResponse response) {
        String userId = request.getParameter("userId");
        String reportYear = request.getParameter("reportYear");
        JSONObject paramJson = new JSONObject();
        paramJson.put("userId",userId);
        paramJson.put("reportYear",reportYear);
        return portraitSecretManager.getPersonSecretList(paramJson);
    }
}
