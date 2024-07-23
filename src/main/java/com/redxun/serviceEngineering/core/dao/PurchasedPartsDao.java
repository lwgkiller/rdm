package com.redxun.serviceEngineering.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface PurchasedPartsDao {
    //..
    List<JSONObject> dataListQuery(Map<String, Object> params);

    //..
    Integer countDataListQuery(Map<String, Object> params);

    //..
    void deleteData(Map<String, Object> param);

    //..
    void insertData(JSONObject dataObj);

    //..
    void updateData(JSONObject dataObj);

    //..
    JSONObject queryDataById(String businessId);

    //..
    List<JSONObject> weeklyReportListQuery(Map<String, Object> params);

    //..
    int countWeeklyReportListQuery(Map<String, Object> params);

    //..
    JSONObject queryWeeklyReportById(String id);

    //..
    void deleteWeeklyReportById(String id);

    //..
    void insertWeeklyReport(Map<String, Object> objBody);

    //..
    void updateWeeklyReport(Map<String, Object> objBody);
}
