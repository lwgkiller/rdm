package com.redxun.xcmgProjectManager.report.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author lcj
 */
@Repository
public interface XcmgProjectReportDao {
    /**
     * 根据类别统计项目个数
     * 
     * @param params
     * @return list
     */
    List<Map<String, Object>> countProjectNumByCategoryLevel(JSONObject params);

    /**
     * 查询所有部门
     * 
     * @param params
     * @return list
     */
    List<Map<String, Object>> queryAllDeps(Map<String, Object> params);

    /**
     * 统计运行项目
     * 
     * @param params
     * @return list
     */
    List<Map<String, Object>> countRunningProjProcess(Map<String, Object> params);

    /**
     * 统计月度积分
     * 
     * @param params
     * @return list
     */
    List<Map<String, Object>> countYdjf(Map<String, Object> params);

    /**
     * 统计积分
     * 
     * @param params
     * @return list
     */
    List<Map<String, Object>> countJfzb(Map<String, Object> params);

    /**
     * 统计项目情况
     * 
     * @param params
     * @return list
     */
    List<Map<String, Object>> projectProgressReport(Map<String, Object> params);

    /**
     * 个人积分列表
     * 
     * @param queryParams
     * @return list
     */
    List<Map<String, Object>> personScoreList(Map<String, Object> queryParams);

    /**
     * 部门积分列表
     * 
     * @param queryParams
     * @return list
     */
    List<Map<String, Object>> deptScoreList(Map<String, Object> queryParams);

    /**
     * 办公桌面获取个人积分
     * 
     * @param queryParams
     * @return list
     */
    List<Map<String, Object>> deskHomePersonScore(Map<String, Object> queryParams);

    /**
     * 办公桌面统计类别
     * 
     * @param params
     * @return list
     */
    List<Map<String, Object>> deskHomeProjectNumByCategoryLevel(JSONObject params);

    /**
     * 获取个人参与项目列表
     * 
     * @param params
     * @return list
     */
    List<Map<String, Object>> queryPersonProjectList(Map<String, Object> params);

    /**
     * 查询项目成果计划
     * 
     * @param params
     * @return
     */
    List<JSONObject> queryAchievements(Map<String, Object> params);

    // 查询预估分列表
    List<JSONObject> queryEvaluateScoreList(Map<String, Object> params);

    // 统计预估分列表总数
    int countEvaluateScoreList(Map<String, Object> params);

    // 通过计划结束时间筛选出项目的阶段信息
    List<JSONObject> queryStageByPlanEndTime(Map<String, Object> params);


    List<JSONObject> queryUnfinishStageByPlanEndTime(Map<String, Object> params);

    List<JSONObject> queryDeptByName(Map<String, Object> params);

    List<JSONObject> queryProjectProgress(Map<String, Object> params);

    List<JSONObject> queryProjectCurrentProgress(Map<String, Object> params);

    List<JSONObject> queryChangeNumber(Map<String, Object> params);
    List<Map<String, Object>> queryPlanEndTimeById(Map<String, Object> params);

    //批量
    List<Map<String,Object>> queryPlanEndTimeByIdBatch(Map<String, Object> params);

    List<JSONObject> queryProjectMemberByIds(Map<String, Object> params);
}
