package com.redxun.serviceEngineering.core.controller;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.service.ZzzFileService;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Controller
@RequestMapping("/serviceEngineering/core/zzzFile")
public class ZzzFileController {
    private static final Logger logger = LoggerFactory.getLogger(ZzzFileController.class);
    @Autowired
    ZzzFileService zzzFileService;

    // ..
    @RequestMapping("dataListPage")
    public ModelAndView dataListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/zzzFileList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String menuType = RequestUtil.getString(request, "menuType");
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("menuType", menuType);
        return mv;
    }

    // ..
    @RequestMapping("dataListQuery")
    @ResponseBody
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        return zzzFileService.dataListQuery(request, response);
    }

    // ..
    @RequestMapping("Preview")
    public void Preview(HttpServletRequest request, HttpServletResponse response) {
        zzzFileService.Preview(request, response);
    }

    // ..
    @RequestMapping("Download")
    public ResponseEntity<byte[]> Download(HttpServletRequest request, HttpServletResponse response) {
        String id = RequestUtil.getString(request, "id");
        String description = RequestUtil.getString(request, "description");
        return zzzFileService.Download(request, id, description);
    }

    // ..
    @RequestMapping("EditPage")
    public ModelAndView EditPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "serviceEngineering/core/zzzFileEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String id = RequestUtil.getString(request, "id");
        String action = RequestUtil.getString(request, "action");
        String menuType = RequestUtil.getString(request, "menuType");
        JSONObject obj = new JSONObject();
        if (StringUtils.isNotBlank(id)) {
            obj = zzzFileService.queryDataById(id);
        }
        obj.put("creatorName", ContextUtil.getCurrentUser().getFullname());
        obj.put("menuType", menuType);
        mv.addObject("obj", obj).addObject("businessId", obj.getString("id"));
        mv.addObject("action", action);
        mv.addObject("menuType", menuType);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }

    // ..
    @RequestMapping("deleteBusiness")
    @ResponseBody
    public JSONObject deleteBusiness(HttpServletRequest request, @RequestBody String requestBody,
        HttpServletResponse response) {
        JSONObject result = new JSONObject();
        JSONObject requestBodyObj = JSONObject.parseObject(requestBody);
        String id = requestBodyObj.getString("id");
        String fileName = requestBodyObj.getString("fileName");
        zzzFileService.deleteBusiness(result, id, fileName);
        return result;
    }

    // ..
    @RequestMapping("saveBusiness")
    @ResponseBody
    public JSONObject saveBusiness(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        zzzFileService.saveBusiness(result, request);
        return result;
    }
}
