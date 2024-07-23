package com.redxun.bjjszc.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface GyqrDao {
    List<JSONObject> queryGyqr(Map<String, Object> params);

    List<JSONObject> queryGyqrDetail(Map<String, Object> params);

    void createGyqr(JSONObject messageObj);

    void updateGyqr(JSONObject messageObj);

    JSONObject queryGyqrById(String gyqrId);

    int countGyqrList(Map<String, Object> param);

    void deleteGyqr(Map<String, Object> param);

    void createDetail(JSONObject messageObj);

    void updateDetail(JSONObject messageObj);

    void deleteDetail(Map<String, Object> param);
}
