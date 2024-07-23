package com.redxun.environment.core.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.environment.core.service.ClhbService;
import com.redxun.environment.core.service.ClhbZipUtils;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 国家标准制修订管理
 */
@Controller
@RequestMapping("/environment/core/Wj/")
public class ClhbController extends GenericController {
    @Autowired
    private ClhbService clhbService;
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private ClhbZipUtils clhbZipUtils;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private CommonInfoManager commonInfoManager;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;

    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rap/core/clhbFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("wjId", RequestUtil.getString(request, "wjId", ""));
        mv.addObject("fileType", RequestUtil.getString(request, "fileType", ""));
        return mv;
    }

    @RequestMapping(value = "upload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            clhbService.saveWjUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    @RequestMapping("list")
    public ModelAndView management(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "rap/core/clhbList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 角色信息
        String userId = ContextUtil.getCurrentUserId();
        // 返回当前登录人角色信息
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        boolean isCPZG = rdmZhglUtil.judgeIsPointGwUser(userId, "产品主管");
        mv.addObject("isCPZG", isCPZG);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserId", userId);

        return mv;
    }

    // 标准新增和编辑页面
    @RequestMapping("edit")
    public ModelAndView editWj(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "rap/core/clhbEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String wjId = RequestUtil.getString(request, "wjId");
        String nodeId = RequestUtil.getString(request, "nodeId");
        String action = RequestUtil.getString(request, "action");
        String status = RequestUtil.getString(request, "status");
        String type = RequestUtil.getString(request, "type");
        String oldWjId = RequestUtil.getString(request, "oldWjId");
        if ("old".equals(type) && StringUtils.isNotBlank(oldWjId)) {
            mv.addObject("oldWjId", oldWjId);
        }
        mv.addObject("type", type);
        if (StringUtils.isBlank(action)) {
            // 新增或编辑的时候没有nodeId
            if (StringUtils.isBlank(nodeId) || nodeId.contains("PROCESS")) {
                action = "edit";
            } else {
                // 处理任务的时候有nodeId
                action = "task";
            }
        }
        String userId = ContextUtil.getCurrentUserId();
        mv.addObject("wjId", wjId).addObject("action", action).addObject("status", status);
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "HBXXGK", null);
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
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("getWjDetail")
    @ResponseBody
    public JSONObject getWjDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject wjobj = new JSONObject();
        String wjId = RequestUtil.getString(request, "wjId");
        if (StringUtils.isNotBlank(wjId)) {
            wjobj = clhbService.getWjDetail(wjId);
        }
        return wjobj;
    }

    @RequestMapping("getOldWjDetail")
    @ResponseBody
    public JSONObject getOldWjDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject wjobj = new JSONObject();
        String wjId = RequestUtil.getString(request, "wjId");
        if (StringUtils.isNotBlank(wjId)) {
            wjobj = clhbService.getOldWjDetail(wjId);
        }
        return wjobj;
    }

    // 标准列表查询
    @RequestMapping("queryList")
    @ResponseBody
    public JsonPageResult<?> queryWjList(HttpServletRequest request, HttpServletResponse response) {
        return clhbService.queryWj(request, response, true);
    }

    @RequestMapping("saveWj")
    @ResponseBody
    public JsonResult saveWj(HttpServletRequest request, @RequestBody JSONObject formDataJson,
                             HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "成功");
        try {
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("wjId"))) {
                clhbService.insertWj(formDataJson);
                result.setData(formDataJson.getString("wjId"));
            } else {
                clhbService.updateWj(formDataJson);
                result.setData(formDataJson.getString("wjId"));
            }
        } catch (Exception e) {
            logger.error("Exception in save wj");
            result.setSuccess(false);
            result.setMessage("Exception in save wj");
            return result;
        }
        return result;
    }

    @RequestMapping("getFileList")
    @ResponseBody
    public List<JSONObject> getwjFileList(HttpServletRequest request, HttpServletResponse response) {
        String wjId = RequestUtil.getString(request, "wjId");
        String fileType = RequestUtil.getString(request, "fileType");
        return clhbService.getWjFileList(wjId, fileType);
    }

    @RequestMapping("getBoxList")
    @ResponseBody
    public List<JSONObject> getBoxList(HttpServletRequest request, HttpServletResponse response) {
        String textType = RequestUtil.getString(request, "textType");
        return clhbService.getBoxList(textType);
    }

    @RequestMapping("wjPdfPreview")
    public ResponseEntity<byte[]> wjPdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("wjFilePathBase");
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, fileBasePath);
    }

    @RequestMapping("wjOfficePreview")
    @ResponseBody
    public void wjOfficePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("wjFilePathBase");
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("wjImagePreview")
    @ResponseBody
    public void wjImagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("wjFilePathBase");
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("deletewjFile")
    public void deleteWjFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String wjId = postBodyObj.getString("formId");
        clhbService.deleteOneWjFile(fileId, fileName, wjId);
    }

    @RequestMapping("deletewj")
    @ResponseBody
    public JsonResult deleteCx(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String instIdStr = RequestUtil.getString(request, "instIds");
            String[] ids = uIdStr.split(",");
            String[] instIds = instIdStr.split(",");
            return clhbService.deleteWj(ids, instIds);
        } catch (Exception e) {
            logger.error("Exception in deleteSecret", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("importClhbDownload")
    public ResponseEntity<byte[]> importTemplateDownload(HttpServletRequest request, HttpServletResponse response) {
        return clhbService.importClhbDownload();
    }

    @RequestMapping("exportClhbList")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        clhbService.exportClhbList(request, response);
    }

    @RequestMapping("downloadFile")
    @ResponseBody
    public void downloadFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String wjFilePathBase = WebAppUtil.getProperty("wjFilePathBase");
            String downFile = XcmgProjectUtil.getNowUTCDateStr("yyyyMMddHHmmss") + "-clhb.zip";
            String zipFilePath = wjFilePathBase + File.separator + downFile;
            int count =1;
            String uIdStr = RequestUtil.getString(request, "formId");
            FileOutputStream fos1 = new FileOutputStream(new File(zipFilePath));
            clhbZipUtils.toZip(wjFilePathBase,fos1,true,count,uIdStr);

            ServletOutputStream lastOutputStream = response.getOutputStream();
            String zipName = new String(downFile.getBytes("UTF-8"), "ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + zipName);
            FileInputStream inputStream = new FileInputStream(zipFilePath);
            IOUtils.copy(inputStream, lastOutputStream);
            inputStream.close();
            lastOutputStream.close();

            File fileTempZip = new File(zipFilePath);
            fileTempZip.delete();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("statusChange")
    @ResponseBody
    public JsonResult statusChange(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String wjId = RequestUtil.getString(request, "wjId");
            String noteStatus = RequestUtil.getString(request, "noteStatus");
            return clhbService.statusChange(wjId,noteStatus);
        } catch (Exception e) {
            logger.error("Exception in statusChange", e);
            return new JsonResult(false, e.getMessage());
        }
    }
}
