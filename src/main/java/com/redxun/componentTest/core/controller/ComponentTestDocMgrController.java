package com.redxun.componentTest.core.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.componentTest.core.service.ComponentTestDocMgrService;
import com.redxun.org.api.model.IUser;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/componentTest/core/docmgr")
public class ComponentTestDocMgrController {
    private static final Logger logger = LoggerFactory.getLogger(ComponentTestDocMgrController.class);
    @Autowired
    private ComponentTestDocMgrService componentTestDocMgrService;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;

    //..
    @RequestMapping("dataListPage")
    public ModelAndView xcmgDocMgrPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "componentTest/core/componentTestDocMgrList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        IUser currentUser = ContextUtil.getCurrentUser();
        Map<String, Object> params = new HashMap<>();
        params.put("userId", currentUser.getUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 返回当前登录人角色信息
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        String isComponentTestAdmin = "false";
        for (Map<String, Object> map : currentUserRoles) {
            if (map.get("NAME_").toString().equalsIgnoreCase("零部件试验管理员")) {
                isComponentTestAdmin = "true";
                break;
            }
        }
        mv.addObject("isComponentTestAdmin", isComponentTestAdmin);
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    //..
    @RequestMapping("uploadWindow")
    public ModelAndView uploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "componentTest/core/componentTestDocMgrUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("mainKanbanId", "common");
        return mv;
    }

    //..
    @RequestMapping("dataListQuery")
    @ResponseBody
    public List<JSONObject> dataListQuery(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String docName = RequestUtil.getString(request, "docName");
        JSONObject params = new JSONObject();
        params.put("docName", docName);
        return componentTestDocMgrService.dataListQuery(params);
    }

    //..
    @RequestMapping("pdfPreviewAndAllDownload")
    public ResponseEntity<byte[]> pdfPreviewAndAllDownload(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "componentTestUploadPosition", "commonFile").getValue();
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
                "componentTestUploadPosition", "commonFile").getValue();
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
                "componentTestUploadPosition", "commonFile").getValue();
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, filePathBase, response);
    }

    //..
    @RequestMapping("delFile")
    public void delFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String mainId = postBodyObj.getString("formId");
        componentTestDocMgrService.deleteOneFile(fileId, fileName, mainId);
    }

    //..
    @RequestMapping("fileUpload")
    @ResponseBody
    public Map<String, Object> fileUpload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            //先保存文件，成功后再写入数据库，前台是一个一个文件的调用
            componentTestDocMgrService.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }
}
