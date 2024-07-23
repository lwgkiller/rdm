package com.redxun.portrait.core.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.redxun.portrait.core.manager.PortraitYearScoreManager;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.standardManager.core.util.ResultUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.redxun.portrait.core.manager.PortraitDocumentManager;

import java.util.List;


/**
 * @author zhangzhen
 */
@RestController
@RequestMapping("/portrait/yearScore/")
public class PortraitYearScoreController {
    @Resource
    PortraitYearScoreManager portraitYearScoreManager;
    @Resource
    CommonInfoManager commonInfoManager;
    /**
     * 获取列表页面
     * */
    @RequestMapping("yearScoreListPage")
    public ModelAndView getYearScoreListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "portrait/document/portraitYearScoreList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        JSONObject resultJson = commonInfoManager.hasPermission("YGHXZGLY");
        mv.addObject("permission",resultJson.getBoolean("HX-GLY")||resultJson.getBoolean("YGHXZGLY")||"admin".equals(ContextUtil.getCurrentUser().getUserNo()));
        return mv;
    }
    /**
     * 获取列表数据
     * */
    @RequestMapping("yearScoreList")
    @ResponseBody
    public List<JSONObject> yearScoreList(HttpServletRequest request, HttpServletResponse response) {
        return portraitYearScoreManager.getYearScoreList(request,true);
    }

    @RequestMapping("genYearScore")
    @ResponseBody
    public JSONObject genYearScore(HttpServletRequest request, HttpServletResponse response) {
        JSONArray jsonArray  = portraitYearScoreManager.genYearScore(request);
        if(jsonArray==null){
            return ResultUtil.result(false,"生成失败",null);
        }
        return ResultUtil.result(true,"生成成功",null);
    }
    @RequestMapping("exportExcel")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        portraitYearScoreManager.exportExcel(request, response);
    }
}
