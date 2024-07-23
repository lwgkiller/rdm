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
public interface InfoBusTypeConfigDao {
    /**
     * 查询信息
     *
     * @param params
     * @return List
     */
    List<Map<String, Object>> query(Map<String, Object> params);

    /**
     * 查询字典列表
     *
     * @return jsonArray
     */
    JSONArray getDicBusType();

    /**
     * 保存
     *
     * @param param
     * @return
     */
    int add(Map<String, Object> param);

    /**
     * 更新
     *
     * @param params
     * @return
     */
    int update(Map<String, Object> params);

    /**
     * 删除
     *
     * @param id
     */
    void del(String id);

    /**
     * 根据id获取对象
     *
     * @param factoryCode
     * @return
     */
    JSONObject getObjectById(String factoryCode);
    /**
     * 根据infoTypeName获取对象
     *
     * @param infoTypeName
     * @return
     */
    JSONObject getObjectByName(String infoTypeName);
}
