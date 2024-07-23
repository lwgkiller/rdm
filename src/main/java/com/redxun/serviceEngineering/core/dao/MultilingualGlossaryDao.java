package com.redxun.serviceEngineering.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface MultilingualGlossaryDao {
    //..
    List<Map<String, Object>> dataListQuery(Map<String, Object> params);

    //..
    Integer countDataListQuery(Map<String, Object> params);

    //..
    List<Map<String, Object>> glossaryListQuery(Map<String, Object> params);

    //..
    Integer countGlossaryListQuery(Map<String, Object> params);

    //..
    void deleteBusiness(Map<String, Object> param);

    //..
    void deleteItem(Map<String, Object> param);

    //..
    JSONObject queryDetailById(String businessId);

    //..
    List<JSONObject> queryItemList(String businessId);

    //..
    List<JSONObject> queryItemListByIds(Map<String, Object> param);

    //..
    List<JSONObject> queryGlossaryListByMaterialCodes(Map<String, Object> param);

    //..
    void insertBusiness(JSONObject formData);

    //..
    void updateBusiness(JSONObject formData);

    //..
    void insertItem(JSONObject itemObj);

    //..
    void updateItem(JSONObject itemObj);

    //..
    void insertGlossary(JSONObject glossaryData);

    //..
    void updateGlossary(JSONObject glossaryData);
}
