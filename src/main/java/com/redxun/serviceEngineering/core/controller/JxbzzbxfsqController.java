package com.redxun.serviceEngineering.core.controller;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.serviceEngineering.core.service.JxbzzbxfsqService;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Controller
@RequestMapping("/serviceEngineering/core/jxbzzbxfsq")
public class JxbzzbxfsqController {
    private static final Logger logger = LoggerFactory.getLogger(JxbzzbxfsqController.class);
    @Autowired
    private JxbzzbxfsqService jxbzzbxfsqService;

    @Autowired
    private CommonBpmManager commonBpmManager;

    @Autowired
    private BpmInstManager bpmInstManager;

    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;

    // ..
    @RequestMapping("jxbzzbxfsqListPage")
    public ModelAndView jxbzzbxfsqListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/jxbzzbxfsqList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    // ..
    @RequestMapping("jxbzzbxfsqListQuery")
    @ResponseBody
    public JsonPageResult<?> jxbzzbxfsqListQuery(HttpServletRequest request, HttpServletResponse response) {
        return jxbzzbxfsqService.jxbzzbxfsqListQuery(request, response, true);
    }

    // ..
    @RequestMapping("deleteJxbzzbxfsq")
    @ResponseBody
    public JsonResult deleteJxbzzbxfsq(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
            return jxbzzbxfsqService.deleteJxbzzbxfsq(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteData", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("jxbzzbxfsqEditPage")
    public ModelAndView jxbzzbxfsqEditPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/jxbzzbxfsqEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String jxbzzbxfsqId = RequestUtil.getString(request, "jxbzzbxfsqId");
        String nodeId = RequestUtil.getString(request, "nodeId");
        String action = RequestUtil.getString(request, "action");
        if (StringUtils.isBlank(action)) {
            // 新增或编辑的时候没有nodeId
            if (StringUtils.isBlank(nodeId) || nodeId.contains("PROCESS")) {
                action = "edit";
            } else {
                // 处理任务的时候有nodeId
                action = "task";
            }
        }
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId,"JXBZZBXFSQ",null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        mv.addObject("jxbzzbxfsqId", jxbzzbxfsqId).addObject("action", action);
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("mainGroupId", ContextUtil.getCurrentUser().getMainGroupId());
        mv.addObject("mainGroupName", ContextUtil.getCurrentUser().getMainGroupName());
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        return mv;
    }

    @RequestMapping("getJxbzzbxfsqDetail")
    @ResponseBody
    public JSONObject getJxbzzbxfsqDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject obj = new JSONObject();
        String jxbzzbxfsqId = RequestUtil.getString(request, "jxbzzbxfsqId");
        if (StringUtils.isNotBlank(jxbzzbxfsqId)) {
            obj = jxbzzbxfsqService.getJxbzzbxfsqDetail(jxbzzbxfsqId);
        }
        return obj;
    }

    @RequestMapping("saveJxbzzbxfsq")
    @ResponseBody
    public JsonResult saveJxbzzbxfsq(HttpServletRequest request, @RequestBody String formData,
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
                jxbzzbxfsqService.insertJxbzzbxfsq(formDataJson);
            } else {
                jxbzzbxfsqService.updateJxbzzbxfsq(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save jxbzzbsh");
            result.setSuccess(false);
            result.setMessage("Exception in save jxbzzbsh");
            return result;
        }
        return result;
    }

    @RequestMapping("queryJxbzzbxfsqFileList")
    @ResponseBody
    public List<JSONObject> queryJxbzzbxfsqFileList(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        String jxbzzbshId = RequestUtil.getString(request, "jxbzzbshId");
        return jxbzzbxfsqService.queryJxbzzbxfsqFileList(jxbzzbshId);
    }

    // 文档的下载（预览pdf也调用这里）
    @RequestMapping("jxbzzbxfsqDownload")
    public ResponseEntity<byte[]> jxbzzbxfsqDownload(HttpServletRequest request, HttpServletResponse response) {
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

    @RequestMapping("jxbzzbxfsqOfficePreview")
    @ResponseBody
    public void jxbzzbxfsqOfficePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("jxbzzbFilePathBase");
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("jxbzzbxfsqImagePreview")
    @ResponseBody
    public void jxbzzbxfsqImagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("jxbzzbFilePathBase");
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("openJxbzzbxfsqDialog")
    public ModelAndView openJxbzzbxfsqDialog(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/jxbzzbshSelectList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

}
