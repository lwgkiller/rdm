package com.redxun.fzsj.core.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.fzsj.core.service.FzdxService;
import com.redxun.fzsj.core.service.FzsjService;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/fzsj/core/fzdx")
public class FzdxController {
    private static final Logger logger = LoggerFactory.getLogger(FzdxController.class);

    @Autowired
    private FzdxService fzdxService;

    @Autowired
    private RdmZhglUtil rdmZhglUtil;


    @RequestMapping("fznlzbPage")
    public ModelAndView fznlzbPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "fzsj/core/fznlzbPage.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        boolean fzsjgly = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), "仿真设计管理员");
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("fzsjgly", fzsjgly);
        return mv;
    }

    @RequestMapping("fzdxListQuery")
    @ResponseBody
    public JsonPageResult<?> fzdxListQuery(HttpServletRequest request, HttpServletResponse response) {
        return fzdxService.fzdxListQuery(request, response);
    }

    @RequestMapping("getFzdxTree")
    @ResponseBody
    public List<JSONObject> getFzdxTree(HttpServletRequest request, HttpServletResponse response) {
        return fzdxService.getFzdxTree();
    }

    @RequestMapping("getFzdxEchartsData")
    @ResponseBody
    public List<JSONObject> getFzdxEchartsData(HttpServletRequest request, HttpServletResponse response) {
        return fzdxService.getFzdxEchartsData();
    }

    @RequestMapping("openfzdxEditPage")
    public ModelAndView openfzdxEditPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "fzsj/core/fzdxEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String pid = RequestUtil.getString(request, "pid");
        String path = RequestUtil.getString(request, "path");
        String action = RequestUtil.getString(request, "action");
        String fzdxId = RequestUtil.getString(request, "fzdxId");
        mv.addObject("pid", pid);
        mv.addObject("path", path);
        mv.addObject("action", action);
        mv.addObject("fzdxId", fzdxId);
        return mv;
    }

    @RequestMapping("saveFzdx")
    @ResponseBody
    public JsonResult saveFzdx(HttpServletRequest request ,@RequestBody String formData, HttpServletResponse response) {
        JsonResult result = new JsonResult(true);
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
                fzdxService.createFzdx(formDataJson);
            } else {
                fzdxService.updateFzzx(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save fzdx");
            result.setSuccess(false);
            result.setMessage("Exception in save fzdx");
            return result;
        }
        return result;
    }

    @RequestMapping("getFzdxById")
    @ResponseBody
    public JSONObject getFzdxById(HttpServletRequest request, HttpServletResponse response) {
        String fzdxId = RequestUtil.getString(request,"fzdxId");
        return fzdxService.getFzdxById(fzdxId);
    }

    @RequestMapping("deleteFzdx")
    @ResponseBody
    public JsonResult deleteFzdx(HttpServletRequest request, HttpServletResponse response) {
        try {
            String path = RequestUtil.getString(request, "path");
            return fzdxService.deleteFzdx(path);
        } catch (Exception e) {
            logger.error("Exception in deleteFzdx", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("openFzfxxWindow")
    public ModelAndView openFzfxxWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "fzsj/core/fzfxxList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("fzdxId", RequestUtil.getString(request,"fzdxId"));
        mv.addObject("fzsjgly", RequestUtil.getString(request,"fzsjgly"));
        return mv;
    }

    @RequestMapping("saveFzfxx")
    @ResponseBody
    public JsonResult saveFzfxx(HttpServletRequest request, @RequestBody String formData,
                                   HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(formData)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        try {
            JSONArray jsonArray = JSONObject.parseArray(formData);
            for(int i = 0; i<jsonArray.size(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                if (StringUtils.isBlank(jsonObject.getString("id"))) {
                    fzdxService.createFzfxx(jsonObject);
                } else {
                    fzdxService.updateFzfxx(jsonObject);
                }
            }

        } catch (Exception e) {
            logger.error("Exception in save fzsj");
            result.setSuccess(false);
            result.setMessage("Exception in save fzsj");
            return result;
        }
        return result;
    }

    @RequestMapping("deleteFzfxx")
    @ResponseBody
    public JsonResult deleteFzfxx(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String strs = RequestUtil.getString(request, "ids");
            String[] ids = strs.split(",");
            return fzdxService.deleteFzfxx(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteData", e);
            return new JsonResult(false, e.getMessage());
        }
    }
}
