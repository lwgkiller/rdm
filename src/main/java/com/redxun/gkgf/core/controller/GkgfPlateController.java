package com.redxun.gkgf.core.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.gkgf.core.dao.GkgfPlateDao;
import com.redxun.gkgf.core.manager.GkgfPlateManager;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.context.ContextUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.StringUtil;

/**
 * @author zz

 * */
@Controller
@RequestMapping("/gkgf/core/plate/")
public class GkgfPlateController {
    @Resource
    GkgfPlateManager gkgfPlateManager;
    @Resource
    GkgfPlateDao gkgfPlateDao;
    @Resource
    CommonInfoManager commonInfoManager;
    /**
     * 获取仓储中心列表页面
     * */
    @RequestMapping("listPage")
    public ModelAndView getListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "gkgf/core/gkgfPlateList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        JSONObject adminJson = commonInfoManager.hasPermission("GKGF-GLY");
        mv.addObject("isAdmin",adminJson.getBoolean("GKGF-GLY")||"admin".equals(ContextUtil.getCurrentUser().getUserNo()));
        return mv;
    }
    @RequestMapping("editPage")
    public ModelAndView getEditPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "gkgf/core/gkgfPlateEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String action = request.getParameter("action");
        String id = request.getParameter("id");
        JSONObject applyObj = new JSONObject();
        if(StringUtil.isNotEmpty(id)){
            applyObj = gkgfPlateDao.getObjectById(id);
        }
        mv.addObject("action",action);
        mv.addObject("applyObj", applyObj);
        return mv;
    }
    @RequestMapping("listData")
    @ResponseBody
    public JsonPageResult<?> getListData(HttpServletRequest request, HttpServletResponse response) {
        return gkgfPlateManager.query(request);
    }
    /**
     *
     * */
    @RequestMapping("getDicItem")
    @ResponseBody
    public JSONArray getDicItems(HttpServletRequest request, HttpServletResponse response) {
        return gkgfPlateManager.getDicItems(request.getParameter("categoryId"));
    }
    @RequestMapping("getItemObject")
    @ResponseBody
    public JSONObject getItemObject(HttpServletRequest request, HttpServletResponse response) {
        return gkgfPlateManager.getItemObject(request);
    }
    @RequestMapping("save")
    @ResponseBody
    public JSONObject save(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        JSONObject resultJSON = null;
        if(StringUtil.isEmpty(id)){
            resultJSON = gkgfPlateManager.add(request);
        }else{
            resultJSON = gkgfPlateManager.update(request);
        }
        return resultJSON;
    }
    @RequestMapping("remove")
    @ResponseBody
    public JSONObject remove(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = new JSONObject();
        resultJSON = gkgfPlateManager.remove(request);
        return resultJSON;
    }

}
