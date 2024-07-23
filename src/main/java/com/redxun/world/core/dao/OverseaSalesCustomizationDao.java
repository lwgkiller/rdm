package com.redxun.world.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface OverseaSalesCustomizationDao {
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
    List<JSONObject> configTreeQuery(JSONObject params);

    //..
    void insertConfigNode(JSONObject jsonObject);

    //..
    void updateConfigNode(JSONObject jsonObject);

    //..
    void deleteConfigNode(JSONObject params);

    //..
    List<JSONObject> getFileListInfos(JSONObject params);

    //..
    void insertFileInfos(JSONObject jsonObject);

    //..
    void deleteileInfos(JSONObject params);
}
