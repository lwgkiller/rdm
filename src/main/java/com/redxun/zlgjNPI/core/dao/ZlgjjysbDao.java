package com.redxun.zlgjNPI.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ZlgjjysbDao {

    List <JSONObject> zlgjjysbListQuery(JSONObject params);

    Integer countZlgjjysbQuery(JSONObject params);

    void createZlgjjysb(JSONObject formDataJson);

    void updateZlgjjysb(JSONObject formDataJson);

    JSONObject getZlgjjysbDetail(String zlgjjysbId);

    void addFiles(JSONObject fileInfo);

    List<JSONObject> queryZlgjjysbFileList(JSONObject params);

    void delZlgjjysbFileById(String id);

    void delFileByZlgjjysbId(Map <String, Object> param);

    void deleteZlgjjysb(JSONObject param);

    void createDcry(JSONObject param);

    void updateDcry(JSONObject param);

    List <JSONObject> queryDcry(String zlgjjysbId);

    void deleteDcry(JSONObject param);

    void deleteDcryById(String id);

    List<JSONObject> getDjpdry(JSONObject params);

    List<JSONObject> judgeZlbzb(JSONObject params);

    List<JSONObject> test();
}
