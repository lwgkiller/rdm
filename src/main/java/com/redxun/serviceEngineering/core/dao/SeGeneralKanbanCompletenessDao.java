package com.redxun.serviceEngineering.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface SeGeneralKanbanCompletenessDao {
    //..
    List<JSONObject> dataListQuery(Map<String, Object> params);

    //..
    Integer countDataListQuery(Map<String, Object> params);

    //..
    List<JSONObject> dataListQuery2(Map<String, Object> params);

    //..
    Integer countDataListQuery2(Map<String, Object> params);

    //..
    List<JSONObject> getListByMaterialCode(String materialCode);

    //..
    void deleteData(Map<String, Object> param);

    //..
    void insertData(Map<String, Object> params);

    //..
    void updateData(Map<String, Object> params);

    //..
    void updateDataByMaterialCode(Map<String, Object> params);

    //..
    void updateDataByMaterialCodeAndSingYear(Map<String, Object> params);
}
