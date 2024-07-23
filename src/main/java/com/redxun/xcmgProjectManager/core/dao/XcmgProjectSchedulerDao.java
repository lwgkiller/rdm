package com.redxun.xcmgProjectManager.core.dao;

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
 * @since 2020/7/6 19:18
 */
@Repository
public interface XcmgProjectSchedulerDao {
    // 查询需要进行延迟判断的项目列表(进度追赶的不算)
    List<JSONObject> queryNormalRunningProjectList(Map<String, Object> param);

    // 查询项目的当前阶段的计划结束时间
    List<JSONObject> queryProjectCurrentPlanEndTime(List<Map<String, Object>> queryPlanParams);

    // 查询发送了延迟通知的项目数据
    List<JSONObject> queryDelayNoticeRecord(List<Map<String, Object>> queryParams);

    // 查询项目中的负责人、指导人
    List<JSONObject> queryProjectMemsByRoleName(Map<String, Object> queryParams);

    // 批量插入延误通知消息
    void batchInsertDelayNotice(List<JSONObject> delayNotices);

    List<JSONObject> getProjectListByInfo(JSONObject paramJson);

    String getProductIdByName(String productName);

    List<String> getProductIdsByName(String productName);
}
