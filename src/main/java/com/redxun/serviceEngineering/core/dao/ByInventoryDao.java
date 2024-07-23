package com.redxun.serviceEngineering.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ByInventoryDao {

    List<JSONObject> queryByInventory(Map<String, Object> params);

    int countByInventory(Map<String, Object> param);

    void createByInventory(JSONObject messageObj);

    void updateByInventory(JSONObject messageObj);

    JSONObject queryByInventoryById(String qbgzId);


    void deleteByInventory(Map<String, Object> param);

    void updateByInventoryNumber(Map<String, Object> param);

    JSONObject queryMaxByInventoryNum(Map<String, Object> param);

    void createDetail(JSONObject messageObj);

    void updateDetail(JSONObject messageObj);

    void deleteDetail(Map<String, Object> param);

    void deleteAllDetail(Map<String, Object> param);

    List<JSONObject> queryDetail(Map<String, Object> param);

    void batchInsertDetail(List<JSONObject> params);

}
