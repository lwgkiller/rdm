package com.redxun.environment.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface RjbgglDao {
    List<JSONObject> queryRjbggl(Map<String, Object> params);

    void createRjbggl(JSONObject messageObj);

    void updateRjbggl(JSONObject messageObj);

    JSONObject queryRjbgglById(String qbgzId);

    void deleteRjbggl(Map<String, Object> param);

    void updateRjbgglNumber(Map<String, Object> param);

    JSONObject queryMaxRjbgglNum(Map<String, Object> param);

    void createSzr(JSONObject messageObj);

    void updateSzr(JSONObject messageObj);

    void deleteSzr(Map<String, Object> param);

    void createJsy(JSONObject messageObj);

    void updateJsy(JSONObject messageObj);

    void deleteJsy(Map<String, Object> param);

    void createModel(JSONObject messageObj);

    void updateModel(JSONObject messageObj);

    void deleteModel(Map<String, Object> param);

    List<JSONObject> querySzr(Map<String, Object> param);

    List<JSONObject> queryJsy(Map<String, Object> param);

    List<JSONObject> queryModel(Map<String, Object> param);
}
