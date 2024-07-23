package com.redxun.rdmCommon.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author zz
 */
@Repository
public interface RdmHomeDao {
    List<JSONObject> queryWorkStatus(Map<String,Object> param);

    List<JSONObject> queryWorkStatusEnum();

    List<JSONObject> queryWorkStatusTimeEnum();

    List<JSONObject> queryWorkStatusUserEnum();

    List<JSONObject> queryLeaderScheduleJob(Map<String,Object> param);

    void insertWorkStatus(JSONObject param);

    void updateWorkStatus(JSONObject param);

    void deleteWorkStatus(JSONObject param);

    void updateIfNotice(JSONObject param);
}
