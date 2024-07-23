package com.redxun.rdmZhgl.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface RyydDao {
    List<JSONObject> queryRyyd(Map<String, Object> params);


    List<JSONObject> queryRyydDetail(Map<String, Object> params);


    void createRyyd(JSONObject messageObj);


    void updateRyyd(JSONObject messageObj);


    void createRyydDetail(JSONObject messageObj);


    void updateRyydDetail(JSONObject messageObj);



    int countRyydList(Map<String, Object> param);



    void deleteRyydDetail(Map<String, Object> param);


    void deleteRyyd(Map<String, Object> param);


    JSONObject queryRyydById(String zId);


    List<JSONObject> queryTitle();
}
