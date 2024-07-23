package com.redxun.xcmgProjectManager.core.dao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface XcmgProjectMessageDao {
    List<Map<String, Object>> querySendMsg(Map<String, Object> param);

    List<Map<String, Object>> queryRecMsg(Map<String, Object> param);

    List<Map<String, Object>> queryHasReadMsg(Map<String, Object> param);

    void insertToProjectMessage(JSONObject jsonObject);

    void insertToProjectMessageBox(JSONObject jsonObject);

    List<Map<String, Object>> queryMsgType(Map<String, Object> param);

    List<Map<String, Object>> queryRespProjects(Map<String, Object> param);

    Map<String, String> queryMsgDetailById(Map<String, Object> params);

    List<JSONObject> queryUserIdsByGroupIds(Map<String, Object> params);

    List<JSONObject> queryGroupIdsByUserId(String userId);
}
