package com.redxun.standardManager.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.standardManager.core.manager.StandardUpdateService;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/gzjl/")
public class StandardUpdateController extends GenericController {
    @Autowired
    private CommonInfoManager commonInfoManager;
    @Autowired
    private StandardUpdateService standardUpdateService;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;

    @RequestMapping("gzjlListPage")
    public ModelAndView msgManagement(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "standardManager/core/standardUpdateList.jsp";
        String belongbz = RequestUtil.getString(request, "belongbz", "");
        String action = RequestUtil.getString(request, "action", "");
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("belongbz", belongbz);
        mv.addObject("action", action);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "standardManager/core/standardUpdateEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String updateId = RequestUtil.getString(request, "updateId");
        String action = RequestUtil.getString(request, "action");
        mv.addObject("updateId", updateId).addObject("action", action);
        JSONObject currentUserDepInfo = xcmgProjectManager.isCurrentUserDepRespman();
        JSONObject dept = commonInfoManager.queryDeptNameById(currentUserDepInfo.getString("currentUserMainDepId"));
        String deptName = dept.getString("deptname");
        mv.addObject("deptName", deptName);
        mv.addObject("currentUserMainDepId", currentUserDepInfo.getString("currentUserMainDepId"));
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("queryGzjl")
    @ResponseBody
    public JsonPageResult<?> queryGzjl(HttpServletRequest request, HttpServletResponse response) {
        return standardUpdateService.queryGzjl(request, true);
    }

    @RequestMapping("getGzjlBaseInfo")
    @ResponseBody
    public JSONObject getSzhBaseInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject ydjxObj = new JSONObject();
        String updateId = RequestUtil.getString(request, "updateId");
        if (StringUtils.isNotBlank(updateId)) {
            ydjxObj = standardUpdateService.getGzjlById(updateId);
        }
        return ydjxObj;
    }

    @RequestMapping("getGzjlDetailList")
    @ResponseBody
    public List<JSONObject> getGzjlDetailList(HttpServletRequest request, HttpServletResponse response) {
        return standardUpdateService.getGzjlDetailList(request);
    }

    @RequestMapping("saveGzjlDetail")
    @ResponseBody
    public JsonResult saveZlgj(HttpServletRequest request, @RequestBody String zlgjStr, HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(zlgjStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }

        try {
            JSONObject formDataJson = JSONObject.parseObject(zlgjStr);
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("updateId"))) {
                standardUpdateService.createGzjl(formDataJson);
            } else {
                standardUpdateService.updateGzjl(formDataJson);
            }
            result.setData(formDataJson.getString("updateId"));
        } catch (Exception e) {
            logger.error("Exception in save bzgx",e);
            result.setSuccess(false);
            result.setMessage("Exception in save bzgx");
            return result;
        }
        return result;
    }

    @RequestMapping("deleteGzjl")
    @ResponseBody
    public JsonResult deleteGzjl(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String updateId = RequestUtil.getString(request, "id");
            return standardUpdateService.deleteGzjl(updateId);
        } catch (Exception e) {
            logger.error("Exception in deleteGzjl", e);
            return new JsonResult(false, e.getMessage());
        }
    }
}
