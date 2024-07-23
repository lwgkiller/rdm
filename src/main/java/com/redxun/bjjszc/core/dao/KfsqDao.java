package com.redxun.bjjszc.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface KfsqDao {
    List<JSONObject> queryKfsq(Map<String, Object> params);

    List<JSONObject> queryGcs();


    List<JSONObject> queryKfsqDetail(Map<String, Object> params);

    void createKfsq(JSONObject messageObj);

    void updateKfsq(JSONObject messageObj);

    void createGcs(JSONObject messageObj);


    void updateGcs(JSONObject messageObj);


    void deleteGcs(Map<String, Object> param);


    JSONObject queryKfsqById(String kfsqId);

    int countKfsqList(Map<String, Object> param);

    JSONObject getUserInfoByBj(Map<String, Object> param);

    void deleteKfsq(Map<String, Object> param);

    void createDetail(JSONObject messageObj);

    void updateDetail(JSONObject messageObj);

    void deleteDetail(Map<String, Object> param);

}
