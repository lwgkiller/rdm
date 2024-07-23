package com.redxun.serviceEngineering.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface ManualFileDownloadApplyDao {
    // 表单的新增、更新和删除
    void insertApply(JSONObject param);

    void updateApply(JSONObject param);

    void updateApplyNumber(JSONObject param);


    void deleteApply(JSONObject param);

    // 列表数据
    List<JSONObject> queryApplyList(Map<String, Object> params);

    int countApplyList(Map<String, Object> params);

    // 某个表单的基本信息
    JSONObject queryApplyDetail(JSONObject params);

    // 附件的查询、新增和删除
    List<JSONObject> getApplyFiles(JSONObject params);

    void insertFile(JSONObject params);

    void deleteFile(JSONObject params);

    // 需求项的增删改查
    List<JSONObject> queryDemandList(JSONObject params);

    void insertDemand(JSONObject param);

    void deleteDemand(JSONObject param);

}
