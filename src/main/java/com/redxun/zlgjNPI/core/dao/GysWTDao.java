package com.redxun.zlgjNPI.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface GysWTDao {
    List<JSONObject> queryZlgjList(Map<String, Object> param);

    int countZlgjList(Map<String, Object> param);

    void insertZlgj(JSONObject param);

    void updateZlgj(JSONObject param);

    void addFileInfos(Map<String, Object> params);

    List<JSONObject> getFileList(Map<String, Object> params);

    JSONObject queryZlgjById(String wtId);

    void deleteFileByIds(Map<String, Object> params);

    int addWtyy(Map<String, Object> param);

    int updatWtyy(Map<String, Object> params);

    int delWtyy(String id);

    int addLscs(Map<String, Object> param);

    int updatLscs(Map<String, Object> params);

    int delLscs(String id);

    int addGbyy(Map<String, Object> param);

    int updateGbyy(Map<String, Object> params);

    int delGbyy(String id);

    int addFayz(Map<String, Object> param);

    int updatFayz(Map<String, Object> params);

    int delFayz(String id);

    int addJjfa(Map<String, Object> param);

    int updatJjfa(Map<String, Object> params);

    int delJjfa(String id);

    List<JSONObject> getReasonList(Map<String, Object> params);

    List<JSONObject> getLscsList(Map<String, Object> params);

    List<JSONObject> getGbyyList(Map<String, Object> params);

    List<JSONObject> getFayzList(Map<String, Object> params);

    List<JSONObject> getJjfaList(Map<String, Object> params);

    List<JSONObject> queryZlgjFileList(Map<String, Object> param);

    void deleteZlgjFile(Map<String, Object> param);

    void deleteZlgj(Map<String, Object> param);

    void deleteWtyy(Map<String, Object> param);

    void deleteLscs(Map<String, Object> param);

    void deleteGbyy(Map<String, Object> param);

    void deleteFayz(Map<String, Object> param);

    void deleteJjfa(Map<String, Object> param);

    List<JSONObject> reasonList(JSONObject jsonObject);

    List<JSONObject> querySmallType(Map<String, Object> params);
}
