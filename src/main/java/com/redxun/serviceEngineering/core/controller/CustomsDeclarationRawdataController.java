package com.redxun.serviceEngineering.core.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.service.CustomsDeclarationRawdataService;
import com.redxun.sys.core.manager.SysDicManager;
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
import java.util.List;

@Controller
@RequestMapping("/serviceEngineering/core/customsDeclarationRawdata/")
public class CustomsDeclarationRawdataController {
    private static final Logger logger = LoggerFactory.getLogger(CustomsDeclarationRawdataController.class);
    @Autowired
    private CustomsDeclarationRawdataService customsDeclarationRawdataService;
    @Autowired
    private SysDicManager sysDicManager;

    //..
    @RequestMapping("dataListPage")
    public ModelAndView dataListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/customsDeclarationRawdataList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        String customsDeclarationAdmin =
                sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringGroups", "customsDeclarationAdmin").getValue();
        String[] customsDeclarationAdmins = customsDeclarationAdmin.split(",");
        boolean isCustomsDeclarationAdmin = false;
        for (String userId : customsDeclarationAdmins) {
            if (userId.equalsIgnoreCase(ContextUtil.getCurrentUser().getUserNo())) {
                isCustomsDeclarationAdmin = true;
                break;
            }
        }
        mv.addObject("isCustomsDeclarationAdmin", isCustomsDeclarationAdmin);
        return mv;
    }

    //..
    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/customsDeclarationRawdataEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String businessId = RequestUtil.getString(request, "businessId");
        String action = RequestUtil.getString(request, "action");
        mv.addObject("businessId", businessId).addObject("action", action);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }

    //..
    @RequestMapping("itemListPage")
    public ModelAndView itemListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/customsDeclarationRawdataItemList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        String customsDeclarationAdmin =
                sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringGroups", "customsDeclarationAdmin").getValue();
        String[] customsDeclarationAdmins = customsDeclarationAdmin.split(",");
        boolean isCustomsDeclarationAdmin = false;
        for (String userId : customsDeclarationAdmins) {
            if (userId.equalsIgnoreCase(ContextUtil.getCurrentUser().getUserNo())) {
                isCustomsDeclarationAdmin = true;
                break;
            }
        }
        mv.addObject("isCustomsDeclarationAdmin", isCustomsDeclarationAdmin);
        return mv;
    }

    //..
    @RequestMapping("dataListQuery")
    @ResponseBody
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return customsDeclarationRawdataService.dataListQuery(request, response);
    }

    //..
    @RequestMapping("getDataById")
    @ResponseBody
    public JSONObject getDataById(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = RequestUtil.getString(request, "businessId");
        return customsDeclarationRawdataService.getDataById(id);
    }

    //..
    @RequestMapping("deleteBusiness")
    @ResponseBody
    public JsonResult deleteBusiness(HttpServletRequest request, HttpServletResponse response) {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return customsDeclarationRawdataService.deleteBusiness(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteBusiness", e);
            return new JsonResult(false, e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
        }
    }

    //..
    @RequestMapping(value = "saveBusiness", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveBusiness(HttpServletRequest request, HttpServletResponse response,
                                   @RequestBody String DataStr) {
        JSONObject jsonObject = JSONObject.parseObject(DataStr);
        if (jsonObject == null || jsonObject.isEmpty()) {
            return new JsonResult(false, "表单内容为空，操作失败！");
        }
        try {
            return customsDeclarationRawdataService.saveBusiness(jsonObject);
        } catch (Exception e) {
            logger.error("Exception in saveBusiness", e);
            return new JsonResult(false, e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
        }
    }

    //..
    @RequestMapping(value = "commitBusiness", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult commitBusiness(HttpServletRequest request, HttpServletResponse response,
                                     @RequestBody String DataStr) {
        JSONObject jsonObject = JSONObject.parseObject(DataStr);
        if (jsonObject == null || jsonObject.isEmpty()) {
            return new JsonResult(false, "表单内容为空，操作失败！");
        }
        try {
            return customsDeclarationRawdataService.doCommitBusiness(jsonObject);
        } catch (Exception e) {
            logger.error("Exception in commitBusiness", e);
            return new JsonResult(false, e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
        }
    }

    //..
    @RequestMapping("getItemList")
    @ResponseBody
    public List<JSONObject> getItemList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String businessId = RequestUtil.getString(request, "businessId", "");
        String machineModel = RequestUtil.getString(request, "machineModel", "");
        String materialCode = RequestUtil.getString(request, "materialCode", "");
        String elements = RequestUtil.getString(request, "elements", "");
        String isSupplement = RequestUtil.getString(request, "isSupplement", "");
        JSONObject params = new JSONObject();
        params.put("mainId", businessId);
        params.put("machineModel", machineModel);
        params.put("materialCode", materialCode);
        params.put("elements", elements);
        params.put("isSupplement", isSupplement);
        return customsDeclarationRawdataService.getItemList(params);
    }

    //..
    @RequestMapping("itemListQuery")
    @ResponseBody
    public JsonPageResult<?> itemListQuery(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return customsDeclarationRawdataService.itemListQuery(request, response);
    }

    //..
    @RequestMapping("importItemTDownload")
    public ResponseEntity<byte[]> importItemTDownload(HttpServletRequest request, HttpServletResponse response) {
        return customsDeclarationRawdataService.importItemTDownload();
    }

    //..
    @RequestMapping("importItem")
    @ResponseBody
    public JSONObject importItem(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        customsDeclarationRawdataService.importItem(result, request);
        return result;
    }

    //..
    @RequestMapping("exportItemList")
    public void exportItemList(HttpServletRequest request, HttpServletResponse response) {
        customsDeclarationRawdataService.exportItemList(request, response);
    }

    //..
    @RequestMapping("createSupplement")
    @ResponseBody
    public JsonResult createSupplement(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) throws Exception {
        try {
            String processUserId = RequestUtil.getString(request, "processUserId", "");
            JSONArray jsonArray = JSON.parseArray(postBody);
            return customsDeclarationRawdataService.createSupplement(processUserId, jsonArray);
        } catch (Exception e) {
            logger.error("Exception in createSupplement", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    //..
    @RequestMapping("deleteItem")
    @ResponseBody
    public JsonResult deleteItem(HttpServletRequest request, HttpServletResponse response) {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return customsDeclarationRawdataService.deleteItem(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteItem", e);
            return new JsonResult(false, e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
        }
    }
}
