package com.redxun.jgsp.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface JgspDao {
    List<JSONObject> queryJgsp(Map<String, Object> params);

    void createJgsp(JSONObject messageObj);

    void updateJgsp(JSONObject messageObj);

    JSONObject queryJgspById(String qbgzId);

    void addFileInfos(JSONObject fileInfo);

    int countJgspList(Map<String, Object> param);

    List<JSONObject> queryJgspFileList(Map<String, Object> param);

    void deleteJgspFile(Map<String, Object> param);

    void deleteJgsp(Map<String, Object> param);

}
