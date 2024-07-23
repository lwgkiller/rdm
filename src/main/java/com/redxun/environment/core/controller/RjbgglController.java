package com.redxun.environment.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.BpmSolution;
import com.redxun.bpm.core.entity.ProcessMessage;
import com.redxun.bpm.core.entity.ProcessStartCmd;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.bpm.core.manager.BpmSolutionManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.ExceptionUtil;
import com.redxun.environment.core.service.RjbgglService;
import com.redxun.org.api.model.IUser;
import com.redxun.org.api.service.UserService;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/environment/core/Rjbggl/")
public class RjbgglController extends GenericController {
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private RjbgglService rjbgglService;
    @Resource
    private CommonInfoManager commonInfoManager;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Resource
    private BpmInstManager bpmInstManager;
    @Resource
    private BpmSolutionManager bpmSolutionManager;
    @Autowired
    private UserService userService;


    @RequestMapping("rjbgglListPage")
    public ModelAndView msgManagement(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "rap/core/rjbgglList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rap/core/rjbgglEdit.jsp";
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
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "RJBGGL", null);
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

    @RequestMapping("queryRjbggl")
    @ResponseBody
    public JsonPageResult<?> queryJstb(HttpServletRequest request, HttpServletResponse response) {
        return rjbgglService.queryRjbggl(request, true);
    }

    @RequestMapping("querySzr")
    @ResponseBody
    public List<JSONObject> querySzr(HttpServletRequest request, HttpServletResponse response) {
        String belongId = RequestUtil.getString(request, "belongId");
        return rjbgglService.querySzr(belongId);
    }

    @RequestMapping("queryJsy")
    @ResponseBody
    public List<JSONObject> queryJsy(HttpServletRequest request, HttpServletResponse response) {
        String belongId = RequestUtil.getString(request, "belongId");
        return rjbgglService.queryJsy(belongId);
    }

    @RequestMapping("queryModel")
    @ResponseBody
    public List<JSONObject> queryModel(HttpServletRequest request, HttpServletResponse response) {
        String belongId = RequestUtil.getString(request, "belongId");
        return rjbgglService.queryModel(belongId);
    }

    @RequestMapping("deleteRjbggl")
    @ResponseBody
    public JsonResult deleteJstb(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return rjbgglService.deleteRjbggl(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteRjbggl", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("saveRjbggl")
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
                rjbgglService.createRjbggl(formDataJson);
            } else {
                rjbgglService.updateRjbggl(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save rjbggl");
            result.setSuccess(false);
            result.setMessage("Exception in save rjbggl");
            return result;
        }
        return result;
    }

    @RequestMapping("getRjbggl")
    @ResponseBody
    public JSONObject getRjbggl(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject rjbgglObj = new JSONObject();
        String id = RequestUtil.getString(request, "id");
        if (StringUtils.isNotBlank(id)) {
            rjbgglObj = rjbgglService.getRjbggl(id);
        }
        return rjbgglObj;
    }

    @RequestMapping(value = "startRjProcess", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult startRjProcess(HttpServletRequest request, @RequestBody String postData,
                                        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
        postData = new String(postData.getBytes(), Charset.forName("utf-8"));
        BpmSolution bpmSol = bpmSolutionManager.getByKey("RJBGGL", "1");
        if (bpmSol == null) {
            result.setSuccess(false);
            result.setMessage("流程创建失败，流程为空");
            return result;
        }
        String currentUserId = ContextUtil.getCurrentUserId();
        IUser user = userService.getByUserId(currentUserId);
        if (user == null) {
            result.setSuccess(false);
            result.setMessage("流程创建失败，当前用户 ‘" + currentUserId + "’找不到对应的用户信息");
            return result;
        }
        ContextUtil.setCurUser(user);
        String solId = bpmSol.getSolId();
        JSONObject paramJson = JSONObject.parseObject(postData);
            // 加上处理的消息提示
            ProcessMessage handleMessage = new ProcessMessage();
            try {
                ProcessHandleHelper.setProcessMessage(handleMessage);
                ProcessStartCmd startCmd = new ProcessStartCmd();
                startCmd.setSolId(solId);
                String phone = paramJson.getString("phone");
                String bjName = paramJson.getString("bjName");
                String wlNumber = paramJson.getString("wlNumber");
                String supplier = paramJson.getString("supplier");
                String supplierPer = paramJson.getString("supplierPer");
                String reason = paramJson.getString("reason");
                String content = paramJson.getString("content");
                JSONObject formData = new JSONObject();
//                formData.put("id", IdUtil.getId());
                formData.put("phone", phone);
                formData.put("bjName", bjName);
                formData.put("wlNumber", wlNumber);
                formData.put("supplier", supplier);
                formData.put("supplierPer", supplierPer);
                formData.put("reason", reason);
                formData.put("content", content);
                startCmd.setJsonData(formData.toJSONString());
                // 启动流程
                bpmInstManager.doStartProcess(startCmd);
            } catch (Exception ex) {
                // 把具体的错误放置在内部处理，以显示正确的错误信息提示，在此不作任何的错误处理
                logger.error(ExceptionUtil.getExceptionMessage(ex));
                if (handleMessage.getErrorMsges().size() == 0) {
                    handleMessage.addErrorMsg(ex.getMessage());
                }
            } finally {
                // 在处理过程中，是否有错误的消息抛出
                if (handleMessage.getErrorMsges().size() > 0) {
                    result.setSuccess(false);
                    result.setMessage("流程创建失败，启动流程异常!");
                    result.setData(handleMessage.getErrorMsges());
                }
                ProcessHandleHelper.clearProcessCmd();
                ProcessHandleHelper.clearProcessMessage();
            }
        return result;
    }
}
