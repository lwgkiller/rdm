package com.redxun.serviceEngineering.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface BjInventoryDao {

    List<JSONObject> queryBjInventory(Map<String, Object> params);

    int countBjInventory(Map<String, Object> param);

    void createBjInventory(JSONObject messageObj);

    void updateBjInventory(JSONObject messageObj);

    JSONObject queryBjInventoryById(String qbgzId);
    
    void deleteBjInventory(Map<String, Object> param);

    void updateBjInventoryNumber(Map<String, Object> param);

    JSONObject queryMaxBjInventoryNum(Map<String, Object> param);

    void createDetail(JSONObject messageObj);

    void updateDetail(JSONObject messageObj);

    void deleteDetail(Map<String, Object> param);

    void deleteAllDetail(Map<String, Object> param);

    List<JSONObject> queryDetail(Map<String, Object> param);

    void batchInsertDetail(List<JSONObject> params);

    void addFileInfos(JSONObject fileInfo);

    List<JSONObject> queryBjInventoryFileList(Map<String, Object> param);

    void deleteBjInventoryFile(Map<String, Object> param);

}
