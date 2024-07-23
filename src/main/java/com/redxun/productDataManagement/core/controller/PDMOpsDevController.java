package com.redxun.productDataManagement.core.controller;


import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.productDataManagement.core.dao.PDMOpsDevDao;
import com.redxun.productDataManagement.core.manager.PDMOpsDevManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.redxun.rdmZhgl.core.service.CityProgressAwardService.delFiles;

/*
 * PDM故障知识库
 * */
@Controller
@RequestMapping("/pdm/core/PDMOpsDev/")
public class PDMOpsDevController {
    private static final Logger logger = LoggerFactory.getLogger(PDMOpsDevController.class);
    @Autowired
    private PDMOpsDevManager pdmOpsDevManager;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private PDMOpsDevDao pDMOpsDevDao;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @RequestMapping("getListPage")
    public ModelAndView trainList(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "/productDataManagement/core/pdmOpsDevList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("applyList")
    @ResponseBody
    public JsonPageResult<?> queryApplyList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return pdmOpsDevManager.queryApplyList(request, true);

    }

    @RequestMapping("edit")
    public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) {

        String jspPath = "/productDataManagement/core/pdmOpsDevEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String id = RequestUtil.getString(request, "id");
        mv.addObject("id", id);
        String action = RequestUtil.getString(request, "action");
        JSONObject fileObj = new JSONObject();
        //查询信息
        if (StringUtils.isNotBlank(id)) {
            Map<String, Object> cpaInfo = pdmOpsDevManager.queryfileById(id);
            fileObj = XcmgProjectUtil.convertMap2JsonObject(cpaInfo);
        }
        mv.addObject("fileObj", fileObj);
        mv.addObject("action", action);
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));

        return mv;
    }

    /**
     * 编辑/新增内容
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("saveFile")
    @ResponseBody
    public JSONObject saveFile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JSONObject result = new JSONObject();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            pdmOpsDevManager.saveFile(result,request);
            return result;

        } catch (Exception e) {
            logger.error("Exception in saveFile", e);
            result.put("message", "上传失败！");
            result.put("success", false);
        }
        return result;
    }

    @RequestMapping("deleteFile")
    @ResponseBody
    public JsonResult deleteFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {

            String uIdStr = RequestUtil.getString(request, "ids");

            List<String> ids = Arrays.asList(uIdStr.split(","));

            Map<String, Object> param = new HashMap<>();
            param.put("ids",ids);
            if (!ids.isEmpty()){
                pDMOpsDevDao.deleteFile(param);
            }

            //文件删除
            String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                    "pdmUploadPosition", "pdmSolution").getValue();
            if (StringUtils.isBlank(filePathBase)) {
                logger.error("can't find pdmUploadPosition");
            }
            if(!ids.isEmpty()){
                for (String id : ids) {
                    String filePath = filePathBase + File.separator + id;
                    File pathFile = new File(filePath);
                    delFiles(pathFile);
                }
            }


        } catch (Exception e) {
            logger.error("Exception in deleteFile", e);
            return new JsonResult(false, e.getMessage());
        }

        return new JsonResult(true,"操作成功");
    }

    //..
    @RequestMapping("pdfPreviewAndAllDownload")
    public ResponseEntity<byte[]> pdfPreviewAndAllDownload(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "pdmUploadPosition", "pdmSolution").getValue();
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, filePathBase);
    }

    //..
    @RequestMapping("officePreview")
    @ResponseBody
    public void officePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "pdmUploadPosition", "pdmSolution").getValue();
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, filePathBase, response);
    }

    //..
    @RequestMapping("imagePreview")
    @ResponseBody
    public void imagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "pdmUploadPosition", "pdmSolution").getValue();
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, filePathBase, response);
    }
}
