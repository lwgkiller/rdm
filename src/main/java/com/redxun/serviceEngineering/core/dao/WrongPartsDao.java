package com.redxun.serviceEngineering.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface WrongPartsDao {
    //..
    List<Map<String, Object>> dataListQuery(Map<String, Object> params);

    //..
    Integer countDataListQuery(Map<String, Object> params);

    //..
    void deleteData(Map<String, Object> param);

    //..作废
    void insertData(Map<String, Object> params);

    //..作废
    Integer getGroupCountByName(String depname);

    //..作废
    String getGroupIdByName(String cellValue);

    //..
    void addFileInfos(JSONObject fileInfo);

    //..
    List<JSONObject> queryCjzgFileList(Map<String, Object> params);

    //..
    void deleteFileById(String id);

    //..
    void delFileByWrongPartsId(Map<String, Object> param);

    //..
    List<JSONObject> geRespUserByDeptId(Map<String, Object> param);

    //..
    void updateData(JSONObject formDataJson);

    //..
    JSONObject queryCjzgById(String id);

    //..
    void insertCjzg(JSONObject formData);
}
