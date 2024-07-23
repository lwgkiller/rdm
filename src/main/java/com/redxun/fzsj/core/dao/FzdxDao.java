package com.redxun.fzsj.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FzdxDao {

    List<JSONObject> fzfxxListQuery(JSONObject params);

    int countFzfxxQuery(JSONObject params);

    List<JSONObject> fzdxListQuery(JSONObject params);

    void createFzdx(JSONObject formDataJson);

    void updateFzzx(JSONObject formDataJson);

    JSONObject getFzdxById(String id);

    void deleteFzdx(String path);

    List<JSONObject> selectFzdxByPath(String path);

    void createFzfxx(JSONObject jsonObject);

    void updateFzfxx(JSONObject jsonObject);

    void deleteFzfxxById(JSONObject jsonObject);

    void deleteFzfxxByFzdxId(JSONObject jsonObject);
    /**
    * 通过仿真分析项id获取仿真分析项信息
     *
     * @param analysisId
     * @return json
    * */
    JSONObject getAnalysisObj(String analysisId);
}
