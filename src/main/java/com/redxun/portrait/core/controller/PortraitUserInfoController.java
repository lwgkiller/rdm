package com.redxun.portrait.core.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.portrait.core.manager.PortraitUserInfoManager;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.context.ContextUtil;
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
@RequestMapping("/portrait/userInfo/")
public class PortraitUserInfoController {
    @Resource
    PortraitUserInfoManager portraitUserInfoManager;
    @Resource
    CommonInfoManager commonInfoManager;
    /**
     * 获取编辑页面
     * */
    @RequestMapping("userEditPage")
    public ModelAndView getUserEditPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "portrait/userInfo/portraitUserInfoEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String action = request.getParameter("action");
        String userId = request.getParameter("userId");
        JSONObject applyObj = new JSONObject();
        //1.查看、或者修改 根据id查询
        if(ConstantUtil.FORM_EDIT.equals(action)||ConstantUtil.FORM_VIEW.equals(action)){
            applyObj = portraitUserInfoManager.getObjectById(userId);
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
    @RequestMapping("userListPage")
    public ModelAndView getUserListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "portrait/userInfo/portraitUserInfoList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        JSONObject resultJson = commonInfoManager.hasPermission("YGHXZGLY");
        mv.addObject("permission",resultJson.getBoolean("YGHXZGLY")||"admin".equals(ContextUtil.getCurrentUser().getUserNo()));
        return mv;
    }
    /**
     * 获取列表数据
     * */
    @RequestMapping("userList")
    @ResponseBody
    public JsonPageResult<?> userList(HttpServletRequest request, HttpServletResponse response) {
        return portraitUserInfoManager.query(request);
    }
    @RequestMapping("save")
    @ResponseBody
    public JSONObject save(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        JSONObject resultJSON = null;
        if(StringUtil.isEmpty(id)){
            resultJSON = portraitUserInfoManager.add(request);
        }else{
            resultJSON = portraitUserInfoManager.update(request);
        }
        return resultJSON;
    }
}
