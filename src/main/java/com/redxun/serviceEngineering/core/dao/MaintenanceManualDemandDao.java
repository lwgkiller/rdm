package com.redxun.serviceEngineering.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface MaintenanceManualDemandDao {
    //..
    List<Map<String, Object>> dataListQuery(Map<String, Object> params);

    //..
    Integer countDataListQuery(Map<String, Object> params);

    //..
    List<Map<String, Object>> dataListQueryExact(Map<String, Object> params);

    //..
    List<Map<String, Object>> dataListQueryExactByDemandListNo(Map<String, Object> params);

    //..
    List<JSONObject> queryFileList(Map<String, Object> param);

    //..
    void deleteBusinessFile(Map<String, Object> param);

    //..
    void deleteBusiness(Map<String, Object> param);

    //..
    JSONObject queryDetailById(String businessId);

    //..
    void insertBusiness(JSONObject formData);

    //..
    void updateBusiness(JSONObject formData);

    //..
    void addFileInfos(JSONObject param);

    //..
    List<JSONObject> queryCollect(Map<String, Object> params);

    //..
    void createCollect(JSONObject messageObj);

    //..
    void deleteCollect(Map<String, Object> param);

    //..获取匹配明细
    List<JSONObject> getManualMatchList(JSONObject params);

    //..
    void insertManualMatch(JSONObject oneObject);

    //..
    void deleteManualMatch(Map<String, Object> param);

    //..
    void updateManualMatchRefId(JSONObject oneObject);
}
