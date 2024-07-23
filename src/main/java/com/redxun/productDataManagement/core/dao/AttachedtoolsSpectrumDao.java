package com.redxun.productDataManagement.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachedtoolsSpectrumDao {
    //..
    List<JSONObject> modelListQuery(JSONObject params);

    //..
    Integer countModelListQuery(JSONObject params);

    //..
    JSONObject getModelDataById(String id);

    //..
    void insertModel(JSONObject jsonObject);

    //..
    void updateModel(JSONObject jsonObject);

    //..
    void deleteModel(JSONObject params);

    //..
    List<JSONObject> itemListQuery(JSONObject params);

    //..
    Integer countItemListQuery(JSONObject params);

    //..
    void insertItem(JSONObject jsonObject);

    //..
    void updateItem(JSONObject jsonObject);

    //..
    void updateItemStatus(JSONObject jsonObject);

    //..
    void deleteItems(JSONObject params);

    //..
    List<JSONObject> getFileListInfos(JSONObject params);

    //..
    void insertFileInfos(JSONObject jsonObject);

    //..
    void deleteFileInfos(JSONObject params);

    //..查询设计型号是否存在，如果传了id则查询不等于自己的
    List<JSONObject> checkDesignModelExist(JSONObject params);
}
