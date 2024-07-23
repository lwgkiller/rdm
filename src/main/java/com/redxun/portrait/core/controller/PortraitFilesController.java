package com.redxun.portrait.core.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.portrait.core.manager.PortraitFilesManager;
import com.redxun.saweb.util.RequestUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.StringUtil;
import com.redxun.portrait.core.manager.PortraitHonorManager;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.util.ConstantUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangzhen
 */
@RestController
@RequestMapping("/portrait/files/")
public class PortraitFilesController {
    @Resource
    PortraitFilesManager portraitFilesManager;
    @RequestMapping("fileWindow")
    public ModelAndView getFileWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "portrait/files/portraitFileList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String mainId = RequestUtil.getString(request, "mainId");
        String fileType = RequestUtil.getString(request, "fileType");
        Boolean editable = RequestUtil.getBoolean(request, "editable");
        mv.addObject("mainId", mainId);
        mv.addObject("fileType", fileType);
        mv.addObject("editable", editable);
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        return mv;
    }
    /**
     * 获取列表数据
     * */
    @RequestMapping("files")
    @ResponseBody
    public JsonPageResult<?> getFiles(HttpServletRequest request, HttpServletResponse response) {
        return portraitFilesManager.getFileList(request);
    }
    @RequestMapping("fileUploadWindow")
    public ModelAndView fileUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "portrait/files/portraitFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("mainId", RequestUtil.getString(request, "mainId", ""));
        mv.addObject("fileType", RequestUtil.getString(request, "fileType", ""));
        return mv;
    }
    @RequestMapping(value = "fileUpload")
    @ResponseBody
    public Map<String, Object> fileUpload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件，成功后再写入数据库，前台是一个一个文件的调用
            portraitFilesManager.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            modelMap.put("success", "false");
        }
        return modelMap;
    }
    @RequestMapping("delFile")
    public void delFile(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "id");
        String mainId = RequestUtil.getString(request, "mainId");
        portraitFilesManager.deleteOneSaleFile(fileId, fileName, mainId);
    }
}
