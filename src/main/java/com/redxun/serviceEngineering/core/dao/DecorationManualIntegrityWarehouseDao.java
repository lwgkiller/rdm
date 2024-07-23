package com.redxun.serviceEngineering.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface DecorationManualIntegrityWarehouseDao {

    List<Map<String, Object>> warehouseDataListQuery(Map<String, Object> params);

    Integer countWarehouseDataListQuery(Map<String, Object> params);

    JSONObject queryByMaterialCode(String materialCode);

    void deleteStoreItem(Map<String, Object> param);


    void insertWarehouse(JSONObject jsonObject);


    void updateWarehouse(JSONObject jsonObject);

    void updateWareItem(Map<String, Object> param);

}
