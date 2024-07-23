package com.redxun.embedsoft.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface TxxyAddDao {

    List<JSONObject> queryTxxyMsg(Map<String, Object> params);

    List<JSONObject> getTxxyMsgByUserId(String qbgzId);

}
