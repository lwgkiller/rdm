package com.redxun.keyDesign.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface KeyGjllDao {
    List<JSONObject> queryGjll(Map<String, Object> params);

    void deleteGjll(Map<String, Object> param);

    void createGjll(JSONObject messageObj);


    void updateGjll(JSONObject messageObj);


    JSONObject queryGjllById(String rapId);


    int countGjllList(Map<String, Object> param);


    List<JSONObject> queryGjllFileList(Map<String, Object> param);


    List<JSONObject> queryOldGjllFileList(Map<String, Object> param);


    void deleteGjllFile(Map<String, Object> param);


    void createFile(JSONObject jsonObject);


    JSONObject queryBj(Map<String, Object> param);

}
