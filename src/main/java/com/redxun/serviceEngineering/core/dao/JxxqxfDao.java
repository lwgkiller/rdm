package com.redxun.serviceEngineering.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface JxxqxfDao {
    //..
    List<Map<String, Object>> jxxqxfListQuery(Map<String, Object> params);

    //..
    Integer jxxqxfCountQuery(Map<String, Object> params);

    //..
    void insertJxxqxf(Map<String, Object> params);

    void updateJxxqxf(JSONObject formDataJson);

    //..
    void deleteJxxqxf(Map<String, Object> param);

    JSONObject queryJxxqxfById(String id);

    JSONObject getGroupById(JSONObject jsonObject);

    //组数回传率
    String queryZshcl();

    //查询回传中和未回传总数
    Integer queryNotHcwcCount();
}
