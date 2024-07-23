package com.redxun.componentTest.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.componentTest.core.service.ComponentTestKanbanService;
import com.redxun.core.database.datasource.DbContextHolder;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.standardManager.core.util.StandardManagerUtil;
import com.redxun.sys.core.manager.SysDicManager;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/componentTest/core/kanban")
public class ComponentTestKanbanController {
    private static final Logger logger = LoggerFactory.getLogger(ComponentTestKanbanController.class);
    @Autowired
    private ComponentTestKanbanService componentTestKanbanService;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;

    //..
    @RequestMapping("dataListPage")
    public ModelAndView dataListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "componentTest/core/componentTestKanbanList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    //..
    @RequestMapping("kanbanPage")
    public ModelAndView kanbanPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "componentTest/core/componentTestKanban.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    //..
    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "componentTest/core/componentTestKanbanEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String businessId = RequestUtil.getString(request, "businessId");
        String action = RequestUtil.getString(request, "action");
        boolean isGlNetwork = StandardManagerUtil.judgeGLNetwork(request);
        mv.addObject("isGlNetwork", isGlNetwork);
        mv.addObject("businessId", businessId).addObject("action", action);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        boolean isComponentTestAdmin = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), "零部件试验管理员");
        mv.addObject("isComponentTestAdmin", isComponentTestAdmin);
        return mv;
    }

    //..
    @RequestMapping("dataListQuery")
    @ResponseBody
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        return componentTestKanbanService.dataListQuery(request, response);
    }

    //..
    @RequestMapping("queryDataById")
    @ResponseBody
    public JSONObject queryDataById(HttpServletRequest request, HttpServletResponse response) {
        String businessId = RequestUtil.getString(request, "businessId");
        if (StringUtils.isBlank(businessId)) {
            logger.error("Id is blank");
            return null;
        }
        return componentTestKanbanService.queryDataById(businessId);
    }

    //..
    @RequestMapping(value = "saveBusiness", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveBusiness(HttpServletRequest request, @RequestBody String DataStr,
                                   HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
        if (StringUtils.isBlank(DataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("表单为空，保存失败！");
            return result;
        }
        componentTestKanbanService.saveBusiness(result, DataStr);
        return result;
    }

    //..
    @RequestMapping("deleteBusiness")
    @ResponseBody
    public JsonResult deleteBusiness(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return componentTestKanbanService.deleteBusiness(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteData", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    //..
    @RequestMapping("exportExcel")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        componentTestKanbanService.exportExcel(request, response);
    }

    //..
    @RequestMapping("importExcel")
    @ResponseBody
    public JSONObject importExcel(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        componentTestKanbanService.importExcel(result, request);
        return result;
    }

    //..
    @RequestMapping("importTemplateDownload")
    public ResponseEntity<byte[]> importTemplateDownload(HttpServletRequest request, HttpServletResponse response) {
        return componentTestKanbanService.importTemplateDownload();
    }

    //..
    @RequestMapping("getTestContractFileList")
    @ResponseBody
    public List<JSONObject> getTestContractFileList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<String> mainKanbanIdList = Arrays.asList(RequestUtil.getString(request, "mainKanbanId", ""));
        return componentTestKanbanService.getTestContractFileList(mainKanbanIdList);
    }

    //..
    @RequestMapping("getTestReportFileList")
    @ResponseBody
    public List<JSONObject> getTestReportFileList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<String> mainKanbanIdList = Arrays.asList(RequestUtil.getString(request, "mainKanbanId", ""));
        return componentTestKanbanService.getTestReportFileList(mainKanbanIdList);
    }

    //..
    @RequestMapping("getTestMPTFileList")
    @ResponseBody
    public List<JSONObject> getTestMPTFileList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<String> mainKanbanIdList = Arrays.asList(RequestUtil.getString(request, "mainKanbanId", ""));
        return componentTestKanbanService.getTestMPTFileList(mainKanbanIdList);
    }

    //..
    @RequestMapping("openContractUploadWindow")
    public ModelAndView openContractUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "componentTest/core/componentTestContractFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("mainKanbanId", RequestUtil.getString(request, "mainKanbanId", ""));
        return mv;
    }

    //..
    @RequestMapping("openReportUploadWindow")
    public ModelAndView openReportUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "componentTest/core/componentTestReportFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("mainKanbanId", RequestUtil.getString(request, "mainKanbanId", ""));
        return mv;
    }

    //..
    @RequestMapping("openMPTUploadWindow")
    public ModelAndView openMPTUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "componentTest/core/componentTestMPTFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("mainKanbanId", RequestUtil.getString(request, "mainKanbanId", ""));
        return mv;
    }

    //..
    @RequestMapping("pdfPreviewAndAllDownloadContract")
    public ResponseEntity<byte[]> pdfPreviewAndAllDownloadContract(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "componentTestUploadPosition", "testContract").getValue();
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, filePathBase);
    }

    //..
    @RequestMapping("pdfPreviewAndAllDownloadReport")
    public ResponseEntity<byte[]> pdfPreviewAndAllDownloadReport(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "componentTestUploadPosition", "testReport").getValue();
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, filePathBase);
    }

    //..
    @RequestMapping("pdfPreviewAndAllDownloadMPT")
    public ResponseEntity<byte[]> pdfPreviewAndAllDownloadMPT(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "componentTestUploadPosition", "MPT").getValue();
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, filePathBase);
    }

    //..
    @RequestMapping("officePreviewContract")
    @ResponseBody
    public void officePreviewContract(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "componentTestUploadPosition", "testContract").getValue();
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, filePathBase, response);
    }

    //..
    @RequestMapping("officePreviewReport")
    @ResponseBody
    public void officePreviewReport(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "componentTestUploadPosition", "testReport").getValue();
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, filePathBase, response);
    }

    //..
    @RequestMapping("officePreviewMPT")
    @ResponseBody
    public void officePreviewMPT(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "componentTestUploadPosition", "MPT").getValue();
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, filePathBase, response);
    }

    //..
    @RequestMapping("imagePreviewContract")
    @ResponseBody
    public void imagePreviewContract(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "componentTestUploadPosition", "testContract").getValue();
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, filePathBase, response);
    }

    //..
    @RequestMapping("imagePreviewReport")
    @ResponseBody
    public void imagePreviewReport(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "componentTestUploadPosition", "testReport").getValue();
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, filePathBase, response);
    }

    //..
    @RequestMapping("imagePreviewMPT")
    @ResponseBody
    public void imagePreviewMPT(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "componentTestUploadPosition", "MPT").getValue();
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, filePathBase, response);
    }

    //..
    @RequestMapping("delContractFile")
    public void delContractFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String mainId = postBodyObj.getString("formId");
        componentTestKanbanService.deleteOneContractFile(fileId, fileName, mainId);
    }

    //..
    @RequestMapping("delReportFile")
    public void delReportFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String mainId = postBodyObj.getString("formId");
        componentTestKanbanService.deleteOneReportFile(fileId, fileName, mainId);
    }

    //..
    @RequestMapping("delMPTFile")
    public void delMPTFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String mainId = postBodyObj.getString("formId");
        componentTestKanbanService.deleteOneMPTFile(fileId, fileName, mainId);
    }

    //..
    @RequestMapping("contractFileUpload")
    @ResponseBody
    public Map<String, Object> contractFileUpload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            //先保存文件，成功后再写入数据库，前台是一个一个文件的调用
            componentTestKanbanService.saveContractUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    //..保存合同编号2022-06-30
    @RequestMapping(value = "contractFileNameSave", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult contractFileNameSave(HttpServletRequest request, @RequestBody String businessDataStr,
                                           HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
        componentTestKanbanService.contractFileNameSave(result, businessDataStr);
        return result;
    }

    //..
    @RequestMapping("reportFileUpload")
    @ResponseBody
    public Map<String, Object> reportFileUpload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            //先保存文件，成功后再写入数据库，前台是一个一个文件的调用
            componentTestKanbanService.saveReportUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    //..保存报告编号2022-06-30
    @RequestMapping(value = "reportFileNameSave", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult reportFileNameSave(HttpServletRequest request, @RequestBody String businessDataStr,
                                         HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
        componentTestKanbanService.reportFileNameSave(result, businessDataStr);
        return result;
    }

    //..
    @RequestMapping("MPTFileUpload")
    @ResponseBody
    public Map<String, Object> MPTFileUpload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            //先保存文件，成功后再写入数据库，前台是一个一个文件的调用
            componentTestKanbanService.saveMPTUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    //..以下标准相关
    @RequestMapping("bindingStandard")
    @ResponseBody
    public JsonResult bindingStandard(HttpServletRequest request, HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "关联成功");
        try {
            String mainId = RequestUtil.getString(request, "mainId");
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return componentTestKanbanService.bindingStandard(mainId, ids);
        } catch (Exception e) {
            logger.error("Exception in bindingStandard", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    //..
    @RequestMapping("deleteStandard")
    @ResponseBody
    public JsonResult deleteStandard(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String mainId = RequestUtil.getString(request, "mainId");
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return componentTestKanbanService.deleteStandard(mainId, ids);
        } catch (Exception e) {
            logger.error("Exception in deleteStandard", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    //..
    @RequestMapping("bindingStandardMsgListPage")
    public ModelAndView bindingStandardMsgListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "componentTest/core/componentTestBindingStandardMsgList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String mainId = RequestUtil.getString(request, "mainId");
        String action = RequestUtil.getString(request, "action");
        mv.addObject("mainId", mainId).addObject("action", action);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }

    //..
    @RequestMapping("queryBindingStandardMsgList")
    @ResponseBody
    public JsonPageResult<?> queryBindingStandardMsgList(HttpServletRequest request, HttpServletResponse response) {
        return componentTestKanbanService.queryBindingStandardMsgList(request, true);
    }

    //..
    @RequestMapping("getBindingStandardMsg")
    @ResponseBody
    public JSONObject getBindingStandardMsg(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject jsonObject = new JSONObject();
        String id = RequestUtil.getString(request, "id");
        if (StringUtils.isNotBlank(id)) {
            jsonObject = componentTestKanbanService.getBindingStandardMsg(id);
        }
        return jsonObject;
    }

    //..
    @RequestMapping("deleteBindingStandardMsg")
    @ResponseBody
    public JsonResult deleteBindingStandardMsg(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String id = RequestUtil.getString(request, "id");
            return componentTestKanbanService.deleteBindingStandardMsg(id);
        } catch (Exception e) {
            logger.error("Exception in deleteMsg", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    //..
    @RequestMapping("saveBindingStandardMsg")
    @ResponseBody
    public JsonResult saveBindingStandardMsg(HttpServletRequest request, @RequestBody String formDataStr,
                                             HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        try {
            JSONObject formDataJson = JSONObject.parseObject(formDataStr);
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("id"))) {
                componentTestKanbanService.createBindingStandardMsg(formDataJson);
            } else {
                componentTestKanbanService.updateBindingStandardMsg(formDataJson);
            }
            result.setData(formDataJson.getString("id"));
        } catch (Exception e) {
            logger.error("Exception in save BindingStandardMsg");
            result.setSuccess(false);
            result.setMessage("Exception in save BindingStandardMsg");
            return result;
        }
        return result;
    }

    //..
    @RequestMapping("editBindingStandardMsgPage")
    public ModelAndView editBindingStandardMsgPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "componentTest/core/componentTestBindingStandardMsgEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String action = RequestUtil.getString(request, "action");
        String mainId = RequestUtil.getString(request, "mainId");
        String id = RequestUtil.getString(request, "id");
        mv.addObject("action", action).addObject("mainId", mainId);
        mv.addObject("id", id);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }

    //..以上标准相关
    @RequestMapping("queryBindingStandardMsgItems")
    @ResponseBody
    public List<JSONObject> queryBindingStandardMsgItems(HttpServletRequest request, HttpServletResponse response) {
        return componentTestKanbanService.queryBindingStandardMsgItems(request);
    }


    //////////////////////////////..kanban
    //..零部件试验完成情况
    @RequestMapping("getComponentTestCompletionStatusData")
    @ResponseBody
    public JSONObject getComponentTestCompletionStatusData(HttpServletRequest request, HttpServletResponse response,
                                                           @RequestBody String postDataStr) {
        return componentTestKanbanService.getComponentTestCompletionStatusData(request, response, postDataStr);
    }

    //..零部件试验结果
    @RequestMapping("getComponentTestRestltData")
    @ResponseBody
    public List<JSONObject> getComponentTestRestltData(HttpServletRequest request, HttpServletResponse response,
                                                       @RequestBody String postDataStr) {
        return componentTestKanbanService.getComponentTestRestltData(request, response, postDataStr);
    }

    //..零部件试验计划及进度
    @RequestMapping("getComponentTestScheduleData")
    @ResponseBody
    public JSONObject getComponentTestScheduleData(HttpServletRequest request, HttpServletResponse response,
                                                   @RequestBody String postDataStr) {
        return componentTestKanbanService.getComponentTestScheduleData(request, response, postDataStr);
    }

    //..零部件试验报告出具情况
    @RequestMapping("getComponentTestReportData")
    @ResponseBody
    public JSONObject getComponentTestReportData(HttpServletRequest request, HttpServletResponse response,
                                                 @RequestBody String postDataStr) {
        return componentTestKanbanService.getComponentTestReportData(request, response, postDataStr);
    }
}
