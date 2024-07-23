package com.redxun.wwrz.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zhangzhen
 */
@Repository
public interface WwrzStandardDao {

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
     * 保存详情信息
     * @param jsonObject
     * @return
     * */
    int addStandardDetail(JSONObject jsonObject);
    /**
     * 保存
     * @param params
     * @return
     * */
    int addMapStandardDetail(Map<String, Object> params);
    /**
     * 更新详情信息
     *
     * @param jsonObject
     * @return
     */
    int updateStandardDetail(JSONObject jsonObject);
    /**
     * 删除详情信息
     *
     * @param id
     * */
    void delStandardDetail(String id);
    /**
     * 根据主表id获取子表详情
     *
     * @param mainId
     * @return list
     * */
    List<JSONObject> getStandardDetailList(String mainId);
    /**
     * 批量删除子表信息
     *
     * @param params
     * */
    void batchDeleteDetail(Map<String, Object> params);
    /**
     * 获取标准列表
     *
     * @return list
     * */
    List<JSONObject> getStandardList();

}
