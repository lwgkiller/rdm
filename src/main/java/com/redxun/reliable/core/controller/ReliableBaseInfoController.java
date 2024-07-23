package com.redxun.reliable.core.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.reliable.core.dao.ReliableBaseInfoDao;
import com.redxun.reliable.core.service.ReliableBaseInfoService;
import com.redxun.saweb.context.ContextUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.StringUtil;

/**
 * @author zz

 * */
@Controller
@RequestMapping("/reliable/core/baseInfo/")
public class ReliableBaseInfoController {
    @Resource
    ReliableBaseInfoService reliableBaseInfoService;
    @Resource
    ReliableBaseInfoDao reliableBaseInfoDao;
    @Resource
    CommonInfoManager commonInfoManager;
    /**
     * 获取列表页面
     * */
    @RequestMapping("listPage")
    public ModelAndView getListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "reliable/core/reliableBaseInfoList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }
    @RequestMapping("editPage")
    public ModelAndView getEditPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "reliable/core/reliableInfoTabs.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String action = request.getParameter("action");
        String id = request.getParameter("id");
        if(id==null){
            id = "";
        }
        mv.addObject("action",action);
        mv.addObject("id", id);
        return mv;
    }
    @RequestMapping("listData")
    @ResponseBody
    public JsonPageResult<?> getListData(HttpServletRequest request, HttpServletResponse response) {
        return reliableBaseInfoService.query(request);
    }
    /**
     * 获取列表页面
     * */
    @RequestMapping("listAllPage")
    public ModelAndView getListAllPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "reliable/core/reliableAllList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }
    @RequestMapping("listAllData")
    @ResponseBody
    public JsonPageResult<?> getListAllData(HttpServletRequest request, HttpServletResponse response) {
        return reliableBaseInfoService.getAllData(request);
    }
    @RequestMapping("getItemObject")
    @ResponseBody
    public JSONObject getItemObject(HttpServletRequest request, HttpServletResponse response) {
        return reliableBaseInfoService.getItemObject(request);
    }
    @RequestMapping("save")
    @ResponseBody
    public JSONObject save(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        JSONObject resultJSON = null;
        if(StringUtil.isEmpty(id)){
            resultJSON = reliableBaseInfoService.add(request);
        }else{
            resultJSON = reliableBaseInfoService.update(request);
        }
        return resultJSON;
    }
    @RequestMapping("remove")
    @ResponseBody
    public JSONObject remove(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = new JSONObject();
        resultJSON = reliableBaseInfoService.remove(request);
        return resultJSON;
    }
    @RequestMapping("baseInfoEditPage")
    public ModelAndView getBaseInfoEditPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "reliable/core/reliableBaseInfoEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String action = request.getParameter("action");
        String id = request.getParameter("id");
        JSONObject applyObj = new JSONObject();
        if(StringUtil.isNotEmpty(id)){
            applyObj = reliableBaseInfoDao.getObjectById(id);
        }
        mv.addObject("action",action);
        mv.addObject("applyObj", applyObj);
        return mv;
    }

}
