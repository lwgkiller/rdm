package com.redxun.rdmZhgl.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface PatentInterpretationTechnologyBranchDao {
    //..
    List<JSONObject> dataListQuery(Map<String, Object> params);

    //..
    void deleteData(Map<String, Object> param);

    //..
    void insertData(JSONObject dataObj);

    //..
    void updateData(JSONObject dataObj);

    //..
    JSONObject queryDataById(String businessId);

    //..返回下级节点数量
    Integer getChildrenCount(String id);
}
