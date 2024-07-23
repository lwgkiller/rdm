package com.redxun.serviceEngineering.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface DecorationManualTopicDao {
    //..
    List<JSONObject> dataListQuery(Map<String, Object> params);

    //..
    Integer countDataListQuery(Map<String, Object> params);

    //..
    JSONObject queryDataById(String id);

    //..
    void deleteBusiness(String id);

    //..
    void deleteBusinessFile(Map<String, Object> param);

    //..
    void insertBusiness(Map<String, Object> objBody);

    //..
    void updateBusiness(Map<String, Object> objBody);

    //..
    void addFileInfos(JSONObject param);

    //..
    List<JSONObject> queryFileList(Map<String, Object> param);
}
