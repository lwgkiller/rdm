package com.redxun.environment.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface GsClhbOverviewDao {
    List<JSONObject> queryNumByDept(Map<String, Object> params);

    List<JSONObject> queryDelayByDept();

    List<JSONObject> queryYearNum();

    List<JSONObject> queryMonthNum(Map<String, Object> params);

    List<JSONObject> queryNumByBrand();
}
