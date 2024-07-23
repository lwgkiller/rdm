package com.redxun.componentTest.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ComponentTestDocMgrDao {

    //..
    List<JSONObject> dataListQuery(JSONObject params);

    //..
    void deleteFile(Map<String, Object> param);

    //..
    void addFileInfos(JSONObject fileInfo);
}
