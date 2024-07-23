package com.redxun.newProductAssembly.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface NewproductAssemblyMaterialDao {
    //..
    List<JSONObject> modelListQuery(Map<String, Object> params);

    //..
    Integer countModelListQuery(Map<String, Object> params);

    //..
    List<JSONObject> materialListQuery(Map<String, Object> params);

    //..
    Integer countMaterialListQuery(Map<String, Object> params);

    //..
    List<JSONObject> materialSubListQuery(Map<String, Object> params);

    //..
    Integer countMaterialSubListQuery(Map<String, Object> params);

    //..
    void insertModel(JSONObject jsonObject);

    //..
    void updateModel(JSONObject jsonObject);

    //..
    void insertMaterial(JSONObject jsonObject);

    //..
    void updateMaterial(JSONObject jsonObject);

    //..
    void insertSubMaterial(JSONObject jsonObject);

    //..
    void updateSubMaterial(JSONObject jsonObject);

    //..
    void deleteModel(Map<String, Object> param);

    //..
    void deleteMaterial(Map<String, Object> param);

    //..
    void deleteSubMaterial(Map<String, Object> param);

    //..
    void deleteSubMaterialBy3(Map<String, Object> param);

    //..
    JSONObject getModelDetail(String businessId);

    //..
    JSONObject getMaterialDetail(String businessId);
}
