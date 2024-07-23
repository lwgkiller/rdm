package com.redxun.serviceEngineering.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface StandardvalueShipmentnotmadeDao {
    //..
    List<JSONObject> dataListQuery(Map<String, Object> params);

    //..
    Integer countDataListQuery(Map<String, Object> params);

    //..
    void deleteData(Map<String, Object> param);

    //..
    void insertData(Map<String, Object> param);

    //..
    void updateData(JSONObject jsonObject);

    JSONObject queryById(String id);

    JSONObject getGroupById(JSONObject jsonObject);

    JSONObject queryByMaterialCode(JSONObject formDataJson);

    JSONObject queryUserByFullName(String fullName);

    List<JSONObject> queryByResponseTime();

    List<JSONObject> queryNeedTodoByUserId(Map<String, Object> param);
}
