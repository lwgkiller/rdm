package com.redxun.serviceEngineering.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface WgjzlsjDao {
    //..
    List<Map<String, Object>> dataListQuery(Map<String, Object> params);

    //..
    Integer countDataListQuery(Map<String, Object> params);

    //..
    void deleteData(Map<String, Object> param);

    //..
    void insertData(JSONObject formDataJson);

    //..
    void updateData(JSONObject formDataJson);

    //..
    JSONObject queryById(String id);
    //搜流程签审意见
    List<JSONObject> queryNodeRemark(JSONObject idJson);

    //..
    JSONObject queryByMaterialCodeAndSupplier(JSONObject formDataJson);

    //李文光大改未涉及
    List<Map<String, Object>> queryDepartmentReport(Map<String, Object> params);

    //李文光大改未涉及
    Integer queryDepartmentReportCount(Map<String, Object> params);

    //李文光大改未涉及
    List<Map<String, Object>> querySupplierReport(Map<String, Object> params);

    //李文光大改未涉及
    Integer querySupplierReportCount(Map<String, Object> params);
    //李文光大改未涉及
    JSONObject queryWgjzlsjChart();

    //..
    JSONObject getSeq(JSONObject param);

    //..
    void insertSeq(JSONObject seq);

    //..
    void updateSeq(JSONObject seq);

    //李文光大改未涉及
    List<JSONObject> queryApiList(String searchValue);


}
