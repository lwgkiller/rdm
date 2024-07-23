package com.redxun.portrait.core.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.core.util.DateUtil;
import com.redxun.portrait.core.manager.PortraitHonorManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.StringUtil;
import com.redxun.portrait.core.manager.PortraitBidPlanManager;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.util.ConstantUtil;

/**
 * @author zhangzhen
 */
@RestController
@RequestMapping("/portrait/honor/")
public class PortraitHonorController {
    @Resource
    PortraitHonorManager portraitHonorManager;
    @Resource
    CommonInfoManager commonInfoManager;
    /**
     * 获取编辑页面
     * */
    @RequestMapping("editPage")
    public ModelAndView getEditPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "portrait/honor/portraitHonorEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String action = request.getParameter("action");
        String id = request.getParameter("id");
        JSONObject applyObj = new JSONObject();
        //1.查看、或者修改 根据id查询
        if(ConstantUtil.FORM_EDIT.equals(action)||ConstantUtil.FORM_VIEW.equals(action)){
            applyObj = portraitHonorManager.getObjectById(id);
            if (applyObj.get("rewardDate") != null) {
                applyObj.put("rewardDate",
                        DateUtil.formatDate((Date)applyObj.get("rewardDate"), "yyyy-MM-dd"));
            }
        }
        if(ConstantUtil.FORM_ADD.equals(action)){
        }
        mv.addObject("action",action);
        mv.addObject("applyObj", applyObj);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("editable", true);
        return mv;
    }
    /**
     * 获取列表页面
     * */
    @RequestMapping("listPage")
    public ModelAndView getDataListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "portrait/honor/portraitHonorList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        JSONObject resultJson = commonInfoManager.hasPermission("HX-GLY");
        mv.addObject("permission",resultJson.getBoolean("HX-GLY")||"admin".equals(ContextUtil.getCurrentUser().getUserNo()));
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        return mv;
    }
    /**
     * 获取列表数据
     * */
    @RequestMapping("dataList")
    @ResponseBody
    public JsonPageResult<?> getDataList(HttpServletRequest request, HttpServletResponse response) {
        return portraitHonorManager.query(request);
    }
    @RequestMapping("save")
    @ResponseBody
    public JSONObject save(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        JSONObject resultJSON = null;
        if(StringUtil.isEmpty(id)){
            resultJSON = portraitHonorManager.add(request);
        }else{
            resultJSON = portraitHonorManager.update(request);
        }
        return resultJSON;
    }
    @RequestMapping("remove")
    @ResponseBody
    public JSONObject remove(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = portraitHonorManager.remove(request);
        return resultJSON;
    }
}
