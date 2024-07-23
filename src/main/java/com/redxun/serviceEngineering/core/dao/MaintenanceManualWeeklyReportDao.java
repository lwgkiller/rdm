package com.redxun.serviceEngineering.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface MaintenanceManualWeeklyReportDao {
    //..
    JSONObject queryWeeklyReportById(String id);

    //..
    List<JSONObject> dataListQuery(Map<String, Object> params);

    //..
    int countDataListQuery(Map<String, Object> params);

    //..
    void delete(String id);

    //..
    void insert(Map<String, Object> objBody);

    //..
    void update(Map<String, Object> objBody);
}
