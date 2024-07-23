package com.redxun.trialPartsProcess.core.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.trialPartsProcess.core.dao.TrialPartsProcessDao;
import com.redxun.trialPartsProcess.core.service.TrialPartsProcessService;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.util.*;

@RestController
@RequestMapping("/trialPartsProcess/core/trialPartsProcess/")
public class TrialPartsProcessController {
    private Logger logger = LogManager.getLogger(TrialPartsProcessController.class);
    @Autowired
    CommonBpmManager commonBpmManager;
    @Autowired
    TrialPartsProcessService trialPartsProcessService;
    @Autowired
    SysDicManager sysDicManager;
    @Autowired
    RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    TrialPartsProcessDao trialPartsProcessDao;
    @Autowired
    BpmInstManager bpmInstManager;

    @RequestMapping("listPage")
    public ModelAndView listPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "trialPartsProcess/core/trialPartsProcessList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        return mv;
    }

    @RequestMapping("applyList")
    @ResponseBody
    public JsonPageResult<?> queryApplyList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return trialPartsProcessService.queryBaseInfoList(request, true);
    }

    @RequestMapping("applyEditPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "trialPartsProcess/core/trialPartsProcessEdit.jsp";
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
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "SZLBJJDGZ", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("applyId", applyId);
        mv.addObject("action", action);

        return mv;
    }

    @RequestMapping("getJson")
    @ResponseBody
    public JSONObject queryApplyDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = RequestUtil.getString(request, "id", "");
        if (StringUtils.isBlank(id)) {
            JSONObject result = new JSONObject();
            result.put("creater", ContextUtil.getCurrentUser().getFullname());
            result.put("deptName", ContextUtil.getCurrentUser().getMainGroupName());
            return result;
        }
        JSONObject jsonObject = trialPartsProcessService.queryApplyDetail(id);
        if (jsonObject.get("planDate") != null) {
            jsonObject.put("planDate", DateUtil.formatDate((Date)jsonObject.get("planDate"), "yyyy-MM-dd"));
        }
        Set<String> userIdSet = new HashSet<>();
        if (StringUtils.isNotBlank(jsonObject.getString("memberInfo"))) {
            userIdSet.addAll(Arrays.asList(jsonObject.getString("memberInfo").split(",", -1)));
        }
        if (!userIdSet.isEmpty()) {
            Map<String, String> userId2Name = trialPartsProcessService.queryUserNameByIds(userIdSet);
            if (StringUtils.isNotBlank(jsonObject.getString("memberInfo"))) {
                List<String> userIds = Arrays.asList(jsonObject.getString("memberInfo").split(",", -1));
                String memberNames = trialPartsProcessService.toGetUserNamesByIds(userId2Name, userIds);
                jsonObject.put("memberNames", memberNames);
            }
        }
        return jsonObject;
    }

    @RequestMapping("queryBatchInfo")
    @ResponseBody
    public List<JSONObject> queryBatchInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return trialPartsProcessService.queryBatchInfo(request);
    }

    @RequestMapping("queryBatchInfoById")
    @ResponseBody
    public JSONObject queryBatchInfoById(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return trialPartsProcessService.queryBatchInfoById(request);
    }


    @RequestMapping("queryBatchDetailInfo")
    @ResponseBody
    public List<JSONObject> queryBatchDetailInfo(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        return trialPartsProcessService.queryBatchDetailInfo(request);
    }

    @RequestMapping("saveBaseInfo")
    @ResponseBody
    public JsonResult saveBaseInfo(HttpServletRequest request, @RequestBody String formDataStr,
        HttpServletResponse response) throws Exception {
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
                trialPartsProcessService.saveBaseInfo(formDataJson);
            } else {
                trialPartsProcessService.updateBaseInfo(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save BaseInfo");
            result.setSuccess(false);
            result.setMessage("Exception in save BaseInfo");
            return result;
        }
        return result;

    }

    @RequestMapping("deleteBaseInfo")
    public JsonResult deleteBaseInfo(HttpServletRequest request, HttpServletResponse response) {
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
            return trialPartsProcessService.deleteBaseInfo(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteBaseInfo", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("saveBatchInfo")
    @ResponseBody
    public JsonResult saveBatchInfo(HttpServletRequest request, HttpServletResponse response,
        @RequestBody String postBody) throws Exception {
        JsonResult result = new JsonResult(true, "");
        String applyId = RequestUtil.getString(request, "applyId", "");
        if (StringUtils.isBlank(applyId)) {
            logger.warn("applyId is blank");
            result.setSuccess(false);
            result.setMessage("applyId is blank");
            return result;
        }
        try {
            trialPartsProcessService.saveBatchInfo(request,postBody);
        } catch (Exception e) {
            logger.error("Exception in saveBatchInfo", e);
            result.setSuccess(false);
            result.setMessage("Exception in saveBatchInfo");
        }
        return result;
    }

    @RequestMapping("deleteBatchInfo")
    public void deleteBatchInfo(HttpServletRequest request, HttpServletResponse response) {
        String id = RequestUtil.getString(request, "id", "");
        String applyId = RequestUtil.getString(request, "applyId", "");
        String trialBatch = RequestUtil.getString(request, "trialBatch", "");
        if (StringUtils.isBlank(id) || StringUtils.isBlank(applyId)) {
            return;
        }
        JSONObject param = new JSONObject();
        param.put("id", id);
        //删除批次信息
        trialPartsProcessDao.deleteTrialPartsProcessBatch(param);
        //删除批次附属信息
        JSONObject params = new JSONObject();
        params.put("applyId", applyId);
        params.put("trialBatch", trialBatch);
        trialPartsProcessDao.deleteTrialPartsProcessBatchDetailByTrialBatch(params);
    }

    @RequestMapping("saveBatchDetail")
    @ResponseBody
    public JsonResult saveBatchDetail(HttpServletRequest request, HttpServletResponse response,
        @RequestBody String postBody) throws Exception {
        JsonResult result = new JsonResult(true, "");
        String applyId = RequestUtil.getString(request, "applyId", "");
        if (StringUtils.isBlank(applyId)) {
            logger.warn("applyId is blank");
            result.setSuccess(false);
            result.setMessage("applyId is blank");
            return result;
        }
        try {
            trialPartsProcessService.saveBatchDetail(request,postBody);
        } catch (Exception e) {
            logger.error("Exception in saveBatchDetail", e);
            result.setSuccess(false);
            result.setMessage("Exception in saveBatchDetail");
        }
        return result;
    }

    @RequestMapping("deleteDetailInfo")
    public void deleteDetailInfo(HttpServletRequest request, HttpServletResponse response) {
        String id = RequestUtil.getString(request, "id", "");
        if (StringUtils.isBlank(id)) {
            return;
        }
        JSONObject param = new JSONObject();
        param.put("id", id);
        //删除批次附属信息
        trialPartsProcessDao.deleteTrialPartsProcessBatchDetail(param);
    }

    @RequestMapping("upload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            trialPartsProcessService.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;

    }

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
        List<JSONObject> fileList = trialPartsProcessService.queryFileList(params);

        return fileList;
    }

    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "trialPartsProcess/core/trialPartsProcessFileUpload.jsp";
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
            String fileBasePath = sysDicManager.getBySysTreeKeyAndDicKey("SZLBJJDGZ", "wjscwz").getValue();
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

    @RequestMapping("deleteFiles")
    public void deleteFiles(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileId = postBodyObj.getString("id");
        if (StringUtils.isBlank(fileId)) {
            return;
        }
        String fileName = postBodyObj.getString("fileName");
        String formId = postBodyObj.getString("formId");
        String fileBasePath = sysDicManager.getBySysTreeKeyAndDicKey("SZLBJJDGZ", "wjscwz").getValue();
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, formId, fileBasePath);
        // 清除文件后,将表单中的文件信息置空
        JSONObject param = new JSONObject();
        param.put("id", fileId);
        trialPartsProcessDao.deleteFile(param);
    }

    @RequestMapping("/preview")
    public ResponseEntity<byte[]> filePreview(HttpServletRequest request, HttpServletResponse response) {
        ResponseEntity<byte[]> result = null;
        String fileType = RequestUtil.getString(request, "fileType");
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = sysDicManager.getBySysTreeKeyAndDicKey("SZLBJJDGZ", "wjscwz").getValue();
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

    @RequestMapping("/exportTrialPratsInfo")
    public void exportTrialPratsInfo(HttpServletRequest request, HttpServletResponse response) {
        trialPartsProcessService.exportTrialPratsInfo(request, response);
    }
}
