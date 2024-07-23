package com.redxun.researchTool.core.controller;


import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.researchTool.core.service.ResearchToolService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.RequestUtil;
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
@RequestMapping("/researchTool/tool/")
public class ToolController extends GenericController {
    @Autowired
    private ResearchToolService researchToolService;
    @RequestMapping("ToolListPage")
    public ModelAndView msgManagement(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "researchTool/core/toolList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "researchTool/core/toolEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String toolid = RequestUtil.getString(request, "toolid");
        String action = RequestUtil.getString(request, "action");
        if (StringUtils.isBlank(action)) {
            action = "edit";
        }
        mv.addObject("toolid", toolid).addObject("action", action);
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("queryTool")
    @ResponseBody
    public JsonPageResult<?> queryJstb(HttpServletRequest request, HttpServletResponse response) {
        return researchToolService.queryTool(request, response, true);
    }

    @RequestMapping("deleteTool")
    @ResponseBody
    public JsonResult deleteJstb(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return researchToolService.deletetool(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteTool", e);
            return new JsonResult(false, e.getMessage());
        }
    }
    @RequestMapping("saveTool")
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
            if (StringUtils.isBlank(formDataJson.getString("toolid"))) {
                researchToolService.insertTool(formDataJson);
            } else {
                researchToolService.updateTool(formDataJson);
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
        JSONObject toolobj = new JSONObject();
        String toolid = RequestUtil.getString(request, "toolid");
        if (StringUtils.isNotBlank(toolid)) {
            toolobj = researchToolService.toolDetail(toolid);
        }
        return toolobj;
    }
}

