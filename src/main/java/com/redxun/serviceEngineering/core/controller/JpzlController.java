package com.redxun.serviceEngineering.core.controller;


import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.service.JpzlService;
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

@Controller
@RequestMapping("/serviceEngineering/core/jpzl/")
public class JpzlController extends GenericController {
    @Autowired
    private JpzlService jpzlService;
    @RequestMapping("JpzlListPage")
    public ModelAndView msgManagement(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "serviceEngineering/core/jpzlList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/jpzlEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String id = RequestUtil.getString(request, "id");
        String action = RequestUtil.getString(request, "action");
        if (StringUtils.isBlank(action)) {
            action = "edit";
        }
        mv.addObject("id", id).addObject("action", action);
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("queryJpzl")
    @ResponseBody
    public JsonPageResult<?> queryJstb(HttpServletRequest request, HttpServletResponse response) {
        return jpzlService.queryJpzl(request, response, true);
    }

    @RequestMapping("deleteJpzl")
    @ResponseBody
    public JsonResult deleteJstb(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return jpzlService.deleteJpzl(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteJpzl", e);
            return new JsonResult(false, e.getMessage());
        }
    }
    @RequestMapping("saveJpzl")
    @ResponseBody
    public JsonResult saveStandardChange(HttpServletRequest request, @RequestBody JSONObject formDataJson,
                                         HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
        try {
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("id"))) {
                jpzlService.insertJpzl(formDataJson);
            } else {
                jpzlService.updateJpzl(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save standardChange");
            result.setSuccess(false);
            result.setMessage("Exception in save standardChange");
            return result;
        }
        return result;
    }
    @RequestMapping("getDetail")
    @ResponseBody
    public JSONObject getstandardChangeDetail(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        JSONObject jpzlobj = new JSONObject();
        String id = RequestUtil.getString(request, "id");
        if (StringUtils.isNotBlank(id)) {
            jpzlobj = jpzlService.jpzlDetail(id);
        }
        return jpzlobj;
    }
}

