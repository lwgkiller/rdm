package com.redxun.serviceEngineering.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface MixedinputDao {
    //..
    List<Map<String, Object>> filingDataListQuery(Map<String, Object> params);

    //..
    Integer countFilingDataListQuery(Map<String, Object> params);

    //..
    List<Map<String, Object>> discardDataListQuery(Map<String, Object> params);

    //..
    Integer countDiscardDataListQuery(Map<String, Object> params);

    //..
    List<JSONObject> masterdataListQuery(Map<String, Object> params);

    //..
    Integer countMasterdataListQuery(Map<String, Object> params);

    //..
    void deleteBusinessFiling(Map<String, Object> param);

    //..
    void deleteBusinessDiscard(Map<String, Object> param);

    //..
    void deleteBusinessMasterdata(Map<String, Object> param);

    //..
    void discardBusinessMasterdata(Map<String, Object> param);

    //..
    JSONObject queryFilingDetailById(String businessId);

    //..
    JSONObject queryDiscardDetailById(String businessId);

    //..
    JSONObject queryMasterdataDetailById(String businessId);

    //..
    void insertBusinessFiling(JSONObject formData);

    //..
    void updateBusinessFiling(JSONObject formData);

    //..
    void insertBusinessDiscard(JSONObject formData);

    //..
    void updateBusinessDiscard(JSONObject formData);

    //..
    void insertBusinessMasterData(JSONObject formData);
}
