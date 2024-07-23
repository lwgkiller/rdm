package com.redxun.newProductAssembly.core.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.core.util.StringUtil;
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
import com.redxun.bpm.core.entity.BpmInst;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.newProductAssembly.core.service.NewproductAssemblyKanbanService;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Controller
@RequestMapping("/newproductAssembly/core/kanban")
public class NewproductAssemblyKanbanController {
    private static final Logger logger = LoggerFactory.getLogger(NewproductAssemblyKanbanController.class);
    @Autowired
    private NewproductAssemblyKanbanService newproductAssemblyKanbanService;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private BpmInstManager bpmInstManager;

    //..
    @RequestMapping("doSynZlgjToClearException")
    @ResponseBody
    public void doSynZlgjToClearException(HttpServletRequest request, HttpServletResponse response) {
        String zlgjInstId = RequestUtil.getString(request, "instId");
        BpmInst bpmInst = bpmInstManager.get(zlgjInstId);
        JSONObject zlgjJsonMock = new JSONObject();
        zlgjJsonMock.put("wtId", bpmInst.getBusKey());
        newproductAssemblyKanbanService.doSynZlgjToClearException(zlgjJsonMock);
    }

    //..
    @RequestMapping("dataListPage")
    public ModelAndView dataListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "newProductAssembly/core/newProductAssemblyList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    //..
    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "newProductAssembly/core/newProductAssemblyEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String businessId = RequestUtil.getString(request, "businessId");
        String action = RequestUtil.getString(request, "action");
        mv.addObject("businessId", businessId).addObject("action", action);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        String newproductAssemblyAdmins = sysDicManager.getBySysTreeKeyAndDicKey(
                "newproductAssemblyManGroups", "newproductAssemblyAdmin").getValue();
        mv.addObject("newproductAssemblyAdmins", newproductAssemblyAdmins);
        String newproductAssemblyExceptionInputers = sysDicManager.getBySysTreeKeyAndDicKey(
                "newproductAssemblyManGroups", "newproductAssemblyExceptionInputer").getValue();
        mv.addObject("newproductAssemblyExceptionInputers", newproductAssemblyExceptionInputers);
        return mv;
    }

    //..
    @RequestMapping("exceptionPage")
    public ModelAndView exceptionPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "newProductAssembly/core/newProductAssemblyExceptionEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String mainId = RequestUtil.getString(request, "mainId");
        String pin = RequestUtil.getString(request, "pin");
        String businessId = RequestUtil.getString(request, "businessId");
        String action = RequestUtil.getString(request, "action");
        mv.addObject("mainId", mainId).addObject("pin", pin).addObject("businessId", businessId).addObject("action", action);
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        if(StringUtil.isEmpty(businessId)){
            mv.addObject("feedbackPersonId", ContextUtil.getCurrentUserId());
            mv.addObject("feedbackPerson", ContextUtil.getCurrentUser().getUserNo());
        }
        return mv;
    }

    //..
    @RequestMapping("dataListQuery")
    @ResponseBody
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        return newproductAssemblyKanbanService.dataListQuery(request, response);
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
        return newproductAssemblyKanbanService.queryDataById(businessId);
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
        newproductAssemblyKanbanService.saveBusiness(result, DataStr);
        return result;
    }

    //..
    @RequestMapping("deleteBusiness")
    @ResponseBody
    public JsonResult deleteBusiness(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return newproductAssemblyKanbanService.deleteBusiness(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteData", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    //..
    @RequestMapping("getExceptionList")
    @ResponseBody
    public List<JSONObject> getExceptionList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String businessId = RequestUtil.getString(request, "businessId", "");
        return newproductAssemblyKanbanService.getExceptionList(businessId);
    }

    //..
    @RequestMapping("getExceptionDetail")
    @ResponseBody
    public JSONObject getExceptionDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject jsonObject = new JSONObject();
        String businessId = RequestUtil.getString(request, "businessId");
        if (StringUtils.isNotBlank(businessId)) {
            jsonObject = newproductAssemblyKanbanService.getExceptionDetail(businessId);
        }
        return jsonObject;
    }

    //..
    @RequestMapping("deleteExceptions")
    @ResponseBody
    public JsonResult deleteExceptions(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return newproductAssemblyKanbanService.deleteExceptions(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteData", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    //..
    @RequestMapping("synException")
    @ResponseBody
    public JsonResult synException(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String mainId = RequestUtil.getString(request, "mainId");
            String id = RequestUtil.getString(request, "id");
            return newproductAssemblyKanbanService.doSynException(mainId, id);
        } catch (Exception e) {
            logger.error("Exception in deleteData", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    //..
    @RequestMapping(value = "saveException", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveException(HttpServletRequest request, @RequestBody String itemDataStr,
                                    HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
        if (StringUtils.isBlank(itemDataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("数据为空，保存失败！");
            return result;
        }
        newproductAssemblyKanbanService.saveException(result, itemDataStr);
        return result;
    }

    //..
    @RequestMapping("getFileList")
    @ResponseBody
    public List<JSONObject> getFileList(HttpServletRequest request, HttpServletResponse response) {
        List<String> businessIdList = Arrays.asList(RequestUtil.getString(request, "mainId", ""));
        return newproductAssemblyKanbanService.getFileList(businessIdList);
    }

    //..
    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "newProductAssembly/core/newproductAssemblyExceptionFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("mainId", RequestUtil.getString(request, "mainId", ""));
        return mv;
    }

    //..
    @RequestMapping("importItem")
    @ResponseBody
    public JSONObject importItem(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        newproductAssemblyKanbanService.importItem(result, request);
        return result;
    }

    //..
    @RequestMapping("importItemTemplateDownload")
    public ResponseEntity<byte[]> importItemTemplateDownload(HttpServletRequest request, HttpServletResponse response) {
        return newproductAssemblyKanbanService.importItemTemplateDownload();
    }

    //..
    @RequestMapping("pdfPreviewAndAllDownload")
    public ResponseEntity<byte[]> pdfPreviewAndAllDownload(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "newproductAssemblyUploadPosition", "exceptionFile").getValue();
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, filePathBase);
    }

    //..
    @RequestMapping("delFile")
    public void delFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String businessId = postBodyObj.getString("formId");
        newproductAssemblyKanbanService.deleteOneExceptionFile(fileId, fileName, businessId);
    }

    //..
    @RequestMapping("officePreview")
    @ResponseBody
    public void officePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "newproductAssemblyUploadPosition", "exceptionFile").getValue();
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, filePathBase, response);
    }

    //..
    @RequestMapping("imagePreview")
    @ResponseBody
    public void imagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "newproductAssemblyUploadPosition", "exceptionFile").getValue();
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, filePathBase, response);
    }

    //..
    @RequestMapping("fileUpload")
    @ResponseBody
    public Map<String, Object> fileUpload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            //先保存文件，成功后再写入数据库，前台是一个一个文件的调用
            newproductAssemblyKanbanService.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    //..以下异常汇总相关
    //..
    @RequestMapping("exceptionSummaryPage")
    public ModelAndView exceptionSummaryPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "newProductAssembly/core/newProductAssemblyExceptionSummary.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    //..
    @RequestMapping("exceptionSummaryListQuery")
    @ResponseBody
    public JsonPageResult<?> exceptionSummaryListQuery(HttpServletRequest request, HttpServletResponse response) {
        return newproductAssemblyKanbanService.exceptionSummaryListQuery(request, response);
    }

    //..
    @RequestMapping("exportExceptionSummaryList")
    public void exportExceptionSummaryList(HttpServletRequest request, HttpServletResponse response) {
        newproductAssemblyKanbanService.exportExceptionSummaryList(request, response);
    }

    //..以下年度计划相关
    //..
    @RequestMapping("annualPlanDepListPage")
    public ModelAndView annualPlanDepListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "newProductAssembly/core/newProductAssemblyAnnualPlanDep.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    //..
    @RequestMapping("annualPlanDepListQuery")
    @ResponseBody
    public JsonPageResult<?> annualPlanDepListQuery(HttpServletRequest request, HttpServletResponse response) {
        return newproductAssemblyKanbanService.annualPlanDepListQuery(request, response);
    }

    //..
    @RequestMapping("deleteAnnualPlanDep")
    @ResponseBody
    public JsonResult deleteAnnualPlanDep(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return newproductAssemblyKanbanService.deleteAnnualPlanDep(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteData", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    //..
    @RequestMapping(value = "saveAnnualPlanDep", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveAnnualPlanDep(HttpServletRequest request, @RequestBody String DataStr,
                                        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
        if (StringUtils.isBlank(DataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("表单为空，保存失败！");
            return result;
        }
        newproductAssemblyKanbanService.saveAnnualPlanDep(result, DataStr);
        return result;
    }

    //..
    @RequestMapping("annualPlanPrdListPage")
    public ModelAndView annualPlanPrdListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "newProductAssembly/core/newProductAssemblyAnnualPlanPrd.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    //..
    @RequestMapping("annualPlanPrdListQuery")
    @ResponseBody
    public JsonPageResult<?> annualPlanPrdListQuery(HttpServletRequest request, HttpServletResponse response) {
        return newproductAssemblyKanbanService.annualPlanPrdListQuery(request, response);
    }

    //..
    @RequestMapping("deleteAnnualPlanPrd")
    @ResponseBody
    public JsonResult deleteAnnualPlanPrd(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return newproductAssemblyKanbanService.deleteAnnualPlanPrd(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteData", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    //..
    @RequestMapping(value = "saveAnnualPlanPrd", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveAnnualPlanPrd(HttpServletRequest request, @RequestBody String DataStr,
                                        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
        if (StringUtils.isBlank(DataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("表单为空，保存失败！");
            return result;
        }
        newproductAssemblyKanbanService.saveAnnualPlanPrd(result, DataStr);
        return result;
    }

    //..以下看板相关
    //..
    @RequestMapping("kanbanPage")
    public ModelAndView kanbanPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "newProductAssembly/core/newProductAssemblyKanban.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    //..
    @RequestMapping("kanbanExceptionPage")
    public ModelAndView kanbanExceptionPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "newProductAssembly/core/newProductAssemblyExceptionKanban.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }


    //..试验执行及进度
    @RequestMapping("getCompletionData")
    @ResponseBody
    public JSONObject getCompletionData(HttpServletRequest request, HttpServletResponse response,
                                        @RequestBody String postDataStr) {
        return newproductAssemblyKanbanService.getCompletionData(request, response, postDataStr);
    }

    //..试验执行及进度-饼图用
    @RequestMapping("getCompletionDataPie")
    @ResponseBody
    public List<JSONObject> getCompletionDataPie(HttpServletRequest request, HttpServletResponse response,
                                                 @RequestBody String postDataStr) {
        return newproductAssemblyKanbanService.getCompletionDataPie(request, response, postDataStr);
    }

    //..新品试制异常统计
    @RequestMapping("exceptionStatisticListQuery")
    @ResponseBody
    public JsonPageResult<?> exceptionStatisticListQuery(HttpServletRequest request, HttpServletResponse response) {
        return newproductAssemblyKanbanService.exceptionStatisticListQuery(request, response);
    }

    //..异常闭环及进度
    @RequestMapping("getExceptionData")
    @ResponseBody
    public JSONObject getExceptionData(HttpServletRequest request, HttpServletResponse response,
                                       @RequestBody String postDataStr) {
        return newproductAssemblyKanbanService.getExceptionData(request, response, postDataStr);
    }

}
