package com.redxun.serviceEngineering.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface PartsAtlasDao {
    List<JSONObject> dataListQuery(Map<String, Object> params);
    //..
    Integer getStorageCountByMaterialCode(String materialCode);
    //..
    Integer getStorageCountByMaterialCodeAndStorageTime(Map<String, Object> params);
    //..
    Integer getShipmentCountByMaterialCode(String materialCode);
    //..
    String getModelStatusByMaterialCodeAndStorageTime(Map<String, Object> params);
    //..
    String getMaterialDescription(Map<String, Object> params);
    //..根据action：storage，shipment和yearMonthBegin和yearMonthEnd获取指定区间内指定类型的数据列表
    List<JSONObject> getListByActionAndDaterange(Map<String, Object> params);

    //..获取机型零件图册制作总数
    Integer getPartsAtlasModelTotal();

    //..实例零件图册制作总数
    Integer getPartsAtlasInstanceTotal();


}
