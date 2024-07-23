package com.redxun.rdmZhgl.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Repository
public interface GjjsDao {
    void insertGjjsData(JSONObject param);

    void updateGjjsData(JSONObject param);

    void insertJiShuData(JSONObject jiShuObj);

    void updateJiShuData(JSONObject jiShuObj);

    List<JSONObject> queryList(Map<String, Object> param);

    int countList(Map<String, Object> param);

    List<Map<String, Object>> queryDataId(Map<String, Object> params);

    List<JSONObject> getJiShuList(JSONObject jsonObject);

    void addFileInfos(JSONObject map);

    JSONArray getFiles(Map<String, Object> params);

    void deleteFileByIds(Map<String, Object> params);

    void deleteJiShu(Map<String, Object> param);

    List<JSONObject> queryGjjsFileList(Map<String, Object> param);

    void deleteGjjsFile(Map<String, Object> param);

    void deleteJishu(Map<String, Object> param);

    void deleteGjjs(Map<String, Object> param);
}
