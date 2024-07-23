package com.redxun.xcmgProjectManager.core.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface XcmgProjectPlanDao {
    void createProjectPlan(JSONObject param);

    JSONObject getPlanById(String id);

    void delPlanByProjectId(String projectId);

    List<JSONObject> getPlanByProjectId(String projectId);
}
