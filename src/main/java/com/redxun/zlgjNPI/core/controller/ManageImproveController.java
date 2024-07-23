package com.redxun.zlgjNPI.core.controller;

import java.io.File;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.bpm.core.entity.BpmInst;
import com.redxun.sys.core.manager.SysDicManager;
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

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import com.redxun.zlgjNPI.core.manager.ManageImproveService;

@Controller
@RequestMapping("/zlgjNPI/core/manageImprove/")
public class ManageImproveController {
    private static final Logger logger = LoggerFactory.getLogger(ManageImproveController.class);
    @Autowired
    private ManageImproveService manageImproveService;
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private BpmInstManager bpmInstManager;

    @RequestMapping("manageImproveListPage")
    public ModelAndView manageImproveListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "zlgjNPI/manageImproveList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    @RequestMapping("manageImproveEditPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String jspPath = "zlgjNPI/manageImproveEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String manageImproveId = RequestUtil.getString(request, "manageImproveId");
        String nodeId = RequestUtil.getString(request, "nodeId");
        String action = RequestUtil.getString(request, "action");
//        String status = RequestUtil.getString(request, "status");
        if (StringUtils.isBlank(action)) {
            // 新增或编辑的时候没有nodeId
            if (StringUtils.isBlank(nodeId) || nodeId.contains("PROCESS")) {
                action = "edit";
            } else {
                // 处理任务的时候有nodeId
                action = "task";
            }
        }
        mv.addObject("manageImproveId", manageImproveId).addObject("action", action);
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "GLGJJYTB", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("mainGroupId", ContextUtil.getCurrentUser().getMainGroupId());
        mv.addObject("mainGroupName", ContextUtil.getCurrentUser().getMainGroupName());
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        return mv;
    }
    @RequestMapping("manageImproveListQuery")
    @ResponseBody
    public JsonPageResult<?> manageImproveListQuery(HttpServletRequest request, HttpServletResponse response) {
        return manageImproveService.manageImproveListQuery(request, response, true);
    }

    @RequestMapping("getManageImproveDetail")
    @ResponseBody
    public JSONObject getManageImproveDetail(HttpServletRequest request, HttpServletResponse response) {
        String manageImproveId = RequestUtil.getString(request, "manageImproveId");
        return manageImproveService.getManageImproveDetail(manageImproveId);
    }

    @RequestMapping("querySubTypeByCoreType")
    @ResponseBody
    public List<JSONObject> querySubTypeByCoreType(HttpServletRequest request, HttpServletResponse response) {
        String coreTypeName = RequestUtil.getString(request, "coreTypeId");
        return manageImproveService.querySubTypeByCoreType(coreTypeName);
    }
    @RequestMapping("saveManageImproveForm")
    @ResponseBody
    public JsonResult saveManageImproveForm(HttpServletRequest request, @RequestBody String formData,
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
                manageImproveService.createManageImprove(formDataJson);
            } else {
                manageImproveService.updateManageImprove(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save jxbzzbsh");
            result.setSuccess(false);
            result.setMessage("Exception in save jxbzzbsh");
            return result;
        }
        return result;
    }
    @RequestMapping("openManageImproveUploadWindow")
    public ModelAndView openManageImproveUploadWindow(HttpServletRequest request, HttpServletResponse response)
            throws Exception{
        String jspPath = "zlgjNPI/ManageImproveFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("belongId", RequestUtil.getString(request, "belongId"));
        mv.addObject("filetype", RequestUtil.getString(request, "filetype"));
        return mv;
    }

    @RequestMapping("queryManageImproveFileList")
    @ResponseBody
    public List<JSONObject> queryManageImproveFileList(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String belongId = RequestUtil.getString(request, "belongId");
        if (StringUtils.isBlank(belongId)) {
            logger.error("belongId is blank");
            return null;
        }
        JSONObject params = new JSONObject();
        List<String> belongIds = new ArrayList<>();
        belongIds.add(belongId);
        params.put("belongIds", belongIds);
        params.put("filetype", RequestUtil.getString(request, "filetype"));
        List<JSONObject> fileInfos = manageImproveService.queryManageImproveFileList(params);
        return fileInfos;
    }

    @RequestMapping(value = "manageImproveUpload")
    @ResponseBody
    public Map<String, Object> manageImproveUpload(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            manageImproveService.manageImproveUpload(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    @RequestMapping("deleteManageImprove")
    @ResponseBody
    public JsonResult deleteManageImprove(HttpServletRequest request, HttpServletResponse response) throws Exception{
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
            return manageImproveService.deleteManageImprove(ids);
        } catch (Exception e) {
            logger.error("Exception in delete", e);
            return new JsonResult(false, e.getMessage());
        }

    }

    // 删除某个文件（从文件表和磁盘上都删除）
    @RequestMapping("delManageImproveFileById")
    public void delManageImproveFileById(HttpServletRequest request, HttpServletResponse response,
                                    @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String id = postBodyObj.getString("id");
        String manageImproveId = postBodyObj.getString("formId");
        manageImproveService.delManageImproveFileById(id, fileName, manageImproveId);
    }
    // 文档的下载（预览pdf也调用这里）
    @RequestMapping("manageImproveDownload")
    public ResponseEntity<byte[]> zlgjjysbDownload(HttpServletRequest request, HttpServletResponse response) {
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
            String manageImproveId = RequestUtil.getString(request, "formId");
            if (StringUtils.isBlank(manageImproveId)) {
                logger.error("操作失败，主表Id为空！");
                return null;
            }
            String fileBasePath = WebAppUtil.getProperty("manageImprovePathBase");
            if (StringUtils.isBlank(fileBasePath)) {
                logger.error("操作失败，找不到存储路径");
                return null;
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fullFilePath = fileBasePath + File.separator + manageImproveId + File.separator + fileId + "." + suffix;
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

    @RequestMapping("manageImproveOfficePreview")
    @ResponseBody
    public void manageImproveOfficePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("manageImprovePathBase");
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("manageImproveImagePreview")
    @ResponseBody
    public void manageImproveImagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("manageImprovePathBase");
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, fileBasePath, response);
    }
}
