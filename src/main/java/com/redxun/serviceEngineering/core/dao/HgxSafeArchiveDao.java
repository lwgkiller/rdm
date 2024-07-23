package com.redxun.serviceEngineering.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface HgxSafeArchiveDao {
    //列表查询
    List<JSONObject> dataListQuery(Map<String, Object> params);

    //数量查询
    Integer countDataListQuery(Map<String, Object> params);

    //..
    JSONObject queryDataById(String id);

    //增删改
    void deleteBusiness(String id);

    void insertBusiness(Map<String, Object> objBody);

    void updateBusiness(Map<String, Object> objBody);

    // topic关联项的增删改查
    List<JSONObject> queryTopicRelList(JSONObject params);

    void insertTopicRel(JSONObject param);

    void updateTopicRel(JSONObject param);

    void deleteTopicRel(JSONObject param);

    // 合规性关联项的增删改查
    List<JSONObject> queryStandardRelList(JSONObject params);

    void insertStandardRel(JSONObject param);

    void updateStandardRel(JSONObject param);

    void deleteStandardRel(JSONObject param);

    // 更新versionStauts状态为历史
    void updateVersion(JSONObject param);


}
