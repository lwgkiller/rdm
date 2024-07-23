package com.redxun.zlgjNPI.core.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import com.redxun.zlgjNPI.core.dao.FsjDao;
import com.redxun.zlgjNPI.core.manager.FsjService;

@Controller
@RequestMapping("/zlgjNPI/core/Fsj/")
public class FsjController extends GenericController {
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private FsjService fsjService;
    @Autowired
    private FsjDao fsjDao;
    @Resource
    private CommonInfoManager commonInfoManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;

    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "zlgjNPI/fsjFileUpload.jsp";
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
            fsjService.saveFsjUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    @RequestMapping("fileList")
    public ModelAndView fileList(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "zlgjNPI/fsjFileList.jsp";
        String detailId = RequestUtil.getString(request, "detailId");
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("detailId", detailId);
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("fsjListPage")
    public ModelAndView msgManagement(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "zlgjNPI/fsjList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "zlgjNPI/fsjEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String fsjId = RequestUtil.getString(request, "fsjId");
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
        mv.addObject("fsjId", fsjId).addObject("action", action).addObject("status", status);
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "XPFSJ", null);
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

    @RequestMapping("queryFsj")
    @ResponseBody
    public JsonPageResult<?> queryJstb(HttpServletRequest request, HttpServletResponse response) {
        return fsjService.queryFsj(request, true);
    }

    @RequestMapping("deleteFsj")
    @ResponseBody
    public JsonResult deleteFsj(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return fsjService.deleteFsj(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteFsj", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("getFsj")
    @ResponseBody
    public JSONObject getFsj(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject fsjObj = new JSONObject();
        String fsjId = RequestUtil.getString(request, "fsjId");
        if (StringUtils.isNotBlank(fsjId)) {
            fsjObj = fsjService.getFsjById(fsjId);
        }
        return fsjObj;
    }

    @RequestMapping("getFsjDetailList")
    @ResponseBody
    public List<JSONObject> getFsjDetailList(HttpServletRequest request, HttpServletResponse response) {
        return fsjService.getFsjDetailList(request);
    }

    @RequestMapping("getFsjFileList")
    @ResponseBody
    public List<JSONObject> getFsjFileList(HttpServletRequest request, HttpServletResponse response) {
        return fsjService.getFsjDetailFileList(request);
    }

    @RequestMapping("deleteFsjFile")
    public void deleteFsjFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String fsjId = postBodyObj.getString("formId");
        fsjService.deleteOneFsjFile(fileId, fileName, fsjId);
    }

    @RequestMapping("fsjPdfPreview")
    public ResponseEntity<byte[]> fsjPdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("fsjFilePathBase");
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, fileBasePath);
    }

    @RequestMapping("fsjOfficePreview")
    @ResponseBody
    public void fsjOfficePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("fsjFilePathBase");
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("fsjImagePreview")
    @ResponseBody
    public void fsjImagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("fsjFilePathBase");
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("editFsjDetail")
    public ModelAndView editCn(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "zlgjNPI/fsjDetailEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String action = RequestUtil.getString(request, "action");
        String belongId = RequestUtil.getString(request, "fsjId");
        String detailId = RequestUtil.getString(request, "detailId");
        JSONObject detailObj = fsjDao.queryFsjById(belongId);
        String status = detailObj.getString("status");
        if (StringUtils.isBlank(action)) {
            action = "edit";
        }
        mv.addObject("action", action);
        mv.addObject("status", status);
        mv.addObject("belongId", belongId);
        mv.addObject("detailId", detailId);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("saveFsjDetail")
    @ResponseBody
    public JsonResult saveFsjDetail(HttpServletRequest request, @RequestBody JSONObject formDataJson,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "成功");
        try {
            String belongId = RequestUtil.getString(request, "belongId");
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("detailId"))) {
                formDataJson.put("belongId", belongId);
                fsjService.createFsjDetail(formDataJson);
            } else {
                fsjService.updateFsjDetail(formDataJson);
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

    @RequestMapping("getFsjDetail")
    @ResponseBody
    public JSONObject getFsjDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject fsjObj = new JSONObject();
        String detailId = RequestUtil.getString(request, "detailId");
        if (StringUtils.isNotBlank(detailId)) {
            fsjObj = fsjService.getFsjDetail(detailId);
        }
        return fsjObj;
    }

    @RequestMapping("deleteFsjDetail")
    @ResponseBody
    public JsonResult deleteFsjDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String detailId = RequestUtil.getString(request, "detailId");
            return fsjService.deleteOneFsjDetail(detailId);
        } catch (Exception e) {
            logger.error("Exception in deleteJsmm", e);
            return new JsonResult(false, e.getMessage());
        }
    }

}
