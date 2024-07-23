package com.redxun.serviceEngineering.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface MaintainabilityEvaluationFormDao {
    //..
    List<Map<String, Object>> dataListQuery(Map<String, Object> params);

    //..
    Integer countDataListQuery(Map<String, Object> params);


    //..
    List<String> getFileNamesListByMainId(Object id);

    //..
    List<JSONObject> queryFileList(Map<String, Object> param);

    //..
    void deleteBusinessFile(Map<String, Object> param);

    //..
    void deleteBusiness(Map<String, Object> param);

    //..
    JSONObject queryDetailById(String businessId);

    //..
    void insertBusiness(JSONObject formData);

    //..
    void updateBusiness(JSONObject formData);

    //..
    void addFileInfos(JSONObject param);

    //获取挖掘机械研究院所有的所长
    List<Map<String, String>> getAllSuozhang(Map<String, Object> params);

}
