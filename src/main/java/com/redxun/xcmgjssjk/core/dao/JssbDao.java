package com.redxun.xcmgjssjk.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface JssbDao {
    List<JSONObject> queryJssb(Map<String, Object> params);

    List<JSONObject> queryExport(Map<String, Object> params);

    List<JSONObject> queryJszbList(Map<String, Object> params);

    void createJssb(JSONObject messageObj);

    void updateJssb(JSONObject messageObj);

    JSONObject queryJssbById(String qbgzId);

    void addFileInfos(JSONObject fileInfo);

    int countJssbList(Map<String, Object> param);

    List<JSONObject> queryJssbFileList(Map<String, Object> param);

    void deleteJssbFile(Map<String, Object> param);

    void deleteJssb(Map<String, Object> param);

    void createJszb(JSONObject messageObj);

    void updateJszb(JSONObject messageObj);

    void deleteDetail(Map<String, Object> param);
}
