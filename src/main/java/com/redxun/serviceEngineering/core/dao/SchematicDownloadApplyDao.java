package com.redxun.serviceEngineering.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface SchematicDownloadApplyDao {
    // ..
    void insertApply(JSONObject param);

    // ..
    void updateApply(JSONObject param);

    // ..
    void updateApplyNumber(JSONObject param);

    // ..
    void deleteApply(JSONObject param);

    // ..
    List<JSONObject> queryApplyList(Map<String, Object> params);

    // ..
    int countApplyList(Map<String, Object> params);

    // ..
    JSONObject queryApplyDetail(JSONObject params);

    // ..
    List<JSONObject> queryDemandList(JSONObject params);

    // ..
    void insertDemand(JSONObject param);

    // ..
    void deleteDemand(JSONObject param);

}
