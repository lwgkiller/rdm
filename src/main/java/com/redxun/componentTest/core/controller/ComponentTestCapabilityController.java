package com.redxun.componentTest.core.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.componentTest.core.service.ComponentTestCapabilityService;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/componentTest/core/capability")
public class ComponentTestCapabilityController {
    private static final Logger logger = LoggerFactory.getLogger(ComponentTestCapabilityController.class);
    @Autowired
    private ComponentTestCapabilityService componentTestCapabilityService;
    @Autowired
    private CommonInfoManager commonInfoManager;

    //..
    @RequestMapping("componentTestCapability")
    public ModelAndView componentTestCapability(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "componentTest/core/componentTestCapability.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        boolean isComponentTestAdmin =
                commonInfoManager.judgeUserIsPointRole("零部件试验管理员", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("isComponentTestAdmin", isComponentTestAdmin);
        return mv;
    }

    //..
    @RequestMapping("componentTestCapabilityItemListQuery")
    @ResponseBody
    public JsonPageResult<?> componentTestCapabilityItemListQuery(HttpServletRequest request, HttpServletResponse response) {
        return componentTestCapabilityService.componentTestCapabilityItemListQuery(request, response);
    }

    //..新方法,服务于树选择小窗口的回调
    @RequestMapping("componentTestCapabilityItemListQuery2")
    @ResponseBody
    public List<JSONObject> componentTestCapabilityItemListQuery2(HttpServletRequest request, HttpServletResponse response) {
        String capabilityId = RequestUtil.getString(request, "capabilityId");
        return componentTestCapabilityService.componentTestCapabilityItemListQuery2(capabilityId);
    }

    //..
    @RequestMapping("getComponentTestCapabilityTree")
    @ResponseBody
    public List<JSONObject> getComponentTestCapabilityTree(HttpServletRequest request, HttpServletResponse response) {
        return componentTestCapabilityService.getComponentTestCapabilityTree();
    }

    //..新方法
    @RequestMapping("getComponentTestCapabilityTreeByPath")
    @ResponseBody
    public List<JSONObject> getComponentTestCapabilityTreeByPath(HttpServletRequest request, HttpServletResponse response) {
        String path = RequestUtil.getString(request, "path");
        return componentTestCapabilityService.getComponentTestCapabilityTreeByPath(path);
    }

    //..
    @RequestMapping("getComponentTestCapabilityChartData")
    @ResponseBody
    public List<JSONObject> getComponentTestCapabilityChartData(HttpServletRequest request, HttpServletResponse response) {
        return componentTestCapabilityService.getComponentTestCapabilityChartData();
    }

    //..
    @RequestMapping("openComponentTestCapabilityEditPage")
    public ModelAndView openComponentTestCapabilityEditPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "componentTest/core/componentTestCapabilityEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String pid = RequestUtil.getString(request, "pid");
        String path = RequestUtil.getString(request, "path");
        String action = RequestUtil.getString(request, "action");
        String componentTestCapabilityId = RequestUtil.getString(request, "componentTestCapabilityId");
        mv.addObject("pid", pid);
        mv.addObject("path", path);
        mv.addObject("action", action);
        mv.addObject("componentTestCapabilityId", componentTestCapabilityId);
        return mv;
    }

    //..
    @RequestMapping("saveComponentTestCapability")
    @ResponseBody
    public JsonResult saveComponentTestCapability(HttpServletRequest request, @RequestBody String formData, HttpServletResponse response) {
        JsonResult result = new JsonResult(true);
        if (StringUtils.isBlank(formData)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        try {
            JSONObject formDataJson = JSONObject.parseObject(formData);
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("id"))) {
                componentTestCapabilityService.createComponentTestCapability(formDataJson);
            } else {
                componentTestCapabilityService.updateComponentTestCapability(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save saveComponentTestCapability");
            result.setSuccess(false);
            result.setMessage("Exception in save saveComponentTestCapability");
            return result;
        }
        return result;
    }

    //..
    @RequestMapping("getComponentTestCapabilityById")
    @ResponseBody
    public JSONObject getComponentTestCapabilityById(HttpServletRequest request, HttpServletResponse response) {
        String componentTestCapabilityId = RequestUtil.getString(request, "componentTestCapabilityId");
        return componentTestCapabilityService.getComponentTestCapabilityById(componentTestCapabilityId);
    }

    //..
    @RequestMapping("deleteComponentTestCapability")
    @ResponseBody
    public JsonResult deleteComponentTestCapability(HttpServletRequest request, HttpServletResponse response) {
        try {
            String path = RequestUtil.getString(request, "path");
            return componentTestCapabilityService.deleteComponentTestCapability(path);
        } catch (Exception e) {
            logger.error("Exception in deleteComponentTestCapability", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    //..
    @RequestMapping("openComponentTestCapabilityItemListPage")
    public ModelAndView openComponentTestCapabilityItemListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "componentTest/core/componentTestCapabilityItemList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("componentTestCapabilityId", RequestUtil.getString(request, "componentTestCapabilityId"));
        mv.addObject("isComponentTestAdmin", RequestUtil.getString(request, "isComponentTestAdmin"));
        return mv;
    }

    //..
    @RequestMapping("saveComponentTestCapabilityItem")
    @ResponseBody
    public JsonResult saveComponentTestCapabilityItem(HttpServletRequest request, @RequestBody String formData,
                                                      HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
        if (StringUtils.isBlank(formData)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        try {
            JSONArray jsonArray = JSONObject.parseArray(formData);
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                if (StringUtils.isBlank(jsonObject.getString("id"))) {
                    componentTestCapabilityService.createComponentTestCapabilityItem(jsonObject);
                } else {
                    componentTestCapabilityService.updateComponentTestCapabilityItem(jsonObject);
                }
            }

        } catch (Exception e) {
            logger.error("Exception in saveComponentTestCapabilityItem");
            result.setSuccess(false);
            result.setMessage("Exception in saveComponentTestCapabilityItem");
            return result;
        }
        return result;
    }

    //..
    @RequestMapping("deleteComponentTestCapabilityItem")
    @ResponseBody
    public JsonResult deleteComponentTestCapabilityItem(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String strs = RequestUtil.getString(request, "ids");
            String[] ids = strs.split(",");
            return componentTestCapabilityService.deleteComponentTestCapabilityItem(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteComponentTestCapabilityItem", e);
            return new JsonResult(false, e.getMessage());
        }
    }
}
