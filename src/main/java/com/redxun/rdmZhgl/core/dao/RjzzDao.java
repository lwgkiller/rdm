package com.redxun.rdmZhgl.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface RjzzDao {
    void insertRjzz(JSONObject param);

    void updateRjzz(JSONObject param);

    void updateRjzzNum(Map<String, Object> param);

    JSONObject queryMaxRjzzNum_Obsoleted(Map<String, Object> param);

    JSONObject queryMaxRjzzNum(Map<String, Object> param);

    JSONObject queryRjzzById(String rjzzId);

    List<Map<String, Object>> queryRjzzList(Map<String, Object> param);

    int countRjzzList(Map<String, Object> param);

    List<JSONObject> queryRjzzFileList(Map<String, Object> param);

    void addRjzzFileInfos(JSONObject param);

    void deleteRjzzFile(Map<String, Object> param);

    void deleteRjzz(Map<String, Object> param);

    List<JSONObject> queryUserInfos();

    void updaterjkfId(Map<String, Object> param);
}
