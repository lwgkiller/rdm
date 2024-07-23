package com.redxun.info.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @author zhangzhen
 */
@Repository
public interface InfoRewardDao {
    /**
     * 查询
     *
     * @param params
     * @return List
     * */
    List<Map<String, Object>> query(Map<String, Object> params);

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
     * 批量导入
     *
     * @param dataList
     * */
    void batchInsertObject(List<JSONObject> dataList);
    /**
     * 更新内容
     *
     * @param data
     * */
    void updateObject(JSONObject data);
    /**
     * 根据title获取详情信息
     *
     * @return JSONObject
     * @param title
     * */
    JSONObject getObjectByTitle(String title);
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
    int updateReward(Map<String, Object> params);
}
