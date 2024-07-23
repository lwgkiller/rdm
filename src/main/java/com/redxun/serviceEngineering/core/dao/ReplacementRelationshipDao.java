package com.redxun.serviceEngineering.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ReplacementRelationshipDao {
    //..
    List<Map<String, Object>> dataListQuery(Map<String, Object> params);

    //..
    Integer countDataListQuery(Map<String, Object> params);

    //..
    List<JSONObject> masterdataListQuery(Map<String, Object> params);

    //..
    Integer countMasterdataListQuery(Map<String, Object> params);

    //..
    void deleteBusiness(Map<String, Object> param);

    //..
    void deleteBusinessMasterdata(Map<String, Object> param);

    //..
    JSONObject queryDetailById(String businessId);

    //..
    void insertBusiness(JSONObject formData);

    //..
    void updateBusiness(JSONObject formData);

    //..
    void insertBusinessMasterData(JSONObject formData);

    //..
    List<String> getDistinctGroupList(Map<String, Object> params);
}
