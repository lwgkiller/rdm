package com.redxun.rdmZhgl.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @author zhangzhen
 */
@Repository
public interface ProductDao {

    /**
     * 查询
     *
     * @param params
     * @return List
     */
    List<Map<String, Object>> query(Map<String, Object> params);

    /**
     * 获取主表单列表
     *
     * @return list
     */
    List<Map<String, Object>> getBaseList(Map<String, Object> params);

    /**
     * 保存
     * 
     * @param param
     * @return
     */
    int addObject(Map<String, Object> param);

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
     */
    void batchDelete(Map<String, Object> params);

    /**
     * 批量子计划
     *
     * @param params
     */
    void batchDeleteItems(Map<String, Object> params);

    /**
     * 根据id获取详情信息
     *
     * @return JSONObject
     * @param id
     */
    JSONObject getObjectById(String id);

    /**
     * 保存
     * 
     * @param param
     * @return
     */
    int addItem(Map<String, Object> param);

    /**
     * 更新
     *
     * @param params
     * @return
     */
    int updateItem(Map<String, Object> params);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    int delItem(String id);

    /**
     * 查询详情列表
     *
     * @param jsonObject
     * @return
     */
    List<JSONObject> getItemList(JSONObject jsonObject);

    /**
     * 更新时间
     *
     * @param jsonObject
     */
    void updateDate(JSONObject jsonObject);

    /**
     * 1
     * 
     * @param jsonObject
     * @return j
     */
    JSONArray getJsonArray(JSONObject jsonObject);

    /**
     * 新增文件
     *
     * @param params
     */
    void addFileInfos(Map<String, Object> params);

    /**
     * 查询证明文件
     *
     * @param workPlanId
     * @return
     */
    List<JSONObject> getFileListByMainId(String workPlanId);

    /**
     * 删除项目文件
     *
     * @param params
     */
    void deleteFileByIds(Map<String, Object> params);

    /**
     * 查询主表信息
     *
     * @return
     */
    List<JSONObject> getBaseInfoList();

    /**
     * 查询子项
     *
     * @param mainId
     * @return
     */
    List<JSONObject> getItemListByMainId(String mainId);

    /**
     * 更新时间
     *
     * @param jsonObject
     */
    void updatePlanDate(JSONObject jsonObject);

    /**
     * 更新计划时间
     *
     * @param jsonObject
     */
    void updateOrgDate(JSONObject jsonObject);

    /**
     * 获取条目对象
     *
     * @param jsonObject
     * @return json
     */
    Map<String, Object> getItemObject(JSONObject jsonObject);

    /**
     * 获取进度状态报表
     *
     * @param jsonObject
     * @return json
     */
    List<JSONObject> getReportJdzt(JSONObject jsonObject);

    /**
     * 获取进度统计报表
     *
     * @param jsonObject
     * @return json
     */
    JSONObject getReportJdtj(JSONObject jsonObject);

    /**
     * 获取
     *
     * @param mainId
     * @return json
     */
    JSONObject getProductObjByMainId(String mainId);

    /**
     * 获取
     *
     * @param paramJson
     * @return json
     */
    List<JSONObject> getProductObjByProductIdAndYear(JSONObject paramJson);

    /**
     * 根据设计型号名，从产品型谱中找到id，根据id从新品试制中找到是否本年度已存在关联
     * @param paramJson
     * @return
     */
    List<JSONObject> queryXpszAndXpByProductName(JSONObject paramJson);

    /**
     * 获取
     *
     * @param mainId
     * @return json
     */
    JSONObject getProductInfoByMainId(String mainId);

    /**
     * 查询变更记录
     *
     * @param mainId
     * @return
     */
    List<JSONObject> getChangeRecordListByMainId(String mainId);

    /**
     * 获取进度状态报表
     *
     * @param jsonObject
     * @return json
     */
    List<JSONObject> getProducts(JSONObject jsonObject);

    /**
     * 获取时间
     *
     * @param mainId
     * @return json
     */
    JSONObject getPlanDate(String mainId);

    /**
     * 根据关联的型谱id获取尚未关闭的新品试制信息，认为应该只有一个
     *
     * @param productId
     * @return json
     */
    JSONObject getNewProductInfo(String productId);

    /**
     * 根据产品id获取科技项目信息
     * 
     * @param productId
     * @return json
     */
    JSONObject getProjectByProductId(String productId);

    /**
     * 找到该产品对应交付物的负责人
     *
     * @param paramJson
     * @return json
     */
    JSONObject getDeliveryResponse(JSONObject paramJson);

    /**
     * 找到阶段是否有完成时间
     *
     * @param paraJson
     * @return String
     */
    String getStageFinishDate(JSONObject paraJson);



    List<JSONObject> getDeliveryByUserId(JSONObject paraJson);


    List<JSONObject> getFinishDeliveryByProject(String projectId);
}
