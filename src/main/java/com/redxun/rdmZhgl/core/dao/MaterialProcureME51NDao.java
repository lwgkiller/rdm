package com.redxun.rdmZhgl.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface MaterialProcureME51NDao {
    //..
    List<JSONObject> queryList(Map<String, Object> params);

    //..
    int countQueryList(Map<String, Object> params);

    //..
    List<JSONObject> queryListItems(Map<String, Object> params);

    //..
    List<JSONObject> getItemList(Map<String, Object> params);

    //..
    JSONObject getDetail(String id);

    //..
    void insertApply(JSONObject jsonObject);

    //..
    void updateApply(JSONObject jsonObject);

    //..
    void deleteApply(String id);

    //..
    void insertItem(JSONObject jsonObject);

    //..
    void updateItem(JSONObject jsonObject);

    //..
    void deleteItem(String id);
}
