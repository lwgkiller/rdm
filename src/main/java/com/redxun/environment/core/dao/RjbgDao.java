package com.redxun.environment.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface RjbgDao {
    List<JSONObject> checkModel(Map<String, Object> params);

    List<JSONObject> queryRjbg(Map<String, Object> params);

    void createRjbg(JSONObject messageObj);

    void updateRjbg(JSONObject messageObj);

    JSONObject queryRjbgById(String qbgzId);

    void addFileInfos(JSONObject fileInfo);

    int countRjbgList(Map<String, Object> param);

    List<JSONObject> queryRjbgFileList(Map<String, Object> param);

    void deleteRjbgFile(Map<String, Object> param);

    void deleteRjbg(Map<String, Object> param);

    void updateRjbgNumber(Map<String, Object> param);

    JSONObject queryMaxRjbgNum(Map<String, Object> param);

    void createReason(JSONObject messageObj);

    void updateReason(JSONObject messageObj);

    void deleteReason(Map<String, Object> param);

    List<JSONObject> queryReason(Map<String, Object> param);

}
