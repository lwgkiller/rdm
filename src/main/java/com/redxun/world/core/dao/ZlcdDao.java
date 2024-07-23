package com.redxun.world.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ZlcdDao {
    List<JSONObject> queryZlcd(Map<String, Object> params);

    List<JSONObject> queryZlcdDetail(Map<String, Object> params);

    void createZlcd(JSONObject messageObj);

    void updateZlcd(JSONObject messageObj);

    JSONObject queryZlcdById(String qbgzId);

    void addFileInfos(JSONObject fileInfo);

    int countZlcdList(Map<String, Object> param);

    List<JSONObject> queryZlcdFileList(Map<String, Object> param);

    void deleteZlcdFile(Map<String, Object> param);

    void deleteZlcd(Map<String, Object> param);

    void createDetail(JSONObject messageObj);

    void updateDetail(JSONObject messageObj);

    void deleteDetail(Map<String, Object> param);

}
