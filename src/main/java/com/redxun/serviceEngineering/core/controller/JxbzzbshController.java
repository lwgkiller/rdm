package com.redxun.serviceEngineering.core.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.serviceEngineering.core.service.JxbzzbshService;
import com.redxun.serviceEngineering.core.service.JxcshcService;
import com.redxun.serviceEngineering.core.service.StandardvalueShipmentnotmadeService;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.sys.org.manager.GroupServiceImpl;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/serviceEngineering/core/jxbzzbsh")
public class JxbzzbshController {
    private static final Logger logger = LoggerFactory.getLogger(JxbzzbshController.class);
    @Autowired
    private JxbzzbshService jxbzzbshService;

    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;

    @Autowired
    private BpmInstManager bpmInstManager;

    @Autowired
    private JxcshcService jxcshcService;

    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;

    @Autowired
    private StandardvalueShipmentnotmadeService standardvalueShipmentnotmadeService;
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private GroupServiceImpl groupServiceImpl;

    // ..
    @RequestMapping("jxbzzbshListPage")
    public ModelAndView dataListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/jxbzzbshList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        boolean isJXZY = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), "检修标准值模板专员");
        mv.addObject("isJXZY", isJXZY);
        // 返回当前登录人角色信息
        Map<String, Object> params = new HashMap<>();
        params.put("userId",ContextUtil.getCurrentUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        JSONArray userRoleKeys = new JSONArray();
        for (int index = 0; index < userRolesJsonArray.size(); index++) {
            JSONObject oneRole = (JSONObject) userRolesJsonArray.get(index);
            userRoleKeys.add(oneRole.getString("KEY_"));
        }
        mv.addObject("currentUserRoles", userRoleKeys);
        return mv;
    }

    // ..
    @RequestMapping("jxbzzbshListQuery")
    @ResponseBody
    public JsonPageResult<?> jxbzzbshListQuery(HttpServletRequest request, HttpServletResponse response) {
        return jxbzzbshService.jxbzzbshListQuery(request, response, true);
    }

    // ..
    @RequestMapping("deleteJxbzzbsh")
    @ResponseBody
    public JsonResult deleteJxbzzbsh(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String instIdStr = RequestUtil.getString(request, "instIds");
            if (StringUtils.isNotBlank(instIdStr)) {
                String[] instIds = instIdStr.split(",");
                for (int index = 0; index < instIds.length; index++) {
                    bpmInstManager.deleteCascade(instIds[index], "");
                }
            }
            String[] ids = uIdStr.split(",");
            return jxbzzbshService.deleteJxbzzbsh(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteData", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("jxbzzbshEditPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/jxbzzbshEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String jxbzzbshId = RequestUtil.getString(request, "jxbzzbshId");
        String change = RequestUtil.getString(request, "change");
        String nodeId = RequestUtil.getString(request, "nodeId");
        String action = RequestUtil.getString(request, "action");
        String taskStatus = RequestUtil.getString(request, "taskStatus");
        String oldId = RequestUtil.getString(request, "oldId");
        if ("yes".equals(change) && StringUtils.isNotBlank(oldId)) {
            mv.addObject("oldId", oldId);
        }
        if (StringUtils.isBlank(action)) {
            // 新增或编辑的时候没有nodeId
            if (StringUtils.isBlank(nodeId) || nodeId.contains("PROCESS")) {
                action = "edit";
            } else {
                // 处理任务的时候有nodeId
                action = "task";
            }
        }
        mv.addObject("jxbzzbshId", jxbzzbshId).addObject("action", action)
                .addObject("taskStatus", taskStatus).addObject("change", change);
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "JXBZZBSH", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        String mainGroupId = ContextUtil.getCurrentUser().getMainGroupId();
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        String wdzy = "false";
        // 返回当前登录人角色信息
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        if (currentUserRoles.size() > 0) {
            for (Map<String, Object> map : currentUserRoles) {
                String name = (String) map.get("NAME_");
                if (name.equals("检修标准值表审核技术文档专员")) {
                    wdzy = "true";
                }
            }
        }
        mv.addObject("wdzy", wdzy);
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("mainGroupId", mainGroupId);
        JSONObject matObj = new JSONObject();
        String notMadeId = RequestUtil.getString(request, "notMadeId");
        if (StringUtils.isNotBlank(notMadeId) && StringUtils.isBlank(jxbzzbshId)) {
            JSONObject matObjTmp = standardvalueShipmentnotmadeService.getStandardvalueShipmentnotmadeDetail(notMadeId);
            if (matObjTmp != null) {
                matObj = matObjTmp;
            }
        }
        mv.addObject("matObj", matObj);
        //是否服务所的人
        boolean isFWGCS = false;
        if (groupServiceImpl.getMainByUserId(ContextUtil.getCurrentUserId()).getIdentityName().equalsIgnoreCase(RdmConst.FWGCS_NAME)) {
            isFWGCS = true;
        }
        mv.addObject("isFWGCS", isFWGCS);
        return mv;
    }

    @RequestMapping("getJxbzzbshDetail")
    @ResponseBody
    public JSONObject getJxbzzbshDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject obj = new JSONObject();
        String jxbzzbshId = RequestUtil.getString(request, "jxbzzbshId");
        if (StringUtils.isNotBlank(jxbzzbshId)) {
            obj = jxbzzbshService.getJxbzzbshDetail(jxbzzbshId);
        }
        return obj;
    }

    @RequestMapping("getOldJxbzzbshDetail")
    @ResponseBody
    public JSONObject getOldJxbzzbshDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject obj = new JSONObject();
        String jxbzzbshId = RequestUtil.getString(request, "jxbzzbshId");
        if (StringUtils.isNotBlank(jxbzzbshId)) {
            obj = jxbzzbshService.getOldJxbzzbshDetail(jxbzzbshId);
        }
        return obj;
    }

    @RequestMapping("saveJxbzzbsh")
    @ResponseBody
    public JsonResult saveJxbzzbsh(HttpServletRequest request, @RequestBody String formData,
                                   HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(formData)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        try {
            JSONObject formDataJson = JSONObject.parseObject(formData);
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("id"))) {
                jxbzzbshService.createJxbzzbsh(formDataJson);
            } else {
                jxbzzbshService.updateJxbzzbsh(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save jxbzzbsh");
            result.setSuccess(false);
            result.setMessage("Exception in save jxbzzbsh");
            return result;
        }
        return result;
    }

    @RequestMapping("openJxbzzbshUploadWindow")
    public ModelAndView fileUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/jxcshcFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("masterId", RequestUtil.getString(request, "masterId"));
        mv.addObject("productType", RequestUtil.getString(request, "productType"));
        mv.addObject("versionType", RequestUtil.getString(request, "versionType"));
        mv.addObject("applicationNumber", RequestUtil.getString(request, "applicationNumber"));
        mv.addObject("salesModel", RequestUtil.getString(request, "salesModel"));
        mv.addObject("step", RequestUtil.getString(request, "step"));
        mv.addObject("jxbzzsh", "yes");
        return mv;
    }

    @RequestMapping(value = "jxcshcUpload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            jxcshcService.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    @RequestMapping("queryTestFileList")
    @ResponseBody
    public List<JSONObject> queryTestFileList(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String materialCode = RequestUtil.getString(request, "materialCode");
        if (StringUtils.isBlank(materialCode)) {
            logger.error("materialCode is blank");
            return null;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("materialCode", materialCode);
        List<JSONObject> fileInfos = jxbzzbshService.queryFileListByMaterialCode(params);
        return fileInfos;
    }

    @RequestMapping("queryJxbzzbshFileList")
    @ResponseBody
    public List<JSONObject> queryJxbzzbshFileList(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String jxbzzbshId = RequestUtil.getString(request, "jxbzzbshId");
        if (StringUtils.isBlank(jxbzzbshId)) {
            logger.error("jxbzzbshId is blank");
            return null;
        }
        List<JSONObject> fileInfos = jxbzzbshService.queryFileList(jxbzzbshId);
        return fileInfos;
    }

    // 删除某个文件（从文件表和磁盘上都删除）
    @RequestMapping("delJxbzzshFile")
    public void delJxbzzshFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String id = postBodyObj.getString("id");
        String masterId = postBodyObj.getString("formId");
        jxcshcService.delUploadFile(id, fileName, masterId);
    }

    // 文档的下载（预览pdf也调用这里）
    @RequestMapping("jxbzzbshDownload")
    public ResponseEntity<byte[]> fileDownload(HttpServletRequest request, HttpServletResponse response) {
        try {
            String fileName = RequestUtil.getString(request, "fileName");
            if (StringUtils.isBlank(fileName)) {
                logger.error("操作失败，文件名为空！");
                return null;
            }
            String fileId = RequestUtil.getString(request, "fileId");
            if (StringUtils.isBlank(fileId)) {
                logger.error("操作失败，文件Id为空！");
                return null;
            }
            String masterId = RequestUtil.getString(request, "formId");
            if (StringUtils.isBlank(masterId)) {
                logger.error("操作失败，主表Id为空！");
                return null;
            }
            String fileBasePath = WebAppUtil.getProperty("jxbzzbFilePathBase");
            if (StringUtils.isBlank(fileBasePath)) {
                logger.error("操作失败，找不到存储路径");
                return null;
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fullFilePath = fileBasePath + File.separator + masterId + File.separator + fileId + "." + suffix;
            // 创建文件实例
            File file = new File(fullFilePath);
            // 修改文件名的编码格式
            String downloadFileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");

            // 设置httpHeaders,使浏览器响应下载
            HttpHeaders headers = new HttpHeaders();
            // 告诉浏览器执行下载的操作，“attachment”告诉了浏览器进行下载,下载的文件 文件名为 downloadFileName
            headers.setContentDispositionFormData("attachment", downloadFileName);
            // 设置响应方式为二进制，以二进制流传输
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception in fileDownload", e);
            return null;
        }
    }

    @RequestMapping("jxbzzbshOfficePreview")
    @ResponseBody
    public void officePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("jxbzzbFilePathBase");
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("jxbzzbshImagePreview")
    @ResponseBody
    public void imagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("jxbzzbFilePathBase");
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    /**
     * 下载模板
     */
    @RequestMapping("/downloadJxbzzbshTemplate")
    public ResponseEntity<byte[]> downloadTemplate(HttpServletRequest request, HttpServletResponse response) {
        String productType = RequestUtil.getString(request, "productType");
        String versionType = RequestUtil.getString(request, "versionType");
        return jxbzzbshService.downloadJxbzzbshTemplate(productType, versionType);
    }

    @RequestMapping("openDialog")
    public ModelAndView openDialog(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/jxbzzbshxzwlbm.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    @RequestMapping("checkShipmentnotmadeIdUnique")
    @ResponseBody
    public JsonResult checkShipmentnotmadeIdUnique(HttpServletRequest request, @RequestBody String formData,
                                                   HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        JSONObject formDataJson = JSONObject.parseObject(formData);
        boolean unique = jxbzzbshService.checkShipmentnotmadeIdUnique(formDataJson);
        if (!unique) {
            result.setSuccess(false);
        }
        return result;
    }

    @RequestMapping("discardJxbzzbshInst")
    @ResponseBody
    public JsonResult discardJxbzzbshInst(HttpServletRequest request, @RequestBody String formData,
                                          HttpServletResponse response) {
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String shipmentnotmadeId = formDataJson.getString("shipmentnotmadeId");
        return jxbzzbshService.discardJxbzzbshInst(shipmentnotmadeId);
    }

    @RequestMapping("saveOldData")
    @ResponseBody
    public JsonResult saveOldData(HttpServletRequest request, @RequestBody String formData,
                                  HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(formData)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        try {
            JSONObject formDataJson = JSONObject.parseObject(formData);
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("applicationNumber"))) {
                jxbzzbshService.setApplicationNumber(formDataJson);
            }
            jxbzzbshService.updateJxbzzbsh(formDataJson);
        } catch (Exception e) {
            logger.error("Exception in save jxbzzbsh");
            result.setSuccess(false);
            result.setMessage("Exception in save jxbzzbsh");
            return result;
        }
        return result;
    }

    @RequestMapping("fileList")
    public ModelAndView fileList(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "serviceEngineering/core/jxbzFileList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        Map<String, Object> params = new HashMap<>();
        boolean isJSGLBUser = false;
        params.put("currentUserId", ContextUtil.getCurrentUserId());
//        List<JSONObject> isJSGLB = jstbDao.isJSGLB(params);
//        for (JSONObject jstb : isJSGLB) {
//            if (jstb.getString("deptname").equals(RdmConst.JSGLB_NAME)) {
//                isJSGLBUser = true;
//                break;
//            }
//        }
        mv.addObject("isJSGLBUser", isJSGLBUser);
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("exportJxbzzbsh")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        jxbzzbshService.exportJxbzzbsh(request, response);
    }
}
