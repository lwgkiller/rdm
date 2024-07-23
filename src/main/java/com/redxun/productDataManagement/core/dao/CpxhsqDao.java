package com.redxun.productDataManagement.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface CpxhsqDao {
    // 表单的新增、更新和删除
    void insertCpxhsq(JSONObject param);

    void updateCpxhsq(JSONObject param);

    void updateCpxhsqNumber(JSONObject param);

    void deleteCpxhsq(JSONObject param);

    // 列表数据
    List<JSONObject> queryApplyList(Map<String, Object> params);

    // 某个表单的基本信息
    JSONObject queryApplyDetail(JSONObject params);

    List<JSONObject> queryDesignModelValid(JSONObject params);

}
