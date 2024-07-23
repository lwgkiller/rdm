package com.redxun.keyDesign.core.controller;


import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.keyDesign.core.dao.YzbgDao;
import com.redxun.keyDesign.core.service.YzbgService;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.manager.LoginRecordManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.sys.org.manager.OsGroupManager;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/yzbg/")
public class YzbgController extends GenericController{
    @Autowired
    private CommonInfoManager commonInfoManager;
    @Autowired
    private YzbgService yzbgService;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;

    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "keyDesign/core/yzbgFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("detailId", RequestUtil.getString(request, "detailId", ""));
        return mv;
    }
    @RequestMapping(value = "upload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            yzbgService.saveYzbgUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }
    @RequestMapping("fileList")
    public ModelAndView fileList(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "keyDesign/core/yzbgFileList.jsp";
        String type =RequestUtil.getString(request,"type","");
        String detailId = RequestUtil.getString(request, "detailId");
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("detailId", detailId);
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }
    @RequestMapping("yzbgListPage")
    public ModelAndView msgManagement(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "keyDesign/core/yzbgList.jsp";
        String type =RequestUtil.getString(request,"type","");

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
        String jspPath = "keyDesign/core/yzbgEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String bgId = RequestUtil.getString(request, "bgId");
        String action = RequestUtil.getString(request, "action");
        String type = RequestUtil.getString(request, "type");
        mv.addObject("bgId", bgId).
                addObject("action", action).addObject("type", type);
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

    @RequestMapping("saveYzbg")
    @ResponseBody
    public JsonResult saveYzbg(HttpServletRequest request, @RequestBody JSONObject formDataJson, HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "成功");
        try {
            String type = RequestUtil.getString(request, "type");
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("bgId"))) {
                formDataJson.put("belongbj",type);
                yzbgService.createYzbg(formDataJson);
            }else {
                yzbgService.updateYzbg(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save Cnsx");
            result.setSuccess(false);
            result.setMessage("Exception in save Cnsx");
            return result;
        }
        result.setData(formDataJson.getString("bgId"));
        return result;
    }

    @RequestMapping("queryYzbg")
    @ResponseBody
    public JsonPageResult<?> queryJstb(HttpServletRequest request, HttpServletResponse response) {
        return yzbgService.queryYzbg(request, true);
    }
    @RequestMapping("deleteYzbg")
    @ResponseBody
    public JsonResult deleteYzbg(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return yzbgService.deleteYzbg(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteYzbg", e);
            return new JsonResult(false, e.getMessage());
        }
    }


    @RequestMapping("getYzbg")
    @ResponseBody
    public JSONObject getYzbg(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject yzbgObj = new JSONObject();
        String bgId = RequestUtil.getString(request, "bgId");
        if (StringUtils.isNotBlank(bgId)) {
            yzbgObj = yzbgService.getYzbgById(bgId);
        }
        return yzbgObj;
    }

    @RequestMapping("getYzbgDetailList")
    @ResponseBody
    public List<JSONObject> getYzbgDetailList(HttpServletRequest request, HttpServletResponse response) {
        return yzbgService.getYzbgDetailList(request);
    }

    @RequestMapping("getYzbgFileList")
    @ResponseBody
    public List<JSONObject> getYzbgFileList(HttpServletRequest request, HttpServletResponse response) {
        return yzbgService.getYzbgDetailFileList(request);
    }

    @RequestMapping("deleteYzbgFile")
    public void deleteYzbgFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String bgId = postBodyObj.getString("formId");
        yzbgService.deleteOneYzbgFile(fileId, fileName, bgId);
    }
    @RequestMapping("yzbgPdfPreview")
    public ResponseEntity<byte[]> yzbgPdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("yzbgFilePathBase");
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, fileBasePath);
    }

    @RequestMapping("yzbgOfficePreview")
    @ResponseBody
    public void yzbgOfficePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("yzbgFilePathBase");
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("yzbgImagePreview")
    @ResponseBody
    public void yzbgImagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("yzbgFilePathBase");
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, fileBasePath, response);
    }
    @RequestMapping("editYzbgDetail")
    public ModelAndView editCn(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "keyDesign/core/yzbgDetailEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String action = RequestUtil.getString(request, "action");
        String belongBg = RequestUtil.getString(request, "bgId");
        String detailId = RequestUtil.getString(request, "detailId");
        if (StringUtils.isBlank(action)) {
            action = "edit";
        }
        mv.addObject("action", action);
        mv.addObject("belongBg", belongBg);
        mv.addObject("detailId", detailId);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }
    @RequestMapping("saveYzbgDetail")
    @ResponseBody
    public JsonResult saveYzbgDetail(HttpServletRequest request, @RequestBody JSONObject formDataJson, HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "成功");
        try {
            String belongBg = RequestUtil.getString(request, "belongBg");
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("detailId"))) {
                formDataJson.put("belongBg",belongBg);
                yzbgService.createYzbgDetail(formDataJson);
            }else {
                yzbgService.updateYzbgDetail(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save Cnsx");
            result.setSuccess(false);
            result.setMessage("Exception in save Cnsx");
            return result;
        }
        result.setData(formDataJson.getString("detailId"));
        return result;
    }
    @RequestMapping("getYzbgDetail")
    @ResponseBody
    public JSONObject getYzbgDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject yzbgObj = new JSONObject();
        String detailId = RequestUtil.getString(request, "detailId");
        if (StringUtils.isNotBlank(detailId)) {
            yzbgObj = yzbgService.getYzbgDetail(detailId);
        }
        return yzbgObj;
    }
    @RequestMapping("deleteYzbgDetail")
    @ResponseBody
    public JsonResult deleteYzbgDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String detailId = RequestUtil.getString(request, "detailId");
            return yzbgService.deleteOneYzbgDetail(detailId);
        } catch (Exception e) {
            logger.error("Exception in deleteJsmm", e);
            return new JsonResult(false, e.getMessage());
        }
    }

}

