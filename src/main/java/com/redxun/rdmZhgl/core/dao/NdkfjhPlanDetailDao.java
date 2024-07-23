package com.redxun.rdmZhgl.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zhangzhen
 */
@Repository
public interface NdkfjhPlanDetailDao {

    /**
     * 查询
     *
     * @param params
     * @return List
     * @Mapkey Map<String, Object>
     * */
    List<Map<String, Object>> query(Map<String, Object> params);

    /**
     * 保存
     * @param jsonObject
     * @return
     * */
    int addObject(JSONObject jsonObject);

    /**
     * 更新
     *
     * @param params
     * @return
     */
    int updateObject(Map<String, Object> params);
    /**
     * 批量删除
     *
     * @param params
     * */
    void batchDelete(Map<String, Object> params);
    /**
     * 通过id删除计划详情信息
     * @param id
     * */
    void delById(String id);
    /**
     * 通过mainId删除历史信息表
     * @param mainId
     * */
    void delHistoryById(String mainId);
    /**
     * 根据id获取详情信息
     *
     * @return JSONObject
     * @param id
     * */
    JSONObject getObjectById(String id);
    /**
     * 根据detailId获取详情信息
     *
     * @return JSONObject
     * @param detailId
     * */
    JSONObject getObjectByDetailId(String detailId);
    /**
     * 根据条件获取详情信息
     *
     * @return JSONObject
     * @param paramJson
     * */
    JSONObject getObjectByParam(JSONObject paramJson);
    /**
     * 获取特殊订单列表
     *
     * @param params
     * @return List
     * @Mapkey Map<String, Object>
     * */
    List<Map<String, Object>> getSpecialOrderList(Map<String, Object> params);
    /**
     * 获取新品试制列表
     *
     * @param params
     * @return List
     * @Mapkey Map<String, Object>
     * */
    List<Map<String, Object>> getNewProductList(Map<String, Object> params);
    /**
     * 获取科技项目列表
     *
     * @param params
     * @return List
     * @Mapkey Map<String, Object>
     * */
    List<Map<String, Object>> getProjectList(Map<String, Object> params);

    /**
     * 获取新品试制信息
     * @param id
     * @return map
     * */
    Map<String,Object> getNewProductObj(String id);

    /**
     * 获取计划详情列表
     *
     * @param paramJson
     * @return list
     * */
    List<Map<String, Object>> getPlanDetailList(JSONObject paramJson);
    /**
     * 获取项目信息
     *
     * @param projectId
     * @return json
     * */
    JSONObject getProjectObj(String projectId);

    /**
     * 插入过程历史表
     * @param params
     * */
    void addProcessHistory(Map<String, Object> params);
    /**
     * 更新过程历史表
     * @param params
     * */
    void updateProcessHistory(Map<String, Object> params);
    /**
     * 获取历史表信息
     * @param paramJson
     * @return json
     * */
    JSONObject getProcessHistory(JSONObject paramJson);

    /**
     * 通过月份获取延期列表
     *
     * @param paramJson
     * @return list
     * */
    List<JSONObject> getProcessList(JSONObject paramJson);

    /**
     * 获取各个责任所长得计划
     *
     * @param paramJson
     * @return json
     * */
    List<JSONObject> reportResponseFinishRate(JSONObject paramJson);
    /**
    * 根据责任所长获取计划个数
     *
     * @param paramJson
     * @return int
    * */
    Integer getPlanNumByResponse(JSONObject paramJson);

    /**
     * 获取各个所的计划
     *
     * @param paramJson
     * @return json
     * */
    List<JSONObject> reportDeptFinishRate(JSONObject paramJson);
    /**
     * 根据部门id获取计划个数
     *
     * @param paramJson
     * @return int
     * */
    Integer getPlanNumByDept(JSONObject paramJson);
    /**
     * 获取统计报表列表
     * */
    List<Map<String, Object>> getReportDetailList(JSONObject paramJson);

}
