package com.redxun.zlgjNPI.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ZlgjApplyForExtensionDao {
    //..
    List<JSONObject> queryApply(Map<String, Object> params);

    //..
    void insertApply(JSONObject messageObj);

    //..
    void updateApply(JSONObject messageObj);

    //..
    JSONObject queryApplyById(String businessId);

    //..
    void deleteApply(Map<String, Object> param);
}
