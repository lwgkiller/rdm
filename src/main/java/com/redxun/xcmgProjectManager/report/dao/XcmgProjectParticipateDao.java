package com.redxun.xcmgProjectManager.report.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * @author lcj
 */
@Repository
public interface XcmgProjectParticipateDao {
    List<JSONObject> queryParticipateList(Map<String, Object> params);

    int countParticipateList(Map<String, Object> params);
}
