package com.redxun.zlgjNPI.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface XppqDao {
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

    List<JSONObject> getProcessList(Map<String, Object> params);

    List<JSONObject> queryZlgjFileList(Map<String, Object> param);

    void deleteZlgjFile(Map<String, Object> param);

    void deleteZlgj(Map<String, Object> param);

    void deleteWtyy(Map<String, Object> param);

    void updateProcessStatus(Map<String, Object> param);

    List<JSONObject> queryUserByRoleName(Map<String, Object> param);
}