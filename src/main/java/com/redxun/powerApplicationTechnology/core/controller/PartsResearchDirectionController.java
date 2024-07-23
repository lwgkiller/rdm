package com.redxun.powerApplicationTechnology.core.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.redxun.powerApplicationTechnology.core.service.PartsResearchDirectionService;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Controller
@RequestMapping("/powerApplicationTechnology/core/partsIntegration/partsResearchDirection")
public class PartsResearchDirectionController {
    private static final Logger logger = LoggerFactory.getLogger(PartsResearchDirectionController.class);
    @Autowired
    private PartsResearchDirectionService partsResearchDirectionService;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;

    //..
    @RequestMapping("dataListPage")
    public ModelAndView dataListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "powerApplicationTechnology/core/partsResearchDirectionList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }

    //..
    @RequestMapping("dataListQuery")
    @ResponseBody
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        return partsResearchDirectionService.dataListQuery(request, response);
    }

    //..
    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "powerApplicationTechnology/core/partsResearchDirectionEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String parentId = RequestUtil.getString(request, "parentId");
        String businessId = RequestUtil.getString(request, "businessId");
        String action = RequestUtil.getString(request, "action");
        mv.addObject("businessId", businessId).addObject("action", action)
                .addObject("currentUserId", ContextUtil.getCurrentUserId())
                .addObject("currentUserName", ContextUtil.getCurrentUser().getFullname())
                .addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    //..
    @RequestMapping("deleteData")
    @ResponseBody
    public JsonResult deleteData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String id = RequestUtil.getString(request, "id");
            return partsResearchDirectionService.deleteData(id);
        } catch (Exception e) {
            logger.error("Exception in deleteData", e);
            return new JsonResult(false, e.getMessage());
        }
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
        return partsResearchDirectionService.queryDataById(businessId);
    }

    //..
    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "powerApplicationTechnology/core/partsResearchDirectionFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("businessId", RequestUtil.getString(request, "businessId", ""));
        return mv;
    }


    //..
    @RequestMapping(value = "saveData", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveData(HttpServletRequest request, @RequestBody String DataStr,
                               HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
        if (StringUtils.isBlank(DataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("表单为空，保存失败！");
            return result;
        }
        partsResearchDirectionService.saveData(result, DataStr);
        return result;
    }


    //..
    @RequestMapping("getFileList")
    @ResponseBody
    public List<JSONObject> getFileList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<String> businessIdList = Arrays.asList(RequestUtil.getString(request, "businessId", ""));
        return partsResearchDirectionService.getFileList(businessIdList);
    }

    //..
    @RequestMapping("delFile")
    public void delFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String businessId = postBodyObj.getString("formId");
        partsResearchDirectionService.deleteOneFile(fileId, fileName, businessId);
    }

    //..
    @RequestMapping("fileUpload")
    @ResponseBody
    public Map<String, Object> fileUpload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            //先保存文件，成功后再写入数据库，前台是一个一个文件的调用
            partsResearchDirectionService.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    //..
    @RequestMapping("pdfPreviewAndAllDownload")
    public ResponseEntity<byte[]> pdfPreviewAndAllDownload(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "powerApplicationTechnologyUploadPosition", "PartsResearchDirection").getValue();
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, filePathBase);
    }

    //..
    @RequestMapping("officePreview")
    @ResponseBody
    public void officePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "powerApplicationTechnologyUploadPosition", "PartsResearchDirection").getValue();
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
                "powerApplicationTechnologyUploadPosition", "PartsResearchDirection").getValue();
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, filePathBase, response);
    }

    //..
    @RequestMapping("getItemList")
    @ResponseBody
    public List<JSONObject> getItemList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String businessId = RequestUtil.getString(request, "businessId", "");
        return partsResearchDirectionService.getItemList(businessId);
    }

    //..
    @RequestMapping(value = "saveItems", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveItems(HttpServletRequest request, @RequestBody String itemsDataStr,
                                HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
        if (StringUtils.isBlank(itemsDataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("数据为空，保存失败！");
            return result;
        }
        partsResearchDirectionService.saveItems(result, itemsDataStr);
        return result;
    }

    //..
    @RequestMapping("deleteItem")
    @ResponseBody
    public JsonResult deleteItem(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String id = RequestUtil.getString(request, "id");
            return partsResearchDirectionService.deleteItem(id);
        } catch (Exception e) {
            logger.error("Exception in deleteData", e);
            return new JsonResult(false, e.getMessage());
        }
    }
}
