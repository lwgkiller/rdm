package com.redxun.info.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface QbgzDao {
    List<Map<String, Object>> queryQbgz(Map<String, Object> params);


    void createQbgz(JSONObject messageObj);


    void updateQbgz(JSONObject messageObj);


    void updateNumber(JSONObject messageObj);


    JSONObject queryQbgzById(String qbgzId);


    JSONObject queryMaxQbgzNum(Map<String, Object> param);


    void addFileInfos(JSONObject fileInfo);


    int countQbgzList(Map<String, Object> param);


    List<JSONObject> queryQbgzFileList(Map<String, Object> param);


    void deleteQbgzFile(Map<String, Object> param);


    void deleteQbgz(Map<String, Object> param);


    List<JSONObject> isLD(Map<String, Object> param);


    List<JSONObject> queryProvide(Map<String, Object> param);


    List<JSONObject> queryType(Map<String, Object> param);
}
