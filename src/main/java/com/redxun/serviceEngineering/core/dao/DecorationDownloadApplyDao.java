package com.redxun.serviceEngineering.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface DecorationDownloadApplyDao {
    //..
    void insertApply(JSONObject param);

    //..
    void updateApply(JSONObject param);

    //..
    void updateApplyNumber(JSONObject param);

    //..
    void deleteApply(JSONObject param);

    //..
    List<JSONObject> queryApplyList(Map<String, Object> params);

    //..
    int countApplyList(Map<String, Object> params);

    //..
    JSONObject queryApplyDetail(JSONObject params);

    //..
    List<JSONObject> getApplyFiles(JSONObject params);

    //..
    void insertFile(JSONObject params);

    //..
    void deleteFile(JSONObject params);

    //..
    List<JSONObject> queryDemandList(JSONObject params);

    //..
    void insertDemand(JSONObject param);

    //..
    void deleteDemand(JSONObject param);

}
