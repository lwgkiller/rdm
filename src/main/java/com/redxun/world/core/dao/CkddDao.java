package com.redxun.world.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CkddDao {
    List<JSONObject> queryCkdd(Map<String, Object> params);

    List<JSONObject> queryCkddDetail(Map<String, Object> params);

    List<JSONObject> queryTimeList(Map<String, Object> params);

    void createCkdd(JSONObject messageObj);

    void updateCkdd(JSONObject messageObj);

    void updateNum(JSONObject messageObj);

    JSONObject queryCkddById(String qbgzId);

    void addFileInfos(JSONObject fileInfo);

    int countCkddList(Map<String, Object> param);

    List<JSONObject> queryCkddFileList(Map<String, Object> param);

    List<JSONObject> queryZzr(Map<String, Object> param);

    void deleteCkddFile(Map<String, Object> param);

    void deleteCkdd(Map<String, Object> param);

    void createTime(JSONObject messageObj);

    void updateTime(JSONObject messageObj);

    void deleteTime(Map<String, Object> param);

    void createDetail(JSONObject messageObj);

    void updateDetail(JSONObject messageObj);

    void deleteDetail(Map<String, Object> param);

    JSONObject queryNowYearMaxNum(Map<String, Object> param);
    //结构化文档
    List<JSONObject> queryApiList(String searchValue);
}
