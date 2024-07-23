package com.redxun.xcmgJsjl.core.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgJsjl.core.manager.XcmgJsjlManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * 应用模块名称
 * <p>
 * 代码描述
 * <p>
 * Copyright: Copyright (C) 2020 XXX, Inc. All rights reserved.
 * <p>
 * Company: 徐工挖掘机械有限公司
 * <p>
 *
 * @author liangchuanjiang
 * @since 2020/11/23 18:59
 */
@Controller
@RequestMapping("/jsjl/core/")
public class XcmgJsjlController {
    private static final Logger logger = LoggerFactory.getLogger(XcmgJsjlController.class);
    @Autowired
    private XcmgJsjlManager xcmgJsjlManager;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;

    @RequestMapping("dataListPage")
    public ModelAndView dataListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "xcmgJsjl/core/xcmgJsjlList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("communicateStartTime", RequestUtil.getString(request, "yearMonthBegin"))
            .addObject("communicateEndTime", RequestUtil.getString(request, "yearMonthEnd"))
            .addObject("deptName", RequestUtil.getString(request, "clickDepartment"))
            .addObject("dimensionName", RequestUtil.getString(request, "clickModel"))
            .addObject("recordStatus", RequestUtil.getString(request, "recordStatus"))
            .addObject("scene", RequestUtil.getString(request, "scene"));
        return mv;
    }

    @RequestMapping("dataListQuery")
    @ResponseBody
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        return xcmgJsjlManager.dataListQuery(request, response);
    }

    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "xcmgJsjl/core/xcmgJsjlEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String jsjlId = RequestUtil.getString(request, "jsjlId");
        String action = RequestUtil.getString(request, "action");
        mv.addObject("jsjlId", jsjlId).addObject("action", action);
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserMainGroupId", ContextUtil.getCurrentUser().getMainGroupId());
        mv.addObject("currentUserMainGroupName", ContextUtil.getCurrentUser().getMainGroupName());
        mv.addObject("scene", RequestUtil.getString(request, "scene", ""));
        return mv;
    }

    @RequestMapping(value = "saveJsjlDataDraft", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveJsjlDataDraft(HttpServletRequest request, @RequestBody String jsjlDataStr,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
        if (StringUtils.isBlank(jsjlDataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("表单为空，保存失败！");
            return result;
        }
        xcmgJsjlManager.saveOrCommitJsjlData(result, jsjlDataStr);
        return result;
    }

    @RequestMapping(value = "commitJsjlData", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult commitAndNoticeJsjlData(HttpServletRequest request, @RequestBody String jsjlDataStr,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "提交成功");
        if (StringUtils.isBlank(jsjlDataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("表单为空，提交失败！");
            return result;
        }
        xcmgJsjlManager.saveOrCommitJsjlData(result, jsjlDataStr);
        JSONObject jsjlDataObj = JSONObject.parseObject(jsjlDataStr);
        if (result.getSuccess() && jsjlDataObj.getBooleanValue("sendNotice")) {
            if (StringUtils.isNotBlank(jsjlDataObj.getString("meetingUserIds"))) {
                JSONObject noticeObj = new JSONObject();
                StringBuilder stringBuilder = new StringBuilder("【业务交流参会通知】");
                stringBuilder.append("请您于").append(jsjlDataObj.getString("communicateTime"));
                stringBuilder.append("，前往").append(jsjlDataObj.getString("communicateRoom"));
                stringBuilder.append("，参加与").append(jsjlDataObj.getString("communicateCompany"));
                stringBuilder.append("的业务交流会。通知人：").append(ContextUtil.getCurrentUser().getFullname());
                stringBuilder.append("，通知时间：").append(XcmgProjectUtil.getNowLocalDateStr(DateUtil.DATE_FORMAT_FULL));
                noticeObj.put("content", stringBuilder.toString());
                sendDDNoticeManager.sendNoticeForCommon(jsjlDataObj.getString("meetingUserIds"), noticeObj);
            }
        }
        return result;
    }

    @RequestMapping(value = "feedbackJsjlData", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult feedbackJsjlData(HttpServletRequest request, @RequestBody String jsjlDataStr,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
        if (StringUtils.isBlank(jsjlDataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("表单为空，提交失败！");
            return result;
        }
        xcmgJsjlManager.saveOrCommitJsjlData(result, jsjlDataStr);
        return result;
    }

    @RequestMapping("queryJsjlById")
    @ResponseBody
    public JSONObject queryJsjlById(HttpServletRequest request, HttpServletResponse response) {
        String jsjlId = RequestUtil.getString(request, "jsjlId");
        if (StringUtils.isBlank(jsjlId)) {
            logger.error("jsjlId is blank");
            return null;
        }
        return xcmgJsjlManager.queryJsjlById(jsjlId);
    }

    @RequestMapping("deleteData")
    @ResponseBody
    public JsonResult deleteData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return xcmgJsjlManager.deleteJsjl(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteData", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    // ..
    @RequestMapping("getMeetingPlanList")
    @ResponseBody
    public List<JSONObject> getMeetingPlanList(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        String meetingId = RequestUtil.getString(request, "meetingId", "");
        return xcmgJsjlManager.getMeetingPlanList(meetingId);
    }

    // ..
    @RequestMapping(value = "saveMeetingPlan", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveMeetingPlan(HttpServletRequest request, @RequestBody String meetingPlanDataStr,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
        if (StringUtils.isBlank(meetingPlanDataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("数据为空，保存失败！");
            return result;
        }
        String meetingId = RequestUtil.getString(request, "meetingId", "");
        xcmgJsjlManager.saveMeetingPlan(result, meetingPlanDataStr, meetingId);
        return result;
    }

    // ..
    @RequestMapping("deleteOneMeetingPlan")
    @ResponseBody
    public JsonResult deleteOneMeetingPlan(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String id = RequestUtil.getString(request, "id");
            String meetingId = RequestUtil.getString(request, "meetingId");
            return xcmgJsjlManager.deleteOneMeetingPlan(id, meetingId);
        } catch (Exception e) {
            logger.error("Exception in deleteData", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    // ..
    @RequestMapping("exportPlanList")
    public void exportPlanExcel(HttpServletRequest request, HttpServletResponse response) {
        xcmgJsjlManager.exportPlanExcel(request, response);
    }
}
