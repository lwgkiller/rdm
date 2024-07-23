package com.redxun.meeting.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

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
public interface HyglDao {
    // ..
    List<JSONObject> getHyglType();


    List<JSONObject> getHyglList(Map<String, Object> params);

    // ..
    Integer countHyglList(Map<String, Object> params);

    // ..
    void insertMeetingData(JSONObject param);

    // ..
    void updateMeetingData(JSONObject param);

    // ..
    JSONObject queryMeetingById(String meetingId);


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

    List<JSONObject> getHyglFinishList(String meetingId);

    List<JSONObject> queryAllMeetingPlanList(Map<String, Object> param);

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

    JSONObject getDeptCode(JSONObject param);

    void updateMeetingPlanRespUserIds(JSONObject param);

}
