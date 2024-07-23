package com.redxun.meeting.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface HyglOverviewDao {
    // ..
    List<JSONObject> queryMeetingType(Map<String, Object> params);

    List<JSONObject> queryMeetingDelay(Map<String, Object> params);

    List<JSONObject> queryMeetingDelayLevel(Map<String, Object> params);

    List<JSONObject> queryRwfjList(Map<String, Object> params);

    List<JSONObject> queryMeetingByDept(Map<String, Object> params);

    List<JSONObject> queryZRMeetingDelay(Map<String, Object> params);

    List<JSONObject> queryZRMeetingDelayLevel(Map<String, Object> params);

    List<JSONObject> queryZRRwfjList(Map<String, Object> params);

}
