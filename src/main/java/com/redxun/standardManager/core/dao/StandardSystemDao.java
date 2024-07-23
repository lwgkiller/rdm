package com.redxun.standardManager.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface StandardSystemDao {
    List<Map<String, Object>> querySystemCategory(Map<String, Object> params);

    List<Map<String, Object>> querySystem(Map<String, Object> params);

    Integer queryStandardBySystemIds(Map<String, Object> params);

    void delSystemNode(String nodeId);

    void updateSystemNode(JSONObject jsonObject);

    void saveSystemNode(JSONObject jsonObject);

    List<Map<String, Object>> queryExportSystem(Map<String, Object> params);

    List<JSONObject> queryGroupNamesByIds(Map<String, Object> params);
}
