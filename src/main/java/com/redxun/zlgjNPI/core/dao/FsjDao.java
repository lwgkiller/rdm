package com.redxun.zlgjNPI.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface FsjDao {
    List<JSONObject> queryFsj(Map<String, Object> params);

    List<JSONObject> queryFsjDetail(Map<String, Object> params);

    void createFsj(JSONObject messageObj);

    void updateFsj(JSONObject messageObj);

    JSONObject queryFsjById(String qbgzId);

    JSONObject queryFsjDetailById(String qbgzId);

    void addFileInfos(JSONObject fileInfo);

    int countFsjList(Map<String, Object> param);

    List<JSONObject> queryFsjDetailFileList(Map<String, Object> param);

    void deleteFsjDetailFile(Map<String, Object> param);

    void deleteFsj(Map<String, Object> param);

    void createFsjDetail(JSONObject messageObj);

    void updateFsjDetail(JSONObject messageObj);

    void deleteFsjDetail(Map<String, Object> param);

}
