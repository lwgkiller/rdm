package com.redxun.fzsj.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface FzsjDao {
    // ..
    List<JSONObject> fzsjListQuery(JSONObject params);

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
