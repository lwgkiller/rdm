package com.redxun.rdmZhgl.core.dao;

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
 * @author liwenguang
 */
@Repository
public interface HyglInternalDao {
    // ..
    List<JSONObject> dataListQuery(Map<String, Object> params);

    // ..
    Integer countDataListQuery(Map<String, Object> params);

    // ..
    void insertMeetingData(JSONObject param);

    // ..
    void updateMeetingData(JSONObject param);

    // ..
    JSONObject queryMeetingById(String meetingId);

    // ..
    List<String> getFileNamesListByMeetingId(String id);

    // ..
    List<JSONObject> queryMeetingFileList(Map<String, Object> param);

    // .
    void deleteMeetingFile(Map<String, Object> param);

    // ..
    void deleteMeeting(Map<String, Object> param);

    // ..
    void addMeetingFileInfos(JSONObject param);

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
    JSONObject getSeqInternal(JSONObject param);

    List<JSONObject> getDicByParam(JSONObject param);

    void updateMeetingPlanRespUserIds(JSONObject param);

}
