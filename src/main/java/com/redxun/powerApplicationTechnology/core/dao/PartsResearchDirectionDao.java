package com.redxun.powerApplicationTechnology.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface PartsResearchDirectionDao {
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

    //
    Integer getChildrenCount(String id);

    //..
    List<JSONObject> queryFileList(Map<String, Object> param);

    //..
    List<JSONObject> queryItemList(String businessId);

    //..
    void deleteFileInfos(Map<String, Object> param);

    //..
    void insertItemData(JSONObject itemObj);

    //..
    void updateItemData(JSONObject itemObj);

    //..
    void deleteItem(String id);

    //..
    void addFileInfos(JSONObject fileInfo);

    //..
    void deleteItemsByBusinessId(String businessId);


}
