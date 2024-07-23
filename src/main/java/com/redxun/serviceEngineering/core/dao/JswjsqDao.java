package com.redxun.serviceEngineering.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface JswjsqDao {

    // 表单的新增、更新和删除
    void insertJswjsq(JSONObject param);

    void updateJswjsq(JSONObject param);

    void updateJswjsqNumber(JSONObject param);

    void deleteJswjsq(JSONObject param);

    // 列表数据
    List<JSONObject> queryApplyList(Map<String, Object> params);

    // 某个表单的基本信息
    JSONObject queryApplyDetail(JSONObject params);

    JSONObject getUserInfoByPartsType(Map<String, Object> param);



}
