package com.redxun.rdmZhgl.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface KjlwDao {
    void insertKjlw(JSONObject param);

    void updateKjlw(JSONObject param);

    void updateKjlwNum(Map<String, Object> param);

    JSONObject queryMaxKjlwNum(Map<String, Object> param);

    JSONObject queryKjlwById(String kjlwId);

    List<JSONObject> queryKjlwFileList(Map<String, Object> param);

    void addKjlwFileInfos(JSONObject param);

    void deleteKjlwFile(Map<String, Object> param);

    void deleteKjlw(Map<String, Object> param);

    List<Map<String, Object>> queryKjlwList(Map<String, Object> param);

    int countKjlwList(Map<String, Object> param);
}
