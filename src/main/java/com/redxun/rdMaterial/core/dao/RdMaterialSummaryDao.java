package com.redxun.rdMaterial.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RdMaterialSummaryDao {
    //..
    List<JSONObject> summaryListQuery(JSONObject params);

    //..
    Integer countSummaryListQuery(JSONObject params);

    //..
    List<JSONObject> itemListQuery(JSONObject params);

    //..
    Integer countItemListQuery(JSONObject params);

    //..
    List<JSONObject> handingItemListQuery(JSONObject params);
}
