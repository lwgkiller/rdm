package com.redxun.qualityfxgj.core.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.qualityfxgj.core.dao.GyfkDao;
import com.redxun.qualityfxgj.core.service.GyfkService;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Controller
@RequestMapping("/qualityfxgj/core/Gyfk/")
public class GyfkController extends GenericController {
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private GyfkService gyfkService;
    @Autowired
    private GyfkDao gyfkDao;
    @Resource
    private CommonInfoManager commonInfoManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;

    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "qualityfxgj/core/gyfkFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("gyxjId", RequestUtil.getString(request, "gyxjId", ""));
        return mv;
    }

    @RequestMapping(value = "upload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            gyfkService.saveGyfkUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    @RequestMapping("fileList")
    public ModelAndView fileList(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "qualityfxgj/core/gyfkFileList.jsp";
        String gyxjId = RequestUtil.getString(request, "gyxjId");
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("gyxjId", gyxjId);
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("gyfkListPage")
    public ModelAndView msgManagement(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "qualityfxgj/core/gyfkList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "qualityfxgj/core/gyfkEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String gyfkId = RequestUtil.getString(request, "gyfkId");
        String nodeId = RequestUtil.getString(request, "nodeId");
        String action = RequestUtil.getString(request, "action");
        String status = RequestUtil.getString(request, "status");
        if (StringUtils.isBlank(action)) {
            // 新增或编辑的时候没有nodeId
            if (StringUtils.isBlank(nodeId) || nodeId.contains("PROCESS")) {
                action = "edit";
            } else {
                // 处理任务的时候有nodeId
                action = "task";
            }
        }
        mv.addObject("gyfkId", gyfkId).addObject("action", action).addObject("status", status);
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId,"GYXXFK",null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
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

    @RequestMapping("queryGyfk")
    @ResponseBody
    public JsonPageResult<?> queryJstb(HttpServletRequest request, HttpServletResponse response) {
        return gyfkService.queryGyfk(request, true);
    }

    @RequestMapping("deleteGyfk")
    @ResponseBody
    public JsonResult deleteGyfk(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return gyfkService.deleteGyfk(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteGyfk", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("getGyfk")
    @ResponseBody
    public JSONObject getGyfk(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject gyfkObj = new JSONObject();
        String gyfkId = RequestUtil.getString(request, "gyfkId");
        if (StringUtils.isNotBlank(gyfkId)) {
            gyfkObj = gyfkService.getGyfkById(gyfkId);
        }
        return gyfkObj;
    }

    @RequestMapping("getGyfkDetailList")
    @ResponseBody
    public List<JSONObject> getGyfkDetailList(HttpServletRequest request, HttpServletResponse response) {
        return gyfkService.getGyfkDetailList(request);
    }

    @RequestMapping("getGyfkFileList")
    @ResponseBody
    public List<JSONObject> getGyfkFileList(HttpServletRequest request, HttpServletResponse response) {
        return gyfkService.getGyfkDetailFileList(request);
    }

    @RequestMapping("deleteGyfkFile")
    public void deleteGyfkFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String gyfkId = postBodyObj.getString("formId");
        gyfkService.deleteOneGyfkFile(fileId, fileName, gyfkId);
    }

    @RequestMapping("gyfkPdfPreview")
    public ResponseEntity<byte[]> gyfkPdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("gyfkFilePathBase");
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, fileBasePath);
    }

    @RequestMapping("gyfkOfficePreview")
    @ResponseBody
    public void gyfkOfficePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("gyfkFilePathBase");
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("gyfkImagePreview")
    @ResponseBody
    public void gyfkImagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("gyfkFilePathBase");
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("editGyfkDetail")
    public ModelAndView editCn(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "qualityfxgj/core/gyfkDetailEdit.jsp";
        boolean chuli = false;
        boolean zrr = false;
        ModelAndView mv = new ModelAndView(jspPath);
        String action = RequestUtil.getString(request, "action");
        String belongGyfkId = RequestUtil.getString(request, "gyfkId");
        String gyxjId = RequestUtil.getString(request, "gyxjId");
        String isChuangjian = RequestUtil.getString(request, "isChuangjian");
        String isZZRXZ = RequestUtil.getString(request, "isZZRXZ");
        if (StringUtils.isBlank(isChuangjian) && StringUtils.isBlank(isZZRXZ)) {
            chuli = true;
        }
        if (StringUtils.isNotBlank(isZZRXZ) && StringUtils.isBlank(isChuangjian)) {
            zrr = true;
        }
        JSONObject detailObj = gyfkDao.queryGyfkById(belongGyfkId);
        String status = detailObj.getString("status");
        if (StringUtils.isBlank(action)) {
            action = "edit";
        }
        mv.addObject("action", action);
        mv.addObject("status", status);
        mv.addObject("belongGyfkId", belongGyfkId);
        mv.addObject("zrr", zrr);
        mv.addObject("isZZRXZ", isZZRXZ);
        mv.addObject("isChuangjian", isChuangjian);
        mv.addObject("chuli", chuli);
        mv.addObject("gyxjId", gyxjId);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("saveGyfkDetail")
    @ResponseBody
    public JsonResult saveGyfkDetail(HttpServletRequest request, @RequestBody JSONObject formDataJson,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "成功");
        try {
            String belongGyfkId = RequestUtil.getString(request, "belongGyfkId");
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("gyxjId"))) {
                formDataJson.put("belongGyfkId", belongGyfkId);
                gyfkService.createGyfkDetail(formDataJson);
            } else {
                gyfkService.updateGyfkDetail(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save Cnsx");
            result.setSuccess(false);
            result.setMessage("Exception in save Cnsx");
            return result;
        }
        result.setData(formDataJson.getString("gyxjId"));
        return result;
    }

    @RequestMapping("getGyfkDetail")
    @ResponseBody
    public JSONObject getGyfkDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject gyfkObj = new JSONObject();
        String gyxjId = RequestUtil.getString(request, "gyxjId");
        if (StringUtils.isNotBlank(gyxjId)) {
            gyfkObj = gyfkService.getGyfkDetail(gyxjId);
        }
        return gyfkObj;
    }

    @RequestMapping("deleteGyfkDetail")
    @ResponseBody
    public JsonResult deleteGyfkDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String detailId = RequestUtil.getString(request, "gyxjId");
            return gyfkService.deleteOneGyfkDetail(detailId);
        } catch (Exception e) {
            logger.error("Exception in deleteJsmm", e);
            return new JsonResult(false, e.getMessage());
        }
    }

}
