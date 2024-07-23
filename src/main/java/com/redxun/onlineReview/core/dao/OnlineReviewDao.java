package com.redxun.onlineReview.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface OnlineReviewDao {
    List<JSONObject> queryOnlineReview(Map<String, Object> params);

    int countOnlineReview(Map<String, Object> params);

    void createOnlineReview(JSONObject messageObj);

    void updateOnlineReview(JSONObject messageObj);

    JSONObject queryOnlineReviewBaseById(String qbgzId);

    JSONObject queryModelDetailById(String qbgzId);

    void deleteOnlineReview(Map<String, Object> param);

    void updateOnlineReviewNumber(Map<String, Object> param);

    JSONObject queryMaxOnlineReviewNum(Map<String, Object> param);

    void createTime(JSONObject messageObj);

    void updateTime(JSONObject messageObj);

    void deleteTime(Map<String, Object> param);

    void createConfig(JSONObject messageObj);

    void updateConfig(JSONObject messageObj);

    void deleteConfig(Map<String, Object> param);

    void createModel(JSONObject messageObj);

    void updateModel(JSONObject messageObj);

    void deleteModel(Map<String, Object> param);

    List<JSONObject> queryTime(Map<String, Object> param);

    List<JSONObject> queryCzqTime();

    List<JSONObject> queryTimeName();

    List<JSONObject> queryConfig(Map<String, Object> param);

    List<JSONObject> queryModel(Map<String, Object> param);

    List<Map<String, Object>> getModel(Map<String, Object> param);

    void updateOneTime(JSONObject messageObj);

    void updatePlanTime(JSONObject messageObj);

    void updateTimeRisk(JSONObject messageObj);

    void addFileInfos(JSONObject fileInfo);
    
    List<JSONObject> queryOnlineReviewFileList(Map<String, Object> param);

    void deleteOnlineReviewFile(Map<String, Object> param);
}
