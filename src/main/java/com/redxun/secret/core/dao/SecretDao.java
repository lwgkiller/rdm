package com.redxun.secret.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface SecretDao {
    List<JSONObject> querySecret(Map<String, Object> params);


    JSONObject queryDept(Map<String, Object> param);


    void createSecret(JSONObject messageObj);


    void updateSecret(JSONObject messageObj);


    JSONObject querySecretById(String qbgzId);


    int countSecretList(Map<String, Object> param);


    int countSecretNumber(Map<String, Object> param);


    void updateSecretNum(Map<String, Object> param);


    void deleteSecret(Map<String, Object> param);

    List<JSONObject> queryMaxSecretNumber(Map<String, Object> param);

}
