package com.redxun.info.core.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.info.core.manager.InfoNewsManager;
import com.redxun.info.core.manager.SpiderManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.util.ConstantUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;

import java.util.Date;

/**
 * @author zhangzhen
 */
@RestController
@RequestMapping("/info/news/")
public class InfoNewsController {
    @Resource
    InfoNewsManager infoNewsManager;
    @Resource
    SpiderManager spiderManager;
    /**
     * 获取列表页面
     * */
    @RequestMapping("newsListPage")
    public ModelAndView getIndexListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "info/news/infoNewsList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }
    /**
     * 获取列表数据
     * */
    @RequestMapping("newsList")
    @ResponseBody
    public JsonPageResult<?> newsList(HttpServletRequest request, HttpServletResponse response) {
        return infoNewsManager.query(request);
    }
    /**
     * 获取新闻详情
     * */
    @RequestMapping("infoDetailViewPage")
    public ModelAndView getInfoDetailViewPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "info/news/infoDetailView.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String id = request.getParameter("id");
        JSONObject applyObj = new JSONObject();
        JSONObject paramJSON = new JSONObject();
        paramJSON.put("id", id);
        applyObj = infoNewsManager.getObjectById(id);
        applyObj.put("CREATE_TIME_", DateUtil.formatDate((Date)applyObj.get("CREATE_TIME_"), "yyyy-MM-dd"));
        mv.addObject("applyObj", applyObj.getString("content"));
        return mv;
    }
    /**
     * 获取编辑页面
     * */
    @RequestMapping("editPage")
    public ModelAndView getEditPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "info/news/infoNewsEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String action = request.getParameter("action");
        String id = request.getParameter("id");
        JSONObject applyObj = new JSONObject();
        //1.查看、或者修改 根据id查询
        if(ConstantUtil.FORM_EDIT.equals(action)||ConstantUtil.FORM_VIEW.equals(action)){
            applyObj = infoNewsManager.getObjectById(id);
        }
        mv.addObject("action",action);
        mv.addObject("applyObj", applyObj);
        return mv;
    }

    @RequestMapping("doSpider")
    @ResponseBody
    public JSONObject doSpider(HttpServletRequest request, HttpServletResponse response) {
        String infoTypeName = RequestUtil.getString(request, "infoTypeName", "");
        String deptId = ContextUtil.getCurrentUser().getMainGroupId();
        spiderManager.doSpider(infoTypeName, deptId);
        return ResultUtil.result(true,"",null);
    }
    @RequestMapping("save")
    @ResponseBody
    public JSONObject save(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        JSONObject resultJSON = null;
        if(StringUtil.isEmpty(id)){
            resultJSON = infoNewsManager.add(request);
        }else{
            resultJSON = infoNewsManager.update(request);
        }
        return resultJSON;
    }
    @RequestMapping("remove")
    @ResponseBody
    public JSONObject remove(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = infoNewsManager.remove(request);
        return resultJSON;
    }
}
