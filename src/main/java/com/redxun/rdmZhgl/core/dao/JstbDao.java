package com.redxun.rdmZhgl.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface JstbDao {
    List<JSONObject> queryJstb(Map<String, Object> params);


    void insertJstb(JSONObject messageObj);


    void updateJstb(JSONObject messageObj);


    JSONObject queryJstbById(String jstbId);

    JSONObject queryJstbStatus(Map<String, Object> param);

    List<Map<String, Object>> queryReaders(Map<String, Object> params);


    void addFileInfos(JSONObject fileInfo);

    void addFileReaders(JSONObject fileInfo);

    int countJsglList(Map<String, Object> param);

    int countJsglNumber(Map<String, Object> param);

    List<JSONObject> queryJstbFileList(Map<String, Object> param);

    List<Map<String, Object>> queryTaskAll(Map<String, Object> param);


    void deleteJstbFile(Map<String, Object> param);


    void deleteJstb(Map<String, Object> param);

    void cancelJstb(Map<String, Object> param);

    List<JSONObject> jSXZtoDep(Map<String, Object> param);


    List<JSONObject> isJSGLB(Map<String, Object> param);
}
