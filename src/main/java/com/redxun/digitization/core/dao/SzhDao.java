package com.redxun.digitization.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface SzhDao {
    List<JSONObject> getMenus(Map<String, Object> param);

    List<JSONObject> getSubsysByParam(Map<String, Object> param);

    void insertPageClick(JSONObject formDataJson);

    void insertPageClickYearMonth(JSONObject formDataJson);

    void deletePageClickOneYearAgo(Map<String, Object> param);

    List<JSONObject> pageClickYearMonthQueryGroupMonth(Map<String, Object> param);

    List<JSONObject> pageClickYearMonthQueryGroupDept(Map<String, Object> param);
}
