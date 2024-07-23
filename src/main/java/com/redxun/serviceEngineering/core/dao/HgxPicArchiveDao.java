package com.redxun.serviceEngineering.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface HgxPicArchiveDao {
    //列表查询
    List<JSONObject> dataListQuery(Map<String, Object> params);

    //数量查询
    Integer countDataListQuery(Map<String, Object> params);

    //..
    JSONObject queryDataById(String id);

    //增删改
    void deleteBusiness(String id);

    void insertBusiness(Map<String, Object> objBody);

    void updateBusiness(Map<String, Object> objBody);

    List<JSONObject> getListByCode(String picCode);

    String queryIdByFileName(String id);



}
