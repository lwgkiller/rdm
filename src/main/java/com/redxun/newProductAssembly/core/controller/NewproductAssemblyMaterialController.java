package com.redxun.newProductAssembly.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.newProductAssembly.core.service.NewproductAssemblyMaterialService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.service.StandardvalueService;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.org.manager.OsUserManager;
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

@Controller
@RequestMapping("/newproductAssembly/core/material")
public class NewproductAssemblyMaterialController {
    private static final Logger logger = LoggerFactory.getLogger(NewproductAssemblyMaterialController.class);
    @Autowired
    private NewproductAssemblyMaterialService newproductAssemblyMaterialService;
    @Autowired
    private SysDicManager sysDicManager;

    //..
    @RequestMapping("modelListPage")
    public ModelAndView dataListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "newProductAssembly/core/newProductAssemblyModelList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        String newproductAssemblyAdmins = sysDicManager.getBySysTreeKeyAndDicKey(
                "newproductAssemblyManGroups", "newproductAssemblyAdmin").getValue();
        mv.addObject("newproductAssemblyAdmins", newproductAssemblyAdmins);
        return mv;
    }

    //..
    @RequestMapping("materialListPage")
    public ModelAndView materialListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "newProductAssembly/core/newProductAssemblyMaterialList.jsp";
        String mainId = RequestUtil.getString(request, "mainId");
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("mainId", mainId);
        JSONObject modelDetail = newproductAssemblyMaterialService.getModelDetail(mainId);
        mv.addObject("salesModel", modelDetail.getString("salesModel"));
        String newproductAssemblyAdmins = sysDicManager.getBySysTreeKeyAndDicKey(
                "newproductAssemblyManGroups", "newproductAssemblyAdmin").getValue();
        mv.addObject("newproductAssemblyAdmins", newproductAssemblyAdmins);
        String newproductAssemblyMaterialReceivers = sysDicManager.getBySysTreeKeyAndDicKey(
                "newproductAssemblyManGroups", "newproductAssemblyMaterialReceiver").getValue();
        mv.addObject("newproductAssemblyMaterialReceivers", newproductAssemblyMaterialReceivers);
        return mv;
    }

    //..
    @RequestMapping("modelListQuery")
    @ResponseBody
    public JsonPageResult<?> modelListQuery(HttpServletRequest request, HttpServletResponse response) {
        return newproductAssemblyMaterialService.modelListQuery(request, response);
    }

    //..
    @RequestMapping("materialListQuery")
    @ResponseBody
    public JsonPageResult<?> materialListQuery(HttpServletRequest request, HttpServletResponse response) {
        return newproductAssemblyMaterialService.materialListQuery(request, response);
    }

    //..
    @RequestMapping(value = "saveModel", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveModel(HttpServletRequest request, @RequestBody String businessDataStr,
                                HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
        newproductAssemblyMaterialService.saveModel(result, businessDataStr);
        return result;
    }

    //..
    @RequestMapping(value = "saveMaterial", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveMaterial(HttpServletRequest request, @RequestBody String businessDataStr,
                                   HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
        String mainId = RequestUtil.getString(request, "mainId");
        newproductAssemblyMaterialService.saveMaterial(result, businessDataStr, mainId);
        return result;
    }

    //..
    @RequestMapping("deleteModel")
    @ResponseBody
    public JsonResult deleteModel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return newproductAssemblyMaterialService.deleteModel(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteModel", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    //..
    @RequestMapping("deleteMaterial")
    @ResponseBody
    public JsonResult deleteMaterial(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return newproductAssemblyMaterialService.deleteMaterial(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteMaterial", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    //..
    @RequestMapping("importMaterialTDownload")
    public ResponseEntity<byte[]> importMaterialTDownload(HttpServletRequest request, HttpServletResponse response) {
        return newproductAssemblyMaterialService.importMaterialTDownload();
    }

    //..
    @RequestMapping("importSubMaterialTDownload")
    public ResponseEntity<byte[]> importSubMaterialTDownload(HttpServletRequest request, HttpServletResponse response) {
        return newproductAssemblyMaterialService.importSubMaterialTDownload();
    }

    //..
    @RequestMapping("importMaterial")
    @ResponseBody
    public JSONObject importMaterial(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        newproductAssemblyMaterialService.importMaterial(result, request);
        return result;
    }

    //..
    @RequestMapping("importSubMaterial")
    @ResponseBody
    public JSONObject importSubMaterial(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        newproductAssemblyMaterialService.importSubMaterial(result, request);
        return result;
    }

    //..
    @RequestMapping("exportExcel")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        newproductAssemblyMaterialService.exportExcel(request, response);
    }
}
