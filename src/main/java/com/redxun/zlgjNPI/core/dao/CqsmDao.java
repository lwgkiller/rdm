package com.redxun.zlgjNPI.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CqsmDao {
    List<JSONObject> queryCqsm(Map<String, Object> params);


    void insertCqsm(JSONObject messageObj);


    void updateCqsm(JSONObject messageObj);


    void updateJsjds(JSONObject messageObj);


    JSONObject queryCqsmById(String jstbId);


    void deleteCqsmFile(Map<String, Object> param);


    void deleteCqsm(Map<String, Object> param);


}
