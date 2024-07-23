package com.redxun.environment.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface RapDao {
    List<JSONObject> queryRap(Map<String, Object> params);

    void deleteRap(Map<String, Object> param);

    void insertRap(JSONObject messageObj);


    void updateRap(JSONObject messageObj);


    JSONObject queryRapById(String rapId);



    void addFileInfos(JSONObject fileInfo);


    int countRapList(Map<String, Object> param);


    List<JSONObject> queryRapFileList(Map<String, Object> param);


    void deleteRapFile(Map<String, Object> param);



}
