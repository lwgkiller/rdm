package com.redxun.rdMaterial.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RdMaterialInStorageDao {
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
    List<JSONObject> itemListQuery(JSONObject params);

    //..
    Integer countItemListQuery(JSONObject params);

    //..
    void insertItem(JSONObject jsonObject);

    //..
    void updateItem(JSONObject jsonObject);

    //..
    void deleteItems(JSONObject params);

    //..
    JSONObject getItemById(String id);
}
