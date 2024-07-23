package com.redxun.meeting.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.plugin.table.LoopRowTableRenderPolicy;
import com.deepoove.poi.util.PoitlIOUtils;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.BpmSolution;
import com.redxun.bpm.core.entity.ProcessMessage;
import com.redxun.bpm.core.entity.ProcessStartCmd;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.bpm.core.manager.BpmSolutionManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.ExceptionUtil;
import com.redxun.meeting.core.dao.HyglDao;
import com.redxun.meeting.core.service.HyglService;
import com.redxun.org.api.model.IUser;
import com.redxun.org.api.service.UserService;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
@RequestMapping("/zhgl/core/hygl/")
public class HyglController {
    private static final Logger logger = LoggerFactory.getLogger(HyglController.class);
    @Autowired
    private HyglService hyglService;
    @Resource
    private BpmSolutionManager bpmSolutionManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Resource
    private HyglDao hyglDao;
    @Resource
    private BpmInstManager bpmInstManager;
    @Autowired
    private UserService userService;

    // ..
    @RequestMapping("hyglListPage")
    public ModelAndView dataListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "meeting/core/hyglList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    @RequestMapping("getHyglType")
    @ResponseBody
    public List<JSONObject> getDicByParam(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<JSONObject> sysDics = hyglDao.getHyglType();
        return sysDics;
    }

    // ..
    @RequestMapping("getHyglList")
    @ResponseBody
    public JsonPageResult<?> getHyglList(HttpServletRequest request, HttpServletResponse response) {
        return hyglService.getHyglList(request, response);
    }

    // ..
    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "meeting/core/hyglEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String meetingId = RequestUtil.getString(request, "meetingId");
        String action = RequestUtil.getString(request, "action");
        mv.addObject("meetingId", meetingId).addObject("action", action);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentUserMainGroupId", ContextUtil.getCurrentUser().getMainGroupId());
        mv.addObject("currentUserMainGroupName", ContextUtil.getCurrentUser().getMainGroupName());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    // ..
    @RequestMapping(value = "saveMeetingData", method = RequestMethod.POST)
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
        hyglService.saveOrCommitMeetingData(result, meetingDataStr);
        return result;
    }

    @RequestMapping(value = "commitMeetingData", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult commitMeetingData(HttpServletRequest request, @RequestBody String meetingDataStr,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
        if (StringUtils.isBlank(meetingDataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("表单为空，保存失败！");
            return result;
        }
        hyglService.saveOrCommitMeetingData(result, meetingDataStr);

        BpmSolution bpmSol = bpmSolutionManager.getByKey("HYGLRWFJ", "1");
        if (bpmSol == null) {
            result.setSuccess(false);
            result.setMessage("流程创建失败，流程为空");
            return result;
        }
        String currentUserId = ContextUtil.getCurrentUserId();
        IUser user = userService.getByUserId(currentUserId);
        if (user == null) {
            result.setSuccess(false);
            result.setMessage("流程创建失败，当前用户 ‘" + currentUserId + "’找不到对应的用户信息");
            return result;
        }
        ContextUtil.setCurUser(user);
        String solId = bpmSol.getSolId();
        JSONObject meetingObj = JSONObject.parseObject(meetingDataStr);
        String meetingId = meetingObj.getString("id");
        List<JSONObject> meetingPlanList = hyglService.getMeetingPlanList(meetingId);
        for (JSONObject onePlan : meetingPlanList) {
            // 加上处理的消息提示
            ProcessMessage handleMessage = new ProcessMessage();
            try {
                ProcessHandleHelper.setProcessMessage(handleMessage);
                ProcessStartCmd startCmd = new ProcessStartCmd();
                startCmd.setSolId(solId);
                String id = onePlan.getString("id");
                String meetingContent = onePlan.getString("meetingContent");
                String meetingPlanRespUserIds = onePlan.getString("meetingPlanRespUserIds");
                String otherPlanRespUserIds = onePlan.getString("otherPlanRespUserIds");
                String meetingPlanEndTime = onePlan.getString("meetingPlanEndTime");
                String meetingPlanCompletion = onePlan.getString("meetingPlanCompletion");
                String isComplete = onePlan.getString("isComplete");
                JSONObject formData = new JSONObject();
                formData.put("id", id);
                formData.put("meetingContent", meetingContent);
                formData.put("meetingPlanRespUserIds", meetingPlanRespUserIds);
                formData.put("otherPlanRespUserIds", otherPlanRespUserIds);
                formData.put("meetingPlanEndTime", meetingPlanEndTime);
                formData.put("meetingPlanCompletion", meetingPlanCompletion);
                formData.put("isComplete", isComplete);
                startCmd.setJsonData(formData.toJSONString());
                // 启动流程
                bpmInstManager.doStartProcess(startCmd);
            } catch (Exception ex) {
                // 把具体的错误放置在内部处理，以显示正确的错误信息提示，在此不作任何的错误处理
                logger.error(ExceptionUtil.getExceptionMessage(ex));
                if (handleMessage.getErrorMsges().size() == 0) {
                    handleMessage.addErrorMsg(ex.getMessage());
                }
            } finally {
                // 在处理过程中，是否有错误的消息抛出
                if (handleMessage.getErrorMsges().size() > 0) {
                    result.setSuccess(false);
                    result.setMessage("流程创建失败，启动流程异常!");
                    result.setData(handleMessage.getErrorMsges());
                }
                ProcessHandleHelper.clearProcessCmd();
                ProcessHandleHelper.clearProcessMessage();
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
        hyglService.saveOrCommitMeetingData(result, meetingDataStr);
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
        return hyglService.queryMeetingById(meetingId);
    }

    // ..
    @RequestMapping("deleteMeeting")
    @ResponseBody
    public JsonResult deleteMeeting(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return hyglService.deleteMeeting(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteData", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    // ..
    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "meeting/core/hyglFileUpload.jsp";
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
        return hyglService.getMeetingFileList(meetingIdList);
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
        hyglService.deleteOneMeetingFile(fileId, fileName, meetingId);
    }

    // ..
    @RequestMapping(value = "fileUpload")
    @ResponseBody
    public Map<String, Object> fileUpload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件，成功后再写入数据库，前台是一个一个文件的调用
            hyglService.saveUploadFiles(request);
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
        return hyglService.getMeetingPlanList(meetingId);
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
        hyglService.saveMeetingPlan(result, meetingPlanDataStr, meetingId);
        return result;
    }

    // ..
    @RequestMapping("deleteOneMeetingPlan")
    @ResponseBody
    public JsonResult deleteOneMeetingPlan(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String id = RequestUtil.getString(request, "id");
            String meetingId = RequestUtil.getString(request, "meetingId");
            return hyglService.deleteOneMeetingPlan(id, meetingId);
        } catch (Exception e) {
            logger.error("Exception in deleteData", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    // ..
    @RequestMapping("exportPlanList")
    public void exportPlanExcel(HttpServletRequest request, HttpServletResponse response) {
        hyglService.exportPlanExcel(request, response);
    }

    @RequestMapping("downloadHyglWord")
    public void downloadBpmnFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String meetingId = RequestUtil.getString(request, "formId");
        List<JSONObject> planDetails =  hyglService.getMeetingPlanList(meetingId);
        JSONObject meetJson =  hyglService.queryMeetingById(meetingId);
        int index = 1;
        for (JSONObject jsonObject : planDetails) {
            jsonObject.put("index", index);
            if (StringUtils.isNotBlank(jsonObject.getString("isComplete"))) {
                if("true".equalsIgnoreCase(jsonObject.getString("isComplete"))){
                    jsonObject.put("isComplete", "是");
                }else {
                    jsonObject.put("isComplete", "否");
                }
            }
            index++;
        }
        String path = new ClassPathResource("templates/hygl/会议纪要模板.docx").getURL().getPath();
        LoopRowTableRenderPolicy policy = new LoopRowTableRenderPolicy();
        Configure config = Configure.builder()
                .bind("details", policy).build();
        XWPFTemplate template = XWPFTemplate.compile(path,config).render(new HashMap<String, Object>() {
            {
                put("details", planDetails);
                put("meetingNo", meetJson.getString("meetingNo"));
                put("contentAndConclusion", meetJson.getString("contentAndConclusion"));
                put("meetingModel",meetJson.getString("meetingModel"));
                put("meetingTheme",meetJson.getString("meetingTheme"));
                put("meetingTime",meetJson.getString("meetingTime"));
                put("meetingPlace",meetJson.getString("meetingPlace"));
                put("meetingOrgUserName",meetJson.getString("meetingOrgUserName"));
                put("meetingOrgDepName",meetJson.getString("meetingOrgDepName"));
                put("meetingUserNames",meetJson.getString("meetingUserNames"));
                put("applyName",meetJson.getString("applyName"));
                put("meetingTheme ",meetJson.getString("meetingTheme"));
            }
        });
        response.setContentType("application/octet-stream");
        String fileName = meetJson.getString("meetingNo")+"-"+meetJson.getString("meetingTheme")+ "会议记录.docx";
        fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
        response.setHeader("Content-disposition", "attachment;filename=" +fileName);
        // HttpServletResponse response
        OutputStream out = response.getOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(out);
        template.write(bos);
        bos.flush();
        out.flush();
        PoitlIOUtils.closeQuietlyMulti(template, bos, out);
    }
}
