package com.redxun.rdmZhgl.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ZljdOverviewDao {
    // ..
    List<JSONObject> queryValue(Map<String, Object> params);

    JSONObject queryZljdUnfinishNum(Map<String, Object> params);

    JSONObject queryZljdDelay(Map<String, Object> params);

    JSONObject queryZljdFinished(Map<String, Object> params);

    List<JSONObject> queryReTotalNum(Map<String, Object> params);

    List<JSONObject> queryZljdNumByuser(Map<String, Object> params);

    List<JSONObject> queryReNumByuser(Map<String, Object> params);

    List<JSONObject> queryApplyNum(Map<String, Object> params);

    List<JSONObject> queryNewApplyNum(Map<String, Object> params);

    List<JSONObject> queryIPCNum(Map<String, Object> params);

    List<JSONObject> queryIPCType(Map<String, Object> params);

    int countIPCNum(Map<String, Object> param);

    // ..
    Integer countFzsjQuery(JSONObject jsonObject);

    void insertFzsj(JSONObject jsonObject);

    void updateFzsj(JSONObject formDataJson);

    // ..
    void deleteFzsj(JSONObject jsonObject);

    JSONObject queryFzsjById(String id);

    List<JSONObject> queryFzsjFileList(JSONObject jsonObject);

    void delFzsjFileByBelongDetailId(JSONObject param);

    void deleteFzsjFileById(String id);

    void addFileInfos(JSONObject fileInfo);

    List<JSONObject> queryFzzx(String fzsjId);

    JSONObject getFzzxDetail(String zxclId);

    void createFzzx(JSONObject formDataJson);

    void updateFzzx(JSONObject formDataJson);

    JSONObject fzzxAddValid(String fzsjId);

    void deleteFzzx(String fzzxId);

    JSONObject queryLastFzzx(String fzsjId);

    List<JSONObject> getYearList();

    List<JSONObject> getNdtjList(JSONObject jsonObject);

    JSONObject queryFZLYEnum(String fzlbId);

    JSONObject queryNowYearMaxNum(Map<String, Object> param);

    List<JSONObject> queryFzsjByProject(Map<String, Object> param);
}
