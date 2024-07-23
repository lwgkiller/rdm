package com.redxun.keyDesign.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface JscsDao {
    List<JSONObject> queryJscs(Map<String, Object> params);


    List<JSONObject> queryJscsDetail(Map<String, Object> params);


    JSONObject queryDept(Map<String, Object> param);


    void createJscs(JSONObject messageObj);


    void updateJscs(JSONObject messageObj);


    JSONObject queryJscsById(String qbgzId);


    JSONObject queryJscsDetailById(String qbgzId);


    void addFileInfos(JSONObject fileInfo);


    int countJscsList(Map<String, Object> param);


    List<JSONObject> queryJscsDetailFileList(Map<String, Object> param);


    void deleteJscsDetailFile(Map<String, Object> param);


    void deleteJscs(Map<String, Object> param);


    void createJscsDetail(JSONObject messageObj);


    void updateJscsDetail(JSONObject messageObj);


    void deleteJscsDetail(Map<String, Object> param);


}
