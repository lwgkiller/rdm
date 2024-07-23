package com.redxun.xcmgProjectManager.core.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface XcmgProjectAchievementDao {
    void createProjectAchievement(JSONObject param);

    JSONObject getAchievementById(String id);

    void updateAchievement(JSONObject param);

    void removeAchievementById(String id);

    void delAchievementByProjectId(String projectId);

    List<JSONObject> getAchievementByProjectId(String projectId);
}
