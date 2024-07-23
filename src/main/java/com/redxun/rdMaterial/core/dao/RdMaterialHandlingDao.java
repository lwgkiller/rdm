package com.redxun.rdMaterial.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RdMaterialHandlingDao {
    //..
    List<JSONObject> dataListQuery(JSONObject params);

    //..
    Integer countDataListQuery(JSONObject params);

    //..
    JSONObject getDataById(String id);

    //..
    void deleteBusiness(JSONObject params);

    //..
    void insertBusiness(JSONObject jsonObject);

    //..
    void updateBusiness(JSONObject jsonObject);

    //..
    List<JSONObject> getItemList(JSONObject params);

    //..
    void insertItem(JSONObject jsonObject);

    //..
    void updateItem(JSONObject jsonObject);

    //..
    void deleteItems(JSONObject params);
}
