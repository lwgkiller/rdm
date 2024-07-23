
package com.redxun.xcmgProjectManager.core.controller;

import java.text.ParseException;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectMsgManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * project_baseInfo控制器
 *
 * @author x
 */
@Controller
@RequestMapping("/xcmgProjectManager/core/message/")
public class XcmgProjectMsgController {
    @Resource
    private XcmgProjectMsgManager xcmgProjectMsgManager;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Resource
    private XcmgProjectManager xcmgProjectManager;

    // 主页面
    @RequestMapping("management")
    public ModelAndView management(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "xcmgProjectManager/core/messageManagement.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    // 发送消息界面
    @RequestMapping("edit")
    public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "xcmgProjectManager/core/messageEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 返回当前登录人所属或者所负责的所有部门信息
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> currentUserDeps = xcmgProjectOtherDao.queryUserDeps(params);
        JSONArray jsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserDeps);
        mv.addObject("currentUserDeps", jsonArray);
        // 返回当前登录人角色信息
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        Map<String, Object> userInfoMap = new HashMap<>();
        xcmgProjectManager.getUserInfoById(ContextUtil.getCurrentUserId(), userInfoMap);
        JSONObject userInfoObj = XcmgProjectUtil.convertMap2JsonObject(userInfoMap);
        mv.addObject("currentUserInfo", userInfoObj);
        return mv;
    }

    // 查看消息页面
    @RequestMapping("see")
    public ModelAndView see(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "xcmgProjectManager/core/messageSee.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String id = RequestUtil.getString(request, "id");
        String status = RequestUtil.getString(request, "status");
        if (status.equals("0")) {
            xcmgProjectMsgManager.setMsgRead(id);
        }
        // 根据id查询消息信息
        Map<String, String> msgDetail = xcmgProjectMsgManager.queryMsgDetailById(id);
        JSONObject msgDetailObj = XcmgProjectUtil.convertMap2JsonString(msgDetail);
        mv.addObject("msgDetailObj", msgDetailObj);
        return mv;
    }

    // 批量已读
    @RequestMapping("batchRead")
    @ResponseBody
    public void batchRead(HttpServletRequest request, @RequestBody String postBody, HttpServletResponse response)
        throws Exception {
        if (StringUtils.isBlank(postBody)) {
            return;
        }
        JSONObject postJSON = JSONObject.parseObject(postBody);
        String[] messageIdArr = postJSON.getString("messageIdStr").split(",", -1);
        for (String oneId : messageIdArr) {
            xcmgProjectMsgManager.setMsgRead(oneId);
        }
    }

    // 发送消息
    @RequestMapping("send")
    @ResponseBody
    public void send(HttpServletRequest request, @RequestBody String postData, HttpServletResponse response)
        throws Exception {
        xcmgProjectMsgManager.sendMsg(postData);
    }

    // 消息状态为已读
    @RequestMapping("setToRead")
    @ResponseBody
    public void setToRead(HttpServletRequest request, HttpServletResponse response) throws Exception {
        xcmgProjectMsgManager.setMsgRead(RequestUtil.getString(request, "messageId"));
    }

    // 查询已发送消息
    @RequestMapping("querySend")
    @ResponseBody
    public List<Map<String, Object>> querySend(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        return xcmgProjectMsgManager.querySendMsg(ContextUtil.getCurrentUserId());
    }

    // 查询已收到消息
    @RequestMapping("queryRec")
    @ResponseBody
    public List<Map<String, Object>> queryRec(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        String messageType = RequestUtil.getString(request, "messageType");
        String homeMessage = RequestUtil.getString(request, "homeMessage");
        String status = RequestUtil.getString(request, "status", "");
        List<Map<String, Object>> list =
            xcmgProjectMsgManager.queryRecMsg(ContextUtil.getCurrentUserId(), messageType, "");
        // 说明：如果是个人办公首页的消息，则默认只展示homeMessage条数
        if (!"".equals(homeMessage)) {
            if (list != null && list.size() > 0) {
                if (list.size() > Integer.parseInt(homeMessage)) {
                    list = list.subList(0, Integer.parseInt(homeMessage));
                }
            }
        } else if (StringUtils.isNotBlank(status)) {
            Iterator<Map<String, Object>> iterator = list.iterator();
            while (iterator.hasNext()) {
                Map<String, Object> oneMessage = iterator.next();
                String messageStatus = oneMessage.get("status").toString();
                if ("yes".equalsIgnoreCase(status)) {
                    if (!messageStatus.equalsIgnoreCase("已读")) {
                        iterator.remove();
                    }
                }
                if ("no".equalsIgnoreCase(status)) {
                    if (!messageStatus.equalsIgnoreCase("未读")) {
                        iterator.remove();
                    }
                }
            }
        }

        return list;
    }

    private boolean popAtFirst(Map<String, Object> ParamMap) throws ParseException {
        if (ParamMap.get("expireTime") == null) {
            return true;
        }
        Date expireTimeDate = (Date)ParamMap.get("expireTime");
        long currentTime = DateFormatUtil.parseDateTime(XcmgProjectUtil.getNowLocalDateStr("")).getTime();
        if (expireTimeDate.getTime() - currentTime > 0) {
            return true;
        }
        return false;
    }

    // 查询消息的类型
    @RequestMapping("queryMsgType")
    @ResponseBody
    public List<Map<String, Object>> queryMsgType(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        return xcmgProjectMsgManager.queryMsgType();
    }

    @RequestMapping("queryRespProjects")
    @ResponseBody
    public List<Map<String, Object>> queryRespProjects(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        return xcmgProjectMsgManager.queryRespProjects();
    }

}
