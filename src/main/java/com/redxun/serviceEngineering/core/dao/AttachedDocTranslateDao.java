/**
 * Created by zhangwentao on 2022-3-15.
 */
package com.redxun.serviceEngineering.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface AttachedDocTranslateDao {
    //..
    List<Map<String, Object>> dataListQuery(Map<String, Object> params);

    //..
    Integer countDataListQuery(Map<String, Object> params);

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

    //..
    void updateFileREFInfos(JSONObject param);

    //..
    List<Map<String, Object>> dataListQuerySdltm(Map<String, Object> params);

    //..
    int countDataListQuerySdltm(Map<String, Object> params);
}
