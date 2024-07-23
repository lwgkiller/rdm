package com.redxun.portrait.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface KpiLeaderDao {

    void addKpiBaseInfo(JSONObject object);

    void updateKpiBaseInfo(JSONObject object);

    List<JSONObject> queryKpiYearList(Map<String, Object> params);

    int countKpiYearList(Map<String, Object> params);

    void batchDeleteKpiBaseInfo(List<String> kpiIds);

    void saveUsersList(JSONObject param);

    void updateUsersList(JSONObject param);

    void deleteUsersList(JSONObject param);

    List<JSONObject> queryUsersList(Map<String, Object> params);

    int countUsersList(Map<String, Object> params);

    void insertKpiMonthBaseInfo(JSONObject param);

    void updateKpiMonthBaseInfo(JSONObject param);

    void insertKpiMonthDetail(JSONObject param);

    void updateKpiMonthDetail(JSONObject param);

    void updateKpiMonthDetailList(JSONObject param);

    List<JSONObject> findKpiListByBkhId(Map<String, Object> params);

    //根据流程标识获得流程列表的SolId
    String querySolIdByKey();

    List<JSONObject> queryKpiLeaderFlowList(Map<String, Object> params);

    void delKpiLeaderBaseFlowById(String id);

    void delKpiLeaderDetailFlowById(String id);

    void delTaskData(String instId);

    List<JSONObject> getKpiLeaderBaseById(Map<String, Object> params);

    List<JSONObject> getKpiLeaderDetailById(Map<String, Object> params);

    void updateFjScore(Map<String, Object> params);

    int checkKpiYearMonth(Map<String, Object> params);

    List<JSONObject> getKplLeaderMonth(Map<String, Object> params);

    List<JSONObject> getKplLeaderCopyDetail(Map<String, Object> params);
}
