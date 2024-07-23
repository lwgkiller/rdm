package com.redxun.serviceEngineering.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.service.StandardvalueShipmentnotmadeService;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
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

@Controller
@RequestMapping("/serviceEngineering/core/standardvalue/shipmentnotmade")
public class StandardvalueShipmentnotmadeController {
    private static final Logger logger = LoggerFactory.getLogger(StandardvalueShipmentnotmadeController.class);
    @Autowired
    private StandardvalueShipmentnotmadeService standardvalueShipmentnotmadeService;

    //..
    @RequestMapping("dataListPage")
    public ModelAndView dataListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/standardvalueShipmentnotmadeList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String mainGroupName = ContextUtil.getCurrentUser().getMainGroupName();
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("mainGroupName",mainGroupName);
        return mv;
    }

    //..
    @RequestMapping("dataListQuery")
    @ResponseBody
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        return standardvalueShipmentnotmadeService.dataListQuery(request, response);
    }

    //..
    @RequestMapping("deleteData")
    @ResponseBody
    public JsonResult deleteData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return standardvalueShipmentnotmadeService.deleteData(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteData", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    //..
    @RequestMapping(value = "saveBusiness", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveBusiness(HttpServletRequest request, @RequestBody String businessDataStr,
                                      HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
        if (StringUtils.isBlank(businessDataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        standardvalueShipmentnotmadeService.saveBusiness(result, businessDataStr);
        return result;
    }

    @RequestMapping("toEditPage")
    public ModelAndView jxxqxfEditPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/standardvalueShipmentnotmadeEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String zzcsbId = RequestUtil.getString(request, "zzcsbId");
        String action = RequestUtil.getString(request, "action");
        String mainGroupName = ContextUtil.getCurrentUser().getMainGroupName();
        mv.addObject("zzcsbId", zzcsbId).addObject("action", action);
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("mainGroupName",mainGroupName);
        return mv;
    }

    @RequestMapping("getStandardvalueShipmentnotmadeDetail")
    @ResponseBody
    public JSONObject getStandardvalueShipmentnotmadeDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject obj = new JSONObject();
        String zzcsbId = RequestUtil.getString(request, "zzcsbId");
        if (StringUtils.isNotBlank(zzcsbId)) {
            obj = standardvalueShipmentnotmadeService.getStandardvalueShipmentnotmadeDetail(zzcsbId);
        }
        return obj;
    }

    /**
     * 下载模板
     * */
    @RequestMapping("/downloadTemplate")
    public ResponseEntity<byte[]> downloadTemplate(HttpServletRequest request, HttpServletResponse response) {
        return standardvalueShipmentnotmadeService.downloadTemplate();
    }

    /**
     * 批量导入
     * */
    @RequestMapping("importStandardvalueShipmentnotmade")
    @ResponseBody
    public JSONObject importStandardvalueShipmentnotmade(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        standardvalueShipmentnotmadeService.importStandardvalueShipmentnotmade(result, request);
        return result;
    }

    @RequestMapping("validRepetition")
    @ResponseBody
    public JsonResult  validRepetition(HttpServletRequest request, @RequestBody String formData, HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(formData)) {
            logger.warn("requestBody is blank");
            result.setMessage("requestBody is blank");
            result.setSuccess(false);
            return result;
        }
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson == null || formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        // 判断是否有重复物料
        boolean repetition = standardvalueShipmentnotmadeService.queryByMaterialCode(formDataJson);
        if (!repetition) {
            result.setSuccess(false);
            result.setMessage("物料已存在!");
        }
        return result;
    }
}
