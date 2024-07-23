package com.redxun.rdmZhgl.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zhangzhen
 */
@Repository
public interface NdkfjhPlanDao {

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
     * budgetId
     *
     * @return JSONObject
     * @param budgetId
     * */
    JSONObject getObjectByBudgetId(String budgetId);

    /**
     * 获取最大序号
     *
     * @param planYear
     * @return int
     * */
    Integer getMaxIndex(String planYear);


}
