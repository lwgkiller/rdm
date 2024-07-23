package com.redxun.portrait.core.dao;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zhangzhen
 */
@Repository
public interface PortraitIndexDao {
    /**
     * 查询
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
     * 根据id获取详情信息
     *
     * @return JSONObject
     * @param id
     * */
    JSONObject getObjectById(String id);
    /**
     * 获取指标字典项
     *
     * @param jsonObject
     * @return array
     * */
    JSONArray getIndexDic(JSONObject jsonObject);

    /**
     * 批量导入
     *
     * @param dataList
     * */
    void batchInsertIndex(List<Map<String, Object>> dataList);
    /**
     *根据适用部门和指标名称获取指标信息
     *
     * @param jsonObject
     * @return list
     * */
    List<JSONObject> getIndexInfo(JSONObject jsonObject);
}
