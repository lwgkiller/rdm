package com.redxun.zlgjNPI.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ProductRequireDao {
    List<JSONObject> queryProductRequire(Map<String, Object> params);

    void createProductRequire(JSONObject messageObj);

    void updateProductRequire(JSONObject messageObj);

    JSONObject queryProductRequireById(String qbgzId);

    void addFileInfos(JSONObject fileInfo);

    int countProductRequireList(Map<String, Object> param);

    List<JSONObject> queryProductRequireFileList(Map<String, Object> param);

    void deleteProductRequireFile(Map<String, Object> param);

    void deleteProductRequire(Map<String, Object> param);


    List<JSONObject> queryMaxNumber(Map<String, Object> param);

    void updateNum(Map<String, Object> param);

    List<JSONObject> queryProductRequireByProjectId(Map<String, Object> param);
}
