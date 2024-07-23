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
public interface MonthWorkDao {

    /**
     *获取个人项目信息
     *
     * @param jsonObject
     * @return json
     * */
    List<Map<String,Object>> getPersonProjectList(JSONObject jsonObject);
    /**
     *获取项目信息
     *
     * @param projectId
     * @return json
     * */
    JSONObject getProjectById(String projectId);
    /**
     *获取阶段信息
     *
     * @param jsonObject
     * @return json
     * */
    JSONArray getStageList(JSONObject jsonObject);

    /**
     * 查询
     *
     * @param params
     * @return List
     * */
    List<Map<String, Object>> getPlanLst(Map<String, Object> params);

    /**
     * 保存
     * @param param
     * @return
     * */
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
     * */
    void batchDelete(Map<String, Object> params);
    /**
     * 批量修改
     *
     * @param params
     * */
    void batchUpdate(Map<String, Object> params);
    /**
     * 批量子计划
     *
     * @param params
     * */
    void batchDeleteItems(Map<String, Object> params);
    /**
     * 批量子计划
     *
     * @param params
     * */
    void batchDeleteItemsById(Map<String, Object> params);
    /**
     * 根据id获取详情信息
     *
     * @return JSONObject
     * @param id
     * */
    JSONObject getObjectById(String id);
    /**
     * 保存
     * @param param
     * @return
     * */
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
     *首页公司级计划明细 报表
     *
     * @param jsonObject
     * @return json
     * */
    List<Map<String, Object>> reportCompanyPlan(JSONObject jsonObject);
    /**
     * 统计公司级计划个数
     *
     * @param jsonObject
     * @return list
     * */
    List<JSONObject> reportCompanyPlanNum(JSONObject jsonObject);
    /**
     * 根据id获取详情信息
     *
     * @return map
     * @param id
     * */
    Map<String,Object> getObjMap(String id);
    /**
     * 子表数据
     *
     * @param mainId
     * @return list
     * */
    List<Map<String, Object>> getItemMapList(String mainId);
    /**
     * 查询
     *
     * @param params
     * @return List
     * */
    List<Map<String, Object>> getPersonPlanList(Map<String, Object> params);
    /**
     * 查询未完成项目
     *
     * @param params
     * @return List
     * */
    List<Map<String, Object>> getDeptUnFinishList(Map<String, Object> params);
    /**
     * 根据年月和编号获取项目
     *
     * @param paramJson
     * @return json
     * */
    JSONObject getPlanByCodeAndMonth(JSONObject paramJson);
    /**
     * 统计项目计划个数
     *
     * @param jsonObject
     * @return list
     * */
    List<JSONObject> reportPlanNum(JSONObject jsonObject);
    /**
     * 统计非项目计划个数
     *
     * @param jsonObject
     * @return list
     * */
    List<JSONObject> reportUnPlanNum(JSONObject jsonObject);
    /**
     * 统计计划外计划个数
     *
     * @param jsonObject
     * @return list
     * */
    List<JSONObject> reportTaskNum(JSONObject jsonObject);
}
