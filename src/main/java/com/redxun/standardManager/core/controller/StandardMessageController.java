package com.redxun.standardManager.core.controller;

import java.util.Date;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.query.Page;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.standardManager.core.dao.StandardDao;
import com.redxun.standardManager.core.dao.StandardMessageSendListDao;
import com.redxun.standardManager.core.manager.StandardMessageManager;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.standardManager.core.util.StandardConstant;
import com.redxun.standardManager.core.util.StandardManagerUtil;
import com.redxun.sys.org.entity.OsGroup;
import com.redxun.sys.org.manager.OsGroupManager;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * 应用模块名称
 * <p>
 * 代码描述
 * <p>
 * Copyright: Copyright (C) 2019 XXX, Inc. All rights reserved.
 * <p>
 * Company: 徐工挖掘机械有限公司
 * <p>
 *
 * @author liangchuanjiang
 * @since 2019/12/14 10:21
 */
@Controller
@RequestMapping("/standardManager/core/standardMessage/")
public class StandardMessageController extends GenericController {
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private StandardMessageManager standardMessageManager;
    @Resource
    private OsGroupManager osGroupManager;
    @Autowired
    private StandardDao standardDao;
    @Resource
    private StandardMessageSendListDao standardMessageSendListDao;

    // 待办页面
    @RequestMapping("taskTodo")
    public ModelAndView taskTodo(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = getPathView(request);

        return mv;
    }

    // 消息管理主页面
    @RequestMapping("management")
    public ModelAndView msgManagement(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = getPathView(request);
        IUser currentUser = ContextUtil.getCurrentUser();
        Map<String, Object> params = new HashMap<>();
        params.put("userId", currentUser.getUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        mv.addObject("currentUserRoles", userRolesJsonArray);

        String webappName = WebAppUtil.getProperty("webappName", "rdm");
        String systemCategoryValue = StandardConstant.SYSTEMCATEGORY_GL;
        if ("rdm".equalsIgnoreCase(webappName) && !StandardManagerUtil.judgeGLNetwork(request)) {
            systemCategoryValue = StandardConstant.SYSTEMCATEGORY_JS;
        }
        mv.addObject("systemCategoryValue", systemCategoryValue);
        return mv;
    }

    // 查看消息页面
    @RequestMapping("see")
    public ModelAndView msgSee(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = getPathView(request);
        String id = RequestUtil.getString(request, "id");
        Map<String, Object> oneMessage = null;
        if (StringUtils.isNotBlank(id)) {
            Map<String, Object> params = new HashMap<>();
            params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            params.put("id", id);
            params.put("userId", ContextUtil.getCurrentUserId());
            //todo:mark3
            JsonPageResult queryResult = standardMessageManager.queryStandardMsgList(params, false);
            List<Map<String, Object>> messageInfos = queryResult.getData();
            if (messageInfos != null && !messageInfos.isEmpty()) {
                oneMessage = messageInfos.get(0);
                String userDepFullName = "";
                // 查找创建人的部门
                OsGroup mainDep = osGroupManager.getMainDeps(oneMessage.get("CREATE_BY_").toString(),
                    ContextUtil.getCurrentTenantId());
                if (mainDep != null) {
                    // 获取部门的全路径
                    String fullName = osGroupManager.getGroupFullPathNames(mainDep.getGroupId());
                    if (fullName.contains("-")) {
                        String[] fullNames = fullName.split("[-]");
                        for (int i = 1; i < fullNames.length; i++) {
                            if (StringUtil.isEmpty(userDepFullName)) {
                                userDepFullName = fullNames[i];
                            } else {
                                userDepFullName += "-" + fullNames[i];
                            }
                        }
                    } else {
                        userDepFullName = fullName;
                    }
                }
                oneMessage.put("creatorDepFullName", userDepFullName);
                if ("1".equals(CommonFuns.nullToString(oneMessage.get("isSelf")))) {
                    // 是否私发
                    if ("0".equals(CommonFuns.nullToString(oneMessage.get("isRead")))) {
                        // 是否未读
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("isRead", "1");
                        jsonObject.put("readTime", new Date());
                        jsonObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                        jsonObject.put("UPDATE_TIME_", new Date());
                        jsonObject.put("messageId", CommonFuns.nullToString(oneMessage.get("id")));
                        jsonObject.put("userId", ContextUtil.getCurrentUserId());
                        standardMessageSendListDao.updateReadStatus(jsonObject);
                    } else if (oneMessage.get("isRead") == null
                        || StringUtils.isBlank(oneMessage.get("isRead").toString())) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id", IdUtil.getId());
                        jsonObject.put("messageId", id);
                        jsonObject.put("recId", ContextUtil.getCurrentUserId());
                        jsonObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                        jsonObject.put("CREATE_TIME_", new Date());
                        jsonObject.put("TENANT_ID_", ContextUtil.getTenant().getTenantId());
                        jsonObject.put("isRead", "1");
                        jsonObject.put("readTime", new Date());
                        jsonObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                        jsonObject.put("UPDATE_TIME_", new Date());
                        standardMessageSendListDao.addReadInfo(jsonObject);
                    }
                } else {
                    // 查看时候看看是否已经存在了，存在了就不重复插入数据
                    String messageId = CommonFuns.nullToString(oneMessage.get("id"));
                    String recId = ContextUtil.getCurrentUserId();
                    JSONObject paramJson = new JSONObject();
                    paramJson.put("messageId", messageId);
                    paramJson.put("recId", recId);
                    JSONObject resultJson = standardMessageSendListDao.getReadInfoByUserIdAndMsgId(paramJson);
                    if (resultJson == null) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id", IdUtil.getId());
                        jsonObject.put("messageId", messageId);
                        jsonObject.put("recId", recId);
                        jsonObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                        jsonObject.put("CREATE_TIME_", new Date());
                        jsonObject.put("TENANT_ID_", ContextUtil.getTenant().getTenantId());
                        jsonObject.put("isRead", "1");
                        jsonObject.put("readTime", new Date());
                        jsonObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                        jsonObject.put("UPDATE_TIME_", new Date());
                        standardMessageSendListDao.addReadInfo(jsonObject);
                    }
                }
            }
        }
        JSONObject messageJOSNObject = XcmgProjectUtil.convertMap2JsonObject(oneMessage);
        mv.addObject("messageObj", messageJOSNObject);
        String isPublicScene = messageJOSNObject.getString("isPublicScene");
        // 如果有关联的标准，则查询用于超链接跳转的信息
        String standardId = RequestUtil.getString(request, "standardId");
        JSONObject standardObj = new JSONObject();
        if (StringUtils.isNotBlank(standardId)) {
            if (!"yes".equalsIgnoreCase(isPublicScene)) {
                JSONObject queryStandardObj = standardDao.queryStandardById(standardId);
                if (queryStandardObj != null) {
                    standardObj = queryStandardObj;
                }
            } else {
                Map<String, Object> param = new HashMap<>();
                param.put("id", standardId);
                Map<String, Object> queryStandardObj = standardDao.queryPublicStandard(param);
                if (queryStandardObj != null) {
                    standardObj = XcmgProjectUtil.convertMap2JsonObject(queryStandardObj);
                    standardObj.put("isPublicScene", "yes");
                }
            }
        }

        mv.addObject("standardObj", standardObj);
        return mv;
    }

    // 编辑消息页面
    @RequestMapping("edit")
    public ModelAndView msgEdit(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = getPathView(request);
        // 查询关联的标准信息
        String standardId = RequestUtil.getString(request, "standardId");
        String scene = RequestUtil.getString(request, "scene");
        String standardName = "";
        String standardNumber = "";
        if (StringUtils.isNotBlank(standardId)) {
            if ("public".equalsIgnoreCase(scene)) {
                Map<String, Object> param = new HashMap<>();
                param.put("id", standardId);
                Map<String, Object> queryStandardObj = standardDao.queryPublicStandard(param);
                if (queryStandardObj == null) {
                    queryStandardObj = new HashMap<>();
                }
                mv.addObject("standardObj", XcmgProjectUtil.convertMap2JsonObject(queryStandardObj));
                standardName = queryStandardObj.get("standardName").toString();
                standardNumber = queryStandardObj.get("standardNumber").toString();
            } else {
                JSONObject queryStandardObj = standardDao.queryStandardById(standardId);
                if (queryStandardObj == null) {
                    queryStandardObj = new JSONObject();
                }
                mv.addObject("standardObj", queryStandardObj);
                standardName = queryStandardObj.getString("standardName");
                standardNumber = queryStandardObj.getString("standardNumber");
            }
        } else {
            mv.addObject("standardObj", new JSONObject());
        }
        // 查询消息信息
        String messageId = RequestUtil.getString(request, "id");
        Map<String, Object> oneMessage = null;
        if (StringUtils.isNotBlank(messageId)) {
            Map<String, Object> params = new HashMap<>();
            params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            params.put("id", messageId);
            params.put("userId", ContextUtil.getCurrentUserId());
            //todo:mark2
            JsonPageResult queryResult = standardMessageManager.queryStandardMsgList(params, false);
            List<Map<String, Object>> messageInfos = queryResult.getData();
            if (messageInfos != null && !messageInfos.isEmpty()) {
                oneMessage = messageInfos.get(0);
                JSONObject receiverIds = standardMessageManager.getReceiverIds(oneMessage.get("id").toString());
                oneMessage.put("recUserIds", receiverIds.getString("receiverIds"));
                oneMessage.put("recUserNames", receiverIds.getString("recUserNames"));
            }
        }
        JSONObject messageJOSNObject = XcmgProjectUtil.convertMap2JsonObject(oneMessage);
        if (StringUtils.isNotBlank(standardName)) {
            if (StringUtils.isBlank(messageJOSNObject.getString("standardMsgTitle"))) {
                messageJOSNObject.put("standardMsgTitle", "《" + standardNumber + " " + standardName + "》已发布");
            }
            if (StringUtils.isBlank(messageJOSNObject.getString("standardMsgContent"))) {
                messageJOSNObject.put("standardMsgContent",
                    messageJOSNObject.getString("standardMsgTitle") + "，请大家认真学习，严格按照标准执行！");
            }
        }
        mv.addObject("messageObj", messageJOSNObject);

        boolean canEditStandard = true;
        String canEditStandardStr = RequestUtil.getString(request, "canEditStandard");
        if (StringUtils.isNotBlank(canEditStandardStr)) {
            canEditStandard = Boolean.parseBoolean(canEditStandardStr);
        }
        mv.addObject("canEditStandard", canEditStandard);
        mv.addObject("scene", scene);
        return mv;
    }

    // 查看消息列表
    @RequestMapping("queryMsgList")
    @ResponseBody
    public List<Map<String, Object>> queryMsgList(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("userId", ContextUtil.getCurrentUserId());
        params.put("systemCategoryId", RequestUtil.getString(request, "systemCategoryValue", ""));
        String pageIndex = RequestUtil.getString(request, "pageIndex", "0");
        String pageSize = RequestUtil.getString(request, "pageSize", String.valueOf(Page.DEFAULT_PAGE_SIZE));
        String startIndex = String.valueOf(Integer.parseInt(pageIndex) * Integer.parseInt(pageSize));
        params.put("startIndex", startIndex);
        params.put("pageSize", pageSize);
        params.put("standardName", RequestUtil.getString(request, "standardName", ""));
        //todo:mark4
        JsonPageResult queryResult = standardMessageManager.queryStandardMsgList(params, true);
        List<Map<String, Object>> msgList = queryResult.getData();
        return msgList;
    }

    // 发送消息
    @RequestMapping("send")
    @ResponseBody
    public JSONObject send(HttpServletRequest request, @RequestBody String requestBody, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(requestBody)) {
            logger.error("消息体为空");
            result.put("message", "发布失败，内容为空！");
            return result;
        }
        standardMessageManager.sendMessage(requestBody, result);
        return result;
    }

    // 删除消息
    @RequestMapping("delete")
    @ResponseBody
    public JSONObject deleteMsg(HttpServletRequest request, @RequestBody String requestBody,
        HttpServletResponse response) {
        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(requestBody)) {
            logger.error("requestBody is blank");
            result.put("message", "删除失败，消息体为空！");
            return result;
        }
        JSONObject requestBodyObj = JSONObject.parseObject(requestBody);
        if (StringUtils.isBlank(requestBodyObj.getString("ids"))) {
            logger.error("ids is blank");
            result.put("message", "删除失败，主键为空！");
            return result;
        }
        String standardMsgIds = requestBodyObj.getString("ids");
        standardMessageManager.deleteStandardMsg(result, standardMsgIds);
        return result;
    }

    // 消息状态为已读
    @RequestMapping("setToRead")
    @ResponseBody
    public void setToRead(HttpServletRequest request, HttpServletResponse response) throws Exception {
        standardMessageManager.setMsgRead(RequestUtil.getString(request, "messageId"));
    }

    @RequestMapping("/readStatusPage")
    public ModelAndView readStatusPage(HttpServletRequest request, HttpServletResponse response) {
        String msgId = RequestUtil.getString(request, "msgId");
        String isSelf = RequestUtil.getString(request, "isSelf");
        String jspPath = "standardManager/core/standardMessageReadStatusPage.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        List<Map<String, Object>> tempData = null;
        Map<String, Object> params = new HashMap<>(16);
        params.put("messageId", msgId);
        tempData = standardMessageSendListDao.queryMsgReceiverList(params);
        if (tempData != null && !tempData.isEmpty()) {
            for (Map<String, Object> oneMsg : tempData) {
                if (oneMsg.get("readTime") != null) {
                    oneMsg.put("readTime", DateUtil.formatDate((Date)oneMsg.get("readTime"), "yyyy-MM-dd HH:mm:ss"));
                }
            }
        }
        JSONArray jsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(tempData);
        mv.addObject("gridData", jsonArray);
        return mv;
    }
}
