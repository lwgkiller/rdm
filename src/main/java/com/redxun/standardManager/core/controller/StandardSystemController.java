package com.redxun.standardManager.core.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.org.api.model.IUser;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.standardManager.core.manager.StandardManager;
import com.redxun.standardManager.core.manager.StandardSystemManager;
import com.redxun.standardManager.core.util.BussinessUtil;
import com.redxun.standardManager.core.util.StandardConstant;
import com.redxun.standardManager.core.util.StandardManagerUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@RequestMapping("/standardManager/core/standardSystem/")
public class StandardSystemController extends GenericController {
    @Autowired
    private StandardSystemManager standardSystemManager;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;

    // 标准体系管理框架页面
    @RequestMapping("management")
    public ModelAndView management(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = getPathView(request);
        List<Map<String, Object>> systemCategorys = standardSystemManager.systemCategoryQuery();
        if (StandardManagerUtil.judgeGLNetwork(request)) {
            Iterator<Map<String, Object>> it = systemCategorys.iterator();
            while (it.hasNext()) {
                Map<String, Object> oneObj = it.next();
                if (oneObj.get("systemCategoryId") != null
                        && "JS".equalsIgnoreCase(oneObj.get("systemCategoryId").toString())) {
                    it.remove();
                }
            }
        }
        JSONArray systemCategorysJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(systemCategorys);
        mv.addObject("systemCategorysJsonArray", systemCategorysJsonArray);
        String webappName = WebAppUtil.getProperty("webappName", "rdm");
        // 从工作台传递的要查询的体系类别
        String systemCategory = RequestUtil.getString(request, "systemCategory", "");
        String activeTabIndex = "0";
        if (StringUtils.isBlank(systemCategory)) {
            if ("rdm".equalsIgnoreCase(webappName)) {
                activeTabIndex = BussinessUtil.toQuerySystemIndex(systemCategorys, StandardConstant.SYSTEMCATEGORY_JS);
            }
            if ("sim".equalsIgnoreCase(webappName)) {
                activeTabIndex = BussinessUtil.toQuerySystemIndex(systemCategorys, StandardConstant.SYSTEMCATEGORY_GL);
            }
        } else {
            activeTabIndex = BussinessUtil.toQuerySystemIndex(systemCategorys, systemCategory);
        }
        mv.addObject("activeTabIndex", activeTabIndex);
        // 获取可能从工作台页面传递的参数
        String standardNumber = RequestUtil.getString(request, "standardNumber", "");
        String standardName = RequestUtil.getString(request, "standardName", "");
        String standardCategory = RequestUtil.getString(request, "standardCategory", "");
        mv.addObject("standardNumber", standardNumber);
        mv.addObject("standardName", standardName);
        mv.addObject("standardCategory", standardCategory);
        return mv;
    }

    // 标准体系管理tab页面
    @RequestMapping("tabPage")
    public ModelAndView tabPage(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = getPathView(request);
        IUser currentUser = ContextUtil.getCurrentUser();
        Map<String, Object> params = new HashMap<>();
        params.put("userId", currentUser.getUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        mv.addObject("tabName", RequestUtil.getString(request, "tabName"));
        return mv;
    }

    // 编辑节点
    @RequestMapping("treeEdit")
    public ModelAndView treeEdit(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = getPathView(request);
        return mv;
    }

    // 根据体系类别查询体系
    @RequestMapping("treeQuery")
    @ResponseBody
    public JSONArray treeQuery(HttpServletRequest request, HttpServletResponse response) {
        String systemCategoryId = RequestUtil.getString(request, "systemCategoryId");
        if (StringUtils.isBlank(systemCategoryId)) {
            logger.error("systemCategoryId is blank");
            return new JSONArray();
        }
        List<Map<String, Object>> systemInfos = standardSystemManager.systemQuery(systemCategoryId);
        JSONArray systemArray = XcmgProjectUtil.convertListMap2JsonArrObject(systemInfos);
        return systemArray;
    }

    // 保存体系树
    @RequestMapping("treeSave")
    @ResponseBody
    public JSONObject treeSave(HttpServletRequest request, @RequestBody String changedDataStr,
        HttpServletResponse response) {
        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(changedDataStr)) {
            logger.warn("requestBody is blank");
            result.put("message", "数据为空");
            return result;
        }
        standardSystemManager.treeSave(result, changedDataStr);
        return result;
    }

    // 查询标准体系下标准的个数
    @RequestMapping("queryStandardBySystemIds")
    @ResponseBody
    public JSONObject queryStandardBySystemIds(HttpServletRequest request, @RequestBody String systemIds,
        HttpServletResponse response) {
        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(systemIds)) {
            logger.warn("requestBody is blank");
            result.put("num", 0);
            return result;
        }
        standardSystemManager.queryStandardBySystemIds(result, systemIds);
        return result;
    }

    @RequestMapping("treeExport")
    public void treeExport(HttpServletRequest request, HttpServletResponse response) {
        standardSystemManager.exportStandardSystem(request, response);
    }

    @RequestMapping("queryCategory")
    @ResponseBody
    public List<Map<String, Object>> querySystemCategory(HttpServletRequest request, HttpServletResponse response) {
        List<Map<String, Object>> systemCategorys = standardSystemManager.systemCategoryQuery();
        /*if (StandardManagerUtil.judgeGLNetwork(request)) {
            Iterator<Map<String, Object>> it = systemCategorys.iterator();
            while (it.hasNext()) {
                Map<String, Object> oneObj = it.next();
                if (oneObj.get("systemCategoryId") != null
                    && "JS".equalsIgnoreCase(oneObj.get("systemCategoryId").toString())) {
                    it.remove();
                }
            }
        }*/
        return systemCategorys;
    }
}
