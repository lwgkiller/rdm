package com.redxun.rdmZhgl.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmZhgl.core.service.CxyProjectService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 产学研<p>
 * 产学研模块<p>
 * Copyright: Copyright (C) 2021 XXX, Inc. All rights reserved. <p>
 * Company: 徐工挖掘机械有限公司<p>
 *
 * @author liwenguang
 * @since 2021/2/25
 */
@Controller
@RequestMapping("/zhgl/core/cxy")
public class CxyProjectController {
    private static final Logger logger = LoggerFactory.getLogger(CxyProjectController.class);
    @Autowired
    private CxyProjectService cxyProjectService;

    @RequestMapping("dataListPage")
    public ModelAndView dataListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/cxyProjectList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("responsibleDepName", RequestUtil.getString(request, "clickDepartment"))
                .addObject("beginTime", RequestUtil.getString(request, "yearMonthBegin"))
                .addObject("endTime", RequestUtil.getString(request, "yearMonthEnd"))
                .addObject("implementation", RequestUtil.getString(request, "clickImplementation"))
                .addObject("projectProperties", RequestUtil.getString(request, "clickProjectProperties"));
        return mv;
    }

    @RequestMapping("dataListQuery")
    @ResponseBody
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        return cxyProjectService.dataListQuery(request, response);
    }

    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/cxyProjectEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String cxyProjectId = RequestUtil.getString(request, "cxyProjectId");
        String action = RequestUtil.getString(request, "action");
        mv.addObject("cxyProjectId", cxyProjectId).addObject("action", action);
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserMainGroupId", ContextUtil.getCurrentUser().getMainGroupId());
        mv.addObject("currentUserMainGroupName", ContextUtil.getCurrentUser().getMainGroupName());
        return mv;
    }

    @RequestMapping(value = "saveCxyProjectDataDraft", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveCxyProjectDataDraft(HttpServletRequest request, @RequestBody String cxyProjectDataStr,
                                              HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
        if (StringUtils.isBlank(cxyProjectDataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("表单为空，保存失败！");
            return result;
        }
        cxyProjectService.saveOrCommitCxyProjectData(result, cxyProjectDataStr);
        return result;
    }

    @RequestMapping(value = "commitCxyProjectData", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult commitCxyProjectData(HttpServletRequest request, @RequestBody String cxyProjectDataStr,
                                           HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "提交成功");
        if (StringUtils.isBlank(cxyProjectDataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("表单为空，提交失败！");
            return result;
        }
        cxyProjectService.saveOrCommitCxyProjectData(result, cxyProjectDataStr);
        return result;
    }


    @RequestMapping(value = "feedbackCxyProjectData", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult feedbackCxyProjectData(HttpServletRequest request, @RequestBody String cxyProjectDataStr,
                                             HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
        if (StringUtils.isBlank(cxyProjectDataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("表单为空，提交失败！");
            return result;
        }
        cxyProjectService.saveOrCommitCxyProjectData(result, cxyProjectDataStr);
        return result;
    }


    @RequestMapping("queryCxyProjectById")
    @ResponseBody
    public JSONObject queryCxyProjectById(HttpServletRequest request, HttpServletResponse response) {
        String cxyProjectId = RequestUtil.getString(request, "cxyProjectId");
        if (StringUtils.isBlank(cxyProjectId)) {
            logger.error("cxyProjectId is blank");
            return null;
        }
        return cxyProjectService.queryCxyProjectById(cxyProjectId);
    }

    @RequestMapping("deleteData")
    @ResponseBody
    public JsonResult deleteData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return cxyProjectService.deleteCxyProject(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteData", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    //..
    @RequestMapping("exportList")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        cxyProjectService.exportList(request, response);
    }
}
