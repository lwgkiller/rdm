package com.redxun.environment.core.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.redxun.environment.core.service.YjbgService;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Controller
@RequestMapping("/evn/core/Yjbg/")
public class YjbgController extends GenericController {
    @Autowired
    private YjbgService yjbgService;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private CommonBpmManager commonBpmManager;

    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rap/core/yjbgFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("id", RequestUtil.getString(request, "id", ""));
        mv.addObject("fileType", RequestUtil.getString(request, "fileType", ""));
        return mv;
    }

    @RequestMapping("yjbgOverview")
    public ModelAndView yjbgOverview(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rap/core/yjbgOverview.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    @RequestMapping("YjbgListPage")
    public ModelAndView msgManagement(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "rap/core/yjbgList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    @RequestMapping("editGysPage")
    public ModelAndView editGysPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rap/core/yjbgGysEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String id = RequestUtil.getString(request, "id");
        String nodeId = RequestUtil.getString(request, "nodeId");
        String action = RequestUtil.getString(request, "action");
        String changeType = RequestUtil.getString(request, "changeType");
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
        mv.addObject("id", id).addObject("action", action).addObject("status", status).addObject("changeType",
            changeType);
        // 取出节点变量，返回到页面

        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "GYSZG", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("editZjcPage")
    public ModelAndView editZjcPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rap/core/yjbgZjcEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String id = RequestUtil.getString(request, "id");
        String nodeId = RequestUtil.getString(request, "nodeId");
        String action = RequestUtil.getString(request, "action");
        String changeType = RequestUtil.getString(request, "changeType");
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
        mv.addObject("id", id).addObject("action", action).addObject("status", status).addObject("changeType",
            changeType);
        // 取出节点变量，返回到页面

        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "ZJCZG", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("queryYjbg")
    @ResponseBody
    public JsonPageResult<?> queryJstb(HttpServletRequest request, HttpServletResponse response) {
        return yjbgService.queryYjbg(request, true);
    }

    @RequestMapping("deleteYjbg")
    @ResponseBody
    public JsonResult deleteJstb(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String id = RequestUtil.getString(request, "id");
            return yjbgService.deleteYjbg(id);
        } catch (Exception e) {
            logger.error("Exception in deleteWj", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("saveYjbg")
    @ResponseBody
    public JsonResult saveJstb(HttpServletRequest request, @RequestBody String xcmgProjectStr,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(xcmgProjectStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        try {
            JSONObject formDataJson = JSONObject.parseObject(xcmgProjectStr);
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("id"))) {
                yjbgService.createYjbg(formDataJson);
            } else {
                yjbgService.updateYjbg(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save yjbg");
            result.setSuccess(false);
            result.setMessage("Exception in save yjbg");
            return result;
        }
        return result;
    }

    @RequestMapping("getYjbgDetail")
    @ResponseBody
    public JSONObject getYjbgDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject yjbgObj = new JSONObject();
        String id = RequestUtil.getString(request, "id");
        if (StringUtils.isNotBlank(id)) {
            yjbgObj = yjbgService.getYjbgDetail(id);
        }
        return yjbgObj;
    }

    @RequestMapping(value = "upload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            yjbgService.saveYjbgUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    @RequestMapping("getYjbgFileList")
    @ResponseBody
    public List<JSONObject> getYjbgFileList(HttpServletRequest request, HttpServletResponse response) {
        String id = RequestUtil.getString(request, "id");
        String fileType = RequestUtil.getString(request, "fileType");
        return yjbgService.getYjbgFileList(id, fileType);
    }

    @RequestMapping("deleteYjbgFile")
    public void deleteYjbgFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String id = postBodyObj.getString("formId");
        yjbgService.deleteOneYjbgFile(fileId, fileName, id);
    }

    @RequestMapping("yjbgPdfPreview")
    public ResponseEntity<byte[]> yjbgPdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("yjbgFilePathBase");
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, fileBasePath);
    }

    @RequestMapping("yjbgOfficePreview")
    @ResponseBody
    public void yjbgOfficePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("yjbgFilePathBase");
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("yjbgImagePreview")
    @ResponseBody
    public void yjbgImagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("yjbgFilePathBase");
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

}
