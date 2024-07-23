package com.redxun.zlgjNPI.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface NewItemFxpgDao {
    List<Map<String, Object>> queryXpsxList(Map<String, Object> param);

    int countXpsxList(Map<String, Object> param);

    void insertXpsx(JSONObject param);

    void updateXpsx(JSONObject param);

    JSONObject queryXpsxById(String xpsxId);

    List<Map<String, Object>> queryUser(Map<String, Object> params);

    int addFxpg(Map<String, Object> param);

    int updatFxpg(Map<String, Object> params);

    int delFxpg(String id);

    List<JSONObject> getFxpgList(JSONObject jsonObject);

    void deleteXpsx(Map<String, Object> param);

    void deleteFxpg(Map<String, Object> param);
}
