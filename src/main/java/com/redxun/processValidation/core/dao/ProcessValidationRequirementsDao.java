package com.redxun.processValidation.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ProcessValidationRequirementsDao {
    //..
    List<Map<String, Object>> applyDataListQuery(Map<String, Object> params);

    //..
    Integer countApplyDataListQuery(Map<String, Object> params);

    //..
    void deleteApply(Map<String, Object> param);

    //..
    JSONObject queryApplyDetailById(String businessId);

    //..
    void insertApply(JSONObject formData);

    //..
    void updateApply(JSONObject formData);
}
