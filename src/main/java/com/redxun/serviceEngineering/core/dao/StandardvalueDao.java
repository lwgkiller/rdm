package com.redxun.serviceEngineering.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface StandardvalueDao {
    //..
    List<JSONObject> dataListQuery(Map<String, Object> params);

    //..
    Integer countDataListQuery(Map<String, Object> params);

    //..
    void deleteData(Map<String, Object> param);

    //..
    void insertData(JSONObject jsonObject);

    //..
    void updateData(JSONObject jsonObject);

    //..根据action获取各版总数
    Integer getStandardvalueTotal(JSONObject postDataJson);

    //..根据action获取各类回传组数
    Integer getStandardvalueReturnedGroups(JSONObject postDataJson);
}
