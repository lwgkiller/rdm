package com.redxun.rdmZhgl.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface JszbDao {
    void insertJszb(JSONObject param);

    void updateJszb(JSONObject param);

    List<Map<String, Object>> queryJszbList(Map<String, Object> param);

    int countJszbList(Map<String, Object> param);

    JSONObject queryJszbById(String jszbId);

    List<Map<String, Object>> queryJszbckData(Map<String, Object> params);

    List<JSONObject> queryJszbFileList(Map<String, Object> param);

    List<JSONObject> queryJszbFileTypes(Map<String, Object> param);

    void addJszbFileInfos(JSONObject param);

    void deleteJszbFile(Map<String, Object> param);

    void deleteJszb(Map<String, Object> param);

    List<JSONObject> queryJszbTemplateFileList(Map<String, Object> param);
}
