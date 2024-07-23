package com.redxun.standardManager.core.dao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface StandardConfigDao {
    JSONArray queryStandardCategoryByName(Map<String, Object> params);

    void saveStandardCategory(JSONObject jsonObject);

    void updateStandardCategory(JSONObject jsonObject);

    void delStandardCategoryByIds(Map<String, Object> params);

    List<Map<String, Object>> queryStandardCategory(Map<String, Object> params);

    List<Map<String, Object>> queryUsingCategoryIds(Map<String, Object> params);

    void saveStandardBelongDep(JSONObject jsonObject);

    void updateStandardBelongDep(JSONObject jsonObject);

    void delStandardBelongDepByIds(Map<String, Object> params);

    List<Map<String, Object>> queryStandardBelongDep(Map<String, Object> params);

    List<Map<String, Object>> queryUsingBelongDepIds(Map<String, Object> params);

    List<Map<String, Object>> queryStandardTemplateList(Map<String, Object> params);

    List<Map<String, Object>> queryStandardTemplateByName(Map<String, Object> params);

    void updateStandardTemplate(Map<String, Object> fileInfo);

    void createStandardTemplate(Map<String, Object> fileInfo);

    void deleteStandardTemplate(Map<String, Object> fileInfo);

    void updateTemplateDownloadNum(Map<String, Object> params);
}
