package com.redxun.rdmZhgl.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zz
 * */
@Repository
public interface MonthWorkDelayApplyDao {
    /**
     * 获取申请信息
     * @param id
     * @return
     * */
    Map<String, Object> getObjectById(String id);

    /**
     * 新增申请单
     * @param params
     * */
    void add(Map<String, Object> params);

    /**
     * 更新申请单
     * @param params
     * */
    void update(Map<String, Object> params);

    /**
     * 删除作审请单信息
     * @param id
     * */
    void delete(String id);

    /**
     * 查询流程列表
     * @param params
     * @return
     * */
    List<Map<String, Object>> queryList(Map<String, Object> params);
    /**
     * 根据solid 查询流程key
     * @param params
     * @return
     * */
    List<Map<String,Object>> getBmpSolution(Map<String, Object> params);
    /**
     * 查询申请信息
     * @param params
     * @return
     * */
    List<Map<String, String>> queryAbolishInfo(Map<String, Object> params);
    /**
    * 根据deptId,yearMonth,applyType 获取是否已经存在
     *
     * @param jsonObject
     * @return list
    * */
    List<JSONObject> getApplyListByParam(JSONObject jsonObject);
    /**
     * 获取流程当前的任务名称
     *
     * @param projectId
     * @return list
     * */
    JSONObject getActTaskById(String projectId);
    /**
     * 根据任务获取审批信息
     *
     * @param projectId
     * @return list
     * */
    List<JSONObject> getProjectApplyInfo(String projectId);
    /**
     * 根据任务获取审批信息
     *
     * @param projectId
     * @return list
     * */
    List<JSONObject> getUnProjectApplyInfo(String projectId);

}
