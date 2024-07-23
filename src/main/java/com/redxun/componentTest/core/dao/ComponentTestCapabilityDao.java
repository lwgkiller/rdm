package com.redxun.componentTest.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComponentTestCapabilityDao {
    //..
    List<JSONObject> componentTestCapabilityItemListQuery(JSONObject params);

    //..
    int countComponentTestCapabilityItemListQuery(JSONObject params);

    //..
    List<JSONObject> componentTestCapabilityListQuery(JSONObject params);

    //..
    void createComponentTestCapability(JSONObject formDataJson);

    //..
    void updateComponentTestCapability(JSONObject formDataJson);

    //..
    JSONObject getComponentTestCapabilityById(String id);

    //..
    void deleteComponentTestCapability(String path);

    //..
    List<JSONObject> selectComponentTestCapabilityByPath(String path);

    //..
    void createComponentTestCapabilityItem(JSONObject jsonObject);

    //..
    void updateComponentTestCapabilityItem(JSONObject jsonObject);

    //..
    void deleteComponentTestCapabilityItem(JSONObject jsonObject);
}
