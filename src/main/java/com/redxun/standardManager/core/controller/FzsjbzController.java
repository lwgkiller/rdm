package com.redxun.standardManager.core.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.org.api.model.IUser;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.standardManager.core.manager.FzsjbzManager;
import com.redxun.standardManager.core.manager.SubManagerUserService;
import com.redxun.standardManager.core.util.StandardManagerUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/standardManager/core/fzbz/")
public class FzsjbzController {
    @Autowired
    private FzsjbzManager fzsjbzManager;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private SubManagerUserService subManagerUserService;

    // 仿真标准
    @RequestMapping("tabPage")
    public ModelAndView getStorageEditPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "standardManager/core/fzsjbzTabPage.jsp";
        boolean isGlNetwork= StandardManagerUtil.judgeGLNetwork(request);
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        Map<String, Object> params = new HashMap<>();
        params.put("userId", currentUser.getUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 角色信息
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        // 获取可能从工作台页面传递的参数
        String standardNumber = RequestUtil.getString(request, "standardNumber", "");
        String standardName = RequestUtil.getString(request, "standardName", "");
        String standardCategory = RequestUtil.getString(request, "standardCategory", "");
        String publishTimeFrom = RequestUtil.getString(request, "publishTimeFrom", "");
        String publishTimeTo = RequestUtil.getString(request, "publishTimeTo", "");
        String standardStatus = RequestUtil.getString(request, "standardStatus", "");
        // 职级信息
        List<Map<String, Object>> currentUserZJ = xcmgProjectOtherDao.queryUserZJ(params);
        JSONArray userZJJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserZJ);
        mv.addObject("currentUserZJ", userZJJsonArray);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        // 获取当前用户在标准子管理组中，标准类别与标准体系的数据
        JSONObject subManagerObj = subManagerUserService.querySubManagerSystemIds(ContextUtil.getCurrentUserId());
        mv.addObject("subManagerObj", subManagerObj);
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("subManagerObj", subManagerObj);
        mv.addObject("standardNumber", standardNumber);
        mv.addObject("standardName", standardName);
        mv.addObject("standardCategory", standardCategory);
        mv.addObject("publishTimeFrom", publishTimeFrom);
        mv.addObject("publishTimeTo", publishTimeTo);
        mv.addObject("standardStatus", standardStatus);
        mv.addObject("isGlNetwork",isGlNetwork);
        mv.addObject("tabName", "JS");
        return mv;
    }
    // 仿真标准列表查询
    @RequestMapping("queryFzbzList")
    @ResponseBody
    public JsonPageResult<?> queryFzbzList(HttpServletRequest request, HttpServletResponse response) {
        return fzsjbzManager.queryFzbzList(request);
    }
}
