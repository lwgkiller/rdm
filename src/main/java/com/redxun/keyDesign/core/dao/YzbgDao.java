package com.redxun.keyDesign.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface YzbgDao {
    List<JSONObject> queryYzbg(Map<String, Object> params);

    List<JSONObject> queryYzbgDetail(Map<String, Object> params);

    void createYzbg(JSONObject messageObj);

    void updateYzbg(JSONObject messageObj);

    JSONObject queryYzbgById(String qbgzId);

    JSONObject queryYzbgDetailById(String qbgzId);

    void addFileInfos(JSONObject fileInfo);

    int countYzbgList(Map<String, Object> param);

    List<JSONObject> queryYzbgDetailFileList(Map<String, Object> param);

    void deleteYzbgDetailFile(Map<String, Object> param);

    void deleteYzbg(Map<String, Object> param);

    void createYzbgDetail(JSONObject messageObj);

    void updateYzbgDetail(JSONObject messageObj);

    void deleteYzbgDetail(Map<String, Object> param);

}
