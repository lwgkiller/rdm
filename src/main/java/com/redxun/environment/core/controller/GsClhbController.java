package com.redxun.environment.core.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.environment.core.service.GsClhbService;
import com.redxun.environment.core.service.GsClhbZipUtils;
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
@RequestMapping("/environment/core/Cx/")
public class GsClhbController extends GenericController {
    @Autowired
    private GsClhbService gsClhbService;
    @Autowired
    private CommonBpmManager commonBpmManager;
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
    @Autowired
    private GsClhbZipUtils gsClhbZipUtils;

    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rap/core/gsclhbFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("cxId", RequestUtil.getString(request, "cxId", ""));
        mv.addObject("upNode", RequestUtil.getString(request, "upNode", ""));
        return mv;
    }

    @RequestMapping(value = "upload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            gsClhbService.saveCxUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    @RequestMapping("list")
    public ModelAndView management(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "rap/core/gsclhbList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String deptName = RequestUtil.getString(request, "deptName");
        String checkDelay = RequestUtil.getString(request, "checkDelay");
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 角色信息
        String userId = ContextUtil.getCurrentUserId();
        boolean isCPZG = rdmZhglUtil.judgeIsPointGwUser(userId, "产品主管");
        // 返回当前登录人角色信息
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        mv.addObject("isCPZG", isCPZG);
        mv.addObject("deptName", deptName);
        mv.addObject("checkDelay", checkDelay);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserId", userId);

        return mv;
    }

    // 标准新增和编辑页面
    @RequestMapping("edit")
    public ModelAndView editCx(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "rap/core/gsclhbEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String cxId = RequestUtil.getString(request, "cxId");
        String nodeId = RequestUtil.getString(request, "nodeId");
        String action = RequestUtil.getString(request, "action");
        String status = RequestUtil.getString(request, "status");
        String type = RequestUtil.getString(request, "type");
        String oldCxId = RequestUtil.getString(request, "oldCxId");
        if("old".equals(type)&&StringUtils.isNotBlank(oldCxId)){
            mv.addObject("oldCxId", oldCxId);
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
        mv.addObject("cxId", cxId).addObject("action", action).addObject("status", status);
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId,"GSCLHBXXGK",null);
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

    @RequestMapping("getCxDetail")
    @ResponseBody
    public JSONObject getCxDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject cxobj = new JSONObject();
        String cxId = RequestUtil.getString(request, "cxId");
        if (StringUtils.isNotBlank(cxId)) {
            cxobj = gsClhbService.getCxDetail(cxId);
        }
        return cxobj;
    }

    @RequestMapping("getOldCxDetail")
    @ResponseBody
    public JSONObject getOldCxDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject wjobj = new JSONObject();
        String cxId = RequestUtil.getString(request, "cxId");
        if (StringUtils.isNotBlank(cxId)) {
            wjobj = gsClhbService.getOldCxDetail(cxId);
        }
        return wjobj;
    }

    // 标准列表查询
    @RequestMapping("queryList")
    @ResponseBody
    public JsonPageResult<?> queryCxList(HttpServletRequest request, HttpServletResponse response) {
        return gsClhbService.queryCx(request, response, true);
    }

    @RequestMapping("saveCx")
    @ResponseBody
    public JsonResult saveCx(HttpServletRequest request, @RequestBody JSONObject formDataJson,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "成功");
        try {
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("cxId"))) {
                gsClhbService.insertCx(formDataJson);
                result.setData(formDataJson.getString("cxId"));
            } else {
                gsClhbService.updateCx(formDataJson);
                result.setData(formDataJson.getString("cxId"));
            }
        } catch (Exception e) {
            logger.error("Exception in save cx");
            result.setSuccess(false);
            result.setMessage("Exception in save cx");
            return result;
        }
        return result;
    }

    @RequestMapping("getFileList")
    @ResponseBody
    public List<JSONObject> getcxFileList(HttpServletRequest request, HttpServletResponse response) {
        String cxId = RequestUtil.getString(request, "cxId");
        String upNode = RequestUtil.getString(request, "upNode");
        return gsClhbService.getCxFileList(cxId, upNode);
    }

    @RequestMapping("cxPdfPreview")
    public ResponseEntity<byte[]> cxPdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("gsclhbFilePathBase");
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, fileBasePath);
    }

    @RequestMapping("cxOfficePreview")
    @ResponseBody
    public void cxOfficePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("gsclhbFilePathBase");
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("cxImagePreview")
    @ResponseBody
    public void cxImagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("gsclhbFilePathBase");
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("deletecxFile")
    public void deleteCxFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String cxId = postBodyObj.getString("formId");
        gsClhbService.deleteOneCxFile(fileId, fileName, cxId);
    }

    @RequestMapping("deletecx")
    @ResponseBody
    public JsonResult deleteCx(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String instIdStr = RequestUtil.getString(request, "instIds");
            String[] ids = uIdStr.split(",");
            String[] instIds = instIdStr.split(",");
            return gsClhbService.deleteCx(ids, instIds);
        } catch (Exception e) {
            logger.error("Exception in deleteSecret", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("downloadPic")
    public ResponseEntity<byte[]> downloadPic(HttpServletRequest request, HttpServletResponse response) {
        return gsClhbService.downloadPic();
    }

    @RequestMapping("importGsClhbDownload")
    public ResponseEntity<byte[]> importTemplateDownload(HttpServletRequest request, HttpServletResponse response) {
        return gsClhbService.importGsClhbDownload();
    }

    @RequestMapping("importCzDownload")
    public ResponseEntity<byte[]> importCzDownload(HttpServletRequest request, HttpServletResponse response) {
        return gsClhbService.importCzDownload();
    }

    @RequestMapping("importWjDownload")
    public ResponseEntity<byte[]> importWjDownload(HttpServletRequest request, HttpServletResponse response) {
        return gsClhbService.importWjDownload();
    }

    @RequestMapping("importPfDownload")
    public ResponseEntity<byte[]> importPfDownload(HttpServletRequest request, HttpServletResponse response) {
        return gsClhbService.importPfDownload();
    }

    @RequestMapping("importSxDownload")
    public ResponseEntity<byte[]> importSxDownload(HttpServletRequest request, HttpServletResponse response) {
        return gsClhbService.importSxDownload();
    }

    @RequestMapping("importDjxxDownload")
    public ResponseEntity<byte[]> importDjxxDownload(HttpServletRequest request, HttpServletResponse response) {
        return gsClhbService.importDjxxDownload();
    }

    @RequestMapping("exportGsClhbList")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        gsClhbService.exportGsClhbList(request, response);
    }

    @RequestMapping("statusChange")
    @ResponseBody
    public JsonResult statusChange(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String cxId = RequestUtil.getString(request, "cxId");
            String noteStatus = RequestUtil.getString(request, "noteStatus");
            return gsClhbService.statusChange(cxId,noteStatus);
        } catch (Exception e) {
            logger.error("Exception in statusChange", e);
            return new JsonResult(false, e.getMessage());
        }
    }
    @RequestMapping("downloadFile")
    @ResponseBody
    public void downloadFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String gsclhbFilePathBase = WebAppUtil.getProperty("gsclhbFilePathBase");
            String downFile = XcmgProjectUtil.getNowUTCDateStr("yyyyMMddHHmmss") + "-gsclhb.zip";
            String zipFilePath = gsclhbFilePathBase + File.separator + downFile;
            int count =1;
            String uIdStr = RequestUtil.getString(request, "formId");
            FileOutputStream fos1 = new FileOutputStream(new File(zipFilePath));
            gsClhbZipUtils.toZip(gsclhbFilePathBase,fos1,true,count,uIdStr);

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
}
