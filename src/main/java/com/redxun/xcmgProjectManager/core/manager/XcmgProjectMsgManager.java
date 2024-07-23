
package com.redxun.xcmgProjectManager.core.manager;

import java.util.*;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectMessageDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * <pre>
 * 描述：project_baseInfo 处理接口
 * 作者:x
 * 日期:2019-08-22 08:59:49
 * 版权：xx
 * </pre>
 */
@Service
public class XcmgProjectMsgManager {
    private Logger logger = LoggerFactory.getLogger(XcmgProjectMsgManager.class);
    @Resource
    private XcmgProjectMessageDao xcmgProjectMessageDao;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;

    public List<Map<String, Object>> querySendMsg(String userId) {
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        if (!ContextUtil.getCurrentUser().getUserNo().equalsIgnoreCase("admin")) {
            params.put("userId", ContextUtil.getCurrentUserId());
        }
        result = xcmgProjectMessageDao.querySendMsg(params);
        for (Map<String, Object> oneMap : result) {
            if (oneMap.get("CREATE_TIME_") != null) {
                oneMap.put("CREATE_TIME_",
                    DateUtil.formatDate((Date)oneMap.get("CREATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
            }
            String messageType = oneMap.get("messageType").toString();
            if (StringUtils.isNotBlank(messageType)) {
                if (messageType.equals("system")) {
                    oneMap.put("typeName", "平台消息");
                } else if (messageType.equals("group")) {
                    oneMap.put("typeName", "组消息");
                }
            }
        }

        return result;
    }

    // 生成id--name对应的关系
    private Map<String, String> convertList2Map(List<Map<String, Object>> infoList) {
        Map<String, String> result = new HashMap<>();
        for (Map<String, Object> oneMap : infoList) {
            result.put(oneMap.get("id").toString(), oneMap.get("name").toString());
        }
        return result;
    }

    /**
     * 查询自己能看到的系统消息
     * 
     * @param userId
     * @param recType
     * @param webappName
     * @return
     */
    public List<Map<String, Object>> queryRecMsg(String userId, String recType, String webappName) {
        // 先从message表中查询自己收到的消息，再从box表中查询哪些是已读的
        // 查询所属的组
        Set<String> depIds = new HashSet<>();
        List<JSONObject> currentUserDeps = xcmgProjectMessageDao.queryGroupIdsByUserId(userId);
        if (currentUserDeps != null && !currentUserDeps.isEmpty()) {
            for (JSONObject oneDept : currentUserDeps) {
                depIds.add(oneDept.getString("groupId"));
            }
        }

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        if (StringUtils.isNotBlank(webappName)) {
            params.put("appName", webappName);
        }
        if (StringUtils.isNotBlank(recType)) {
            params.put("recType", recType);
        }
        List<Map<String, Object>> recMsgInfos = xcmgProjectMessageDao.queryRecMsg(params);
        if (recMsgInfos == null || recMsgInfos.isEmpty()) {
            return recMsgInfos;
        }
        // 进一步过滤出最终的消息列表
        Iterator iterator = recMsgInfos.iterator();
        while (iterator.hasNext()) {
            Map<String, Object> msgInfo = (Map<String, Object>)iterator.next();
            String messageTypeInfo = msgInfo.get("messageType").toString();
            // 平台消息
            if (RdmConst.MESSAGE_SYSTEM.equalsIgnoreCase(messageTypeInfo)) {
                continue;
            }
            Object recUserIdsObj = msgInfo.get("recUserIds");
            // 收件人包含自己
            if (recUserIdsObj != null && StringUtils.isNotBlank(recUserIdsObj.toString())) {
                List<String> recUserIdList = Arrays.asList(recUserIdsObj.toString().split(",", -1));
                if (recUserIdList.contains(userId)) {
                    continue;
                }
            }
            // 收件组中包含自己所在组
            Object recGroupIdsObj = msgInfo.get("recGroupIds");
            if (recGroupIdsObj != null && StringUtils.isNotBlank(recGroupIdsObj.toString())) {
                List<String> recGroupIdList = new ArrayList<>(Arrays.asList(recGroupIdsObj.toString().split(",", -1)));
                recGroupIdList.retainAll(depIds);
                if (!recGroupIdList.isEmpty()) {
                    continue;
                }
            }
            iterator.remove();
        }
        if (recMsgInfos.isEmpty()) {
            return recMsgInfos;
        }
        // 查询哪些消息已经读了
        Set<String> messageIds = new HashSet<>();
        for (Map<String, Object> oneMap : recMsgInfos) {
            if (oneMap.get("CREATE_TIME_") != null) {
                oneMap.put("CREATE_TIME_",
                    DateUtil.formatDate((Date)oneMap.get("CREATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
            }
            oneMap.put("status", "未读");
            messageIds.add(oneMap.get("id").toString());
        }
        if (!messageIds.isEmpty()) {
            params.put("messageIds", messageIds);
            List<Map<String, Object>> queryHasReadMsg = xcmgProjectMessageDao.queryHasReadMsg(params);
            if (queryHasReadMsg != null && !queryHasReadMsg.isEmpty()) {
                Set<String> hasReadMsgIds = new HashSet<>();
                for (Map<String, Object> oneMap : queryHasReadMsg) {
                    hasReadMsgIds.add(oneMap.get("messageId").toString());
                }
                List<Map<String, Object>> noReadMsgs = new ArrayList<>();
                List<Map<String, Object>> readMsgs = new ArrayList<>();
                for (Map<String, Object> oneMap : recMsgInfos) {
                    if (hasReadMsgIds.contains(oneMap.get("id").toString())) {
                        oneMap.put("status", "已读");
                        readMsgs.add(oneMap);
                    } else {
                        noReadMsgs.add(oneMap);
                    }
                }
                // 未读的放上面
                noReadMsgs.addAll(readMsgs);
                return noReadMsgs;
            } else {
                return recMsgInfos;
            }
        }
        return recMsgInfos;
    }

    // 发送消息，用于页面主动发送的消息
    public void sendMsg(String postData) {
        try {
            if (StringUtils.isBlank(postData)) {
                logger.error("postData is blank");
                return;
            }
            // 保存到消息表
            String currentUserId = ContextUtil.getCurrentUserId();
            JSONObject postDataObj = JSONObject.parseObject(postData);
            postDataObj.put("id", IdUtil.getId());
            postDataObj.put("CREATE_BY_", StringUtils.isBlank(currentUserId) ? "1" : currentUserId);
            postDataObj.put("CREATE_TIME_", new Date());
            if (StringUtils.isBlank(postDataObj.getString("expireTime"))) {
                postDataObj.put("expireTime", null);
            } else {
                String expireTime =
                    DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(postDataObj.getString("expireTime")), -8),
                        "yyyy-MM-dd HH:mm:ss");
                postDataObj.put("expireTime", expireTime);
            }
            xcmgProjectMessageDao.insertToProjectMessage(postDataObj);
            // 发送消息到钉钉
            if (postDataObj.getString("recType").contains(RdmConst.RECTYPE_DINGDING)) {
                String finalUserIdStr =
                    finalSendDDUserIdStr(postDataObj.getString("recUserIds"), postDataObj.getString("recGroupIds"));
                if (StringUtils.isBlank(finalUserIdStr)) {
                    return;
                }
                String title = postDataObj.getString("title");
                String plainTxt = postDataObj.getString("plainTxt");
                String sendUserName = ContextUtil.getCurrentUser().getFullname();
                String sendMsg = "【RDM平台消息】标题：" + title + "，内容：" + plainTxt + "。发送人：" + sendUserName;
                JSONObject taskJson = new JSONObject();
                taskJson.put("content", sendMsg);
                sendDDNoticeManager.sendNoticeForCommon(finalUserIdStr, taskJson);
            }
        } catch (Exception e) {
            logger.error("Exception in sendMsg", e);
        }

    }

    // 通过收件人和收件组，得到最终的发送人员id
    private String finalSendDDUserIdStr(String recUserIds, String recGroupIds) {
        if (StringUtils.isBlank(recGroupIds)) {
            return recUserIds;
        }
        Map<String, Object> params = new HashMap<>();
        Set<String> groupIdSet = new HashSet<>(Arrays.asList(recGroupIds.split(",", -1)));
        params.put("groupIds", new ArrayList<>(groupIdSet));
        List<JSONObject> userInfos = xcmgProjectMessageDao.queryUserIdsByGroupIds(params);
        Set<String> finalUserIds = new HashSet<>(Arrays.asList(recUserIds.split(",", -1)));
        for (JSONObject oneUser : userInfos) {
            finalUserIds.add(oneUser.getString("userId"));
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String userId : finalUserIds) {
            stringBuilder.append(userId).append(",");
        }
        if (stringBuilder.length() > 0) {
            return stringBuilder.substring(0, stringBuilder.length() - 1);
        }
        return "";
    }

    // 保存已读信息到box
    public void setMsgRead(String messageId) {
        if (StringUtils.isBlank(messageId)) {
            logger.error("messageId is blank");
            return;
        }
        JSONObject readMessage = new JSONObject();
        readMessage.put("id", IdUtil.getId());
        readMessage.put("messageId", messageId);
        readMessage.put("readUserId", ContextUtil.getCurrentUserId());
        readMessage.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        readMessage.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        readMessage.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        xcmgProjectMessageDao.insertToProjectMessageBox(readMessage);
    }

    public List<Map<String, Object>> queryMsgType() {
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> messageTypes = xcmgProjectMessageDao.queryMsgType(params);
        return messageTypes;
    }

    public List<Map<String, Object>> queryRespProjects() {
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("userId", ContextUtil.getCurrentUserId());

        List<Map<String, Object>> messageTypes = xcmgProjectMessageDao.queryRespProjects(params);
        return messageTypes;
    }

    public Map<String, String> queryMsgDetailById(String id) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        Map<String, String> messageDetail = xcmgProjectMessageDao.queryMsgDetailById(params);
        return messageDetail;
    }
}
