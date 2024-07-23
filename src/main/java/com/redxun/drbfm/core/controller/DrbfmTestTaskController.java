package com.redxun.drbfm.core.controller;

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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.drbfm.core.dao.DrbfmTestTaskDao;
import com.redxun.drbfm.core.service.DrbfmTestTaskService;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.sys.core.util.SysPropertiesUtil;

@Controller
@RequestMapping("/drbfm/testTask/")
public class DrbfmTestTaskController {
    private static final Logger logger = LoggerFactory.getLogger(DrbfmTestTaskController.class);
    @Autowired
    private DrbfmTestTaskService drbfmTestTaskService;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private DrbfmTestTaskDao drbfmTestTaskDao;
    @Resource
    private BpmInstManager bpmInstManager;
    @Autowired
    private CommonBpmManager commonBpmManager;

    // 单一项目的主体框架页面
    @RequestMapping("testTaskListPage")
    public ModelAndView singleFramePage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "drbfm/drbfmTestTaskList.jsp";
        String singleId = RequestUtil.getString(request, "singleId");
        ModelAndView mv = new ModelAndView(jspPath);
        List<Map<String, String>> nodeVars = new ArrayList<>();
        mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
        mv.addObject("singleId", singleId);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }

    @RequestMapping("getTestTaskList")
    @ResponseBody
    public JsonPageResult<?> getTestTaskList(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        boolean doPage = false;
        String doPageStr = RequestUtil.getString(request, "doPage");
        if (!doPageStr.isEmpty() && "true".equalsIgnoreCase(doPageStr)) {
            doPage = true;
        }
        return drbfmTestTaskService.getTestTaskList(request, response, doPage);
    }

    @RequestMapping("testTaskEditPage")
    public ModelAndView testTaskEditPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "drbfm/drbfmTestTaskEdit.jsp";
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String applyId = RequestUtil.getString(request, "applyId", "");
        String singleId = RequestUtil.getString(request, "singleId", "");
        String nodeId = RequestUtil.getString(request, "nodeId", "");
        String action = RequestUtil.getString(request, "action", "");
        String status = RequestUtil.getString(request, "status", "");
        String testType = RequestUtil.getString(request, "testType", "");
        if (StringUtils.isBlank(testType)) {
            testType = "专项测试";
        }
        if ("常规附带".equals(testType)) {
            jspPath = "drbfm/drbfmNoFlowTestTaskEdit.jsp";
        }
        ModelAndView mv = new ModelAndView(jspPath);
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
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "drbfmTestTask", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        mv.addObject("applyId", applyId);
        mv.addObject("action", action);
        mv.addObject("status", status);
        if (StringUtils.isBlank(singleId) && StringUtils.isNotBlank(applyId)) {
            JSONObject applyDetail = drbfmTestTaskService.queryApplyDetail(applyId);
            if (applyDetail != null) {
                singleId = applyDetail.getString("belongSingleId");
            }
        }
        mv.addObject("singleId", singleId);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentDeptId", ContextUtil.getCurrentUser().getMainGroupId());
        mv.addObject("currentDeptName", ContextUtil.getCurrentUser().getMainGroupName());
        mv.addObject("testType", testType);
        return mv;
    }

    @RequestMapping("getJson")
    @ResponseBody
    public JSONObject queryApplyDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = RequestUtil.getString(request, "id", "");
        if (StringUtils.isBlank(id)) {
            JSONObject result = new JSONObject();
            result.put("CREATE_BY_", ContextUtil.getCurrentUser().getUserId());
            return result;
        }
        return drbfmTestTaskService.queryApplyDetail(id);
    }

    // 附件列表
    @RequestMapping("demandList")
    @ResponseBody
    public List<JSONObject> demandList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String applyId = RequestUtil.getString(request, "applyId", "");
        String fileType = RequestUtil.getString(request, "fileType", "");
        if (StringUtils.isBlank(applyId)) {
            return null;
        }
        JSONObject params = new JSONObject();
        params.put("applyId", applyId);
        params.put("fileType", fileType);
        List<JSONObject> demandList = drbfmTestTaskService.queryDemandList(params);

        return demandList;
    }

    // 指标列表
    @RequestMapping("testTaskDemandList")
    @ResponseBody
    public List<JSONObject> testTaskDemandList(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        String applyId = RequestUtil.getString(request, "applyId", "");
        if (StringUtils.isBlank(applyId)) {
            return null;
        }
        JSONObject params = new JSONObject();
        params.put("applyId", applyId);
        List<JSONObject> demandList = drbfmTestTaskService.queryTestTaskDemandList(params);

        return demandList;
    }

    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "/drbfm/testTaskFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String applyId = RequestUtil.getString(request, "applyId", "");
        String fileType = RequestUtil.getString(request, "fileType", "");
        String belongSingleId = RequestUtil.getString(request, "belongSingleId", "");
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("applyId", applyId);
        mv.addObject("fileType", fileType);
        mv.addObject("belongSingleId", belongSingleId);
        return mv;
    }

    // 文件相关

    @RequestMapping("upload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            drbfmTestTaskService.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    @RequestMapping("/fileDownload")
    public ResponseEntity<byte[]> fileDownload(HttpServletRequest request, HttpServletResponse response) {
        try {
            String fileName = RequestUtil.getString(request, "fileName");
            if (StringUtils.isBlank(fileName)) {
                // logger.error("操作失败，文件名为空！");
                return null;
            }
            String fileId = RequestUtil.getString(request, "fileId");
            if (StringUtils.isBlank(fileId)) {
                // logger.error("操作失败，文件主键为空！");
                return null;
            }
            String formId = RequestUtil.getString(request, "formId");
            String fileBasePath = SysPropertiesUtil.getGlobalProperty("drbfm");
            if (StringUtils.isBlank(fileBasePath)) {
                // logger.error("操作失败，找不到存储根路径");
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

        String fileBasePath = SysPropertiesUtil.getGlobalProperty("drbfm");
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, formId, fileBasePath);
        // 清除文件后,将表单中的文件信息置空
        JSONObject param = new JSONObject();
        param.put("id", fileId);
        drbfmTestTaskDao.deleteDemand(param);

    }

    @RequestMapping("/preview")
    public ResponseEntity<byte[]> filePreview(HttpServletRequest request, HttpServletResponse response) {
        ResponseEntity<byte[]> result = null;
        String fileType = RequestUtil.getString(request, "fileType");
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = SysPropertiesUtil.getGlobalProperty("drbfm");
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

    @RequestMapping("deleteTestTask")
    @ResponseBody
    public JsonResult deleteApply(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String instIdStr = RequestUtil.getString(request, "instIds");
            if (StringUtils.isNotBlank(instIdStr)) {
                String[] instIds = instIdStr.split(",");
                for (int index = 0; index < instIds.length; index++) {
                    String instId = instIds[index];
                    if (StringUtils.isNotBlank(instId)) {
                        bpmInstManager.deleteCascade(instId, "");
                    }
                }
            }
            String[] ids = uIdStr.split(",");
            return drbfmTestTaskService.delApplys(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteTestTask", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("queryQuotaTestData")
    @ResponseBody
    public List<JSONObject> queryQuotaTestData(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        String belongSingleId = RequestUtil.getString(request, "belongSingleId", "");
        String relQuotaId = RequestUtil.getString(request, "relQuotaId", "");
        if (StringUtils.isBlank(belongSingleId) || StringUtils.isBlank(relQuotaId)) {
            return null;
        }
        JSONObject params = new JSONObject();
        params.put("belongSingleId", belongSingleId);
        params.put("relQuotaId", relQuotaId);
        List<JSONObject> testDataList = drbfmTestTaskDao.queryQuotaTestData(params);
        for (JSONObject oneData : testDataList) {
            if (oneData.getDate("testActualEndTime") != null) {
                oneData.put("testActualEndTime",
                    DateFormatUtil.format(oneData.getDate("testActualEndTime"), "yyyy-MM-dd HH:mm"));
            }
        }
        return testDataList;
    }

    @RequestMapping(value = "noFlowTestTaskSave", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult noFlowTestTaskSave(HttpServletRequest request, @RequestBody String postDataStr,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(postDataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("请求体为空！");
            return result;
        }
        try {
            JSONObject formDataJson = JSONObject.parseObject(postDataStr);
            drbfmTestTaskService.noFlowTestTaskSave(formDataJson, result);
        } catch (Exception e) {
            logger.error("Exception in noFlowTestTaskSave");
            result.setSuccess(false);
            result.setMessage("系统异常，请联系管理员！");
            return result;
        }
        return result;
    }
}
