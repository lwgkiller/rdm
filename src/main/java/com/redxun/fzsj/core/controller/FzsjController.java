package com.redxun.fzsj.core.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.fzsj.core.service.FzsjService;
import com.redxun.fzsj.core.service.SdmService;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Controller
@RequestMapping("/fzsj/core/fzsj")
public class FzsjController {
    private static final Logger logger = LoggerFactory.getLogger(FzsjController.class);
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private FzsjService fzsjService;

    @Autowired
    private BpmInstManager bpmInstManager;

    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;

    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;

    @Resource
    private SdmService sdmService;

    @RequestMapping("grgztListPage")
    public ModelAndView grgztListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "fzsj/core/grgztList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    // ..
    @RequestMapping("grgztListQuery")
    @ResponseBody
    public JsonPageResult<?> grgztListQuery(HttpServletRequest request, HttpServletResponse response) {
        return fzsjService.getFzsjList(request, response, true, "grgzt");
    }

    // ..
    @RequestMapping("deleteFzsj")
    @ResponseBody
    public JsonResult deleteFzsj(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String instIdStr = RequestUtil.getString(request, "instIds");
            if (StringUtils.isNotBlank(instIdStr)) {
                String[] instIds = instIdStr.split(",");
                for (int index = 0; index < instIds.length; index++) {
                    bpmInstManager.deleteCascade(instIds[index], "");
                }
            }
            String[] ids = uIdStr.split(",");
            return fzsjService.deleteFzsj(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteData", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("fzsjEditPage")
    public ModelAndView fzsjEditPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "fzsj/core/fzsjEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String fzsjId = RequestUtil.getString(request, "fzsjId");
        String nodeId = RequestUtil.getString(request, "nodeId");
        String action = RequestUtil.getString(request, "action");
        String taskStatus = RequestUtil.getString(request, "taskStatus");
        if (StringUtils.isBlank(action)) {
            // 新增或编辑的时候没有nodeId
            if (StringUtils.isBlank(nodeId) || nodeId.contains("PROCESS")) {
                action = "edit";
            } else {
                // 处理任务的时候有nodeId
                action = "task";
            }
        }
        mv.addObject("fzsjId", fzsjId).addObject("action", action).addObject("taskStatus", taskStatus);
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "FZSJ", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("mainGroupId", ContextUtil.getCurrentUser().getMainGroupId());
        mv.addObject("mainGroupName", ContextUtil.getCurrentUser().getMainGroupName());
        boolean flag = fzsjService.downloadPermission(fzsjId);
        mv.addObject("downloadPermission", flag);
        // 返回当前登录人角色信息
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUserId());
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        return mv;
    }

    @RequestMapping("getFzsjDetail")
    @ResponseBody
    public JSONObject getFzsjDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject obj = new JSONObject();
        String fzsjId = RequestUtil.getString(request, "fzsjId");
        if (StringUtils.isNotBlank(fzsjId)) {
            obj = fzsjService.getFzsjDetail(fzsjId);
        }
        return obj;
    }

    @RequestMapping("saveFzsj")
    @ResponseBody
    public JsonResult saveFzsj(HttpServletRequest request, @RequestBody String formData, HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
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
                fzsjService.createFzsj(formDataJson);
            } else {
                fzsjService.updateFzsj(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save fzsj");
            result.setSuccess(false);
            result.setMessage("Exception in save fzsj");
            return result;
        }
        return result;
    }

    @RequestMapping("openFzsjUploadWindow")
    public ModelAndView openFzsjUploadWindow(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String jspPath = "fzsj/core/fzsjFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("belongDetailId", RequestUtil.getString(request, "belongDetailId"));
        return mv;
    }

    @RequestMapping(value = "fzsjUpload")
    @ResponseBody
    public Map<String, Object> fzsjUpload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            fzsjService.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    @RequestMapping("queryFzsjFileList")
    @ResponseBody
    public List<JSONObject> queryFzsjFileList(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String belongDetailId = RequestUtil.getString(request, "belongDetailId");
        if (StringUtils.isBlank(belongDetailId)) {
            logger.error("belongDetailId is blank");
            return null;
        }
        JSONObject params = new JSONObject();
        List<String> fzsjIds = new ArrayList<>();
        fzsjIds.add(belongDetailId);
        params.put("belongDetailIds", fzsjIds);
        List<JSONObject> fileInfos = fzsjService.queryFzsjFileList(params);
        return fileInfos;
    }

    // 删除某个文件（从文件表和磁盘上都删除）
    @RequestMapping("delFzsjFile")
    public void delFzsjFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String id = postBodyObj.getString("id");
        String belongDetailId = postBodyObj.getString("formId");
        fzsjService.deleteFzsjFileById(id, fileName, belongDetailId);
    }

    // 文档的下载（预览pdf也调用这里）
    @RequestMapping("fzsjFileDownload")
    public ResponseEntity<byte[]> fzsjFileDownload(HttpServletRequest request, HttpServletResponse response) {
        try {
            String fileName = RequestUtil.getString(request, "fileName");
            if (StringUtils.isBlank(fileName)) {
                logger.error("操作失败，文件名为空！");
                return null;
            }
            String fileId = RequestUtil.getString(request, "fileId");
            if (StringUtils.isBlank(fileId)) {
                logger.error("操作失败，文件Id为空！");
                return null;
            }
            String belongDetailId = RequestUtil.getString(request, "formId");
            if (StringUtils.isBlank(belongDetailId)) {
                logger.error("操作失败，主表Id为空！");
                return null;
            }
            String filePath = WebAppUtil.getProperty("fzsjFilePathBase");
            if (StringUtils.isBlank(filePath)) {
                logger.error("操作失败，找不到存储路径");
                return null;
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fullFilePath = filePath + File.separator + belongDetailId + File.separator + fileId + "." + suffix;
            // 创建文件实例
            File file = new File(fullFilePath);
            // 修改文件名的编码格式
            String downloadFileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");

            // 设置httpHeaders,使浏览器响应下载
            HttpHeaders headers = new HttpHeaders();
            // 告诉浏览器执行下载的操作，“attachment”告诉了浏览器进行下载,下载的文件 文件名为 downloadFileName
            headers.setContentDispositionFormData("attachment", downloadFileName);
            // 设置响应方式为二进制，以二进制流传输
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception in fileDownload", e);
            return null;
        }
    }

    @RequestMapping("fzsjOfficePreview")
    @ResponseBody
    public void fzsjOfficePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String filePath = WebAppUtil.getProperty("fzsjFilePathBase");
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, filePath, response);
    }

    @RequestMapping("fzsjImagePreview")
    @ResponseBody
    public void fzsjImagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String filePath = WebAppUtil.getProperty("fzsjFilePathBase");
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, filePath, response);
    }

    @RequestMapping("openFzlbDialog")
    public ModelAndView openFzlbDialog(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "fzsj/core/fzlbList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    @RequestMapping("queryFzzx")
    @ResponseBody
    public List<JSONObject> queryFzzx(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String fzsjId = RequestUtil.getString(request, "fzsjId");
        if (StringUtils.isBlank(fzsjId)) {
            logger.error("fzsjId is blank");
            return null;
        }
        return fzsjService.queryFzzx(fzsjId);
    }

    @RequestMapping("openFzzxDialog")
    public ModelAndView openZxclDialog(HttpServletRequest request) {
        String jspPath = "fzsj/core/fzzxEdit.jsp";
        String fzsjId = RequestUtil.getString(request, "fzsjId");
        String fzzxId = RequestUtil.getString(request, "fzzxId");
        String fzzxAction = RequestUtil.getString(request, "fzzxAction");
        String step = RequestUtil.getString(request, "step");
        String downloadPermission = RequestUtil.getString(request, "downloadPermission");
        String detailType = RequestUtil.getString(request, "detailType");
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("fzsjId", fzsjId);
        mv.addObject("fzzxId", fzzxId);
        mv.addObject("fzzxAction", fzzxAction);
        mv.addObject("step", step);
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("downloadPermission", downloadPermission);
        mv.addObject("detailType", detailType);
        return mv;
    }

    @RequestMapping("getFzzxDetail")
    @ResponseBody
    public JSONObject getFzzxDetail(HttpServletRequest request) {
        String fzzxId = RequestUtil.getString(request, "fzzxId");
        return fzsjService.getFzzxDetail(fzzxId);
    }

    @RequestMapping("saveFzzx")
    @ResponseBody
    public JsonResult saveFzzx(HttpServletRequest request, @RequestBody String formData, HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
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
                fzsjService.createFzzx(formDataJson);
            } else {
                fzsjService.updateFzzx(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save fzzx", e);
            result.setSuccess(false);
            result.setMessage("Exception in save fzzx");
            return result;
        }
        return result;
    }

    @RequestMapping("fzzxAddValid")
    @ResponseBody
    public JsonResult fzzxAddValid(HttpServletRequest request) {
        String fzsjId = RequestUtil.getString(request, "fzsjId");
        String fzzxAction = RequestUtil.getString(request, "fzzxAction");
        String step = RequestUtil.getString(request, "step");
        return fzsjService.fzzxAddValid(fzsjId, fzzxAction, step);
    }

    @RequestMapping("deleteFzzx")
    @ResponseBody
    public JsonResult deleteFzzx(HttpServletRequest request) {
        String fzzxId = RequestUtil.getString(request, "fzzxId");
        return fzsjService.deleteFzzx(fzzxId);
    }

    @RequestMapping("fzszrshValid")
    @ResponseBody
    public JsonResult fzszrshValid(HttpServletRequest request) {
        String fzsjId = RequestUtil.getString(request, "fzsjId");
        return fzsjService.fzszrshValid(fzsjId);
    }

    @RequestMapping("fzrwhzListPage")
    public ModelAndView fzrwhzListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "fzsj/core/fzrwhzList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        List<JSONObject> nodeSetListWithName =
                commonBpmManager.getNodeSetListWithName("FZSJ", "userTask");
        mv.addObject("nodeSetListWithName", nodeSetListWithName);
        return mv;
    }

    // ..
    @RequestMapping("fzrwhzListQuery")
    @ResponseBody
    public JsonPageResult<?> fzrwhzListQuery(HttpServletRequest request, HttpServletResponse response) {
        return fzsjService.getFzsjList(request, response, true, "fzrwhz");
    }

    @RequestMapping("ycxmglListPage")
    public ModelAndView ycxmglListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "fzsj/core/ycxmglList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    // ..
    @RequestMapping("ycxmglListQuery")
    @ResponseBody
    public JsonPageResult<?> ycxmglListQuery(HttpServletRequest request, HttpServletResponse response) {
        return fzsjService.getFzsjList(request, response, true, "ycxmg");
    }

    @RequestMapping("getYearList")
    @ResponseBody
    public List<JSONObject> getYearList(HttpServletRequest request, HttpServletResponse response) {
        return fzsjService.getYearList();
    }

    @RequestMapping("getNdtjList")
    @ResponseBody
    public List<JSONObject> getNdtjList(HttpServletRequest request, HttpServletResponse response) {
        String startYear = RequestUtil.getString(request, "startYear");
        String endYear = RequestUtil.getString(request, "endYear");
        return fzsjService.getNdtjList(startYear, endYear);
    }

    //..作废完给历史审批者发个通知
    @RequestMapping("sendMessageAfterDiscard")
    @ResponseBody
    public void sendMessageAfterDiscard(HttpServletRequest request, HttpServletResponse response,
                                        @RequestBody String requestData) {
        JSONObject requestDataJson = JSONObject.parseObject(requestData);
        try {
            fzsjService.sendMessageAfterDiscard(requestDataJson);
        } catch (Exception e) {
            logger.error("Exception in sendMessageAfterDiscard", e);
        }
    }
}
