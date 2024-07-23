package com.redxun.rdmZhgl.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zhangzhen
 */
@Repository
public interface NdkfjhBudgetDao {

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
     * 保存
     * @param params
     * @return
     * */
    int addMapObject(Map<String, Object> params);

    /**
     * 更新
     *
     * @param jsonObject
     * @return
     */
    int updateObject(JSONObject jsonObject);
    /**
     * 批量删除
     *
     * @param params
     * */
    void batchDelete(Map<String, Object> params);
    /**
     * 根据id获取详情信息
     *
     * @return JSONObject
     * @param id
     * */
    JSONObject getObjectById(String id);
    /**
     * 根据预算年份和项目编号详情信息
     *
     * @return JSONObject
     * @param jsonObject
     * */
    JSONObject getObjByInfo(JSONObject jsonObject);
    /**
     * 保存预算详情信息
     * @param jsonObject
     * @return
     * */
    int addBudgetDetail(JSONObject jsonObject);
    /**
     * 保存
     * @param params
     * @return
     * */
    int addMapBudgetDetail(Map<String, Object> params);
    /**
     * 更新预算详情信息
     *
     * @param jsonObject
     * @return
     */
    int updateBudgetDetail(JSONObject jsonObject);
    /**
     * 删除预算详情信息
     *
     * @param id
     * */
    void delBudgetDetail(String id);
    /**
     * 根据年份获取预算详情
     *
     * @param budgetYear
     * @return list
     * */
    List<JSONObject> getBudgetListByYear(String budgetYear);
    /**
     * 根据主表id获取子表详情
     *
     * @param mainId
     * @return list
     * */
    List<JSONObject> getBudgetDetailList(String mainId);
    /**
     * 批量删除子表信息
     *
     * @param params
     * */
    void batchDeleteDetail(Map<String, Object> params);
    /**
     * 批量导入
     *
     * @param dataList
     * */
    void batchInsertBudget(List<Map<String, Object>> dataList);
    /**
     * 批量导入
     *
     * @param dataList
     * */
    void batchInsertBudgetDetail(List<Map<String, Object>> dataList);

    /**
     * 判断是否已经提交了
     *
     * @param  params
     * @return json
     * */
    JSONObject getBudgetByInfo(Map<String, Object> params);

}
