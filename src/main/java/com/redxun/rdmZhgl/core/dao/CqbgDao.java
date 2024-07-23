package com.redxun.rdmZhgl.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CqbgDao {
    List<JSONObject> queryCqbg(Map<String, Object> params);


    void insertCqbg(JSONObject messageObj);


    void updateCqbg(JSONObject messageObj);


    void updateJsjds(JSONObject messageObj);
    void updateZgzl (JSONObject messageObj);
    List<JSONObject> queryCqbgByJds (String jstbId);
    JSONObject queryCqbgById(String jstbId);

    JSONObject queryCqbgProjectStage(String jstbId);

    void deleteCqbg(Map<String, Object> param);


}
