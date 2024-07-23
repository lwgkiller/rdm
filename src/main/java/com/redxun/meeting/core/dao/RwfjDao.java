package com.redxun.meeting.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface RwfjDao {
    List<JSONObject> queryRwfj(Map<String, Object> params);

    void createRwfj(JSONObject messageObj);

    void updateRwfj(JSONObject messageObj);

    JSONObject queryRwfjById(String qbgzId);

    void addFileInfos(JSONObject fileInfo);

    int countRwfjList(Map<String, Object> param);

    List<JSONObject> queryRwfjFileList(Map<String, Object> param);

    void deleteRwfjFile(Map<String, Object> param);

    void deleteRwfj(Map<String, Object> param);

    void updateFinishTime(JSONObject messageObj);

    List<JSONObject> getHyglDelay(JSONObject param);

    void updateLowSendDD(JSONObject messageObj);

    void updateMiddleSendDD(JSONObject messageObj);

    void updateHighSendDD(JSONObject messageObj);
}
