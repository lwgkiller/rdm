package com.redxun.rdmZhgl.core.controller;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import com.redxun.rdmZhgl.core.service.HyglInternalService;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.sys.core.entity.SysTree;
import com.redxun.sys.core.manager.SysTreeManager;
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
 * @author 李文光
 */
@Controller
@RequestMapping("/zhgl/core/hyglInternal")
public class HyglInternalController {
    private static final Logger logger = LoggerFactory.getLogger(HyglInternalController.class);
    @Autowired
    private HyglInternalService hyglInternalService;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Resource
    private SysTreeManager sysTreeManager;

    // ..
    @RequestMapping("dataListPage")
    public ModelAndView dataListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/hyglInternalList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("meetingTimeBegin", RequestUtil.getString(request, "yearMonthBegin"))
            .addObject("meetingTimeEnd", RequestUtil.getString(request, "yearMonthEnd"))
            .addObject("meetingOrgDepName", RequestUtil.getString(request, "clickDepartment"))
            .addObject("meetingModel", RequestUtil.getString(request, "clickModel"))
            .addObject("recordStatus", RequestUtil.getString(request, "recordStatus"))
            .addObject("scene", RequestUtil.getString(request, "scene"));
        return mv;
    }

    @RequestMapping("getDicByParam")
    @ResponseBody
    public List<JSONObject> getDicByParam(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List sysDics = new ArrayList<JSONObject>();
        String dicKey = request.getParameter("dicKey");
        String descp = request.getParameter("descp");
        if (StringUtils.isBlank(dicKey)) {
            return sysDics;
        }
        SysTree sysTree = sysTreeManager.getByKey(dicKey);
        if (sysTree == null) {
            return sysDics;
        }
        JSONObject param = new JSONObject();
        param.put("TYPE_ID_", sysTree.getTreeId());
        if (StringUtils.isNotBlank(descp)) {
            param.put("DESCP_", descp);
        }
        sysDics = hyglInternalService.getDicByParam(param);
        return sysDics;
    }

    // ..
    @RequestMapping("dataListQuery")
    @ResponseBody
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        return hyglInternalService.dataListQuery(request, response);
    }

    // ..
    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/hyglInternalEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String meetingId = RequestUtil.getString(request, "meetingId");
        String action = RequestUtil.getString(request, "action");
        mv.addObject("meetingId", meetingId).addObject("action", action);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentUserMainGroupId", ContextUtil.getCurrentUser().getMainGroupId());
        mv.addObject("currentUserMainGroupName", ContextUtil.getCurrentUser().getMainGroupName());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("scene", RequestUtil.getString(request, "scene", ""));
        return mv;
    }

    // ..
    @RequestMapping(value = "saveMeetingDataDraft", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveMeetingDataDraft(HttpServletRequest request, @RequestBody String meetingDataStr,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
        if (StringUtils.isBlank(meetingDataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("表单为空，保存失败！");
            return result;
        }
        hyglInternalService.saveOrCommitMeetingData(result, meetingDataStr);
        return result;
    }

    // ..
    @RequestMapping(value = "commitMeetingData", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult commitAndNoticeMeetingData(HttpServletRequest request, @RequestBody String meetingDataStr,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "提交成功");
        if (StringUtils.isBlank(meetingDataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("表单为空，提交失败！");
            return result;
        }
        hyglInternalService.saveOrCommitMeetingData(result, meetingDataStr);
        JSONObject meetingDataObj = JSONObject.parseObject(meetingDataStr);
        if (result.getSuccess() && meetingDataObj.getBooleanValue("sendNotice")) {
            if (StringUtils.isNotBlank(meetingDataObj.getString("meetingUserIds"))) {
                JSONObject noticeObj = new JSONObject();
                StringBuilder stringBuilder = new StringBuilder("【内部会议参会通知】");
                stringBuilder.append("请您于").append(meetingDataObj.getString("meetingTime"));
                stringBuilder.append("，前往").append(meetingDataObj.getString("meetingPlace"));
                stringBuilder.append("，参加主题为【").append(meetingDataObj.getString("meetingTheme"));
                stringBuilder.append("】的内部会议。通知人：").append(ContextUtil.getCurrentUser().getFullname());
                stringBuilder.append("，通知时间：").append(XcmgProjectUtil.getNowLocalDateStr(DateUtil.DATE_FORMAT_FULL));
                noticeObj.put("content", stringBuilder.toString());
                sendDDNoticeManager.sendNoticeForCommon(meetingDataObj.getString("meetingUserIds"), noticeObj);
            }
        }
        return result;
    }

    // ..
    @RequestMapping(value = "feedbackMeetingData", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult feedbackMeetingData(HttpServletRequest request, @RequestBody String meetingDataStr,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
        if (StringUtils.isBlank(meetingDataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("表单为空，提交失败！");
            return result;
        }
        hyglInternalService.saveOrCommitMeetingData(result, meetingDataStr);
        return result;
    }

    // ..
    @RequestMapping("queryMeetingById")
    @ResponseBody
    public JSONObject queryMeetingById(HttpServletRequest request, HttpServletResponse response) {
        String meetingId = RequestUtil.getString(request, "meetingId");
        if (StringUtils.isBlank(meetingId)) {
            logger.error("meetingId is blank");
            return null;
        }
        return hyglInternalService.queryMeetingById(meetingId);
    }

    // ..
    @RequestMapping("deleteData")
    @ResponseBody
    public JsonResult deleteData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return hyglInternalService.deleteMeeting(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteData", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    // ..
    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/hyglInternalFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("meetingId", RequestUtil.getString(request, "meetingId", ""));
        return mv;
    }

    // ..
    @RequestMapping("getMeetingFileList")
    @ResponseBody
    public List<JSONObject> getMeetingFileList(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        List<String> meetingIdList = Arrays.asList(RequestUtil.getString(request, "meetingId", ""));
        return hyglInternalService.getMeetingFileList(meetingIdList);
    }

    // ..
    @RequestMapping("meetingPdfPreviewAndAllDownload")
    public ResponseEntity<byte[]> meetingPdfPreviewAndAllDownload(HttpServletRequest request,
        HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("meetingFilePathBase");
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, fileBasePath);
    }

    // ..
    @RequestMapping("meetingOfficePreview")
    @ResponseBody
    public void meetingOfficePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("meetingFilePathBase");
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    // ..
    @RequestMapping("meetingImagePreview")
    @ResponseBody
    public void meetingImagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("meetingFilePathBase");
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    // ..
    @RequestMapping("delMeetingFile")
    public void delMeetingFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String meetingId = postBodyObj.getString("formId");
        hyglInternalService.deleteOneMeetingFile(fileId, fileName, meetingId);
    }

    // ..
    @RequestMapping(value = "fileUpload")
    @ResponseBody
    public Map<String, Object> fileUpload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件，成功后再写入数据库，前台是一个一个文件的调用
            hyglInternalService.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    // ..
    @RequestMapping("getMeetingPlanList")
    @ResponseBody
    public List<JSONObject> getMeetingPlanList(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        String meetingId = RequestUtil.getString(request, "meetingId", "");
        return hyglInternalService.getMeetingPlanList(meetingId);
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
        hyglInternalService.saveMeetingPlan(result, meetingPlanDataStr, meetingId);
        return result;
    }

    // ..
    @RequestMapping("deleteOneMeetingPlan")
    @ResponseBody
    public JsonResult deleteOneMeetingPlan(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String id = RequestUtil.getString(request, "id");
            String meetingId = RequestUtil.getString(request, "meetingId");
            return hyglInternalService.deleteOneMeetingPlan(id, meetingId);
        } catch (Exception e) {
            logger.error("Exception in deleteData", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    // ..
    @RequestMapping("exportPlanList")
    public void exportPlanExcel(HttpServletRequest request, HttpServletResponse response) {
        hyglInternalService.exportPlanExcel(request, response);
    }

}
