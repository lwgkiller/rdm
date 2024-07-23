package com.redxun.rdmZhgl.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface TxlDao {
    void updateTxl(JSONObject params);

    void insertTxl(JSONObject params);

    void deleteTxl(Map<String, Object> params);

    // 查询通讯录
    List<JSONObject> queryTxlList(Map<String, Object> params);

    int countTxlList(Map<String, Object> params);

    List<JSONObject> queryCompany();

    List<JSONObject> queryDeptNameLevelOne(Map<String, Object> params);

    List<JSONObject> queryDeptNameLevelTwo(Map<String, Object> params);

    JSONObject queryTxlById(String id);

}
