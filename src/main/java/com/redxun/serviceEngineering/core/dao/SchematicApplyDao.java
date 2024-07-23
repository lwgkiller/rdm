package com.redxun.serviceEngineering.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface SchematicApplyDao {

    List<JSONObject> querySchematicApply(Map<String, Object> params);

    int countSchematicApply(Map<String, Object> param);

    void createSchematicApply(JSONObject messageObj);

    void updateSchematicApply(JSONObject messageObj);

    JSONObject querySchematicApplyById(String qbgzId);

    void addFileInfos(JSONObject fileInfo);

    List<JSONObject> querySchematicApplyFileList(Map<String, Object> param);

    void deleteSchematicApplyFile(Map<String, Object> param);

    void deleteSchematicApply(Map<String, Object> param);

    void updateSchematicApplyNumber(Map<String, Object> param);

    JSONObject queryMaxSchematicApplyNum(Map<String, Object> param);

    void createDetail(JSONObject messageObj);

    void updateDetail(JSONObject messageObj);

    void deleteDetail(Map<String, Object> param);

    List<JSONObject> queryDetail(Map<String, Object> param);

}
