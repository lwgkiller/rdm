package com.redxun.serviceEngineering.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface JxbzzbxfsqDao {
    //..
    List<JSONObject> jxbzzbxfsqListQuery(JSONObject jsonObject);

    //..
    Integer countJxbzzbxfsqQuery(JSONObject jsonObject);

    void insertJxbzzbxfsq(JSONObject jsonObject);

    void updateJxbzzbxfsq(JSONObject formDataJson);
    //..
    void deleteJxbzzbxfsq(JSONObject jsonObject);

    JSONObject getJxbzzbxfsqDetail(String id);

    // 查询部门负责人
    List<JSONObject> getDepRespMan(JSONObject jsonObject);

}
