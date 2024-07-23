package com.redxun.wwrz.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @author zhangzhen
 */
@Repository
public interface WwrzTestPlanDao {

    /**
     * 根据id获取对象
     *
     * @param id
     * @return JSON
     * */
    JSONObject getObjectById(String id);
    /**
     * 查询信息
     *
     * @param params
     * @return List
     * */
    List<Map<String, Object>> query(Map<String, Object> params);
    /**
     * 保存
     * @param param
     * @return
     * */
    int add(Map<String, Object> param);

    /**
     * 更新
     *
     * @param params
     * @return
     */
    int update(Map<String, Object> params);
    /**
     * 批量删除
     *
     * @param params
     * */
    void batchDelete(Map<String, Object> params);
    /**
     * 获取计划列表
     *
     * @param paramJson
     * */
    List<JSONObject> getPlanListByIds(JSONObject paramJson);
    /**
     * 跟新计划状态
     *
     * @param paramJson
     * */
    void updatePlanStatus(JSONObject paramJson);
    /**
     * 查询计划编号
     *
     * @param planCodeDateStr
     * @return json
     * */
    JSONObject getPlanCode(String planCodeDateStr);
    /**
     * 获取待审批计划
     *
     * @param paramJson
     * @return list
     * */
    List<JSONObject> getPlanListByStatus(JSONObject paramJson);
}
