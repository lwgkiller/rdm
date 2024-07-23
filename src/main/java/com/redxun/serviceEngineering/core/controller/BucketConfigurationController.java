package com.redxun.serviceEngineering.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.ExceptionUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.service.BucketConfigurationScheduler;
import com.redxun.serviceEngineering.core.service.BucketConfigurationService;
import com.redxun.serviceEngineering.core.service.OverseasProductRectificationService;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;
import java.util.*;

@Controller
@RequestMapping("/serviceEngineering/core/bucketConfiguration")
public class BucketConfigurationController {
    private static final Logger logger = LoggerFactory.getLogger(BucketConfigurationController.class);
    @Autowired
    private BucketConfigurationService bucketConfigurationService;
    @Autowired
    private BucketConfigurationScheduler bucketConfigurationScheduler;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;

    //..
    @RequestMapping("dataListPage")
    public ModelAndView dataListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/bucketConfigurationList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        List<JSONObject> nodeSetListWithName =
                commonBpmManager.getNodeSetListWithName("bucketConfiguration", "userTask", "endEvent");
        mv.addObject("nodeSetListWithName", nodeSetListWithName);
        boolean isCustomsDeclarationAdmin = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), "报关信息管理人员");
        mv.addObject("isCustomsDeclarationAdmin", isCustomsDeclarationAdmin);
        return mv;
    }

    //..
    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/bucketConfigurationEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        //新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String businessId = RequestUtil.getString(request, "businessId");
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
        mv.addObject("businessId", businessId).addObject("action", action).addObject("status", status);
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars =
                    commonBpmManager.queryNodeVarsByParam(nodeId, "bucketConfiguration", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }

    //..
    @RequestMapping("openFileWindow")
    public ModelAndView openFileWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/bucketConfigurationFileWindow.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String businessId = RequestUtil.getString(request, "businessId");
        String businessType = RequestUtil.getString(request, "businessType");
        String action = RequestUtil.getString(request, "action");
        String coverContent = RequestUtil.getString(request, "coverContent");
        mv.addObject("businessId", businessId).addObject("businessType", businessType).
                addObject("action", action).addObject("coverContent", coverContent);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        return mv;
    }

    //..
    @RequestMapping("openFileUploadWindow")
    public ModelAndView openFileUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/bucketConfigurationFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("businessId", RequestUtil.getString(request, "businessId", ""));
        mv.addObject("businessType", RequestUtil.getString(request, "businessType", ""));
        mv.addObject("urlCut", RequestUtil.getString(request, "urlCut", ""));
        mv.addObject("fileType", RequestUtil.getString(request, "fileType", ""));
        return mv;
    }

    //..
    @RequestMapping("dataListQuery")
    @ResponseBody
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        return bucketConfigurationService.dataListQuery(request, response);
    }

    //..
    @RequestMapping("deleteBusiness")
    @ResponseBody
    public JsonResult deleteBusiness(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String instIdStr = RequestUtil.getString(request, "instIds");
            String[] ids = uIdStr.split(",");
            String[] instIds = instIdStr.split(",");
            return bucketConfigurationService.deleteBusiness(ids, instIds);
        } catch (Exception e) {
            logger.error("Exception in deleteBusiness", e);
            return new JsonResult(false, ExceptionUtil.getExceptionMessage(e));
        }
    }

    //..
    @RequestMapping(value = "fileUpload")
    @ResponseBody
    public JsonResult fileUpload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            return bucketConfigurationService.saveFiles(request);
        } catch (Exception e) {
            logger.error("Exception in fileUpload", e);
            return new JsonResult(false, e.getCause().toString());
        }
    }

    //..
    @RequestMapping("getFileListInfos")
    @ResponseBody
    public List<JSONObject> getFileListInfos(HttpServletRequest request, HttpServletResponse response) {
        List<String> businessIdList = Arrays.asList(RequestUtil.getString(request, "businessId", ""));
        List<String> businessTypeList = new ArrayList<>();//有时只给第一个参数，第二个就要屏蔽掉，否则会有干扰
        if (StringUtil.isNotEmpty(RequestUtil.getString(request, "businessType", ""))) {
            businessTypeList = Arrays.asList(RequestUtil.getString(request, "businessType", ""));
        }
        JSONObject params = new JSONObject();
        params.put("businessIds", businessIdList);
        params.put("businessTypes", businessTypeList);
        return bucketConfigurationService.getFileListInfos(params);
    }

    //..
    @RequestMapping("deleteFile")
    public void deleteFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String businessId = postBodyObj.getString("businessId");
        String businessType = postBodyObj.getString("businessType");
        try {
            bucketConfigurationService.deleteFile(fileId, fileName, businessId, businessType);
        } catch (Exception e) {
            logger.error("Exception in deleteFile", e);
        }
    }

    //..
    @RequestMapping("pdfPreview")
    public ResponseEntity<byte[]> pdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String businessId = RequestUtil.getString(request, "businessId");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringUploadPosition", "bucketConfiguration").getValue();
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, businessId, filePathBase);
    }

    //..
    @RequestMapping("officePreview")
    @ResponseBody
    public void officePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String businessId = RequestUtil.getString(request, "businessId");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringUploadPosition", "bucketConfiguration").getValue();
        rdmZhglFileManager.officeFilePreview(fileName, fileId, businessId, filePathBase, response);
    }

    //..
    @RequestMapping("imagePreview")
    @ResponseBody
    public void imagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String businessId = RequestUtil.getString(request, "businessId");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringUploadPosition", "bucketConfiguration").getValue();
        rdmZhglFileManager.imageFilePreview(fileName, fileId, businessId, filePathBase, response);
    }

    //..
    @RequestMapping("getDetail")
    @ResponseBody
    public JSONObject getDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject jsonObject = new JSONObject();
        String businessId = RequestUtil.getString(request, "businessId");
        jsonObject = bucketConfigurationService.getDetail(businessId);
        return jsonObject;
    }

    //..
    @RequestMapping("testBusiness")
    @ResponseBody
    public JsonResult testBusiness(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            bucketConfigurationScheduler.createAuto();
            return new JsonResult(true, "测试成功");
        } catch (Exception e) {
            logger.error("Exception in deleteBusiness", e);
            return new JsonResult(false, ExceptionUtil.getExceptionMessage(e));
        }
    }
}
