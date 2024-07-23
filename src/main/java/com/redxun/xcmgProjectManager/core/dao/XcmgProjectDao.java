package com.redxun.xcmgProjectManager.core.dao;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;

@Repository
public interface XcmgProjectDao {
    //..
    List<JSONObject> allProjectListQuery(Map<String, Object> params);

    //..
    Integer countAllProjectListQuery(Map<String, Object> params);

    void createBaseInfo(JSONObject param);

    JSONObject queryProjectById(String projectId);

    void updateProjectBaseInfo(JSONObject param);

    void removeById(String projectId);

    void updateProjectBaseInfoInstId(JSONObject param);

    List<JSONObject> queryXpszInfo(Map<String, Object> params);
}
