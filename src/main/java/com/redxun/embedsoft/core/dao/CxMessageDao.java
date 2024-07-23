package com.redxun.embedsoft.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface CxMessageDao {
    List<JSONObject> queryCxMsgList(Map<String, Object> map);

    int countCxMsgList(Map<String, Object> params);

    List<JSONObject> getCxMsgByUserId(String userId);

    /**
     * 根据msgId 和人员id更新阅读状态
     *
     * @param jsonObject
     */
    void updateReadStatus(JSONObject jsonObject);

}
