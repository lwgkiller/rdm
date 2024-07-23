package com.redxun.portrait.core.controller;


import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.portrait.core.manager.PortraitAnalysisImproveManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.portrait.core.manager.PortraitSecretManager;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.context.ContextUtil;

/**
 * @author zhangzhen
 */
@RestController
@RequestMapping("/portrait/analysisImprove/")
public class PortraitAnalysisImproveController {
    @Resource
    PortraitAnalysisImproveManager portraitAnalysisImproveManager;
    @Resource
    CommonInfoManager commonInfoManager;
    /**
     * 获取列表页面
     * */
    @RequestMapping("listPage")
    public ModelAndView getListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "portrait/analysisImprove/portraitAnalysisImproveList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        JSONObject resultJson = commonInfoManager.hasPermission("analysisImproveAdmin");
        mv.addObject("permission",resultJson.getBoolean("HX-GLY")||resultJson.getBoolean("analysisImproveAdmin")||"admin".equals(ContextUtil.getCurrentUser().getUserNo()));
        return mv;
    }
    /**
     * 获取列表数据
     * */
    @RequestMapping("listData")
    @ResponseBody
    public JsonPageResult<?> getListData(HttpServletRequest request, HttpServletResponse response) {
        return portraitAnalysisImproveManager.query(request);
    }
    @RequestMapping("asyncAnalysisImprove")
    @ResponseBody
    public JSONObject asyncAnalysisImprove(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = portraitAnalysisImproveManager.asyncAnalysisImprove();
        return resultJSON;
    }
    /**
     * 获取列表数据
     * */
    @RequestMapping("analysisImprovePersonList")
    @ResponseBody
    public List<JSONObject> getAnalysisImprovePersonList(HttpServletRequest request, HttpServletResponse response) {
        String userId = request.getParameter("userId");
        String reportYear = request.getParameter("reportYear");
        JSONObject paramJson = new JSONObject();
        paramJson.put("userId",userId);
        paramJson.put("reportYear",reportYear);
        return portraitAnalysisImproveManager.getPersonAnalysisImproveList(paramJson);
    }
}
