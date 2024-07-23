package com.redxun.serviceEngineering.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface MaintenanceManualDao {
    //..
    List<JSONObject> dataListQuery(Map<String, Object> params);

    //..
    Integer countDataListQuery(Map<String, Object> params);

    //..
    void deleteData(Map<String, Object> param);

    //..
    List<JSONObject> getListByCode(String materialCode);

    //..
    void insertData(Map<String, Object> params);

    //..
    void updateData(Map<String, Object> params);

    //..
    void updateDataByCode(Map<String, Object> params);

    //..
    JSONObject getGroupByName(String depname);

    //..操保手册制作总数
    Integer getMaintenanceManualTotal();
}
