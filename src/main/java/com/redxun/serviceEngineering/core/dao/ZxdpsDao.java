package com.redxun.serviceEngineering.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface ZxdpsDao {

    // 表单的新增、更新和删除
    void insertZxdps(JSONObject param);

    void updateZxdps(JSONObject param);

    void updateZxdpsNumber(JSONObject param);
    // 更新topic的状态
    void updateTopicStatus(JSONObject param);

    void deleteZxdps(JSONObject param);

    // 列表数据
    List<JSONObject> queryApplyList(Map<String, Object> params);

    // 某个表单的基本信息
    JSONObject queryApplyDetail(JSONObject params);


    // 文件的增删改查
    List<JSONObject> queryFileList(JSONObject params);

    void insertFile(JSONObject param);


    void deleteFile(JSONObject param);

    // topic的增删改查

    List<JSONObject> queryTopicList(JSONObject params);

    void insertTopic(JSONObject param);


    void deleteTopic(JSONObject param);



    // 意见的增删改查
    List<JSONObject> queryOpinionList(JSONObject params);

    void insertOpinion(JSONObject param);

    void updateOpinion(JSONObject param);

    void deleteOpinion(JSONObject param);









}
