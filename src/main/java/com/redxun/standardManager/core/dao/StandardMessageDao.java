package com.redxun.standardManager.core.dao;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

@Repository
public interface StandardMessageDao {
    List<Map<String, Object>> queryStandardMsgList(Map<String, Object> params);
    //改良后排除消息重复错误
    List<Map<String, Object>> queryStandardMsgListWithJoin(Map<String, Object> params);

    void insertStandardMsg(JSONObject messageObj);

    void updateStandardMsg(JSONObject messageObj);

    void deleteStandardMsg(List<String> ids);

    void deleteSendList(List<String> ids);

    List<Map<String, Object>> getStandardMsgByUserId(String userId);

    List<String> getUserIdsByDeptId(Map<String, Object> map);

    //排错
    List<JSONObject> whatTheHellItIs(String fullName);
    //改良后测试没问题
    List<JSONObject> whatTheHellItIsWithJoin(String fullName);
    List<JSONObject> queryWhatTheHellItIsUser();
}
