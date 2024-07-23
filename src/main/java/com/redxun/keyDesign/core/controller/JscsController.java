package com.redxun.keyDesign.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.keyDesign.core.service.JscsService;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.manager.LoginRecordManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
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
@RequestMapping("/jscs/")
public class JscsController extends GenericController {
    @Autowired
    private CommonInfoManager commonInfoManager;
    @Autowired
    private JscsService jscsService;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private LoginRecordManager loginRecordManager;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;

    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "keyDesign/core/jscsFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("csId", RequestUtil.getString(request, "csId", ""));
        return mv;
    }

    @RequestMapping(value = "upload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            jscsService.saveJscsUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    @RequestMapping("fileList")
    public ModelAndView fileList(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "keyDesign/core/jscsFileList.jsp";
        String type = RequestUtil.getString(request, "type", "");
        String csId = RequestUtil.getString(request, "csId");
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("csId", csId);
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("jscsListPage")
    public ModelAndView msgManagement(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "keyDesign/core/jscsList.jsp";
        String type = RequestUtil.getString(request, "type", "");

        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        boolean isGlr = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), "关键零部件管理人");
        mv.addObject("isGlr", isGlr);
        mv.addObject("type", type);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "keyDesign/core/jscsEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String jsId = RequestUtil.getString(request, "jsId");
        String action = RequestUtil.getString(request, "action");
        String type = RequestUtil.getString(request, "type");
        String jstype = RequestUtil.getString(request, "jstype");
        String oldjsId = RequestUtil.getString(request, "oldjsId");
        if("old".equals(jstype)&&StringUtils.isNotBlank(oldjsId)){
            mv.addObject("oldjsId", oldjsId);
        }
        mv.addObject("jstype", jstype);
        mv.addObject("jsId", jsId).addObject("action", action).addObject("type", type);
        JSONObject currentUserDepInfo = xcmgProjectManager.isCurrentUserDepRespman();
        JSONObject dept = commonInfoManager.queryDeptNameById(currentUserDepInfo.getString("currentUserMainDepId"));
        String deptName = dept.getString("deptname");
        mv.addObject("deptName", deptName);
        mv.addObject("currentUserMainDepId", currentUserDepInfo.getString("currentUserMainDepId"));
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("saveJscs")
    @ResponseBody
    public JsonResult saveJscs(HttpServletRequest request, @RequestBody JSONObject formDataJson,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "成功");
        try {
            String type = RequestUtil.getString(request, "type");
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("jsId"))) {
                formDataJson.put("belongbj", type);
                jscsService.createJscs(formDataJson);
            } else {
                jscsService.updateJscs(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save Cnsx");
            result.setSuccess(false);
            result.setMessage("Exception in save Cnsx");
            return result;
        }
        result.setData(formDataJson.getString("jsId"));
        return result;
    }

    @RequestMapping("saveOldJscs")
    @ResponseBody
    public JsonResult saveOldJscs(HttpServletRequest request, @RequestBody JSONObject formDataJson,
                               HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "复制成功");
        if (formDataJson == null || formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        try {
            String type = RequestUtil.getString(request, "type");
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("jsId"))) {
                formDataJson.put("belongbj", type);
                jscsService.copyJscs(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save ckdd");
            result.setSuccess(false);
            result.setMessage("Exception in save ckdd");
            return result;
        }
        result.setData(formDataJson.getString("jsId"));
        return result;
    }

    @RequestMapping("queryJscs")
    @ResponseBody
    public JsonPageResult<?> queryJstb(HttpServletRequest request, HttpServletResponse response) {
        return jscsService.queryJscs(request, true);
    }

    @RequestMapping("deleteJscs")
    @ResponseBody
    public JsonResult deleteJscs(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return jscsService.deleteJscs(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteJscs", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("getJscs")
    @ResponseBody
    public JSONObject getJscs(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject jscsObj = new JSONObject();
        String jsId = RequestUtil.getString(request, "jsId");
        if (StringUtils.isNotBlank(jsId)) {
            jscsObj = jscsService.getJscsById(jsId);
        }
        return jscsObj;
    }

    @RequestMapping("getJscsDetailList")
    @ResponseBody
    public List<JSONObject> getJscsDetailList(HttpServletRequest request, HttpServletResponse response) {
        return jscsService.getJscsDetailList(request);
    }

    @RequestMapping("getJscsFileList")
    @ResponseBody
    public List<JSONObject> getJscsFileList(HttpServletRequest request, HttpServletResponse response) {
        return jscsService.getJscsDetailFileList(request);
    }

    @RequestMapping("deleteJscsFile")
    public void deleteJscsFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String jsId = postBodyObj.getString("formId");
        jscsService.deleteOneJscsFile(fileId, fileName, jsId);
    }

    @RequestMapping("jscsPdfPreview")
    public ResponseEntity<byte[]> jscsPdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("jscsFilePathBase");
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, fileBasePath);
    }

    @RequestMapping("jscsOfficePreview")
    @ResponseBody
    public void jscsOfficePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("jscsFilePathBase");
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("jscsImagePreview")
    @ResponseBody
    public void jscsImagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("jscsFilePathBase");
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("editJscsDetail")
    public ModelAndView editCn(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "keyDesign/core/jscsDetailEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String action = RequestUtil.getString(request, "action");
        String belongJs = RequestUtil.getString(request, "jsId");
        String csId = RequestUtil.getString(request, "csId");
        if (StringUtils.isBlank(action)) {
            action = "edit";
        }
        String cstype = RequestUtil.getString(request, "cstype");
        String oldcsId = RequestUtil.getString(request, "oldcsId");
        if("old".equals(cstype)&&StringUtils.isNotBlank(oldcsId)){
            mv.addObject("oldcsId", oldcsId);
        }
        mv.addObject("action", action);
        mv.addObject("belongJs", belongJs);
        mv.addObject("csId", csId);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("saveJscsDetail")
    @ResponseBody
    public JsonResult saveJscsDetail(HttpServletRequest request, @RequestBody JSONObject formDataJson,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "成功");
        try {
            String belongJs = RequestUtil.getString(request, "belongJs");
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("csId"))) {
                formDataJson.put("belongJs", belongJs);
                jscsService.createJscsDetail(formDataJson);
            } else {
                jscsService.updateJscsDetail(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save Cnsx");
            result.setSuccess(false);
            result.setMessage("Exception in save Cnsx");
            return result;
        }
        result.setData(formDataJson.getString("csId"));
        return result;
    }

    @RequestMapping("getJscsDetail")
    @ResponseBody
    public JSONObject getJscsDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject jscsObj = new JSONObject();
        String csId = RequestUtil.getString(request, "csId");
        if (StringUtils.isNotBlank(csId)) {
            jscsObj = jscsService.getJscsDetail(csId);
        }
        return jscsObj;
    }

    @RequestMapping("deleteJscsDetail")
    @ResponseBody
    public JsonResult deleteJscsDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String detailId = RequestUtil.getString(request, "csId");
            return jscsService.deleteOneJscsDetail(detailId);
        } catch (Exception e) {
            logger.error("Exception in deleteJsmm", e);
            return new JsonResult(false, e.getMessage());
        }
    }

}
