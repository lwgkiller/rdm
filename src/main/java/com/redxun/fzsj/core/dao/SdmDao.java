package com.redxun.fzsj.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author zhangzhen
 */
@Repository
public interface SdmDao {
    /**
     * 查询
     *
     * @MapKey params<>
     * @return List
     * */
    List<Map<String, Object>> getSdmProject(Map<String, Object> params);
    /**
     * 创建推送项目信息记录
     *
     * @param projectObj
     * */
    void addSdmProject(JSONObject projectObj);
    /**
     * 更新推送项目信息记录
     *
     * @param projectObj
     * */
    void updateSdmProject(JSONObject projectObj);
    /**
     * 获取推送项目信息
     *
     * @param applyId
     * @return json
     * */
    JSONObject getSdmProjectObj(String applyId);
    /**
     * 查询委托书推送信息
     *
     * @MapKey params<>
     * @return List
     * */
    List<Map<String, Object>> getSdmAssignment(Map<String, Object> params);
    /**
     * 创建推送委托书信息记录
     *
     * @param projectObj
     * */
    void addSdmAssignment(JSONObject projectObj);
    /**
     * 更新推送委托书信息记录
     *
     * @param projectObj
     * */
    void updateSdmAssignment(JSONObject projectObj);
    /**
     * 获取推送委托书信息
     *
     * @param applyId
     * @return json
     * */
    JSONObject getSdmAssignmentObj(String applyId);
    /**
     * 查询仿真任务推送信息
     *
     * @MapKey params<>
     * @return List
     * */
    List<Map<String, Object>> getSdmTaskList(Map<String, Object> params);
    /**
     * 创建推送仿真任务信息记录
     *
     * @param projectObj
     * */
    void addSdmTask(JSONObject projectObj);
    /**
     * 更新推送仿真任务信息记录
     *
     * @param projectObj
     * */
    void updateSdmTask(JSONObject projectObj);
    /**
     * 获取推送仿真任务信息
     *
     * @param applyId
     * @return json
     * */
    JSONObject getSdmTaskObj(String applyId);

    /**
     * 创建推送打分信息记录
     *
     * @param projectObj
     * */
    void addSdmScore(JSONObject projectObj);
    /**
     * 更新推送打分信息记录
     *
     * @param projectObj
     * */
    void updateSdmScore(JSONObject projectObj);
    /**
     * 获取推送打分息
     *
     * @param applyId
     * @return json
     * */
    JSONObject getSdmScoreObj(String applyId);
    /**
     * 创建推送采纳信息记录
     *
     * @param projectObj
     * */
    void addSdmAdopt(JSONObject projectObj);
    /**
     * 更新推送采纳信息记录
     *
     * @param projectObj
     * */
    void updateSdmAdopt(JSONObject projectObj);
    /**
     * 获取推送采纳记录
     *
     * @param applyId
     * @return json
     * */
    JSONObject getSdmAdoptObj(String applyId);
    /**
     * 创建推送采纳信息记录
     *
     * @param projectObj
     * */
    void addSdmImplement(JSONObject projectObj);
    /**
     * 更新推送采纳信息记录
     *
     * @param projectObj
     * */
    void updateSdmImplement(JSONObject projectObj);
    /**
     * 获取推送采纳记录
     *
     * @param applyId
     * @return json
     * */
    JSONObject getSdmImplementObj(String applyId);
    /**
     *  创建推送仿真报告信息记录
     *
     * @param projectObj
     * */
    void addSdmReport(JSONObject projectObj);
    /**
     * 更新推送仿真报告信息记录
     *
     * @param projectObj
     * */
    void updateSdmReport(JSONObject projectObj);
    /**
     * 获取推仿真报告分息
     *
     * @param applyId
     * @return json
     * */
    JSONObject getSdmReportObj(String applyId);
    /**
     * 查询
     *
     * @MapKey params<>
     * @return List
     * */
    List<Map<String, Object>> getSdmReport(Map<String, Object> params);

    /**
     * 查询
     *
     * @MapKey params<>
     * @return List
     * */
    List<Map<String, Object>> getSdmScore(Map<String, Object> params);
    /**
     * 查询
     *
     * @MapKey params<>
     * @return List
     * */
    List<Map<String, Object>> getSdmAdopt(Map<String, Object> params);
    /**
     * 查询
     *
     * @MapKey params<>
     * @return List
     * */
    List<Map<String, Object>> getSdmImplement(Map<String, Object> params);

    /**
     *  接收SDM仿真报告
     *
     * @param projectObj
     * */
    void addSdmReceiveReport(JSONObject projectObj);

    /**
     * 获取SDM仿真报告
     *
     * @param paramJson
     * @return json
     * */
    JSONObject getSdmReceiveReportObj(JSONObject paramJson);

    /**
     * 查询仿真报告接收情况
     *
     * @MapKey params<>
     * @return List
     * */
    List<Map<String, Object>> getSdmReceiveReportList(Map<String, Object> params);
}
