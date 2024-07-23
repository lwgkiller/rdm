package com.redxun.rdmZhgl.core.controller;

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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.SqbgService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Controller
@RequestMapping("/Sqbg/")
public class SqbgController extends GenericController {
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private SqbgService sqbgService;
    @Autowired
    private CommonInfoManager commonInfoManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;

    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/sqbgFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("sqbgId", RequestUtil.getString(request, "sqbgId", ""));
        return mv;
    }

    @RequestMapping(value = "upload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            sqbgService.saveSqbgUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    @RequestMapping("sqbgListPage")
    public ModelAndView msgManagement(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "rdmZhgl/core/sqbgList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String applyType = request.getParameter("applyType");
        mv.addObject("applyType", applyType);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/sqbgEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String sqbgId = RequestUtil.getString(request, "sqbgId");
        String nodeId = RequestUtil.getString(request, "nodeId");
        String action = RequestUtil.getString(request, "action");
        String status = RequestUtil.getString(request, "status");
        String applyType = RequestUtil.getString(request, "applyType");
        if (StringUtils.isBlank(action)) {
            // 新增或编辑的时候没有nodeId
            if (StringUtils.isBlank(nodeId) || nodeId.contains("PROCESS")) {
                action = "edit";
            } else {
                // 处理任务的时候有nodeId
                action = "task";
            }
        }
        mv.addObject("sqbgId", sqbgId).addObject("action", action).addObject("status", status);
        // 取出节点变量，返回到页面

        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "SQWJBG", null);
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
        mv.addObject("applyType", applyType);
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        // 判断当前登录人的部门是挖机营销公司、挖机海外营销公司还是挖掘机械研究院下属的部门
        String userRole = "";
        String currentUserDeptId = ContextUtil.getCurrentUser().getMainGroupId();
        if (commonInfoManager.queryWhetherUnderDept(currentUserDeptId, RdmConst.GNYXGS_NAME)) {
            userRole = "gnyx";
        } else if (commonInfoManager.queryWhetherUnderDept(currentUserDeptId, RdmConst.HWYXGS_NAME)) {
            userRole = "hwyx";
        } else if (commonInfoManager.queryWhetherUnderDept(currentUserDeptId, RdmConst.JSZX_NAME)) {
            userRole = "yjy";
        }
        mv.addObject("userRole", userRole);
        return mv;
    }

    @RequestMapping("querySqbg")
    @ResponseBody
    public JsonPageResult<?> queryJstb(HttpServletRequest request, HttpServletResponse response) {
        return sqbgService.querySqbg(request, true);
    }

    @RequestMapping("deleteSqbg")
    @ResponseBody
    public JsonResult deleteJstb(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String sqbgId = RequestUtil.getString(request, "id");
            return sqbgService.deleteSqbg(sqbgId);
        } catch (Exception e) {
            logger.error("Exception in deleteWj", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("saveSqbg")
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
            if (StringUtils.isBlank(formDataJson.getString("sqbgId"))) {
                sqbgService.createSqbg(formDataJson);
            } else {
                sqbgService.updateSqbg(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save sqbg");
            result.setSuccess(false);
            result.setMessage("Exception in save sqbg");
            return result;
        }
        return result;
    }

    @RequestMapping("getSqbgDetail")
    @ResponseBody
    public JSONObject getSqbgDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject sqbgObj = new JSONObject();
        String sqbgId = RequestUtil.getString(request, "sqbgId");
        if (StringUtils.isNotBlank(sqbgId)) {
            sqbgObj = sqbgService.getSqbgDetail(sqbgId);
        }
        return sqbgObj;
    }

    @RequestMapping("getDetailList")
    @ResponseBody
    public List<JSONObject> getCnList(HttpServletRequest request, HttpServletResponse response) {
        String belongId = RequestUtil.getString(request, "belongId");
        return sqbgService.getDetailList(belongId);
    }

    @RequestMapping("getSqbgFileList")
    @ResponseBody
    public List<JSONObject> getSqbgFileList(HttpServletRequest request, HttpServletResponse response) {
        String belongId = RequestUtil.getString(request, "belongId");
        return sqbgService.getSqbgFileList(belongId);
    }

    @RequestMapping("deleteSqbgFile")
    public void deleteSqbgFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String sqbgId = postBodyObj.getString("formId");
        sqbgService.deleteOneSqbgFile(fileId, fileName, sqbgId);
    }

    @RequestMapping("sqbgPdfPreview")
    public ResponseEntity<byte[]> sqbgPdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("sqbgFilePathBase");
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, fileBasePath);
    }

    @RequestMapping("sqbgOfficePreview")
    @ResponseBody
    public void sqbgOfficePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("sqbgFilePathBase");
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("sqbgImagePreview")
    @ResponseBody
    public void sqbgImagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("sqbgFilePathBase");
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    /**
     * 售前变更流程结束之后，生成一条新的售前文件审批流程
     * 
     * @param request
     * @return
     */
    @RequestMapping(value = "startLc", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject startDeliveryApproval(HttpServletRequest request) {
        return sqbgService.startDeliveryApproval(request);
    }
}
