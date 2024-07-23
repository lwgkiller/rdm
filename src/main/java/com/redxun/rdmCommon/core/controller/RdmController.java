package com.redxun.rdmCommon.core.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.sys.org.entity.OsDimension;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmCommon.core.manager.RdmManager;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;

/**
 * @author zhangzhen
 */
@Controller
@RequestMapping("/rdm/core")
public class RdmController {
    private Logger logger = LogManager.getLogger(RdmController.class);
    @Resource
    private RdmManager rdmManager;
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;

    @RequestMapping("/newUserListPage")
    public ModelAndView rdmHomePage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "digitization/core/rdmNewUserList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);

        return mv;
    }

    @RequestMapping("/newUserSave")
    @ResponseBody
    public JSONObject newUserSave(HttpServletRequest request, @RequestBody String requestBody,
                                  HttpServletResponse response) throws DecoderException {
        JSONObject requestObject = JSONObject.parseObject(requestBody);
        return rdmManager.newUserSave(requestObject);
    }

    @RequestMapping("/newUserConfirm")
    @ResponseBody
    public JSONObject newUserConfirm(HttpServletRequest request, @RequestBody String requestBody,
                                     HttpServletResponse response) {
        JSONObject requestObject = JSONObject.parseObject(requestBody);
        return rdmManager.newUserConfirm(requestObject);
    }

    @RequestMapping("/deleteUser")
    @ResponseBody
    public JsonResult deleteUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return rdmManager.deleteUser(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteUser", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("/newUserList")
    @ResponseBody
    public List<JSONObject> newUserList(HttpServletRequest request, HttpServletResponse response) {
        return rdmManager.newUserList(request);
    }

    // 需要用iframe来包裹一些页面，否则grid中的选人或者部门不起作用
    @RequestMapping("/noFlowFormIframe")
    public ModelAndView noFlowFormIframe(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmCommon/rdmNoFlowFormIframe.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String formUrl = RequestUtil.getString(request, "url", "");
        mv.addObject("formUrl", formUrl);
        return mv;
    }

    @RequestMapping("/ajaxAsync")
    @ResponseBody
    public String ajaxAsync(HttpServletRequest request, HttpServletResponse response) {
        return "";
    }

    /**
     * 流程待办提醒
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/taskRemind")
    @ResponseBody
    public JSONObject taskRemind(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        String bpmInstId = RequestUtil.getString(request, "instId", "");
        if (StringUtils.isBlank(bpmInstId)) {
            result.put("message", "找不到流程的任务，请联系管理员！");
            return result;
        }
        // 根据实例找到任务，根据任务找到任务处理人
        List<JSONObject> taskUsers = new ArrayList<>();
        JSONObject taskObj = commonBpmManager.queryTaskAndUsersByInstId(bpmInstId, taskUsers);
        if (taskUsers.isEmpty() || taskObj == null) {
            result.put("message", "找不到流程的任务，请联系管理员！");
            return result;
        }
        String taskName = taskObj.getString("taskName");
        String taskDesc = taskObj.getString("taskDesc");
        taskObj.put("content", taskDesc + "，当前任务：" + taskName + "，提醒人：" + ContextUtil.getCurrentUser().getFullname());
        sendDDNoticeManager.sendNoticeForTask(taskUsers, taskObj);
        return result;
    }

    //..获取表行数页面
    @RequestMapping("rdmCountTableRowsPage")
    public ModelAndView masterDataListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmCommon/rdmCountTableRows.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    //..获取表行数
    @RequestMapping("getCountTableRows")
    @ResponseBody
    public JsonPageResult<?> getCountTableRows(HttpServletRequest request, HttpServletResponse response) {
        return rdmManager.getCountTableRows(request, response);
    }

    //..刷新表行数
    @RequestMapping("refreshCountTableRows")
    @ResponseBody
    public JsonResult refreshCountTableRows(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            return rdmManager.doRefreshCountTableRows();
        } catch (Exception e) {
            logger.error("Exception in refreshCountTableRows", e);
            return new JsonResult(false, e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
        }
    }
}
