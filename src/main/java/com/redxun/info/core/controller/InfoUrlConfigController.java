package com.redxun.info.core.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.info.core.manager.InfoUrlConfigManager;
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
@RequestMapping("/info/url/")
public class InfoUrlConfigController {
    @Resource
    InfoUrlConfigManager infoUrlConfigManager;
    /**
     * 获取编辑页面
     * */
    @RequestMapping("editPage")
    public ModelAndView getEditPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "info/config/infoUrlConfigEditPage.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String action = request.getParameter("action");
        String id = request.getParameter("id");
        JSONObject applyObj = new JSONObject();
        //1.查看、或者修改 根据id查询
        if(ConstantUtil.FORM_EDIT.equals(action)||ConstantUtil.FORM_VIEW.equals(action)){
            applyObj = infoUrlConfigManager.getObjectById(id);
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
    @RequestMapping("listPage")
    public ModelAndView getListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "info/config/infoUrlConfigListPage.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }
    /**
     * 获取列表数据
     * */
    @RequestMapping("listData")
    @ResponseBody
    public JsonPageResult<?> getListData(HttpServletRequest request, HttpServletResponse response) {
        return infoUrlConfigManager.query(request);
    }
    @RequestMapping("save")
    @ResponseBody
    public JSONObject save(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        JSONObject resultJSON = null;
        if(StringUtil.isEmpty(id)){
            resultJSON = infoUrlConfigManager.add(request);
        }else{
            resultJSON = infoUrlConfigManager.update(request);
        }
        return resultJSON;
    }
    @RequestMapping("remove")
    @ResponseBody
    public JSONObject remove(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = infoUrlConfigManager.remove(request);
        return resultJSON;
    }
}
