package com.redxun.xcmgProjectManager.core.dao;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

@Repository
public interface XcmgProjectExtensionDao {
    void createProjectExtension(JSONObject param);

    JSONObject getExtensionById(String id);

    void updateExtension(JSONObject param);

    void removeExtensionById(String id);

    void delExtensionByProjectId(String projectId);

    List<JSONObject> getExtensionByProjectId(String projectId);
}
