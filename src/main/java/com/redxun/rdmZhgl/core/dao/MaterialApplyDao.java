package com.redxun.rdmZhgl.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zz
 */
@Repository
public interface MaterialApplyDao {
    /**
     * 获取申请信息
     *
     * @param id
     * @return
     */
    Map<String, Object> getObjectById(String id);

    /**
     * 获取申请信息
     *
     * @param id
     * @return
     */
    JSONObject getJsonObject(String id);

    /**
     * 新增申请单
     *
     * @param params
     */
    void add(Map<String, Object> params);

    /**
     * 更新申请单
     *
     * @param params
     */
    void update(Map<String, Object> params);

    /**
     * 新增申请明细单
     *
     * @param params
     */
    void addDetail(JSONObject params);

    /**
     * 更新申请明细单
     *
     * @param params
     */
    void updateDetail(JSONObject params);

    /**
     * 删除明细
     *
     * @param id
     */
    void delDetailById(String id);

    /**
     * 删除明细
     *
     * @param applyId
     */
    void delDetailByApplyId(String applyId);

    /**
     * 获取申请明细单列表
     */
    List<JSONObject> getDetailList(String mainId);

    /**
     * 删除作审请单信息
     *
     * @param id
     */
    void delete(String id);

    /**
     * 查询流程列表
     *
     * @param params
     * @return
     */
    List<Map<String, Object>> queryList(Map<String, Object> params);


    /**
     * 查询物料详情列表
     *
     * @param params
     * @return
     */
    List<Map<String, Object>> getDetailReport(Map<String, Object> params);

    /**
     * 更新推送状态
     *
     * @param paramJson
     */
    void updateSendStatus(JSONObject paramJson);

    /**
     * 更新子表行号信息
     *
     * @param paramJson
     */
    void updateDetailLineNo(JSONObject paramJson);

    /**
     * 更新子表状态
     *
     * @param paramJson
     */
    void updateDetailStatus(JSONObject paramJson);

    /**
     * 获取物料单列表
     *
     * @param paramJson
     * @return list
     */
    List<JSONObject> getMaterialItemList(JSONObject paramJson);

    //..tdmII专用
    List<JSONObject> queryListForTdmII(JSONObject paramJson);

    Integer queryListCountForTdmII(JSONObject paramJson);


    List<JSONObject> getProjectByOrderCode(String orderCode);

}
