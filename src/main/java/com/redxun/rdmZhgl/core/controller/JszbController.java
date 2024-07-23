package com.redxun.rdmZhgl.core.controller;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmZhgl.core.dao.JszbDao;
import com.redxun.rdmZhgl.core.service.JszbService;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@RestController
@RequestMapping("/zhgl/core/jszb/")
public class JszbController {
    private Logger logger = LogManager.getLogger(JszbController.class);
    @Resource
    private JszbService jszbService;
    @Autowired
    private JszbDao jszbDao;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Resource
    private BpmInstManager bpmInstManager;
    @Autowired
    private CommonBpmManager commonBpmManager;

    @RequestMapping("listPage")
    public ModelAndView listPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/jszb_commonList.jsp";
        String type = RequestUtil.getString(request, "type", "");
        if ("询比价流程".equalsIgnoreCase(type)) {
            jspPath = "rdmZhgl/core/jszb_xbjList.jsp";
        } else if ("特批流程".equalsIgnoreCase(type)) {
            jspPath = "rdmZhgl/core/jszb_tpList.jsp";
        }
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 是否是招标专员
        String isZbzy = "false";
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        for (int index = 0; index < currentUserRoles.size(); index++) {
            if (currentUserRoles.get(index).get("NAME_").equals("技术招标专员")) {
                isZbzy = "true";
            }
        }
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("isZbzy", isZbzy);
        return mv;
    }

    @RequestMapping("getJszbList")
    @ResponseBody
    public JsonPageResult<?> getJszbList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> params = new HashMap<>();
        return jszbService.getJszbList(request, response, params, true);
    }

    // 导出
    @PostMapping("exportJszbExcel")
    public void exportJszbExcel(HttpServletResponse response, HttpServletRequest request) {
        jszbService.exportJszbExcel(response, request);
    }

    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/jszb_commonEdit.jsp";
        String type = RequestUtil.getString(request, "type", "");
        String jszbId = RequestUtil.getString(request, "jszbId");
        // 查询流程类型
        if (StringUtils.isBlank(type) && StringUtils.isNotBlank(jszbId)) {
            JSONObject jszbObj = jszbService.getJszbDetail(jszbId);
            if (jszbObj != null && StringUtils.isNotBlank(jszbObj.getString("gslclb"))) {
                type = jszbObj.getString("gslclb");
            }
        }
        String solKey = "";
        if ("询比价流程".equalsIgnoreCase(type)) {
            jspPath = "rdmZhgl/core/jszb_xbjEdit.jsp";
            solKey = "JSZB-XBJLC";
        } else if ("特批流程".equalsIgnoreCase(type)) {
            jspPath = "rdmZhgl/core/jszb_tpEdit.jsp";
            solKey = "JSZB-TPLC";
        } else if ("招标流程".equalsIgnoreCase(type)) {
            jspPath = "rdmZhgl/core/jszb_commonEdit.jsp";
            solKey = "JSZB-ZBLC";
        }
        ModelAndView mv = new ModelAndView(jspPath);

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
        mv.addObject("jszbId", jszbId).addObject("action", action).addObject("status", status);
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, solKey, null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        // 返回当前登录人角色信息
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping(value = "save", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveJszb(HttpServletRequest request, @RequestBody String xcmgProjectStr,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(xcmgProjectStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("请求体为空！");
            return result;
        }
        try {
            JSONObject formDataJson = JSONObject.parseObject(xcmgProjectStr);
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("请求体为空！");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("jszbId"))) {
                jszbService.createJszb(formDataJson);
            } else {
                jszbService.updateJszb(formDataJson);
            }

        } catch (Exception e) {
            logger.error("Exception in saveJszb");
            result.setSuccess(false);
            result.setMessage("系统异常！");
            return result;
        }
        return result;
    }

    @RequestMapping("getJszbDetail")
    @ResponseBody
    public JSONObject getJszbDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject jszbObj = new JSONObject();
        String jszbId = RequestUtil.getString(request, "jszbId");
        if (StringUtils.isNotBlank(jszbId)) {
            jszbObj = jszbService.getJszbDetail(jszbId);
        }
        return jszbObj;
    }

    @RequestMapping("deleteJszb")
    @ResponseBody
    public JsonResult deleteJszb(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
            return jszbService.deleteJszb(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteJszb", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("editRuleShow")
    public ModelAndView editRuleShow(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "rdmZhgl/core/jszbckShow.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    @RequestMapping("getCkList")
    @ResponseBody
    public JsonPageResult<?> getCkList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        List<Map<String, Object>> gridData = jszbDao.queryJszbckData(params);
        result.setData(gridData);
        return result;
    }

    @RequestMapping("getJszbFileList")
    @ResponseBody
    public List<JSONObject> getJszbFileList(HttpServletRequest request, HttpServletResponse response) {
        String jszbId = RequestUtil.getString(request, "jszbId", "");
        String ckId = RequestUtil.getString(request, "ckId", "");
        if (StringUtils.isBlank(jszbId) || StringUtils.isBlank(ckId)) {
            return Collections.EMPTY_LIST;
        }
        Map<String, Object> param = new HashMap<>();
        param.put("ckIds", Arrays.asList(ckId));
        param.put("jszbIds", Arrays.asList(jszbId));
        List<JSONObject> fileList = jszbDao.queryJszbFileList(param);
        for (JSONObject oneObj : fileList) {
            if (StringUtils.isNotBlank(oneObj.getString("CREATE_TIME_"))) {
                oneObj.put("CREATE_TIME_", DateFormatUtil.format(oneObj.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        return fileList;
    }

    @RequestMapping("getJszbFileTypeList")
    @ResponseBody
    public List<JSONObject> getJszbFileTypeList(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> param = new HashMap<>();
        String type = RequestUtil.getString(request, "type", "");
        if (StringUtils.isNotBlank(type)) {
            param.put("type", type);
        }

        return jszbService.queryJszbFileTypes(param);
    }

    @RequestMapping("jszbFileWindow")
    public ModelAndView zlgjFileWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/jszbFileList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String jszbId = RequestUtil.getString(request, "jszbId");
        String canOperateFile = RequestUtil.getString(request, "canOperateFile");
        String ckId = RequestUtil.getString(request, "ckId");
        mv.addObject("ckId", ckId);
        mv.addObject("jszbId", jszbId);
        mv.addObject("canOperateFile", canOperateFile);
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));

        return mv;
    }

    @RequestMapping("fileUploadWindow")
    public ModelAndView fileUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/jszbFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("ckId", RequestUtil.getString(request, "ckId", ""));
        mv.addObject("jszbId", RequestUtil.getString(request, "jszbId", ""));
        return mv;
    }

    @RequestMapping(value = "fileUpload")
    @ResponseBody
    public Map<String, Object> fileUpload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件，成功后再写入数据库，前台是一个一个文件的调用
            jszbService.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    // 判断附件必填项
    @RequestMapping("checkJszbFileRequired")
    @ResponseBody
    public Map<String, Object> checkJszbFileRequired(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        Map<String, Object> result = new HashMap<>();
        String jszbId = RequestUtil.getString(request, "jszbId");
        String type = RequestUtil.getString(request, "type");
        String jdType = RequestUtil.getString(request, "jdType");
        if (StringUtils.isBlank(jszbId) || StringUtils.isBlank(type) || StringUtils.isBlank(jdType)) {
            logger.error(" param is blank");
            result.put("result", "false");
            result.put("message", "参数为空！");
            return result;
        }
        // 查询必传文件类型
        Map<String, Object> param = new HashMap<>();
        param.put("type", type);
        param.put("jdType", jdType);
        param.put("required", "yes");
        List<JSONObject> totalList = jszbDao.queryJszbFileTypes(param);
        if (totalList == null || totalList.isEmpty()) {
            result.put("result", "true");
            return result;
        }
        Map<String, String> totalCkId2Name = new HashMap<>();
        for (JSONObject oneData : totalList) {
            totalCkId2Name.put(oneData.getString("ckId"), oneData.getString("lcwjlx"));
        }
        // 查询当前已上传的
        param.clear();
        param.put("jszbIds", Arrays.asList(jszbId));
        param.put("ckIds", new ArrayList<>(totalCkId2Name.keySet()));
        List<JSONObject> currentUploadList = jszbDao.queryJszbFileList(param);
        Set<String> currentUploadFileTypeNames = new HashSet<>();
        for (JSONObject oneData : currentUploadList) {
            currentUploadFileTypeNames.add(oneData.getString("fjlbName"));
        }
        Set<String> totalNames = new HashSet<>(totalCkId2Name.values());
        totalNames.removeAll(currentUploadFileTypeNames);
        if (totalNames.isEmpty()) {
            result.put("result", "true");
        } else {
            result.put("result", "false");
            StringBuilder sb = new StringBuilder();
            for (String oneName : totalNames) {
                sb.append(oneName).append("，");
            }
            result.put("message", "请上传必须交付物【" + sb.substring(0, sb.length() - 1) + "】");
        }
        return result;
    }

    @RequestMapping("preview")
    public ResponseEntity<byte[]> zlgjPdfPreview(HttpServletRequest request, HttpServletResponse response) {
        ResponseEntity<byte[]> result = null;
        String fileType = RequestUtil.getString(request, "fileType");
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("jszbFilePathBase");
        switch (fileType) {
            case "pdf":
                result = rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, fileBasePath);
            case "office":
                rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, fileBasePath, response);
                break;
            case "pic":
                rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, fileBasePath, response);
                break;
        }
        return result;
    }

    @RequestMapping("delJszbFile")
    public void delJszbFile(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "id");
        String jszbId = RequestUtil.getString(request, "formId");
        jszbService.deleteOneJszbFile(fileId, fileName, jszbId);
    }

    // 文档的下载
    @RequestMapping("fileDownload")
    public ResponseEntity<byte[]> fileDownload(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String fileBasePath = WebAppUtil.getProperty("jszbFilePathBase");
        String mainId = RequestUtil.getString(request, "mainId", "");
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, mainId, fileBasePath);
    }
}
