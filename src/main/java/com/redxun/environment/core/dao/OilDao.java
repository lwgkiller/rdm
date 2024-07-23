package com.redxun.environment.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface OilDao {
    List<JSONObject> queryOil(Map<String, Object> params);

    void deleteOil(Map<String, Object> param);

    void insertOil(JSONObject messageObj);

    void updateOil(JSONObject messageObj);

    JSONObject queryOilById(String rapId);

    void addFileInfos(JSONObject fileInfo);

    int countOilList(Map<String, Object> param);

    List<JSONObject> queryOilFileList(Map<String, Object> param);

    void deleteOilFile(Map<String, Object> param);
}
