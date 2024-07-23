package com.redxun.serviceEngineering.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface PartsAtlasFileCollectionDao {
    //图册列表查询
    List<JSONObject> dataListQuery(Map<String, Object> params);

    //图册数量查询
    Integer countDataListQuery(Map<String, Object> params);


    //..
    JSONObject queryDataById(String id);

    //图册 增删改
    void deleteBusiness(String id);

    void insertBusiness(Map<String, Object> objBody);

    void updateBusiness(Map<String, Object> objBody);

    void addFileInfos(JSONObject fileInfo);

}
