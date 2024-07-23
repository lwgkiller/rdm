package com.redxun.qualityfxgj.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface GyfkDao {
    List<JSONObject> queryGyfk(Map<String, Object> params);


    List<JSONObject> queryGyfkZzr(Map<String, Object> params);


    List<JSONObject> queryGyfkDetail(Map<String, Object> params);


    void createGyfk(JSONObject messageObj);


    void updateGyfk(JSONObject messageObj);


    JSONObject queryGyfkById(String qbgzId);


    JSONObject queryGyfkDetailById(String qbgzId);


    void addFileInfos(JSONObject fileInfo);


    int countGyfkList(Map<String, Object> param);


    List<JSONObject> queryGyfkDetailFileList(Map<String, Object> param);


    void deleteGyfkDetailFile(Map<String, Object> param);


    void deleteGyfk(Map<String, Object> param);


    void createGyfkDetail(JSONObject messageObj);


    void updateGyfkDetail(JSONObject messageObj);


    void deleteGyfkDetail(Map<String, Object> param);


}
