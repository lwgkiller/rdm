package com.redxun.standardManager.core.manager;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.standardManager.core.dao.StandardMessageDao;
import com.redxun.standardManager.core.dao.StandardMessageSendListDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import groovy.util.logging.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author zhangzhen
 */
@Service
@Slf4j
public class StandardMessageManager {
    private static Logger logger = LoggerFactory.getLogger(StandardMessageManager.class);
    @Resource
    private StandardMessageDao standardMessageDao;
    @Resource
    private StandardMessageSendListDao standardMessageSendListDao;
    @Resource
    private SendDDNoticeManager sendDDNoticeManager;

    // 查询标准管理的消息列表 todo:???
    public JsonPageResult<?> queryStandardMsgList(Map<String, Object> params, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        //原有方法暂时保留
//        List<Map<String, Object>> msgInfos = standardMessageDao.queryStandardMsgList(params);
        //改良后排除消息重复错误
        List<Map<String, Object>> msgInfos = standardMessageDao.queryStandardMsgListWithJoin(params);
        if (msgInfos != null && !msgInfos.isEmpty()) {
            for (Map<String, Object> oneMsg : msgInfos) {
                if (oneMsg.get("CREATE_TIME_") != null) {
                    oneMsg.put("CREATE_TIME_",
                            DateUtil.formatDate((Date) oneMsg.get("CREATE_TIME_"), "yyyy-MM-dd"));
                }
            }
        }
        result.setData(msgInfos);
        if (doPage) {
            params.remove("startIndex");
            params.remove("pageSize");
            List<Map<String, Object>> totalInfos = standardMessageDao.queryStandardMsgListWithJoin(params);
            result.setTotal(totalInfos.size());
        }
        return result;
    }

    // 发布消息
    public void sendMessage(String requestBody, JSONObject result) {
        try {
            JSONObject requestJSONObject = JSONObject.parseObject(requestBody);
            if (requestJSONObject == null || requestJSONObject.isEmpty()) {
                logger.error("消息体为空");
                result.put("message", "通知发布失败，内容为空！");
                return;
            }
            String recUserIds = "";
            requestJSONObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            requestJSONObject.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            requestJSONObject.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            if ("徐工挖机".equals(requestJSONObject.getString("receiveDeptNames"))) {
                requestJSONObject.put("receiveDeptNames", "");
                requestJSONObject.put("receiveDeptIds", "");
            }
            if (StringUtils.isBlank(requestJSONObject.getString("id"))) {
                String msgId = IdUtil.getId();
                requestJSONObject.put("id", msgId);
                String receiveDeptIds = requestJSONObject.getString("receiveDeptIds");
                if (StringUtil.isNotEmpty(receiveDeptIds)) {
                    recUserIds = batchInsertReceiver(receiveDeptIds, msgId);
                    requestJSONObject.put("isSelf", "1");
                } else {
                    requestJSONObject.put("isSelf", "0");
                }
                standardMessageDao.insertStandardMsg(requestJSONObject);
            } else {
                String msgId = requestJSONObject.getString("id");
                String receiveDeptIds = requestJSONObject.getString("receiveDeptIds");
                if (StringUtil.isNotEmpty(receiveDeptIds)) {
                    recUserIds = updateReceiverList(msgId, receiveDeptIds);
                    requestJSONObject.put("isSelf", "1");
                } else {
                    standardMessageSendListDao.deleteReceiverByMsgId(msgId);
                    requestJSONObject.put("isSelf", "0");
                }
                standardMessageDao.updateStandardMsg(requestJSONObject);
            }
            if (StringUtil.isNotEmpty(recUserIds)) {
                String plainTxt = requestJSONObject.getString("standardMsgContent");
                String sendUserName = ContextUtil.getCurrentUser().getFullname();
                String sendMsg = "【标准管理】" + plainTxt + "发布人：" + sendUserName + " 发布时间："
                        + XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss");
                JSONObject taskJson = new JSONObject();
                taskJson.put("content", sendMsg);
                sendDDNoticeManager.sendNoticeForCommon(recUserIds, taskJson);
            }
        } catch (Exception e) {
            logger.error("发送消息失败", e);
        }

        result.put("message", "通知发布成功");
    }

    public JSONObject getReceiverIds(String msgId) {
        Map<String, Object> map = new HashMap<>(16);
        map.put("messageId", msgId);
        List<Map<String, Object>> list = standardMessageSendListDao.queryMsgReceiverList(map);
        String receiverIds = "";
        String recUserNames = "";
        if (list != null && list.size() > 0) {
            for (Map<String, Object> temp : list) {
                receiverIds += temp.get("recId").toString() + ",";
                recUserNames += temp.get("receiver").toString() + ",";
            }
            if (receiverIds.length() > 1) {
                receiverIds = receiverIds.substring(0, receiverIds.length() - 1);
                recUserNames = recUserNames.substring(0, recUserNames.length() - 1);
            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("receiverIds", receiverIds);
        jsonObject.put("recUserNames", recUserNames);
        return jsonObject;
    }

    public String batchInsertReceiver(String recDeptIds, String msgId) {
        String[] recDeptIdArray = recDeptIds.split(",");
        List<String> deptList = Arrays.asList(recDeptIdArray);
        Map<String, Object> paramMap = new HashMap<>(16);
        paramMap.put("list", deptList);
        List<String> userList = standardMessageDao.getUserIdsByDeptId(paramMap);

        Map<String, Object> receiverObj = null;
        List<Map<String, Object>> list = new ArrayList<>();
        for (String temp : userList) {
            receiverObj = new HashMap<>(16);
            receiverObj.put("id", IdUtil.getId());
            receiverObj.put("messageId", msgId);
            receiverObj.put("recId", temp);
            receiverObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            receiverObj.put("CREATE_TIME_", new Date());
            receiverObj.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            list.add(receiverObj);
        }
        standardMessageSendListDao.batchInsertReceiver(list);
        return String.join(",", userList);
    }

    // 如果是更新的话，则把结束人全部删除，重新插入
    public String updateReceiverList(String msgId, String recDeptIds) {
        standardMessageSendListDao.deleteReceiverByMsgId(msgId);
        return batchInsertReceiver(recDeptIds, msgId);
    }

    // 删除消息
    public void deleteStandardMsg(JSONObject result, String standardMsgIds) {
        try {
            Map<String, Object> param = new HashMap<>();
            String[] idArr = standardMsgIds.split(",", -1);
            List<String> idList = Arrays.asList(idArr);
            standardMessageDao.deleteStandardMsg(idList);
            standardMessageDao.deleteSendList(idList);
            result.put("message", "删除成功！");
        } catch (Exception e) {
            logger.error("Exception in deleteStandardMsg", e);
            result.put("message", "删除失败，系统异常！");
        }
    }

    public List<Map<String, Object>> getStandardMsgByUserId(String userId) {
        return standardMessageDao.getStandardMsgByUserId(userId);
    }

    public void setMsgRead(String msgId) {
        JSONObject paramJson = new JSONObject();
        paramJson.put("id", msgId);
        paramJson.put("isRead", "1");
        paramJson.put("readTime", new Date());
        paramJson.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        paramJson.put("UPDATE_TIME_", new Date());
        standardMessageSendListDao.updateMsgStatus(paramJson);
    }

    //排错
    public void whatTheHellItIs() {
        List<JSONObject> list = standardMessageDao.queryWhatTheHellItIsUser();
        for (JSONObject recId : list) {
            System.out.println("当前处理用户的ID：" + recId.getString("recId") + "注意了！！！");
            //List<JSONObject> list2 = standardMessageDao.whatTheHellItIs(recId.getString("recId"));
            List<JSONObject> list2 = standardMessageDao.whatTheHellItIsWithJoin(recId.getString("recId"));
        }
        System.out.println("------------成功结束，没有错误！！！----------------");
    }
}
