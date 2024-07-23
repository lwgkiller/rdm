
package com.redxun.xcmgProjectManager.core.controller;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonResult;
import com.redxun.org.api.model.IUser;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectConfigDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectConfigManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * project_baseInfo控制器
 *
 * @author x
 */
@Controller
@RequestMapping("/xcmgProjectManager/core/config/")
public class XcmgProjectConfigController {
    private Logger logger = LogManager.getLogger(XcmgProjectConfigController.class);
    @Resource
    private XcmgProjectConfigManager xcmgProjectConfigManager;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Resource
    private XcmgProjectConfigDao xcmgProjectConfigDao;

    @RequestMapping("levelDivide")
    public ModelAndView levelDivide(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 返回当前登录人角色信息
        IUser currentUser = ContextUtil.getCurrentUser();
        Map<String, Object> params = new HashMap<>();
        params.put("userId", currentUser.getUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        String jspPath = "xcmgProjectManager/core/configLevelDivide.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        return mv;
    }

    @RequestMapping(value = "levelDivideList")
    @ResponseBody
    public List<Map<String, Object>> levelDivideList(HttpServletRequest request, HttpServletResponse response) {
        return xcmgProjectConfigManager.levelDivideList();
    }

    @RequestMapping(value = "saveLevelDivide", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveLevelDivide(HttpServletRequest request, @RequestBody String formDataStr,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(formDataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        xcmgProjectConfigManager.saveLevelDivide(result, formDataStr);
        return result;
    }

    @RequestMapping("standardScore")
    public ModelAndView standardScore(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 返回当前登录人角色信息
        IUser currentUser = ContextUtil.getCurrentUser();
        Map<String, Object> params = new HashMap<>();
        params.put("userId", currentUser.getUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);

        // 类别
        params.remove("userId");
        List<Map<String, String>> mapResults2 = xcmgProjectOtherDao.queryProjectCategory(params);
        if (mapResults2 != null && !mapResults2.isEmpty()) {
            // 去掉大类别
            Iterator<Map<String, String>> iterator = mapResults2.iterator();
            while (iterator.hasNext()) {
                Map<String, String> oneMap = iterator.next();
                if ("0".equals(oneMap.get("parentId"))) {
                    iterator.remove();
                }
            }
        }
        JSONArray smallCategoryInfos = XcmgProjectUtil.convertListMap2JsonArrString(mapResults2);
        String jspPath = "xcmgProjectManager/core/configStandardScore.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        mv.addObject("smallCategoryInfos", smallCategoryInfos);
        return mv;
    }

    @RequestMapping(value = "standardScoreList")
    @ResponseBody
    public List<Map<String, Object>> standardScoreList(HttpServletRequest request, HttpServletResponse response) {
        return xcmgProjectConfigManager.standardScoreList(RequestUtil.getString(request, "categoryId"));
    }

    @RequestMapping("getStandardScoreByCategoryLevel")
    @ResponseBody
    public Map<String, Object> getStandardScoreByCategoryLevel(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        String categoryId = RequestUtil.getString(request, "categoryId");
        String levelId = RequestUtil.getString(request, "levelId");
        if (StringUtils.isBlank(categoryId) || StringUtils.isBlank(levelId)) {
            return Collections.emptyMap();
        }
        Map<String, Object> params = new HashMap<>();
        params.put("categoryId", categoryId);
        params.put("levelId", levelId);
        List<Map<String, Object>> result = xcmgProjectConfigDao.standardScoreList(params);
        if (result != null && !result.isEmpty()) {
            return result.get(0);
        } else {
            return Collections.emptyMap();
        }
    }

    @RequestMapping(value = "saveStandardScore", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveStandardScore(HttpServletRequest request, @RequestBody String formDataStr,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(formDataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        xcmgProjectConfigManager.saveStandardScore(result, formDataStr);
        return result;
    }

    @RequestMapping("delivery")
    public ModelAndView deliveryView(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 返回当前登录人角色信息
        IUser currentUser = ContextUtil.getCurrentUser();
        Map<String, Object> params = new HashMap<>();
        params.put("userId", currentUser.getUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);

        // 类别
        params.remove("userId");
        List<Map<String, String>> categoryMap = xcmgProjectOtherDao.queryProjectCategory(params);
        if (categoryMap != null && !categoryMap.isEmpty()) {
            // 去掉大类别
            Iterator<Map<String, String>> iterator = categoryMap.iterator();
            while (iterator.hasNext()) {
                Map<String, String> oneMap = iterator.next();
                if ("0".equals(oneMap.get("parentId"))) {
                    iterator.remove();
                }
            }
        }
        JSONArray smallCategoryInfos = XcmgProjectUtil.convertListMap2JsonArrString(categoryMap);

        // 来源
        List<Map<String, String>> sourceMap = xcmgProjectOtherDao.queryProjectSources(params);
        JSONArray sourceInfos = XcmgProjectUtil.convertListMap2JsonArrString(sourceMap);

        // 级别
        List<Map<String, String>> levelMap = xcmgProjectOtherDao.queryProjectLevel(params);
        JSONArray levelInfos = XcmgProjectUtil.convertListMap2JsonArrString(levelMap);

        String jspPath = "xcmgProjectManager/core/configDelivery.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        mv.addObject("smallCategoryInfos", smallCategoryInfos);
        mv.addObject("sourceInfos", sourceInfos);
        mv.addObject("levelInfos", levelInfos);
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        return mv;
    }

    @RequestMapping(value = "deliveryList")
    @ResponseBody
    public List<Map<String, Object>> deliveryList(HttpServletRequest request, HttpServletResponse response) {
        return xcmgProjectConfigManager.deliveryList(RequestUtil.getString(request, "categoryId"));
    }

    @RequestMapping(value = "saveDelivery", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveDelivery(HttpServletRequest request, @RequestBody String formDataStr,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(formDataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        xcmgProjectConfigManager.saveDelivery(result, formDataStr);
        return result;
    }

    @RequestMapping(value = "delDelivery")
    @ResponseBody
    public JsonResult delDelivery(HttpServletRequest request, HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "成功删除!");
        String uId = RequestUtil.getString(request, "ids");
        if (StringUtils.isNotEmpty(uId)) {
            String[] ids = uId.split(",");
            for (String id : ids) {
                try {
                    xcmgProjectConfigDao.delDelivery(id);
                } catch (Exception e) {
                    logger.error("Exception in delDelivery, id=" + id, e);
                    result.setMessage("删除失败!");
                    return result;
                }
            }
        }
        return result;
    }

    @RequestMapping("deliveryEdit")
    public ModelAndView deliveryEditView(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String categoryId = RequestUtil.getString(request, "categoryId");
        String categoryName = RequestUtil.getString(request, "categoryName");
        String deliveryId = RequestUtil.getString(request, "deliveryId");
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 来源
        List<Map<String, String>> sourceMap = xcmgProjectOtherDao.queryProjectSources(params);
        JSONArray sourceInfos = XcmgProjectUtil.convertListMap2JsonArrString(sourceMap);

        // 级别
        List<Map<String, String>> levelMap = xcmgProjectOtherDao.queryProjectLevel(params);
        JSONArray levelInfos = XcmgProjectUtil.convertListMap2JsonArrString(levelMap);

        // 阶段
        params.put("categoryId", categoryId);
        List<Map<String, String>> stageList = xcmgProjectConfigDao.queryStageByCategory(params);
        JSONArray stageInfos = XcmgProjectUtil.convertListMap2JsonArrString(stageList);

        // 该条记录明细
        Map<String, String> deliveryInfo = new HashMap<>();
        if (StringUtils.isNotBlank(deliveryId)) {
            params.put("deliveryId", deliveryId);
            deliveryInfo = xcmgProjectConfigDao.queryDeliveryById(params);
        } else {
            deliveryInfo.put("fromPdm", "no");
        }
        deliveryInfo.put("categoryName", categoryName);
        JSONObject deliveryObj = XcmgProjectUtil.convertMap2JsonString(deliveryInfo);

        String jspPath = "xcmgProjectManager/core/configDeliveryEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("sourceInfos", sourceInfos);
        mv.addObject("levelInfos", levelInfos);
        mv.addObject("deliveryObj", deliveryObj);
        mv.addObject("stageInfos", stageInfos);
        mv.addObject("deliveryId", deliveryId);
        return mv;
    }

    @RequestMapping("ratingScore")
    public ModelAndView ratingScore(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 返回当前登录人角色信息
        IUser currentUser = ContextUtil.getCurrentUser();
        Map<String, Object> params = new HashMap<>();
        params.put("userId", currentUser.getUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        String jspPath = "xcmgProjectManager/core/configRatingScore.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        return mv;
    }

    @RequestMapping(value = "ratingScoreList")
    @ResponseBody
    public List<Map<String, Object>> ratingScoreList(HttpServletRequest request, HttpServletResponse response) {
        return xcmgProjectConfigManager.ratingScoreList();
    }

    @RequestMapping(value = "achievementTypeList")
    @ResponseBody
    public List<Map<String, Object>> achievementTypeList(HttpServletRequest request, HttpServletResponse response) {
        return xcmgProjectConfigManager.achievementTypeList();
    }

    @RequestMapping(value = "saveRatingScore", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveRatingScore(HttpServletRequest request, @RequestBody String changeGridDataStr,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(changeGridDataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        xcmgProjectConfigManager.saveRatingScore(result, changeGridDataStr);
        return result;
    }

    @RequestMapping(value = "saveAchievementType", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveAchievementType(HttpServletRequest request, @RequestBody String changeGridDataStr,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(changeGridDataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        xcmgProjectConfigManager.saveAchievement(result, changeGridDataStr);
        return result;
    }

    @RequestMapping("memRoleRatio")
    public ModelAndView memRoleRatio(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 返回当前登录人角色信息
        IUser currentUser = ContextUtil.getCurrentUser();
        Map<String, Object> params = new HashMap<>();
        params.put("userId", currentUser.getUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        String jspPath = "xcmgProjectManager/core/configMemRoleRatio.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        return mv;
    }

    @RequestMapping(value = "memRoleRatioList")
    @ResponseBody
    public List<Map<String, Object>> memRoleRatioList(HttpServletRequest request, HttpServletResponse response) {
        return xcmgProjectConfigManager.memRoleRatioList();
    }

    @RequestMapping(value = "saveMemRoleRatio", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveMemRoleRatio(HttpServletRequest request, @RequestBody String formDataStr,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(formDataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        xcmgProjectConfigManager.updatememRoleRatio(result, formDataStr);
        return result;
    }

    @RequestMapping("memRoleRank")
    public ModelAndView memRoleRank(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 返回当前登录人角色信息
        IUser currentUser = ContextUtil.getCurrentUser();
        Map<String, Object> params = new HashMap<>();
        params.put("userId", currentUser.getUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        String jspPath = "xcmgProjectManager/core/configMemRoleRank.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        // 职级和岗位列表
        List<Map<String, Object>> zjList = xcmgProjectOtherDao.queryZjList(params);
        JSONArray zjJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(zjList);
        mv.addObject("zjList", zjJsonArray);

        return mv;
    }

    @RequestMapping(value = "memRoleRankList")
    @ResponseBody
    public List<Map<String, Object>> memRoleRankList(HttpServletRequest request, HttpServletResponse response) {
        return xcmgProjectConfigManager.memRoleRankList();
    }

    @RequestMapping(value = "saveMemRoleRank", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveMemRoleRank(HttpServletRequest request, @RequestBody String formDataStr,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(formDataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        xcmgProjectConfigManager.updatememRoleRank(result, formDataStr);
        return result;
    }

    @RequestMapping("achievementType")
    public ModelAndView achievementTypeView(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 返回当前登录人角色信息
        IUser currentUser = ContextUtil.getCurrentUser();
        Map<String, Object> params = new HashMap<>();
        params.put("userId", currentUser.getUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        String jspPath = "xcmgProjectManager/core/configAchievementType.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        return mv;
    }

    @RequestMapping("bpmSolutionList")
    @ResponseBody
    public List getBpmSolutions(HttpServletRequest request, HttpServletResponse response) {
        return xcmgProjectConfigManager.getBpmSolutions(request, response);
    }
}
