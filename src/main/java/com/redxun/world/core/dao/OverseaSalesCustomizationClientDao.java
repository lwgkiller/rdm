package com.redxun.world.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OverseaSalesCustomizationClientDao {
    //..
    List<JSONObject> clientTreeQuery(JSONObject params);

    //..
    List<JSONObject> clientInstQuery(JSONObject params);

    //..
    void insertClientInst(JSONObject jsonObject);

    //..
    void updateClientInst(JSONObject jsonObject);

    //..
    void deleteClientInst(JSONObject params);

    //..
    void insertClientConfigInst(JSONObject jsonObject);

    //..
    void updateClientConfigInst(JSONObject jsonObject);

    //..
    void deleteClientConfigInst(JSONObject params);
}
