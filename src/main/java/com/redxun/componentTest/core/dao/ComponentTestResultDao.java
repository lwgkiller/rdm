package com.redxun.componentTest.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ComponentTestResultDao {
    //..
    List<JSONObject> dataListQuery(Map<String, Object> params);

    //..
    Integer countDataListQuery(Map<String, Object> params);

    //..
    List<JSONObject> dataPassListQuery(Map<String, Object> params);

    //..
    Integer countDataPassListQuery(Map<String, Object> params);

    //..
    List<JSONObject> dataNotPassListQuery(Map<String, Object> params);

    //..
    Integer countDataNotPassListQuery(Map<String, Object> params);
}
