package com.redxun.environment.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.environment.core.dao.FailureAnalysisCasePresentationDao;
import com.redxun.environment.core.service.FailureAnalysisCasePresentationService;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
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
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

@Controller
@RequestMapping("/environment/core/failureAnalysisCase/")
public class FailureAnalysisCasePresentationController {
    private static final Logger logger = LoggerFactory.getLogger(FailureAnalysisCasePresentationController.class);
    @Autowired
    private FailureAnalysisCasePresentationService failureAnalysisCasePresentationService;
    @Resource
    private BpmInstManager bpmInstManager;
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private FailureAnalysisCasePresentationDao failureAnalysisCasePresentationDao;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;


    @RequestMapping("listPage")
    public ModelAndView getListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rap/core/failureAnalysisCasePresentationList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String yearMonth = DateFormatUtil.format(new Date(), "yyyy-MM");
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("yearMonth", yearMonth);
        // 只有所长才可以选优秀案例
        JSONObject currentUserDepInfo = xcmgProjectManager.isCurrentUserDepRespman();
        boolean isSuozhang = currentUserDepInfo.getBoolean("isDepRespMan");
        mv.addObject("isSuozhang", isSuozhang);
        return mv;
    }

    @RequestMapping("applyList")
    @ResponseBody
    public JsonPageResult<?> queryApplyList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return failureAnalysisCasePresentationService.queryApplyList(request, true);
    }

    @RequestMapping("deleteApply")
    @ResponseBody
    public JsonResult deleteApply(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
            return failureAnalysisCasePresentationService.delApplys(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteApply", e);
            return new JsonResult(false, e.getMessage());
        }
    }
    @RequestMapping("applyEditPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rap/core/failureAnalysisCasePresentationEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String applyId = RequestUtil.getString(request, "applyId", "");
        String nodeId = RequestUtil.getString(request, "nodeId", "");
        String action = RequestUtil.getString(request, "action", "");
        if (StringUtils.isBlank(action)) {
            // 新增或编辑的时候没有nodeId
            if (StringUtils.isBlank(nodeId) || nodeId.contains("PROCESS")) {
                action = "edit";
            } else {
                // 处理任务的时候有nodeId
                action = "task";
            }
        }
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "DLSGZFXALTB", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("applyId", applyId);
        mv.addObject("action", action);
        // 所长可以进行管理员编辑，对非批准状态下的案例均可以进行上传、删除操作
        JSONObject currentUserDepInfo = xcmgProjectManager.isCurrentUserDepRespman();
        boolean isSuozhang = currentUserDepInfo.getBoolean("isDepRespMan");
        mv.addObject("isSuozhang", isSuozhang);

        return mv;
    }
    @RequestMapping("getJson")
    @ResponseBody
    public JSONObject queryApplyDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = RequestUtil.getString(request, "id", "");
        if (StringUtils.isBlank(id)) {
            JSONObject result = new JSONObject();
            result.put("createrName", ContextUtil.getCurrentUser().getFullname());
            result.put("yearMonth", DateFormatUtil.format(new Date(), "yyyy-MM"));
            return result;
        }
        return failureAnalysisCasePresentationService.queryApplyDetail(id);
    }

    @RequestMapping("upload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            failureAnalysisCasePresentationService.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rap/core/failureAnalysisCasePresentationFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String applyId = RequestUtil.getString(request, "applyId", "");
        String fileType = RequestUtil.getString(request, "fileType", "");
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("applyId", applyId);
        mv.addObject("fileType", fileType);
        return mv;
    }

    @RequestMapping("/fileDownload")
    public ResponseEntity<byte[]> fileDownload(HttpServletRequest request, HttpServletResponse response) {
        try {
            String fileName = RequestUtil.getString(request, "fileName");
            if (StringUtils.isBlank(fileName)) {
                logger.error("操作失败，文件名为空！");
                return null;
            }
            String fileId = RequestUtil.getString(request, "fileId");
            if (StringUtils.isBlank(fileId)) {
                logger.error("操作失败，文件主键为空！");
                return null;
            }
            String formId = RequestUtil.getString(request, "formId");
            String fileBasePath = sysDicManager.getBySysTreeKeyAndDicKey("powerApplicationTechnologyUploadPosition", "gzaltbwjscwz").getValue();
            if (StringUtils.isBlank(fileBasePath)) {
                logger.error("操作失败，找不到存储根路径");
                return null;
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String relativeFilePath = "";
            if (StringUtils.isNotBlank(formId)) {
                relativeFilePath = File.separator + formId;
            }
            String realFileName = fileId + "." + suffix;
            String fullFilePath = fileBasePath + relativeFilePath + File.separator + realFileName;
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

    // 清除文件
    @RequestMapping("deleteFiles")
    public void deleteFiles(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileId = postBodyObj.getString("id");
        if (StringUtils.isBlank(fileId)) {
            return;
        }
        String fileName = postBodyObj.getString("fileName");
        String formId = postBodyObj.getString("formId");
        String fileBasePath = sysDicManager.getBySysTreeKeyAndDicKey("powerApplicationTechnologyUploadPosition", "gzaltbwjscwz").getValue();
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, formId, fileBasePath);
        // 清除文件后,将表单中的文件信息置空
        JSONObject param = new JSONObject();
        param.put("id", fileId);
        failureAnalysisCasePresentationDao.deleteFile(param);

    }

    @RequestMapping("/preview")
    public ResponseEntity<byte[]> filePreview(HttpServletRequest request, HttpServletResponse response) {
        ResponseEntity<byte[]> result = null;
        String fileType = RequestUtil.getString(request, "fileType");
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = sysDicManager.getBySysTreeKeyAndDicKey("powerApplicationTechnologyUploadPosition", "gzaltbwjscwz").getValue();
        switch (fileType) {
            case "pdf":
                result = rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, fileBasePath);
            case "office":
                rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, fileBasePath, response);
                break;
            case "pic":
                rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, fileBasePath, response);
                break;
        }
        return result;
    }

    // 文件列表
    @RequestMapping("fileList")
    @ResponseBody
    public List<JSONObject> fileList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String applyId = RequestUtil.getString(request, "applyId", "");
        if (StringUtils.isBlank(applyId)) {
            logger.error("applyId is blank");
            return null;
        }
        JSONObject params = new JSONObject();
        params.put("applyId", applyId);
        List<JSONObject> fileList = failureAnalysisCasePresentationService.queryFileList(params);

        return fileList;
    }

    @RequestMapping("chooseYxal")
    @ResponseBody
    public JsonResult chooseYxal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return failureAnalysisCasePresentationService.chooseYxal(ids);
        } catch (Exception e) {
            logger.error("Exception in chooseYxal", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("cancelYxal")
    @ResponseBody
    public JsonResult cancelYxal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return failureAnalysisCasePresentationService.cancelYxal(ids);
        } catch (Exception e) {
            logger.error("Exception in confirmApply", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("saveBusiness")
    @ResponseBody
    public JsonResult saveBusiness(HttpServletRequest request, @RequestBody String formDataStr,
                                   HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(formDataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        try {
            JSONObject formDataJson = JSONObject.parseObject(formDataStr);
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("id"))) {
                failureAnalysisCasePresentationService.createCase(formDataJson);
            } else {
                failureAnalysisCasePresentationService.updateCase(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save failureAnalysisCase");
            result.setSuccess(false);
            result.setMessage("Exception in save failureAnalysisCase");
            return result;
        }
        return result;
    }
    @RequestMapping("/caseTemplateDownload")
    public ResponseEntity<byte[]> importTemplateDownload(HttpServletRequest request, HttpServletResponse response) {
        return failureAnalysisCasePresentationService.importTemplateDownload();
    }
    @RequestMapping("isExist")
    @ResponseBody
    public JSONObject isExist(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        result.put("success", false);
        String applyId = RequestUtil.getString(request, "applyId", "");
        String caseName = RequestUtil.getString(request, "caseName", "");
        Map<String, Object> param = new HashMap<>();
        param.put("caseName", caseName);
        List<JSONObject> caseList = failureAnalysisCasePresentationDao.queryExist(param);
        Iterator<JSONObject> iterator = caseList.iterator();
        while (iterator.hasNext()) {
            if (!applyId.equals(iterator.next().get("id"))) {
                result.put("success", true);
            }
        }
        return result;
    }
}
