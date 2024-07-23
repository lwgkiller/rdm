package com.redxun.rdmZhgl.core.dao;

import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zhangzhen
 */
@Repository
public interface BbsDiscussDao {
    /**
     * 查询
     *
     * @param jsonObject
     * @return j
     * */
    List<Map<String, Object>> query(JSONObject jsonObject);
    /**
     * 获取对象
     *
     * @param id
     * @return JSON
     * */
    JSONObject getObject(String id);
    /**
     * 保存
     * @param param
     * @return
     * */
    int add(Map<String, Object> param);
    /**
     * 批量删除
     *
     * @param params
     * */
    void batchDelete(Map<String, Object> params);
    /**
     * 批量删除
     *
     * @param params
     * */
    void batchDeleteByMainId(Map<String, Object> params);
    /**
     * 获取评论列表
     *
     * @param paramJson
     * @return list
     * */
    List<JSONObject> getDiscuss(JSONObject paramJson);
    /**
     * 获取子评论列表
     *
     * @param jsonObject
     * @return list
     * */
    List<Map<String, Object>> getChildDiscussList(JSONObject jsonObject);
}
