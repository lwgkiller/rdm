
package com.redxun.rdmZhgl.core.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmZhgl.core.dao.MonthWorkApplyDao;
import com.redxun.rdmZhgl.core.service.MonthWorkApplyService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectChangeDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * 月度工作计划审批流程
 * 
 * @author zz
 */
@Controller
@RequestMapping("/rdmZhgl/core/monthWorkApply/")
public class MonthWorkApplyController {
    @Autowired
    private MonthWorkApplyDao monthWorkApplyDao;
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Resource
    MonthWorkApplyService monthWorkApplyService;
    @Autowired
    XcmgProjectChangeDao xcmgProjectChangeDao;
    @Resource
    SendDDNoticeManager sendDDNoticeManager;
    @Resource
    CommonInfoManager commonInfoManager;

    /**
     * 审批列表页面
     */
    @RequestMapping("getListPage")
    public ModelAndView getListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/monthWorkApplyList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        JSONObject adminJson = commonInfoManager.hasPermission("YDGZ-GLY");
        mv.addObject("isAdmin", adminJson.getBoolean("YDGZ-GLY"));
        return mv;
    }

    /**
     * 获取审批页面
     */
    @RequestMapping("getEditPage")
    public ModelAndView getEditPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/monthWorkApplyEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String taskId_ = RequestUtil.getString(request, "taskId_");
        String nodeId = RequestUtil.getString(request, "nodeId");
        String id = RequestUtil.getString(request, "id");
        mv.addObject("taskId_", taskId_);
        String applyType = RequestUtil.getString(request, "applyType");
        String applyTitle = "";
        if (StringUtil.isEmpty(applyType) && StringUtil.isNotEmpty(taskId_)) {
            // 查询类型
            JSONObject jsonObject = monthWorkApplyDao.getApplyInfoByTaskId(taskId_);
            if (jsonObject != null) {
                applyType = jsonObject.getString("applyType");
            }
        }

        // 新增或编辑的时候没有nodeId
        String action = "";
        if (StringUtils.isBlank(nodeId) || nodeId.contains("PROCESS")) {
            action = "edit";
        } else {
            action = "task";
        }
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "YDGZJHSP", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        mv.addObject("action", action);
        Map<String, Object> planApplyObj = null;
        JSONObject applyObj = new JSONObject();
        if (StringUtils.isNotBlank(id)) {
            planApplyObj = monthWorkApplyDao.getObjectById(id);
            applyObj = XcmgProjectUtil.convertMap2JsonObject(planApplyObj);
            if ("".equals(applyType)) {
                applyType = applyObj.getString("applyType");
            }
        } else {
            applyObj.put("deptName", ContextUtil.getCurrentUser().getMainGroupName());
            applyObj.put("deptId", ContextUtil.getCurrentUser().getMainGroupId());
            applyObj.put("taskId_", taskId_);
            applyObj.put("applyType", applyType);
        }
        if ("1".equals(applyType)) {
            applyTitle = "月度工作完成情况审批单";
        } else {
            applyTitle = "月度工作计划审批单";
        }
        mv.addObject("applyType", applyType);
        mv.addObject("applyTitle", applyTitle);
        mv.addObject("applyObj", applyObj);
        return mv;
    }

    /**
     * 获取list
     */
    @RequestMapping("queryList")
    @ResponseBody
    public JsonPageResult<?> queryList(HttpServletRequest request, HttpServletResponse response) {
        return monthWorkApplyService.queryList(request, response);
    }

    /**
     * 项目作废表单和流程删除
     */
    @RequestMapping("delete")
    @ResponseBody
    public JsonResult delete(HttpServletRequest request, HttpServletResponse response) {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String instIdStr = RequestUtil.getString(request, "instIds");
            if (StringUtils.isNotEmpty(uIdStr) && StringUtils.isNotEmpty(instIdStr)) {
                String[] ids = uIdStr.split(",");
                String[] instIds = instIdStr.split(",");
                return monthWorkApplyService.delete(ids, instIds);
            }
        } catch (Exception e) {
            return new JsonResult(false, e.getMessage());
        }
        return new JsonResult(true, "成功删除!");
    }

    /**
     * 更加solid获取key
     */
    @RequestMapping(value = "getBpmSolution", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject deskHomeProjectType(HttpServletRequest request, @RequestBody String postData,
        HttpServletResponse response) throws Exception {
        Map<String, Object> param = new HashMap<>(16);
        if (StringUtils.isNotBlank(postData)) {
            JSONObject jsonObject = JSONObject.parseObject(postData);
            param.put("solId", jsonObject.getString("solId"));
        }
        return monthWorkApplyService.getBpmSolution(param);
    }

    /**
     * 获取流程作废信息
     */
    @RequestMapping(value = "abolishInfo", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject getAbolishInfo(HttpServletRequest request, @RequestBody String postData,
        HttpServletResponse response) throws Exception {
        JSONObject postDataJson = new JSONObject();
        if (StringUtils.isNotBlank(postData)) {
            postDataJson = JSONObject.parseObject(postData);
        }
        return monthWorkApplyService.getAbolishInfo(postDataJson, response, request);
    }

    /**
     * 明细：查看审批信息
     */
    @RequestMapping("edit")
    public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = RequestUtil.getString(request, "id");
        String action = RequestUtil.getString(request, "action");
        String status = RequestUtil.getString(request, "status");
        String applyType = RequestUtil.getString(request, "applyType");
        String applyTitle = "";
        if ("1".equals(applyType)) {
            applyTitle = "月度工作完成情况审批单";
        } else {
            applyTitle = "月度工作计划审批单";
        }
        String jspPath = "rdmZhgl/core/monthWorkApplyEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("id", id).addObject("action", action).addObject("status", status);
        mv.addObject("applyType", applyType);
        mv.addObject("applyTitle", applyTitle);
        Map<String, Object> abolishApplyObj = null;
        JSONObject applyObj = new JSONObject();
        if (StringUtils.isNotBlank(id)) {
            abolishApplyObj = monthWorkApplyDao.getObjectById(id);
            applyObj = XcmgProjectUtil.convertMap2JsonObject(abolishApplyObj);
        }
        mv.addObject("applyObj", applyObj);
        return mv;
    }

    @RequestMapping(value = "isApply", method = RequestMethod.POST)
    @ResponseBody
    public List<JSONObject> judyeApply(HttpServletRequest request, @RequestBody String postData,
        HttpServletResponse response) throws Exception {
        JSONObject postDataJson = new JSONObject();
        if (StringUtils.isNotBlank(postData)) {
            postDataJson = JSONObject.parseObject(postData);
        }
        return monthWorkApplyService.getApplyListByParam(postDataJson);
    }

    @RequestMapping(value = "isRunning", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject isRunning(HttpServletRequest request, @RequestBody String postData, HttpServletResponse response)
        throws Exception {
        JSONObject postDataJson = new JSONObject();
        if (StringUtils.isNotBlank(postData)) {
            postDataJson = JSONObject.parseObject(postData);
        }
        return monthWorkApplyService.getActTaskById(postDataJson);
    }

    /**
     * 发送项目通知
     */
    @RequestMapping("notice")
    @ResponseBody
    public JsonResult notice(HttpServletRequest request, HttpServletResponse response) {
        try {
            String userId = RequestUtil.getString(request, "userId");
            JSONObject noticeObj = new JSONObject();
            StringBuilder stringBuilder = new StringBuilder("【月度计划】");
            stringBuilder.append("您有月度计划待审批");
            stringBuilder.append("，请及时处理");
            stringBuilder.append("，通知时间：").append(XcmgProjectUtil.getNowLocalDateStr(DateUtil.DATE_FORMAT_FULL));
            noticeObj.put("content", stringBuilder.toString());
            sendDDNoticeManager.sendNoticeForCommon(userId, noticeObj);
        } catch (Exception e) {
            return new JsonResult(false, e.getMessage());
        }
        return new JsonResult(true, "发送成功!");
    }
}
