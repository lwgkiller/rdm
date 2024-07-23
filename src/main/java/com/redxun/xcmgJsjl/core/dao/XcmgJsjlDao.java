package com.redxun.xcmgJsjl.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * 应用模块名称
 * <p>
 * 代码描述
 * <p>
 * Copyright: Copyright (C) 2020 XXX, Inc. All rights reserved.
 * <p>
 * Company: 徐工挖掘机械有限公司
 * <p>
 *
 * @author liangchuanjiang
 * @since 2020/11/23 19:02
 */
@Repository
public interface XcmgJsjlDao {
    List<JSONObject> dataListQuery(Map<String, Object> params);

    Integer countDataListQuery(Map<String, Object> params);

    void insertJsjlData(JSONObject param);

    void updateJsjlData(JSONObject param);

    JSONObject queryJsjlById(String jsjlId);

    void deleteJsjl(Map<String, Object> params);

    // ..
    List<JSONObject> queryMeetingPlanList(String meetingId);

    // ..
    void insertMeetingPlanData(JSONObject meetingPlanObj);

    // ..
    void updateMeetingPlanData(JSONObject meetingPlanObj);

    // ..
    void deleteOneMeetingPlan(String id);

    // ..
    void deleteMeetingPlanByMeetingId(String meetingid);

    // ..
    List<String> getFileNamesListByMainId(String id);

    JSONObject getSeqJsjl(JSONObject param);

    void updateMeetingPlanRespUserIds(JSONObject param);
}
