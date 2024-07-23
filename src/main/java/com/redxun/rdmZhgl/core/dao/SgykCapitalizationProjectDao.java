package com.redxun.rdmZhgl.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface SgykCapitalizationProjectDao {
    //..
    List<JSONObject> dataListQuery(Map<String, Object> params);

    //..
    Integer countDataListQuery(Map<String, Object> params);

    //..
    void deleteData(Map<String, Object> param);

    //..
    List<JSONObject> getListByProjectNameAndPatentName(Map<String, String> map);

    //..
    void insertData(Map<String, Object> params);

    //..
    void updateDataByProjectNameAndPatentName(Map<String, Object> params);
}
