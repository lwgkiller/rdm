package com.redxun.standardManager.core.dao;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

@Repository
public interface StandardDemandDao {
    List<Map<String, Object>> queryStandardDemandList(Map<String, Object> params);

    JSONObject queryDemandDetailById(String formId);

    void addStandardDemand(Map<String, Object> params);

    void updateStandardDemand(Map<String, Object> params);

    void deleteStandardDemandById(String applyId);
}
