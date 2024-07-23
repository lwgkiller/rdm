package com.redxun.keyDesign.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface KeyDesignDao {
    List<Map<String, Object>> querySystemCategory(Map<String, Object> params);

    List<Map<String, Object>> querySystem(Map<String, Object> params);

    Integer queryBySystemIds(Map<String, Object> params);

    void delSystemNode(String nodeId);

    void updateSystemNode(JSONObject jsonObject);

    void saveSystemNode(JSONObject jsonObject);

    List<Map<String, Object>> queryExportSystem(Map<String, Object> params);

    List<JSONObject> queryGroupNamesByIds(Map<String, Object> params);
}
