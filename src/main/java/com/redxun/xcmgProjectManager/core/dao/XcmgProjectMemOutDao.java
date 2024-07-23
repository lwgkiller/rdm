package com.redxun.xcmgProjectManager.core.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface XcmgProjectMemOutDao {
    void createProjectMemberOut(JSONObject param);

    JSONObject getMemberOutById(String id);

    void updateMemberOut(JSONObject param);

    void removeMemberOutById(String id);

    void delMemberOutByProjectId(String projectId);

    List<JSONObject> getMemberOutByProjectId(String projectId);
}
