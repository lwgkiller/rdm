package com.redxun.portrait.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface XfdyPersonDao {
    List<JSONObject> queryXfdyPerson(Map<String, Object> params);

    void createXfdyPerson(JSONObject messageObj);

    void updateXfdyPerson(JSONObject messageObj);

    JSONObject queryXfdyPersonById(String qbgzId);

    void deleteXfdyPerson(Map<String, Object> param);

    JSONObject queryImgById(String qbgzId);

    List<Map<String, Object>> queryXfdyFileList(Map<String, Object> params);

    void addXfdyFileInfos(JSONObject param);

    void deleteXfdyFileInfos(Map<String, Object> params);
}
