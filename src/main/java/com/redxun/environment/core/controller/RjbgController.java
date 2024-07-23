package com.redxun.environment.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.environment.core.dao.RjbgDao;
import com.redxun.environment.core.service.RjbgService;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/environment/core/Rjbg/")
public class RjbgController extends GenericController {
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private RjbgService rjbgService;
    @Autowired
    private RjbgDao rjbgDao;
    @Resource
    private CommonInfoManager commonInfoManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;

    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rap/core/rjbgSmFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("rjbgId", RequestUtil.getString(request, "rjbgId", ""));
        mv.addObject("fileType", RequestUtil.getString(request, "fileType", ""));
        return mv;
    }

    @RequestMapping("rjbgListPage")
    public ModelAndView msgManagement(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "rap/core/rjbgList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rap/core/rjbgEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String rjbgId = RequestUtil.getString(request, "rjbgId");
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
        mv.addObject("rjbgId", rjbgId).addObject("action", action).addObject("status", status);
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId,"FDJRJBG",null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        JSONObject currentUserDepInfo = xcmgProjectManager.isCurrentUserDepRespman();
        Map<String, Object> params = new HashMap<>();
        params.put("currentUserMainDepId", currentUserDepInfo.getString("currentUserMainDepId"));
        JSONObject dept = commonInfoManager.queryDeptNameById(currentUserDepInfo.getString("currentUserMainDepId"));
        String deptName = dept.getString("deptname");
        mv.addObject("deptName", deptName);
        mv.addObject("currentUserMainDepId", currentUserDepInfo.getString("currentUserMainDepId"));
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("queryRjbg")
    @ResponseBody
    public JsonPageResult<?> queryJstb(HttpServletRequest request, HttpServletResponse response) {
        return rjbgService.queryRjbg(request, true);
    }

    @RequestMapping("queryReason")
    @ResponseBody
    public List<JSONObject> getCnList(HttpServletRequest request, HttpServletResponse response) {
        String belongId = RequestUtil.getString(request, "belongId");
        return rjbgService.queryReason(belongId);
    }

    @RequestMapping("deleteRjbg")
    @ResponseBody
    public JsonResult deleteJstb(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return rjbgService.deleteRjbg(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteRjbg", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("saveRjbg")
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
            if (StringUtils.isBlank(formDataJson.getString("rjbgId"))) {
                rjbgService.createRjbg(formDataJson);
            } else {
                rjbgService.updateRjbg(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save rjbg");
            result.setSuccess(false);
            result.setMessage("Exception in save rjbg");
            return result;
        }
        return result;
    }

    @RequestMapping("getRjbgDetail")
    @ResponseBody
    public JSONObject getRjbgDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject rjbgObj = new JSONObject();
        String rjbgId = RequestUtil.getString(request, "rjbgId");
        if (StringUtils.isNotBlank(rjbgId)) {
            rjbgObj = rjbgService.getRjbgDetail(rjbgId);
        }
        return rjbgObj;
    }

    @RequestMapping(value = "upload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            rjbgService.saveRjbgUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }


    @RequestMapping("getRjbgFileList")
    @ResponseBody
    public List<JSONObject> getRjbgFileList(HttpServletRequest request, HttpServletResponse response) {
        List<String> rjbgIdList = Arrays.asList(RequestUtil.getString(request, "rjbgId", ""));
        String fileType = RequestUtil.getString(request, "fileType");
        return rjbgService.getRjbgFileList(rjbgIdList, fileType);
    }

    @RequestMapping("deleteRjbgFile")
    public void deleteRjbgFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String rjbgId = postBodyObj.getString("formId");
        rjbgService.deleteOneRjbgFile(fileId, fileName, rjbgId);
    }

    @RequestMapping("rjbgPdfPreview")
    public ResponseEntity<byte[]> rjbgPdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("rjbgFilePathBase");
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, fileBasePath);
    }

    @RequestMapping("rjbgOfficePreview")
    @ResponseBody
    public void rjbgOfficePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("rjbgFilePathBase");
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("rjbgImagePreview")
    @ResponseBody
    public void rjbgImagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("rjbgFilePathBase");
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("checkModel")
    @ResponseBody
    public JsonResult checkFile(HttpServletRequest request, HttpServletResponse response) {
        String fdjModel = RequestUtil.getString(request, "fdjModel");
        return rjbgService.checkModel(fdjModel);
    }
}
