package com.redxun.trialPartsProcess.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public interface TrialPartsProcessDao {
    // 基础信息
    void insertTrialPartsProcessBase(JSONObject param);

    void updateTrialPartsProcessBase(JSONObject param);

    void deleteTrialPartsProcessBaseALL(JSONObject param);

    List<JSONObject> queryBaseInfoList(Map<String, Object> params);

    JSONObject queryApplyDetail(JSONObject params);

    // 批次信息
    void insertTrialPartsProcessBatch(JSONObject param);

    void updateTrialPartsProcessBatch(JSONObject param);

    void deleteTrialPartsProcessBatch(JSONObject param);

    void deleteTrialPartsProcessBatchALL(JSONObject param);

    List<JSONObject> queryBatchInfo(Map<String, Object> params);

    // 批次详情信息
    void insertTrialPartsProcessBatchDetail(JSONObject param);

    void updateTrialPartsProcessBatchDetail(JSONObject param);

    void deleteTrialPartsProcessBatchDetail(JSONObject param);

    void deleteTrialPartsProcessBatchDetailALL(JSONObject param);

    void deleteTrialPartsProcessBatchDetailByTrialBatch(JSONObject param);

    List<JSONObject> queryBatchDetailInfo(Map<String, Object> params);

    // 文件增删
    List<JSONObject> queryFileList(JSONObject params);

    void insertFile(JSONObject param);

    void deleteFile(JSONObject param);

    void deleteFileALL(JSONObject param);

    JSONObject queryBatchInfoById(HashMap<String, Object> params);

    JSONObject getUserInfoById(String userId);

}
