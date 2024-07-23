package com.redxun.info.core.controller;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.core.util.StringUtil;
import com.redxun.info.core.manager.InfoStandardManager;
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
import com.redxun.core.util.DateUtil;
import com.redxun.info.core.manager.InfoProductManager;

/**
 * @author zhangzhen
 */
@RestController
@RequestMapping("/info/standard/")
public class InfoStandardController {
    @Resource
    InfoStandardManager infoStandardManager;
    @Resource
    SpiderManager spiderManager;
    /**
     * 获取列表页面
     * */
    @RequestMapping("listPage")
    public ModelAndView getListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "info/standard/infoStandardList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }
    /**
     * 获取列表数据
     * */
    @RequestMapping("listData")
    @ResponseBody
    public JsonPageResult<?> getListData(HttpServletRequest request, HttpServletResponse response) {
        return infoStandardManager.query(request);
    }
    /**
     * 获取新闻详情
     * */
    @RequestMapping("detailViewPage")
    public ModelAndView getDetailViewPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "info/standard/infoDetailView.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String id = request.getParameter("id");
        JSONObject applyObj = new JSONObject();
        JSONObject paramJSON = new JSONObject();
        paramJSON = new JSONObject();
        paramJSON.put("id", id);
        applyObj = infoStandardManager.getObjectById(id);
        applyObj.put("CREATE_TIME_", DateUtil.formatDate((Date)applyObj.get("CREATE_TIME_"), "yyyy-MM-dd"));
        mv.addObject("applyObj", applyObj.getString("content"));
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

    /**
     * 获取编辑页面
     * */
    @RequestMapping("editPage")
    public ModelAndView getEditPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "info/standard/infoStandardEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String action = request.getParameter("action");
        String id = request.getParameter("id");
        JSONObject applyObj = new JSONObject();
        //1.查看、或者修改 根据id查询
        if(ConstantUtil.FORM_EDIT.equals(action)||ConstantUtil.FORM_VIEW.equals(action)){
            applyObj = infoStandardManager.getObjectById(id);
        }
        mv.addObject("action",action);
        mv.addObject("applyObj", applyObj);
        return mv;
    }
    @RequestMapping("save")
    @ResponseBody
    public JSONObject save(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        JSONObject resultJSON = null;
        if(StringUtil.isEmpty(id)){
            resultJSON = infoStandardManager.add(request);
        }else{
            resultJSON = infoStandardManager.update(request);
        }
        return resultJSON;
    }
    @RequestMapping("remove")
    @ResponseBody
    public JSONObject remove(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = infoStandardManager.remove(request);
        return resultJSON;
    }
}
