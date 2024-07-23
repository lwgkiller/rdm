package com.redxun.zlgjNPI.core.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import com.redxun.zlgjNPI.core.dao.ZlgjDao;
import com.redxun.zlgjNPI.core.manager.ZlgjService;

/**
 * 应用模块名称
 * <p>
 * 代码描述
 * <p>
 * Copyright: Copyright (C) 2021 XXX, Inc. All rights reserved.
 * <p>
 * Company: 徐工挖掘机械有限公司
 * <p>
 *
 * @author liangchuanjiang
 * @since 2021/2/23 10:43
 */
@RestController
@RequestMapping("/zhgl/core/zlgj/")
public class ZlgjController {
    private Logger logger = LogManager.getLogger(ZlgjController.class);
    @Resource
    private ZlgjService zlgjService;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private ZlgjDao zlgjDao;

    @RequestMapping("gjllPage")
    public ModelAndView listPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "zlgjNPI/gjllFileList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("getGjllFileList")
    @ResponseBody
    public JsonPageResult<?> getJjllFileList(HttpServletRequest request, HttpServletResponse response) {
        return zlgjService.getGjllFileList(request);
    }

    @RequestMapping("fileUploadWindow")
    public ModelAndView fileUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "zlgjNPI/gjllFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    @RequestMapping(value = "fileUpload")
    @ResponseBody
    public Map<String, Object> fileUpload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件，成功后再写入数据库，前台是一个一个文件的调用
            zlgjService.saveGjllUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    @RequestMapping("delGjllFile")
    public void delGjllFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        zlgjService.deleteOneGjllFile(fileId, fileName);
    }

    @RequestMapping("gjllDownload")
    public ResponseEntity<byte[]> pdfPreviewOrDownload(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String fileBasePath = WebAppUtil.getProperty("zlgjllFilePathBase");
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, "", fileBasePath);
    }

    @RequestMapping("gjllOfficePreview")
    @ResponseBody
    public void gjllOfficePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String fileBasePath = WebAppUtil.getProperty("zlgjllFilePathBase");
        rdmZhglFileManager.officeFilePreview(fileName, fileId, "", fileBasePath, response);
    }

    @RequestMapping("gjllImagePreview")
    @ResponseBody
    public void gjllImagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String fileBasePath = WebAppUtil.getProperty("zlgjllFilePathBase");
        rdmZhglFileManager.imageFilePreview(fileName, fileId, "", fileBasePath, response);
    }


    /**
     * 判断问题处理人的选择是否合适
     * 
     * @param request
     * @param response
     */
    @RequestMapping("judgeWtProcessor")
    @ResponseBody
    public JSONObject judgeWtProcessor(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        result.put("result", true);
        String zrrId = RequestUtil.getString(request, "zrrId", "");
        String ssbmId = RequestUtil.getString(request, "ssbmId", "");
        String ssbmName = RequestUtil.getString(request, "ssbmName", "");
        zlgjService.judgeWtProcessor(result, zrrId, ssbmId, ssbmName);
        return result;
    }
}
