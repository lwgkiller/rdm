package com.redxun.serviceEngineering.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface YsInventoryDao {

    List<JSONObject> queryYsInventory(Map<String, Object> params);

    int countYsInventory(Map<String, Object> param);

    void createYsInventory(JSONObject messageObj);

    void updateYsInventory(JSONObject messageObj);

    JSONObject queryYsInventoryById(String qbgzId);


    void deleteYsInventory(Map<String, Object> param);

    void updateYsInventoryNumber(Map<String, Object> param);

    JSONObject queryMaxYsInventoryNum(Map<String, Object> param);

    void createDetail(JSONObject messageObj);

    void updateDetail(JSONObject messageObj);

    void deleteDetail(Map<String, Object> param);

    void deleteAllDetail(Map<String, Object> param);

    List<JSONObject> queryDetail(Map<String, Object> param);

    void batchInsertDetail(List<JSONObject> params);

}
