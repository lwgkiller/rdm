package com.redxun.powerApplicationTechnology.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface RoadtestDao {
    //..
    List<JSONObject> dataListQuery(Map<String, Object> params);

    //..
    Integer countDataListQuery(Map<String, Object> params);

    //..
    List<String> getDailyIdList(String businessId);

    //..
    List<String> getTestdataIdList(String businessId);

    //..
    List<JSONObject> getTestdataFileList(Map<String, Object> param);

    //..
    void deleteBusiness(Map<String, Object> param);

    //..
    void deleteDaily(Map<String, Object> param);

    //..
    void deleteTestdata(Map<String, Object> param);

    //..
    void deleteTestdataFileinfo(Map<String, Object> param);

    //..
    void startBusiness(Map<String, Object> param);

    //..
    void closeBusiness(Map<String, Object> param);

    //..
    List<JSONObject> getDailyList(JSONObject params);

    //..
    List<JSONObject> getTestdataList(String businessId);

    //..
    JSONObject getDetailById(String businessId);

    //..
    void insertBusiness(JSONObject formData);

    //..
    void updateBusiness(JSONObject formData);

    //..
    void insertDaily(JSONObject dataObj);

    //..
    void updateDaily(JSONObject dataObj);

    //..
    JSONObject getTestdataDetailById(String businessId);

    //..
    void insertTestdata(JSONObject dataObj);

    //..
    void updateTestdata(JSONObject dataObj);

    //..
    void addTestdataFileInfos(JSONObject fileInfo);

    //..
    void insertGpscache(JSONObject dataObj);

    //..
    List<JSONObject> getGpscacheList(JSONObject params);

    //..
    void updateGpscache(JSONObject dataObj);
}
