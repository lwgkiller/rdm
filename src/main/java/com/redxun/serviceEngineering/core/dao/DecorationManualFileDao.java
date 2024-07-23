package com.redxun.serviceEngineering.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface DecorationManualFileDao {
    //..
    List<JSONObject> dataListQuery(Map<String, Object> params);

    //..
    Integer countDataListQuery(Map<String, Object> params);

    //..
    JSONObject queryDataById(String id);

    //..
    void deleteBusiness(String id);

    //
    void deleteBusinessLogs(String id);

    //..
    void insertBusiness(Map<String, Object> objBody);

    //..
    void updateBusiness(Map<String, Object> objBody);
}
