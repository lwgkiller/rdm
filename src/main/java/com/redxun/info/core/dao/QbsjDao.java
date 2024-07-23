package com.redxun.info.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface QbsjDao {
    List<JSONObject> queryTypeList(JSONObject params);

    void updateNumber(JSONObject messageObj);

    void createQbsj(JSONObject messageObj);

    void updateQbsj(JSONObject messageObj);

    List<Map<String, Object>> queryQbsj(Map<String, Object> params);

    int countQbsj(Map<String, Object> params);

    List<JSONObject> queryQbsjFileList(Map<String, Object> param);


    void deleteQbsjFile(Map<String, Object> param);


    void deleteQbsj(Map<String, Object> param);

    JSONObject queryQbsjById(String qbgzId);

    void addFileInfos(JSONObject fileInfo);

    JSONObject queryMaxQbsjNum(Map<String, Object> param);
}
