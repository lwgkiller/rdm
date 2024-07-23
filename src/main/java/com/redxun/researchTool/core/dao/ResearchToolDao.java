package com.redxun.researchTool.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ResearchToolDao {
    List<Map<String, Object>> queryTool(Map<String, Object> params);


    void insertTool(JSONObject formData);


    void updateTool(JSONObject formData);


    JSONObject queryToolById(String toolid);


    void deletetool(Map<String, Object> param);

    List<Map<String, Object>> queryTrain(Map<String, Object> params);
}
