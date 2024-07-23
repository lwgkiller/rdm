package com.redxun.bjjszc.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface GjbjDao {
    List<JSONObject> queryGjbj(Map<String, Object> params);

    List<JSONObject> queryGjbjDetail(Map<String, Object> params);

    void createGjbj(JSONObject messageObj);

    void updateGjbj(JSONObject messageObj);

    void updateApplyNumber(JSONObject messageObj);

    JSONObject queryGjbjById(String gjbjId);

    int countGjbjList(Map<String, Object> param);

    void deleteGjbj(Map<String, Object> param);

    void createDetail(JSONObject messageObj);

    void updateDetail(JSONObject messageObj);

    void deleteDetail(Map<String, Object> param);

}
