package com.redxun.serviceEngineering.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.dao.YsInventoryDao;
import com.redxun.serviceEngineering.core.service.YsInventoryService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/serviceEngineer/core/YsInventory/")
public class YsInventoryController extends GenericController {
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private YsInventoryService ysInventoryService;
    @Autowired
    private YsInventoryDao ysInventoryDao;
    @Resource
    private CommonInfoManager commonInfoManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;


    @RequestMapping("ysInventoryListPage")
    public ModelAndView msgManagement(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "serviceEngineering/core/ysInventoryList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/ysInventoryEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String id = RequestUtil.getString(request, "id");
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
        mv.addObject("id", id).addObject("action", action).addObject("status", status);
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId,"YSJQD",null);
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

    @RequestMapping("queryYsInventory")
    @ResponseBody
    public JsonPageResult<?> queryJstb(HttpServletRequest request, HttpServletResponse response) {
        return ysInventoryService.queryYsInventory(request, response);
    }

    @RequestMapping("queryDetail")
    @ResponseBody
    public List<JSONObject> getCnList(HttpServletRequest request, HttpServletResponse response) {
        String belongId = RequestUtil.getString(request, "belongId");
        return ysInventoryService.queryDetail(belongId);
    }

    @RequestMapping("deleteYsInventory")
    @ResponseBody
    public JsonResult deleteJstb(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String instIdStr = RequestUtil.getString(request, "instIds");
            String[] ids = uIdStr.split(",");
            return ysInventoryService.deleteYsInventory(ids,instIdStr);
        } catch (Exception e) {
            logger.error("Exception in deleteYsInventory", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("saveYsInventory")
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
            if (StringUtils.isBlank(formDataJson.getString("id"))) {
                ysInventoryService.createYsInventory(formDataJson);
            } else {
                ysInventoryService.updateYsInventory(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save ysInventory");
            result.setSuccess(false);
            result.setMessage("Exception in save ysInventory");
            return result;
        }
        return result;
    }

    @RequestMapping("getYsInventoryDetail")
    @ResponseBody
    public JSONObject getYsInventoryDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject ysInventoryObj = new JSONObject();
        String id = RequestUtil.getString(request, "id");
        if (StringUtils.isNotBlank(id)) {
            ysInventoryObj = ysInventoryService.getYsInventoryDetail(id);
        }
        return ysInventoryObj;
    }


    @RequestMapping("/importTemplateDownloadYsInventory")
    public ResponseEntity<byte[]> importTemplateDownloadYsInventory(HttpServletRequest request, HttpServletResponse response) {
        return ysInventoryService.importTemplateDownloadYsInventory();
    }

    @RequestMapping("importExcelYsInventory")
    @ResponseBody
    public JSONObject importExcelYsInventory(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        ysInventoryService.importYsInventory(result, request);
        return result;
    }

    @RequestMapping("exportYsInventoryList")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        ysInventoryService.exportYsInventoryList(request, response);
    }
}
