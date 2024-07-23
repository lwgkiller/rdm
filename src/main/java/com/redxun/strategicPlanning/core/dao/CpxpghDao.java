package com.redxun.strategicPlanning.core.dao;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CpxpghDao {
    //..
    List<JSONObject> cpxpghListQuery(Map <String, Object> params);

    //..
    Integer countCpxpghQuery(Map <String, Object> params);

    void insertCpxpgh(JSONObject formData);

    void updateCpxpgh(JSONObject formDataJson);
    //..
    void deleteCpxpgh(Map <String, Object> param);


    // 查询部门负责人
    List<Map<String, String>> getDepRespMans(Map<String, Object> params);


    JSONObject getCpxpghDetail(String cpxpghId);

    List<JSONObject> queryChilds(JSONObject jsonObject);

    void insertChilds(Map<String, Object> params);

    void updateChilds(JSONObject object);

    void delChildsById(JSONObject object);

    void delChildsByCpxpghId(Map<String, Object> params);

    void addFileInfos(JSONObject fileInfo);

    List<JSONObject> queryCpxpghFileList(Map<String, Object> params);

    void delCpxpghFiles(String id);

    void delFileByCpxpghId(Map<String, Object> param);

    List<JSONObject> cpxpghFinalListQuery(Map<String, Object> params);

    int countCpxpghFinalQuery(Map<String, Object> params);

    List<JSONObject> queryChildsBycpxpghId(String cpxpghId);

    int wheterRunning(String finalId);

}
