package com.redxun.rdmZhgl.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONArray;

/**
 * @author zz
 */
@Repository
public interface MaterialCostCenterDao {

    /**
     * 查询
     *
     * @param params
     * @return
     */
    List<Map<String, Object>> getCostCenterList(Map<String, Object> params);

    /**
     * 删除所有数据
     *
     * @return int
     */
    int deleteAll();

    /**
     * 批量添加
     *
     * @param jsonArray
     */
    void batchInsert(JSONArray jsonArray);
    /**
     * 获取字典项
     *
     * @return jsonArray
     * */
    JSONArray getCostCenterDic();

}
