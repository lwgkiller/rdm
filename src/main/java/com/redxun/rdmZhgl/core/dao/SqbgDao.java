package com.redxun.rdmZhgl.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface SqbgDao {
    List<JSONObject> querySqbg(Map<String, Object> params);

    List<JSONObject> queryDetailList(Map<String, Object> params);

    void createSqbg(JSONObject messageObj);

    void updateSqbg(JSONObject messageObj);

    JSONObject querySqbgById(String qbgzId);

    void addFileInfos(JSONObject fileInfo);

    int countSqbgList(Map<String, Object> param);

    List<JSONObject> querySqbgFileList(Map<String, Object> param);

    void deleteSqbgFile(Map<String, Object> param);

    void deleteSqbg(Map<String, Object> param);

    void createDetail(JSONObject messageObj);

    void updateDetail(JSONObject messageObj);

    void deleteDetail(Map<String, Object> param);

    void updateStatus(Map<String, Object> param);

}
