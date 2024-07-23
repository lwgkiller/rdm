package com.redxun.world.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CustomerVisitRecordDao {
    //..
    List<JSONObject> dataListQuery(Map<String, Object> params);

    //..
    Integer countDataListQuery(Map<String, Object> params);

    //..
    JSONObject queryDataById(String businessId);

    //..
    void deleteBusiness(Map<String, Object> params);

    //..
    void insertBusiness(Map<String, Object> params);

    //..
    void updateBusiness(Map<String, Object> params);
}
