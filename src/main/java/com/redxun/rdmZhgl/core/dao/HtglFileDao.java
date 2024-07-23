package com.redxun.rdmZhgl.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface HtglFileDao {
    //..
    void addFileInfos(JSONObject fileInfo);

    //..
    void deleteFileById(String id);

    //..
    void deleteFileByContractIds(Map<String, Object> param);

    //..
    List<JSONObject> queryFilesByContractIds(Map<String, Object> param);
}
