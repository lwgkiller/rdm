package com.redxun.rdmZhgl.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface SgykDao {
    //..
    List<JSONObject> templateList();

    //..
    void saveTemplate(JSONObject param);

    //..
    void updateTemplate(JSONObject param);

    //..
    void delTemplateByIds(Map<String, Object> params);

    //..
    List<JSONObject> sgykList(Map<String, Object> params);

    //..
    List<JSONObject> sgykOneYearList(Map<String, Object> params);

    //..
    List<JSONObject> sgykCycleReportList(Map<String, Object> params);

    //..
    void saveSgykBase(JSONObject param);

    //..
    void saveSgykDetail(JSONObject sgykDetail);

    //..

    void updateSgykDetail(JSONObject sgykDetail);

    //..
    List<JSONObject> sgykDetailListByMainId(String sgykId);
}
