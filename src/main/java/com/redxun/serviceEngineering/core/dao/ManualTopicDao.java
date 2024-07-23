package com.redxun.serviceEngineering.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface ManualTopicDao {

    // 表单的新增、更新和删除
    void insertTopic(JSONObject param);

    void updateTopic(JSONObject param);

    void deleteTopic(JSONObject param);

    // 更新topic状态为待评审
    void confirmTopic(JSONObject param);

    // 更新versionStauts状态为历史
    void updateVersion(JSONObject param);


    // 列表数据
    List<JSONObject> queryApplyList(Map<String, Object> params);

    // 列表导出数据
    List<JSONObject> queryExportApplyList(Map<String, Object> params);

    // 某个表单的基本信息
    JSONObject queryApplyDetail(JSONObject params);

    // 文件的增删改查
    List<JSONObject> queryFileList(JSONObject params);

    void insertFile(JSONObject param);

    void deleteFile(JSONObject param);

    // 标准的增删改查
    List<JSONObject> queryStandardList(JSONObject params);

    void insertStandard(JSONObject param);

    void updateStandard(JSONObject param);

    void deleteStandard(JSONObject param);

    // 对标的增删改查
    List<JSONObject> queryBenchmarkingList(JSONObject params);

    void insertBenchmarking(JSONObject param);

    void updateBenchmarking(JSONObject param);

    void deleteBenchmarking(JSONObject param);



}
