package com.redxun.xcmgProjectManager.core.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface XcmgProjectMemberDao {
    void createProjectMember(JSONObject param);

    void updateMember(JSONObject param);

    void removeMemberByIds(JSONObject param);

    void delMemberByProjectId(String projectId);

    List<JSONObject> getMemberByProjectId(String projectId);
}
