package com.redxun.zlgjNPI.core.controller;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmCommon.core.dao.CommonBpmDao;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.ConstantUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import com.redxun.zlgjNPI.core.dao.NewItemLcpsDao;
import com.redxun.zlgjNPI.core.manager.NewItemLcpsService;

@RestController
@RequestMapping("/zhgl/core/lcps/")
public class NewItemLcpsController {
    private Logger logger = LogManager.getLogger(NewItemLcpsController.class);
    @Resource
    private NewItemLcpsService newItemLcpsService;
    @Autowired
    private NewItemLcpsDao newItemLcpsDao;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Resource
    private BpmInstManager bpmInstManager;
    @Autowired
    private CommonBpmManager commonBpmManager;

    @RequestMapping("listPage")
    public ModelAndView listPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "zlgjNPI/newItemLcpsList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 返回当前登录人角色信息
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        // 返回当前用户是否是技术管理部负责人
        boolean isJsglbRespUser = rdmZhglUtil.judgeUserIsJsglbRespUser(ContextUtil.getCurrentUserId());
        mv.addObject("isJsglbRespUser", isJsglbRespUser);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    @RequestMapping("getXplcList")
    @ResponseBody
    public JsonPageResult<?> getXplcList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return newItemLcpsService.getXplcList(request);
    }

    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "zlgjNPI/newItemLcpsEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String xplcId = RequestUtil.getString(request, "xplcId");
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
        mv.addObject("xplcId", xplcId).addObject("action", action).addObject("status", status);
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            Map<String, Object> params = new HashMap<>();
            params.put("SCOPE_", nodeId);
            params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            params.put("SOL_KEY_", "XPLCQPS");
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "XPLCQPS", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("getXplcDetail")
    @ResponseBody
    public JSONObject getXplcDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject xpsxObj = new JSONObject();
        String xplcId = RequestUtil.getString(request, "xplcId");
        if (StringUtils.isNotBlank(xplcId)) {
            xpsxObj = newItemLcpsService.getXplcDetail(xplcId);
        }
        return xpsxObj;
    }

    @RequestMapping("getXplcFileList")
    @ResponseBody
    public List<JSONObject> getXplcFileList(HttpServletRequest request, HttpServletResponse response) {
        List<String> xplcIdList = Arrays.asList(RequestUtil.getString(request, "xplcId", ""));
        return newItemLcpsService.getXplcFileList(xplcIdList);
    }

    @RequestMapping("fileUploadWindow")
    public ModelAndView fileUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "zlgjNPI/newItemLcpsFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("xplcId", RequestUtil.getString(request, "xplcId", ""));
        return mv;
    }

    @RequestMapping(value = "fileUpload")
    @ResponseBody
    public Map<String, Object> fileUpload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件，成功后再写入数据库，前台是一个一个文件的调用
            newItemLcpsService.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    @RequestMapping("xplcPdfPreview")
    public ResponseEntity<byte[]> xplcPdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("xplcFilePathBase_preview");
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, fileBasePath);
    }

    @RequestMapping("xplcOfficePreview")
    @ResponseBody
    public void xplcOfficePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("xplcFilePathBase_preview");
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("xplcImagePreview")
    @ResponseBody
    public void xplcImagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("xplcFilePathBase_preview");
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("delXplcFile")
    public void delXplcFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String xplcId = postBodyObj.getString("formId");
        newItemLcpsService.deleteOneXplcFile(fileId, fileName, xplcId);
    }

    // 文档的下载
    @RequestMapping("fileDownload")
    public ResponseEntity<byte[]> fileDownload(HttpServletRequest request, HttpServletResponse response) {
        try {
            String fileName = RequestUtil.getString(request, "fileName");
            if (StringUtils.isBlank(fileName)) {
                logger.error("操作失败，文件名为空！");
                return null;
            }
            // 预览还是下载，取的根路径不一样
            String action = RequestUtil.getString(request, "action");
            if (StringUtils.isBlank(action)) {
                logger.error("操作类型为空");
                return null;
            }
            String fileId = RequestUtil.getString(request, "fileId");
            if (StringUtils.isBlank(fileId)) {
                logger.error("操作失败，文件主键为空！");
                return null;
            }
            String standardId = RequestUtil.getString(request, "standardId");
            if (StringUtils.isBlank(standardId)) {
                logger.error("操作失败，主表id为空！");
                return null;
            }
            String fileBasePath = "";
            fileBasePath = ConstantUtil.PREVIEW.equalsIgnoreCase(action) ? WebAppUtil.getProperty("xplcFilePathBase")
                : WebAppUtil.getProperty("xplcFilePathBase_preview");
            if (StringUtils.isBlank(fileBasePath)) {
                logger.error("操作失败，找不到存储根路径");
                return null;
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String relativeFilePath = "";
            if (StringUtils.isNotBlank(standardId)) {
                relativeFilePath = File.separator + standardId;
            }
            String realFileName = fileId + "." + suffix;
            String fullFilePath = fileBasePath + relativeFilePath + File.separator + realFileName;
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

    // 新品量产评审-信息
    @GetMapping("editXplcxx")
    public ModelAndView editXplcxx(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "zlgjNPI/newItemLcpsinfoEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String action = RequestUtil.getString(request, "action");
        String isFqBianZhi = RequestUtil.getString(request, "isFqBianZhi");
        String isShBianZhi = RequestUtil.getString(request, "isShBianZhi");
        String isHqBianZhi = RequestUtil.getString(request, "isHqBianZhi");
        String id = RequestUtil.getString(request, "id");
        JSONObject xplcxxObj = new JSONObject();
        if (StringUtils.isNotBlank(id)) {
            Map<String, Object> xplcxxInfo = newItemLcpsDao.getXplcxx(id);
            xplcxxObj = XcmgProjectUtil.convertMap2JsonObject(xplcxxInfo);
        }
        mv.addObject("xplcxxObj", xplcxxObj);
        mv.addObject("action", action);
        mv.addObject("isFqBianZhi", isFqBianZhi);
        mv.addObject("isShBianZhi", isShBianZhi);
        mv.addObject("isHqBianZhi", isHqBianZhi);
        mv.addObject("xplcId", RequestUtil.getString(request, "xplcId"));
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));

        return mv;
    }

    @RequestMapping("saveXplcxx")
    @ResponseBody
    public JsonResult saveXplcxx(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        newItemLcpsService.saveXplcxx(result, request);
        return new JsonResult(true, "操作成功");
    }

    @RequestMapping("getXplcxxList")
    @ResponseBody
    public List<JSONObject> getXplcxxList(HttpServletRequest request, HttpServletResponse response) {
        String xplcId = RequestUtil.getString(request, "xplcId");
        return newItemLcpsService.getXplcxxList(xplcId);
    }

    @RequestMapping("deleteXplcxx")
    @ResponseBody
    public JsonResult deleteXplcxx(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String id = RequestUtil.getString(request, "id");
            Map<String, Object> param = new HashMap<>();
            param.put("id", id);
            newItemLcpsDao.delXplcxxListById(param);
        } catch (Exception e) {
            logger.error("Exception in delXplcxxListById", e);
            return new JsonResult(false, e.getMessage());
        }
        return new JsonResult(true, "操作成功");
    }

    @RequestMapping("deleteXplc")
    @ResponseBody
    public JsonResult deleteXplc(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
            return newItemLcpsService.deleteXplc(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteXpsx", e);
            return new JsonResult(false, e.getMessage());
        }
    }
}
