package com.redxun.world.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface PtsyDao {
    List<JSONObject> queryPtsy(Map<String, Object> params);


    JSONObject queryDept(Map<String, Object> param);


    void createPtsy(JSONObject messageObj);


    void updatePtsy(JSONObject messageObj);


    JSONObject queryPtsyById(String qbgzId);


    int countPtsyList(Map<String, Object> param);


    List<JSONObject> queryPtsyFileList(Map<String, Object> param);


    void deletePtsyFile(Map<String, Object> param);


    void addFileInfos(JSONObject fileInfo);


    void deletePtsy(Map<String, Object> param);


}
