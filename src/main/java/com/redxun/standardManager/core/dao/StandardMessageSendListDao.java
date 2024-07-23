package com.redxun.standardManager.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zhangzhen
 */
@Repository
public interface StandardMessageSendListDao {
    /**
     * 获取接收人员列表
     *
     * @param params
     * @return list
     * */
    List<Map<String, Object>> queryMsgReceiverList(Map<String, Object> params);

    /**
     * 更新阅读状态
     *
     * @param messageObj
     * @return list
     * */
    void updateMsgStatus(JSONObject messageObj);

    /**
     * 删除
     *
     * @param ids
     * @return list
     * */
    void deleteMsgReceiver(List<String> ids);

    /**
     * 批量插入接收人员
     *
     * @param map
     * @return list
     * */
    void batchInsertReceiver(List<Map<String,Object>> map);

    /**
     * 删除
     *
     * @param msgId
     * */
    void deleteReceiverByMsgId(String msgId);

    /**
     * 根据msgId 和人员id更新阅读状态
     *
     * @param jsonObject
     * */
    void updateReadStatus(JSONObject jsonObject);
    /**
     * 添加阅读情况
     *
     * @param jsonObject
     * */
    void addReadInfo(JSONObject jsonObject);

    /**
     * 根据用户id和消息id获取对象
     *
     * @param jsonObject
     * @return json
     * */
    JSONObject getReadInfoByUserIdAndMsgId(JSONObject jsonObject);
}
