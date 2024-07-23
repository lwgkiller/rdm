package com.redxun.embedsoft.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface DmgzDao {
    List<JSONObject> queryGzdmMsg(Map<String, Object> params);

    List<JSONObject> getDmgzMsgByUserId(String qbgzId);
}
