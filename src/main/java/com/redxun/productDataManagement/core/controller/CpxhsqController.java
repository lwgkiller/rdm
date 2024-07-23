package com.redxun.productDataManagement.core.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.productDataManagement.core.manager.CpxhsqManager;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Controller
@RequestMapping("/pdm/core/cpxhsq/")
public class CpxhsqController {
    private static final Logger logger = LoggerFactory.getLogger(CpxhsqController.class);
    @Autowired
    private CpxhsqManager cpxhsqManager;
    @Resource
    private BpmInstManager bpmInstManager;
    @Autowired
    private CommonBpmManager commonBpmManager;

    @RequestMapping("applyListPage")
    public ModelAndView getListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String cpxhsqList = "/productDataManagement/core/cpxhsqList.jsp";
        ModelAndView mv = new ModelAndView(cpxhsqList);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    @RequestMapping("applyList")
    @ResponseBody
    public JsonPageResult<?> queryApplyList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return cpxhsqManager.queryApplyList(request, true);
    }

    @RequestMapping("deleteApply")
    @ResponseBody
    public JsonResult deleteApply(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
            return cpxhsqManager.delApplys(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteApply", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("applyEditPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "productDataManagement/core/cpxhsqEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String applyId = RequestUtil.getString(request, "applyId", "");
        String nodeId = RequestUtil.getString(request, "nodeId", "");
        String action = RequestUtil.getString(request, "action", "");
        String status = RequestUtil.getString(request, "status", "");
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
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "CPSJXHSQ", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("applyId", applyId);
        mv.addObject("action", action);
        mv.addObject("status", status);

        return mv;
    }

    @RequestMapping("getJson")
    @ResponseBody
    public JSONObject queryApplyDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = RequestUtil.getString(request, "id", "");
        if (StringUtils.isBlank(id)) {
            JSONObject result = new JSONObject();
            result.put("CREATE_BY_", ContextUtil.getCurrentUser().getUserId());
            result.put("creatorName", ContextUtil.getCurrentUser().getFullname());
            result.put("departName", ContextUtil.getCurrentUser().getMainGroupName());
            return result;
        }
        return cpxhsqManager.queryApplyDetail(id);
    }

    @RequestMapping("checkDesignModelValid")
    @ResponseBody
    public JsonResult checkDesignModelValid(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JsonResult result = new JsonResult(true);
        String id = RequestUtil.getString(request, "id", "");
        String designModel = RequestUtil.getString(request, "designModel", "");
        if (StringUtils.isBlank(designModel)) {
            result.setSuccess(false);
            result.setMessage("设计型号为空！");
            return result;
        }
        JSONObject param = new JSONObject();
        param.put("designModel", designModel);
        if (StringUtils.isNotBlank(id)) {
            param.put("id", id);
        }
        cpxhsqManager.checkDesignModelValid(param, result);
        return result;
    }

}
