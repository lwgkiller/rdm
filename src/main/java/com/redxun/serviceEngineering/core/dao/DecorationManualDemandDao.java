package com.redxun.serviceEngineering.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface DecorationManualDemandDao {
    //..
    List<Map<String, Object>> dataListQuery(Map<String, Object> params);

    //..
    Integer countDataListQuery(Map<String, Object> params);

    //..
    List<JSONObject> queryFileList(Map<String, Object> param);

    //..
    void deleteBusinessFile(Map<String, Object> param);

    //..
    void deleteBusiness(Map<String, Object> param);

    //..
    void deleteItem(Map<String, Object> param);

    //..
    void updateItem(Map<String, Object> params);

    //..
    void insertItem(Map<String, Object> params);

    //..
    JSONObject queryDetailById(String businessId);

    //..
    JSONObject queryItemDetailById(String businessId);

    //..
    List<JSONObject> queryItemList(JSONObject params);

    //..
    void insertBusiness(JSONObject formData);

    //..
    void updateBusiness(JSONObject formData);

    //..
    void addFileInfos(JSONObject param);


}
