package com.redxun.standardManager.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface StandardDoCheckDao {
    List<JSONObject> queryDoCheckList(Map<String, Object> param);

    int countDoCheckList(Map<String, Object> param);

    void insertDoCheckBaseInfo(JSONObject param);

    void updateDoCheckBaseInfo(JSONObject param);

    void updateDoCheckStatus(Map<String, Object> param);

    void insertDoCheckDetail(JSONObject param);

    void updateDoCheckDetail(JSONObject param);

    void delDoCheckBaseInfo(JSONObject param);

    void delDoCheckDetail(JSONObject param);

    void delDoCheckFile(JSONObject param);

    JSONObject queryApplyJson(JSONObject params);

    List<JSONObject> queryCheckDetailList(JSONObject param);

    List<JSONObject> queryCheckDetailFileList(JSONObject param);

    void insertCheckFile(JSONObject param);

    List<JSONObject> queryStandardFirstWriterInfo(JSONObject param);

    List<JSONObject> queryDoCheckStatusByStandardIds(JSONObject param);

    List<JSONObject> queryRemindInstByParam(JSONObject param);

    void delRemindInstByIds(JSONObject param);

    void batchInsertDelayPunish(List<JSONObject> params);

    void batchInsertResultNotOkPunish(List<JSONObject> params);

    List<JSONObject> queryCheckDetailResultNotOkList(Map<String, Object> param);

    List<JSONObject> exportDoCheckList(Map<String, Object> param);
}
