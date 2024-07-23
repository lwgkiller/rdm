package com.redxun.digitization.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface DatamigrationModelprogressDao {

    List<JSONObject> dataListQuery(Map<String, Object> params);


    Integer countDataListQuery(Map<String, Object> params);


    void deleteData(Map<String, Object> param);


    void insertData(Map<String, Object> params);


    void updateData(Map<String, Object> params);


}
