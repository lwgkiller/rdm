package com.redxun.componentTest.core.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.ExceptionUtil;
import com.redxun.componentTest.core.service.ComponentResourcesPlatformDataService;
import com.redxun.core.util.FileUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.org.entity.OsDimension;
import com.redxun.componentTest.core.service.ComponentResourcesPlatformDataService;
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
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/resourcesPlatform/core/masterData/")
public class ComponentResourcesPlatformDataController {
    private static final Logger logger = LoggerFactory.getLogger(ComponentResourcesPlatformDataController.class);
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private CommonInfoManager commonInfoManager;
    @Autowired
    private ComponentResourcesPlatformDataService componentResourcesPlatformDataService;

    //..
    @RequestMapping("masterDataViewPage")
    public ModelAndView masterDataViewPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "componentTest/core/componentResourcesPlatformMasterDataView.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }
    //..
    @RequestMapping("masterDatasPage")
    public ModelAndView dataListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "componentTest/core/componentResourcesPlatformDataList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        boolean isComponentResourcesPlatformDataAdmins =
                commonInfoManager.judgeUserIsPointRole("零部件台架资源管理员",ContextUtil.getCurrentUserId());
        mv.addObject("isComponentResourcesPlatformDataAdmins", isComponentResourcesPlatformDataAdmins);
        return mv;
    }

    //..
    @RequestMapping("masterDataEditPage")
    public ModelAndView masterDataEditPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "componentTest/core/componentResourcesPlatformDataEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String businessId = RequestUtil.getString(request, "businessId");
        String action = RequestUtil.getString(request, "action");
        mv.addObject("businessId", businessId).addObject("action", action);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        boolean isComponentResourcesPlatformDataAdmins =
                commonInfoManager.judgeUserIsPointRole("零部件台架资源管理员",ContextUtil.getCurrentUserId());
        mv.addObject("isComponentResourcesPlatformDataAdmins", isComponentResourcesPlatformDataAdmins);
        return mv;
    }

    //..
    @RequestMapping("masterDataListQuery")
    @ResponseBody
    public JsonPageResult<?> masterDataListQuery(HttpServletRequest request, HttpServletResponse response) {
        return componentResourcesPlatformDataService.masterDataListQuery(request, response);
    }

    //..
    @RequestMapping("getMasterDataDataById")
    @ResponseBody
    public JSONObject getmasterDataDataById(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = RequestUtil.getString(request, "businessId");
        if (StringUtil.isEmpty(id)) {
            return new JSONObject().fluentPut("responsibleUserId", ContextUtil.getCurrentUserId())
                    .fluentPut("responsibleUser", ContextUtil.getCurrentUser().getFullname());
        } else {
            return componentResourcesPlatformDataService.getMasterDataById(id);
        }
    }

    //..
    @RequestMapping(value = "saveMasterData", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveMasterData(HttpServletRequest request, HttpServletResponse response) {
        try {
            return componentResourcesPlatformDataService.saveMasterData(request);
        } catch (Exception e) {
            logger.error("Exception in savemasterData", e);
            return new JsonResult(false, e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
        }
    }

    //..
    @RequestMapping("deleteMasterData")
    @ResponseBody
    public JsonResult deleteMasterData(HttpServletRequest request, HttpServletResponse response) {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return componentResourcesPlatformDataService.deleteMasterData(ids);
        } catch (Exception e) {
            logger.error("Exception in deletemasterData", e);
            return new JsonResult(false, e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
        }
    }



    //..
    @RequestMapping("pdfPreview")
    public ResponseEntity<byte[]> pdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String businessId = RequestUtil.getString(request, "businessId");
        String businessType = RequestUtil.getString(request, "businessType");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("worldUploadPosition", businessType).getValue();
        return componentResourcesPlatformDataService.pdfPreviewOrDownLoad(fileName, fileId, businessId, filePathBase);
    }

    //..
    @RequestMapping("officePreview")
    @ResponseBody
    public void officePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String businessId = RequestUtil.getString(request, "businessId");
        String businessType = RequestUtil.getString(request, "businessType");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("worldUploadPosition", businessType).getValue();
        componentResourcesPlatformDataService.officeFilePreview(fileName, fileId, businessId, filePathBase, response);
    }

    //..
    @RequestMapping("imagePreview")
    @ResponseBody
    public void imagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String businessId = RequestUtil.getString(request, "businessId");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("resourcesPlatformUpload",
                "componentResourcesPlatformPic").getValue();
        componentResourcesPlatformDataService.imageFilePreview(fileName, fileId, businessId, filePathBase, response);
    }

}
