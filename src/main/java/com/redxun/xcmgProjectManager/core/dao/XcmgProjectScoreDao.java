package com.redxun.xcmgProjectManager.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface XcmgProjectScoreDao {
    List<Map<String, Object>> queryUserProjectScore(Map<String, Object> queryParams);

    void deleteScoreByPid(String projectId);

    List<Map<String, Object>> queryUserProjectStageScore(Map<String, Object> params);

    List<JSONObject> queryStageScoreByEndTime(Map<String, Object> params);

    void deleteScoreByPidUserIds(Map<String, Object> params);

    void updateScoreUserId(Map<String, Object> params);

    void insertUserStageEvaluate(JSONObject object);

    void updateUserStageEvaluate(JSONObject object);

    void delUserStageEvaluate(JSONObject object);

    void delSomeUserStageEvaluate(Map<String, Object> params);

    List<JSONObject> queryMemFinalRatio(Map<String, Object> params);

    void updateMemFinalRatio(Map<String, Object> params);
}
