package com.redxun.bjjszc.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface JsxxDao {
    List<JSONObject> queryJsxx(Map<String, Object> params);

    List<JSONObject> queryJsxxDetail(Map<String, Object> params);

    void createJsxx(JSONObject messageObj);

    void updateJsxx(JSONObject messageObj);

    void updateApplyNumber(JSONObject messageObj);


    JSONObject queryJsxxById(String jsxxId);

    int countJsxxList(Map<String, Object> param);

    void deleteJsxx(Map<String, Object> param);

    void createDetail(JSONObject messageObj);

    void updateDetail(JSONObject messageObj);

    void deleteDetail(Map<String, Object> param);

}
