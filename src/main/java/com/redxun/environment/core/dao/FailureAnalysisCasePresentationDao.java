package com.redxun.environment.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface FailureAnalysisCasePresentationDao {
    void insertFailureAnalysisCase(JSONObject param);

    void updateFailureAnalysisCase(JSONObject param);

    void chooseYxal(JSONObject param);

    void cancelYxal(JSONObject param);

    void deleteFailureAnalysisCase(JSONObject param);

    // 列表数据
    List<JSONObject> queryApplyList(Map<String, Object> params);

    // 列表数据
    List<JSONObject> queryExist(Map<String, Object> params);

    // 某个表单的基本信息
    JSONObject queryApplyDetail(JSONObject params);

    // 文件的增删改查
    List<JSONObject> queryFileList(JSONObject params);

    void insertFile(JSONObject param);

    void deleteFile(JSONObject param);
    void deleteAllFile(JSONObject param);

}
