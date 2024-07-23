package com.redxun.serviceEngineering.core.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;

import com.redxun.serviceEngineering.core.service.DecorationManualIntegrityService;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@RequestMapping("/serviceEngineering/core/decorationManualIntegrity")
public class DecorationManualIntegrityController {
    private static final Logger logger = LoggerFactory.getLogger(DecorationManualIntegrityController.class);
    @Autowired
    private DecorationManualIntegrityService decorationManualIntegrityService;
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;

    //..
    @RequestMapping("dataListPage")
    public ModelAndView dataListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/decorationManualIntegrityList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        String decorationManualAdmin =
                sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringGroups", "decorationManualAdmin").getValue();
        mv.addObject("decorationManualAdmin", decorationManualAdmin);

        return mv;
    }

    //..
    @RequestMapping("warehouseDataListPage")
    public ModelAndView warehouseDataListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/decorationManualIntegrityStoreList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        String decorationManualAdmin =
                sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringGroups", "decorationManualAdmin").getValue();
        mv.addObject("decorationManualAdmin", decorationManualAdmin);

        return mv;
    }
    //..
    @RequestMapping("dataListQuery")
    @ResponseBody
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        return decorationManualIntegrityService.dataListQuery(request, response);
    }
    //..
    @RequestMapping("storeDataListQuery")
    @ResponseBody
    public JsonPageResult<?> storeDataListQuery(HttpServletRequest request, HttpServletResponse response) {
        return decorationManualIntegrityService.warehouseDataListQuery(request, response);
    }
    //..
    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/decorationManualIntegrityEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String businessId = RequestUtil.getString(request, "businessId");
        String action = RequestUtil.getString(request, "action");

        mv.addObject("businessId", businessId).addObject("action", action);

        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }

    //..
    @RequestMapping("deleteBusiness")
    @ResponseBody
    public JsonResult deleteBusiness(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");

            return decorationManualIntegrityService.deleteBusiness(ids);
        } catch (Exception e) {
            logger.error("Exception in delete", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    //..
    @RequestMapping("getItemList")
    @ResponseBody
    public List<JSONObject> getItemList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String businessId = RequestUtil.getString(request, "businessId", "");
        JSONObject params = new JSONObject();
        params.put("mainId", businessId);
        return decorationManualIntegrityService.getItemList(params);
    }
    //..
    @RequestMapping("getDetail")
    @ResponseBody
    public JSONObject getDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject jsonObject = new JSONObject();
        String businessId = RequestUtil.getString(request, "businessId");
        if (StringUtils.isNotBlank(businessId)) {
            jsonObject = decorationManualIntegrityService.getDetail(businessId);
        }
        return jsonObject;
    }

    //..
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


            return decorationManualIntegrityService.saveBusiness(formDataJson);
        } catch (Exception e) {
            logger.error("Exception in save");
            result.setSuccess(false);
            result.setMessage("Exception in save");
            return result;
        }
    }




    //查询字表仓库里的数据
    @RequestMapping("getMaterialCodeDetail")
    @ResponseBody
    public JSONObject getMaterialCodeDetail(HttpServletRequest request, HttpServletResponse response) {
        String materialCode = RequestUtil.getString(request, "materialCode", "");
        return decorationManualIntegrityService.getMaterialCodeDetail(materialCode);

    }


    //..
    @RequestMapping("storeDataListFresh")
    @ResponseBody
    public JsonResult storeDataListFresh(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {

            return decorationManualIntegrityService.storeDataListFresh();
        } catch (Exception e) {
            logger.error("刷新失败", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("exportStoreExcel")
    public void exportStoreExcel(HttpServletRequest request, HttpServletResponse response) {
        decorationManualIntegrityService.exportStoreExcel(request, response);
    }

    //..
    @RequestMapping("deleteStoreItem")
    @ResponseBody
    public JsonResult deleteStoreItem(HttpServletRequest request, HttpServletResponse response) {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return decorationManualIntegrityService.deleteStoreItem(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteStoreItem", e);
            return new JsonResult(false, e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
        }
    }
}
