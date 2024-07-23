package com.redxun.portrait.core.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.redxun.portrait.core.manager.PortraitProjectManager;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.StringUtil;
import com.redxun.util.ConstantUtil;

/**
 * @author zhangzhen
 */
@RestController
@RequestMapping("/portrait/project/")
public class PortraitProjectController {
    @Resource
    PortraitProjectManager portraitProjectManager;
    @Resource
    CommonInfoManager commonInfoManager;
    /**
     * 获取列表数据
     * */
    @RequestMapping("projectPersonList")
    @ResponseBody
    public JSONArray getProjectPersonList(HttpServletRequest request, HttpServletResponse response) {
        String userId = request.getParameter("userId");
        String reportYear = request.getParameter("reportYear");
        JSONObject paramJson = new JSONObject();
        paramJson.put("userId",userId);
        paramJson.put("reportYear",reportYear);
        return portraitProjectManager.getPersonProjectList(paramJson);
    }

    /**
     * 获取编辑页面
     * */
    @RequestMapping("projectEditPage")
    public ModelAndView getProjectEditPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "portrait/project/portraitProjectEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String action = request.getParameter("action");
        String id = request.getParameter("id");
        JSONObject applyObj = new JSONObject();
        //1.查看、或者修改 根据id查询
        if(ConstantUtil.FORM_EDIT.equals(action)||ConstantUtil.FORM_VIEW.equals(action)){
            applyObj = portraitProjectManager.getObjectById(id);
        }
        if(ConstantUtil.FORM_ADD.equals(action)){
        }
        mv.addObject("action",action);
        mv.addObject("applyObj", applyObj);
        return mv;
    }
    /**
     * 获取列表页面
     * */
    @RequestMapping("projectListPage")
    public ModelAndView getProjectListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "portrait/project/portraitProjectList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }
    /**
     * 获取列表数据
     * */
    @RequestMapping("projectList")
    @ResponseBody
    public JsonPageResult<?> projectList(HttpServletRequest request, HttpServletResponse response) {
        return portraitProjectManager.query(request);
    }
    @RequestMapping("save")
    @ResponseBody
    public JSONObject save(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        JSONObject resultJSON = null;
        if(StringUtil.isEmpty(id)){
            resultJSON = portraitProjectManager.add(request);
        }else{
            resultJSON = portraitProjectManager.update(request);
        }
        return resultJSON;
    }
    @RequestMapping("remove")
    @ResponseBody
    public JSONObject remove(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = portraitProjectManager.remove(request);
        return resultJSON;
    }
}
