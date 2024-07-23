package com.redxun.rdMaterial.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.rdMaterial.core.service.RdMaterialFileService;
import com.redxun.rdMaterial.core.service.RdMaterialInStorageService;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
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
@RequestMapping("/rdMaterial/core/inStorage/")
public class RdMaterialInStorageController {
    private static final Logger logger = LoggerFactory.getLogger(RdMaterialInStorageController.class);
    @Autowired
    private RdMaterialInStorageService rdMaterialInStorageService;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private RdMaterialFileService rdMaterialFileService;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;

    //..
    @RequestMapping("dataListPage")
    public ModelAndView dataListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdMaterial/core/rdMaterialInStorageList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        boolean isYanFaWuLiaoGuanLiYuan = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), "研发物料管理员");
        mv.addObject("isYanFaWuLiaoGuanLiYuan", isYanFaWuLiaoGuanLiYuan);
        return mv;
    }

    //..
    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdMaterial/core/rdMaterialInStorageEdit.jsp";
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
    @RequestMapping("dataListQuery")
    @ResponseBody
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return rdMaterialInStorageService.dataListQuery(request, response);
    }

    //..
    @RequestMapping("getDataById")
    @ResponseBody
    public JSONObject getDataById(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = RequestUtil.getString(request, "businessId");
        return rdMaterialInStorageService.getDataById(id);
    }

    //..
    @RequestMapping("deleteBusiness")
    @ResponseBody
    public JsonResult deleteBusiness(HttpServletRequest request, HttpServletResponse response) {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return rdMaterialInStorageService.deleteBusiness(ids);
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
            return rdMaterialInStorageService.saveBusiness(jsonObject);
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
            return rdMaterialInStorageService.doCommitBusiness(jsonObject);
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
        String materialCode = RequestUtil.getString(request,"materialCode","");
        JSONObject params = new JSONObject();
        params.put("mainId", businessId);
        params.put("materialCode", materialCode);
        return rdMaterialInStorageService.getItemList(params);
    }

    //..
    @RequestMapping("itemListQuery")
    @ResponseBody
    public JsonPageResult<?> itemListQuery(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return rdMaterialInStorageService.itemListQuery(request, response);
    }

    //..
    @RequestMapping("importItemTDownload")
    public ResponseEntity<byte[]> importItemTDownload(HttpServletRequest request, HttpServletResponse response) {
        return rdMaterialInStorageService.importItemTDownload();
    }

    //..
    @RequestMapping("importItem")
    @ResponseBody
    public JSONObject importItem(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        rdMaterialInStorageService.importItem(result, request);
        return result;
    }
}
