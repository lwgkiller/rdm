package com.redxun.digitization.core.controller;

import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.digitization.core.service.SzhService;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * 应用模块名称
 * <p>
 * 代码描述
 * <p>
 * Copyright: Copyright (C) 2021 XXX, Inc. All rights reserved.
 * <p>
 * Company: 徐工挖掘机械有限公司
 * <p>
 *
 * @author liangchuanjiang
 * @since 2021/2/23 10:43
 */
@RestController
@RequestMapping("/digitization/core/Szh/")
public class SzhController {
    private Logger logger = LogManager.getLogger(SzhController.class);
    @Resource
    private SzhService szhService;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;

    @RequestMapping("gnjsListPage")
    public ModelAndView gnjsListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "digitization/core/gnjsList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String showDevelopUser = "false";
        if (ContextUtil.getCurrentUser().getUserNo().equals("admin")) {
            showDevelopUser = "true";
        }
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("showDevelopUser", showDevelopUser);
        return mv;
    }

    /**
     * 按树展示菜单
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("getTreeBySysId")
    @ResponseBody
    public List<JSONObject> getTreeBySysId(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String currentUserId = ContextUtil.getCurrentUserId();
        // 查询展示菜单使用量的特权账号
        Map<String, Object> param = new HashMap<>();
        param.put("userId", currentUserId);
        param.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(param);
        List<String> roleNames =
            currentUserRoles.stream().map(map -> (String)map.get("NAME_")).collect(Collectors.toList());
        boolean allShowRole = false;
        if (roleNames.contains(RdmConst.AllDATA_QUERY_NAME) || roleNames.contains("菜单特权人员")
            || ContextUtil.getCurrentUser().getUserNo().equals("admin")) {
            allShowRole = true;
        }

        param.clear();
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    // 数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
                    if ("publishTimeFrom".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8));
                    }
                    if ("publishTimeTo".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16));
                    }
                    if ("gnStatus".equalsIgnoreCase(name)) {
                        param.put(name, Arrays.asList(value.split(",", -1)));
                    } else {
                        param.put(name, value);
                    }
                }
            }
        }
        String sysId = "";
        if (param.get("sysId") != null) {
            sysId = param.get("sysId").toString();
        }
        List<JSONObject> result = new ArrayList<>();
        if (StringUtils.isNotBlank(sysId)) {
            param.put("sysId", sysId);
        } else {
            param.put("notShowNames", Arrays.asList(RdmConst.NOT_SHOW_SYSTEM_NAMES));
        }
        List<JSONObject> menuList = szhService.getMenus(param);
        Set<String> sysIdSet = new HashSet<>();
        Iterator iterator = menuList.iterator();
        while (iterator.hasNext()) {
            JSONObject oneMenu = (JSONObject)iterator.next();
            String isBtnMenu = oneMenu.getString("IS_BTN_MENU_");
            if (StringUtils.isNotBlank(isBtnMenu) && "YES".equalsIgnoreCase(isBtnMenu)) {
                iterator.remove();
            }
            String sysIdMenu = oneMenu.getString("SYS_ID_");
            if (StringUtils.isNotBlank(sysIdMenu)) {
                sysIdSet.add(sysIdMenu);
            }
            String subsysRespIds = oneMenu.getString("subsysRespIds");
            if(allShowRole || (StringUtils.isNotBlank(subsysRespIds) && subsysRespIds.contains(currentUserId))) {
                oneMenu.put("showClickCount","true");
            } else {
                oneMenu.put("showClickCount","false");
            }
        }
        // 将子模块也补充进去
        if (!sysIdSet.isEmpty()) {
            param.clear();
            param.put("sysIdList", new ArrayList<>(sysIdSet));
            List<JSONObject> subsys = szhService.getSubsysByParam(param);
            result.addAll(subsys);
        }
        result.addAll(menuList);
        return result;
    }

    @RequestMapping("clickCount")
    @ResponseBody
    public JsonResult clickCount(HttpServletRequest request, @RequestBody String formData,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true);
        JSONObject formDataJson = JSONObject.parseObject(formData);
        szhService.clickCount(formDataJson);
        return result;
    }

    @RequestMapping("queryMenuClickData")
    @ResponseBody
    public JSONObject queryMenuClickData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return szhService.queryMenuClickData(request);
    }

    @RequestMapping("clickChartPage")
    public ModelAndView clickChartPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "digitization/core/menuClickCharts.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("menuId", RequestUtil.getString(request, "menuId", ""));
        return mv;
    }
}
