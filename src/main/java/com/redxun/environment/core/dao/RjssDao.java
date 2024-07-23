package com.redxun.environment.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface RjssDao {
    List<JSONObject> queryRjss(Map<String, Object> params);

    void createRjss(JSONObject messageObj);

    void updateRjss(JSONObject messageObj);

    JSONObject queryRjssById(String qbgzId);

    void addFileInfos(JSONObject fileInfo);

    int countRjssList(Map<String, Object> param);

    List<JSONObject> queryRjssFileList(Map<String, Object> param);

    void deleteRjssFile(Map<String, Object> param);

    void deleteRjss(Map<String, Object> param);

    void updateRjssNumber(Map<String, Object> param);

    JSONObject queryMaxRjssNum(Map<String, Object> param);

    void createReason(JSONObject messageObj);

    void updateReason(JSONObject messageObj);

    void deleteReason(Map<String, Object> param);

    void createDetail(JSONObject messageObj);

    void updateDetail(JSONObject messageObj);

    void deleteDetail(Map<String, Object> param);

    List<JSONObject> queryReason(Map<String, Object> param);

    List<JSONObject> queryDetail(Map<String, Object> param);
}
