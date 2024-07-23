package com.redxun.environment.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface YjbgDao {
    List<JSONObject> queryYjbg(Map<String, Object> params);


    void createYjbg(JSONObject messageObj);


    void updateYjbg(JSONObject messageObj);


    JSONObject queryYjbgById(String qbgzId);



    void addFileInfos(JSONObject fileInfo);


    int countYjbgList(Map<String, Object> param);


    List<JSONObject> queryYjbgFileList(Map<String, Object> param);


    void deleteYjbgFile(Map<String, Object> param);


    void deleteYjbg(Map<String, Object> param);



    List<JSONObject> queryType(Map<String, Object> param);



}
