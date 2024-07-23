package com.redxun.environment.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface RjscDao {
    List<JSONObject> queryRjsc(Map<String, Object> params);

    void deleteRjsc(Map<String, Object> param);

    void insertRjsc(JSONObject messageObj);


    void updateRjsc(JSONObject messageObj);


    JSONObject queryRjscById(String rapId);



    void addFileInfos(JSONObject fileInfo);


    int countRjscList(Map<String, Object> param);


    List<JSONObject> queryRjscFileList(Map<String, Object> param);


    void deleteRjscFile(Map<String, Object> param);



}
