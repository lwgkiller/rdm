package com.redxun.presaleDocuments.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.StringUtil;
import com.redxun.presaleDocuments.core.service.PresaleDocumentsApplyService;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.util.RdmCommonUtil;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.service.DecorationManualTopicPSService;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.org.entity.OsDimension;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/presaleDocuments/core/apply")
public class PresaleDocumentsApplyController {
    private static final Logger logger = LoggerFactory.getLogger(PresaleDocumentsApplyController.class);
    @Autowired
    private PresaleDocumentsApplyService presaleDocumentsApplyService;
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private CommonInfoManager commonInfoManager;

    //..
    @RequestMapping("dataListPage")
    public ModelAndView dataListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "presaleDocuments/core/presaleDocumentsApplyList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("businessType", RequestUtil.getString(request, "businessType"));
        List<JSONObject> nodeSetListWithName =
                commonBpmManager.getNodeSetListWithName("PresaleDocumentApply", "userTask", "endEvent");
        mv.addObject("nodeSetListWithName", nodeSetListWithName);
        return mv;
    }

    //..
    @RequestMapping("dataListQuery")
    @ResponseBody
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        return presaleDocumentsApplyService.dataListQuery(request, response);
    }

    //..
    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "presaleDocuments/core/presaleDocumentsApplyEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String businessId = RequestUtil.getString(request, "businessId");
        String nodeId = RequestUtil.getString(request, "nodeId");
        String action = RequestUtil.getString(request, "action");
        String status = RequestUtil.getString(request, "status");
        String masterDataId = RequestUtil.getString(request, "masterDataId");
        String businessType = RequestUtil.getString(request, "businessType");
        if (StringUtils.isBlank(action)) {
            // 新增或编辑的时候没有nodeId
            if (StringUtils.isBlank(nodeId) || nodeId.contains("PROCESS")) {
                action = "edit";
            } else {
                // 处理任务的时候有nodeId
                action = "task";
            }
        }
        mv.addObject("businessId", businessId).addObject("action", action).addObject("status", status)
                .addObject("masterDataId", masterDataId).addObject("businessType", businessType);
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars =
                    commonBpmManager.queryNodeVarsByParam(nodeId, "PresaleDocumentApply", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        boolean isPresaleDocumentsAdmin = commonInfoManager.judgeUserIsPointGroup("PresaleDocumentsAdmin", OsDimension.DIM_ROLE_ID,
                "0", ContextUtil.getCurrentUserId(), "1");
        mv.addObject("isPresaleDocumentsAdmin", isPresaleDocumentsAdmin);
        if (ContextUtil.getCurrentUser().getMainGroupName().equalsIgnoreCase(RdmConst.FWGCS_NAME)) {
            mv.addObject("isService", "true");
        } else {
            mv.addObject("isService", "false");
        }
        return mv;
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
            return presaleDocumentsApplyService.deleteBusiness(ids, instIds);
        } catch (Exception e) {
            logger.error("Exception in deleteBusiness", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    //..
    @RequestMapping("getDataById")
    @ResponseBody
    public JSONObject getDataById(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = RequestUtil.getString(request, "businessId");
        return presaleDocumentsApplyService.getDataById(id);
    }

    //..
    @RequestMapping("saveBusiness")
    @ResponseBody
    public JsonResult saveBusiness(HttpServletRequest request, @RequestBody String formDataStr,
                                   HttpServletResponse response) {
        JSONObject jsonObject = JSONObject.parseObject(formDataStr);
        if (jsonObject == null || jsonObject.isEmpty()) {
            return new JsonResult(false, "表单内容为空，操作失败！");
        }
        try {
            return presaleDocumentsApplyService.saveBusiness(jsonObject);
        } catch (Exception e) {
            logger.error("Exception in saveBusiness", e);
            return new JsonResult(false, e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
        }
    }

    // ..
    @RequestMapping("templateDownload")
    public ResponseEntity<byte[]> templateDownload(HttpServletRequest request, HttpServletResponse response) {
        String type = RequestUtil.getString(request, "type");
        return presaleDocumentsApplyService.templateDownload(type);
    }

    //..测试直接创建翻译
    @RequestMapping("testBusiness")
    @ResponseBody
    public JsonResult testBusiness(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return presaleDocumentsApplyService.testBusiness(ids);
        } catch (Exception e) {
            logger.error("Exception in testBusiness", e);
            return new JsonResult(false, e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
        }
    }
}
