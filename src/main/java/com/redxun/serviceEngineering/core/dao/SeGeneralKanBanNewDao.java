package com.redxun.serviceEngineering.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface SeGeneralKanBanNewDao {
    //..
    void insertGssShipmentCache(Map<String, Object> params);

    //..
    void updateGssShipmentCache(Map<String, Object> params);

    //..
    Integer countGssShipmentCacheListQuery(Map<String, Object> params);

    //..
    List<JSONObject> gssShipmentCacheListQuery(Map<String, Object> params);
}
