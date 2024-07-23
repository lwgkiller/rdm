package com.redxun.standardManager.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @author zhangzhen
 */
@Repository
public interface SubManagerGroupDao {

    /**
     * 获取对象
     *
     * @param jsonObject
     * @return JSON
     * */
    JSONObject getObject(JSONObject jsonObject);
    /**
     * 查询信息
     *
     * @param params
     * @return List
     * */
    List<Map<String, Object>> query(Map<String, Object> params);

    /**
     * 查询字典列表
     *
     * @return jsonArray
     * @param params
     * */
    JSONArray getDicGroup(Map<String, Object> params);
    /**
     * 保存
     * @param param
     * @return
     * */
    int add(Map<String, Object> param);

    /**
     * 更新
     *
     * @param params
     * @return
     */
    int update(Map<String, Object> params);
    /**
     * 批量删除用户组
     *
     * @param params
     * @return
     * */
    int batchGroupDelete(Map<String, Object> params);
    /**
     * 批量删除用户组与用户的关系
     *
     * @param params
     * @return
     * */
    int batchGroupUserDelete(Map<String, Object> params);
    /**
     * 获取对象
     *
     * @param jsonObject
     * @return JSON
     * */
    JSONObject getObjectByName(JSONObject jsonObject);
}
