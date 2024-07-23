package com.redxun.componentTest.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ComponentResourcesPlatformDataDao {
    //..
    List<JSONObject> masterDataListQuery(JSONObject params);

    //..
    Integer countMasterDataListQuery(JSONObject params);

    //..
    JSONObject getMasterDataById(String id);

    //..
    void deleteMasterData(JSONObject params);

    //..
    void insertMasterData(JSONObject jsonObject);

    //..
    void updateMasterData(JSONObject jsonObject);
}
