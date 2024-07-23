package com.redxun.presaleDocuments.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PresaleDocumentsDao {
    //..
    List<JSONObject> dataListQuery(JSONObject params);

    //..
    Integer countDataListQuery(JSONObject params);

    //..
    JSONObject getDataById(String id);

    //..
    JSONObject getDataByApplyId(String applyId);

    //..
    void insertBusiness(JSONObject jsonObject);

    //..
    void updateBusiness(JSONObject jsonObject);

    //..
    void deleteBusiness(JSONObject params);
}
