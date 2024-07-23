package com.redxun.zlgjNPI.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface GjllDao {
    List<JSONObject> queryGjll(Map<String, Object> params);

    void deleteGjll(Map<String, Object> param);

    void updateGjll(JSONObject messageObj);

    JSONObject queryGjllById(String rapId);

    int countGjllList(Map<String, Object> param);

    List<JSONObject> queryGjllFileList(Map<String, Object> param);

    void deleteGjllFile(Map<String, Object> param);

    List<JSONObject> queryLlInfoById(Map<String, Object> param);

    void autoCreateLl(JSONObject jsonObject);

    List<JSONObject> queryLlFileInfoById(Map<String, Object> param);

    void autoCreateLlFile(JSONObject jsonObject);

    List<JSONObject> queryLlNewFileInfoById(Map<String, Object> param);
}
