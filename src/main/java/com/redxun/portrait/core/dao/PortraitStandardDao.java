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
public interface PortraitStandardDao {
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
     *获取个人标准信息
     *
     * @param jsonObject
     * @return json
     * */
    List<Map<String,Object>> getPersonStandardList(JSONObject jsonObject);
    /**
     *获取所有标准信息
     *
     * @return json
     * */
    List<Map<String,Object>> getAllStandard();
    /**
     * 删除所有数据
     * */
    void deleteAllData();
    /**
     * 获取排名信息
     *
     * @return json
     * */
    JSONArray getRankInfo();

}
