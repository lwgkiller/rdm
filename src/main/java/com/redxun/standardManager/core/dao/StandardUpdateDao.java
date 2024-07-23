package com.redxun.standardManager.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface StandardUpdateDao {
    List<JSONObject> queryGzjl(Map<String, Object> params);

    List<JSONObject> queryGzjlDetail(Map<String, Object> params);

    void createGzjl(JSONObject messageObj);

    void updateGzjl(JSONObject messageObj);

    JSONObject queryGzjlById(String qbgzId);

    int countGzjlList(Map<String, Object> param);

    void deleteGzjl(Map<String, Object> param);

    void createGzjlDetail(JSONObject messageObj);

    void updateGzjlDetail(JSONObject messageObj);

    void deleteGzjlDetail(Map<String, Object> param);

}
