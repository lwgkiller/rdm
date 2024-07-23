package com.redxun.rdmZhgl.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ZljsjdsDao {
    void insertJsjds(JSONObject param);

    void updateJsjds(JSONObject param);

    List<JSONObject> queryJsjdsTasks(Map<String, Object> params);

    JSONObject queryJsjdsById(String rjzzId);

    List<JSONObject> queryJsjdsFileList(Map<String, Object> param);

    void addJsjdsFileInfos(JSONObject param);

    void deleteJsjdsFile(Map<String, Object> param);

    void deleteJsjds(Map<String, Object> param);

    void updateBmscwcTime(Map<String, Object> param);

    void resetBmscwcTime(Map<String, Object> param);

    List<JSONObject> queryAllUsers();

    void updateTest(JSONObject param);

    void updateJdsNum(Map<String, Object> param);

    List<JSONObject> queryMaxJdsNumber(Map<String, Object> param);

    List<JSONObject> selectJsjdsPlan(Map<String, Object> param);

    List<JSONObject> selectJsjdsFinish(Map<String, Object> param);

    List<JSONObject> selectProjectPlan(Map<String, Object> param);

    JSONObject selectZgzlByJdsId(String jsjdsId);

    void updateZgzlProject(JSONObject param);
}
