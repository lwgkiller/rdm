package com.redxun.productDataManagement.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public interface PDMOpsDevDao {
    List<Map<String, Object>> queryApplyList(Map<String, Object> params);



    void addFileInfos(JSONObject param);

    void updateFileInfos(JSONObject param);
    Map<String, Object> queryfileById(Map<String, Object> queryParam);

    void deleteFile(Map<String, Object> param);
}

