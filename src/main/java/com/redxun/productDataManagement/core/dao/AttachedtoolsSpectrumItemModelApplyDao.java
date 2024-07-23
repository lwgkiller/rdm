package com.redxun.productDataManagement.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface AttachedtoolsSpectrumItemModelApplyDao {
    //..
    List<JSONObject> dataListQuery(Map<String, Object> params);

    //..
    Integer countDataListQuery(Map<String, Object> params);

    //..
    void deleteBusiness(Map<String, Object> param);

    //..
    JSONObject getDataById(String id);

    //..
    void insertBusiness(JSONObject formData);

    //..
    void updateBusiness(JSONObject formData);

    //..
    List<JSONObject> queryDesignModelValid(JSONObject param);
}
