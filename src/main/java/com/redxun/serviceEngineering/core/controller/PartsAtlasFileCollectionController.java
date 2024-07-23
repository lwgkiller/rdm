package com.redxun.serviceEngineering.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.service.PartsAtlasFileCollectionService;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/serviceEngineering/core/patsAtlasFileCollection")
public class PartsAtlasFileCollectionController {
    private static final Logger logger = LoggerFactory.getLogger(PartsAtlasFileCollectionController.class);
    @Autowired
    PartsAtlasFileCollectionService partsAtlasFileCollectionService;
    private XcmgProjectOtherDao xcmgProjectOtherDao;

    //..
    @RequestMapping("dataListPage")
    public ModelAndView dataListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/partsAtlasArchiveList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));

        //..传入节点变量
        String nodeId = RequestUtil.getString(request, "nodeId");

        return mv;
    }

    //..
    @RequestMapping("dataListQuery")
    @ResponseBody
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {

        return partsAtlasFileCollectionService.dataListQuery(request, response);
    }

    //..
    @RequestMapping("Preview")
    public void Preview(HttpServletRequest request, HttpServletResponse response) {
        partsAtlasFileCollectionService.Preview(request, response);
    }

    //..
    @RequestMapping("previewOffice")
    public void previewOffice(HttpServletRequest request, HttpServletResponse response) {
        partsAtlasFileCollectionService.previewOffice(request, response);
    }

    //..
    @RequestMapping("Download")
    public ResponseEntity<byte[]> Download(HttpServletRequest request, HttpServletResponse response) {
        String id = RequestUtil.getString(request, "id");
        String description = RequestUtil.getString(request, "description");
        return partsAtlasFileCollectionService.Download(request, id, description);
    }

    //..
    @RequestMapping("EditPage")
    public ModelAndView EditPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "serviceEngineering/core/partsAtlasFileCollectEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String id = RequestUtil.getString(request, "id");
        String action = RequestUtil.getString(request, "action");
        JSONObject obj = new JSONObject();
        if (StringUtils.isNotBlank(id)) {
            obj = partsAtlasFileCollectionService.queryDataById(id);
        }
        mv.addObject("obj", obj).addObject("businessId", obj.getString("id"));
        mv.addObject("action", action);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }

    //..
    @RequestMapping("deleteBusiness")
    @ResponseBody
    public JSONObject deleteBusiness(HttpServletRequest request, @RequestBody String requestBody,
                                     HttpServletResponse response) {
        JSONObject result = new JSONObject();
        JSONObject requestBodyObj = JSONObject.parseObject(requestBody);
        String id = requestBodyObj.getString("id");
        partsAtlasFileCollectionService.deleteBusiness(result, id);
        return result;
    }

    //..
    @RequestMapping("saveBusiness")
    @ResponseBody
    public JSONObject saveBusiness(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        partsAtlasFileCollectionService.saveBusiness(result, request);
        return result;
    }

    //..
    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/multiFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    //..
    @RequestMapping(value = "fileUpload")
    @ResponseBody
    public JSONObject upload(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JSONObject result = new JSONObject();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            partsAtlasFileCollectionService.saveUploadFiles(result, request);
            return result;

        } catch (Exception e) {
            logger.error("Exception in upload", e);
            result.put("message", "上传失败！");
            result.put("success", false);
        }
        return result;
    }
}
