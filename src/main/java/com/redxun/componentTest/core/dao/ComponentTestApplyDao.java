package com.redxun.componentTest.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ComponentTestApplyDao {
    //..
    List<JSONObject> dataListQuery(Map<String, Object> params);

    //..
    Integer countDataListQuery(Map<String, Object> params);

    //..
    JSONObject queryDataById(String businessId);

    //..
    void insertBusiness(Map<String, Object> params);

    //..
    void updateBusiness(Map<String, Object> params);
}
