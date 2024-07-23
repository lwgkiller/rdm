package com.redxun.drbfm.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface DrbfmTestTaskDao {

    // 表单的新增、更新和删除
    void insertTestTask(JSONObject param);

    void updateTestTask(JSONObject param);

    void updateTestTaskNumber(JSONObject param);

    void deleteTestTask(JSONObject param);

    // 列表数据
    List<Map<String, Object>> queryTestTaskList(Map<String, Object> params);

    int countTestTaskList(Map<String, Object> param);

    // 某个表单的基本信息
    JSONObject queryApplyDetail(JSONObject params);

    // 查标准id和名称
    List<JSONObject> queryStandardIds(JSONObject params);

    // 文件的增删改查
    List<JSONObject> queryDemandList(JSONObject params);

    void insertDemand(JSONObject param);

    void deleteDemand(JSONObject param);

    // 需求的增删改查
    List<JSONObject> queryTestTaskDemandList(JSONObject params);

    void insertTestTaskDemand(JSONObject param);

    void deleteTestTaskDemand(JSONObject param);

    void updateTestTaskDemand(JSONObject param);

    List<JSONObject> queryQuotaTestData(JSONObject param);

    void updateTestActualEndTime(Map<String, Object> param);

    List<JSONObject> queryTestInfoForQuotaExport(Map<String, Object> params);

    List<JSONObject> queryExpFileList(JSONObject params);
}
