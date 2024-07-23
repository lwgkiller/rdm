package com.redxun.embedsoft.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface StorageAreaManagementDao {

    List<JSONObject> queryStorageAreaMsg(Map<String, Object> params);

    List<JSONObject> getStorageAreaMsgByUserId(String qbgzId);

}
