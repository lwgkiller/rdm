package com.redxun.serviceEngineering.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.service.JxxqxfService;
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

@Controller
@RequestMapping("/serviceEngineering/core/jxxqxf")
public class JxxqxfController {

    private static final Logger logger = LoggerFactory.getLogger(JxxqxfController.class);

    @Autowired
    private JxxqxfService jxxqxfService;


    //..
    @RequestMapping("jxxqxfListPage")
    public ModelAndView jxxqxfListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/jxxqxfList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("mainGroupName",ContextUtil.getCurrentUser().getMainGroupName());
        return mv;
    }

    //..
    @RequestMapping("jxxqxfListQuery")
    @ResponseBody
    public JsonPageResult<?> jxxqxfListQuery(HttpServletRequest request, HttpServletResponse response) {
        return jxxqxfService.jxxqxfListQuery(request, response,true);
    }

    //..
    @RequestMapping("deleteJxxqxf")
    @ResponseBody
    public JsonResult deleteJxxqxf(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return jxxqxfService.deleteJxxqxf(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteJxxqxf", e);
            return new JsonResult(false, e.getMessage());
        }
    }


    @RequestMapping("jxxqxfEditPage")
    public ModelAndView jxxqxfEditPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/jxxqxfEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String jxxqxfId = RequestUtil.getString(request, "jxxqxfId");
        String action = RequestUtil.getString(request, "action");
        mv.addObject("jxxqxfId", jxxqxfId).addObject("action", action);
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        return mv;
    }

    @RequestMapping("getJxxqxfDetail")
    @ResponseBody
    public JSONObject getJxxqxfDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject obj = new JSONObject();
        String jxxqxfId = RequestUtil.getString(request, "jxxqxfId");
        if (StringUtils.isNotBlank(jxxqxfId)) {
            obj = jxxqxfService.getJxxqxfDetail(jxxqxfId);
        }
        return obj;
    }

    @RequestMapping("saveJxxqxf")
    @ResponseBody
    public JsonResult saveJxxqxf(HttpServletRequest request, @RequestBody String formData,
                                  HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
        if (StringUtils.isBlank(formData)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        try {
            JSONObject formDataJson = JSONObject.parseObject(formData);
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("id"))) {
                jxxqxfService.createJxxqxf(formDataJson);
            } else {
                jxxqxfService.updateJxxqxf(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save jxxqxf");
            result.setSuccess(false);
            result.setMessage("Exception in save jxxqxf");
            return result;
        }
        return result;
    }


    /**
     * 下载模板
     * */
    @RequestMapping("/downloadTemplate")
    public ResponseEntity<byte[]> downloadTemplate(HttpServletRequest request, HttpServletResponse response) {
        return jxxqxfService.downloadTemplate();
    }

    /**
     * 批量导入
     * */
    @RequestMapping("importJxxqxfExcel")
    @ResponseBody
    public JSONObject importJxxqxfExcel(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        jxxqxfService.importJxxqxfExcel(result, request);
        return result;
    }

    // 导出
    @RequestMapping("exportJxxqxf")
    public void exportJxxqxf(HttpServletRequest request, HttpServletResponse response) {
        jxxqxfService.exportJxxqxf(request, response);
    }
}
