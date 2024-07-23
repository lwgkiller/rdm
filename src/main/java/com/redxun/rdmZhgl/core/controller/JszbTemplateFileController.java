package com.redxun.rdmZhgl.core.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.rdmZhgl.core.dao.JszbDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Controller
@RequestMapping("/zhgl/core/jszbTemplate/")
public class JszbTemplateFileController {
    private Logger logger = LoggerFactory.getLogger(JszbTemplateFileController.class);
    @Autowired
    private JszbDao jszbDao;

    @RequestMapping("listPage")
    public ModelAndView listPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/jszbTemplateList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }
    @RequestMapping("fileList")
    @ResponseBody
    public List<JSONObject> fileList(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String docName = RequestUtil.getString(request, "fileName");
        Map<String, Object> params = new HashMap<>();
        if (StringUtils.isNotBlank(docName)) {
            params.put("fileName", docName);
        }
        List<JSONObject> fileList = jszbDao.queryJszbTemplateFileList(params);
        for (JSONObject oneObj : fileList) {
            if (StringUtils.isNotBlank(oneObj.getString("CREATE_TIME_"))) {
                oneObj.put("CREATE_TIME_", DateFormatUtil.format(oneObj.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        return fileList;
    }

}
