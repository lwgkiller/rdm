package com.redxun.serviceEngineering.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface JpzlDao {
    List<Map<String, Object>> queryJpzl(Map<String, Object> params);


    void insertJpzl(JSONObject formData);


    void updateJpzl(JSONObject formData);


    JSONObject queryJpzlById(String toolid);


    void deleteJpzl(Map<String, Object> param);


}
