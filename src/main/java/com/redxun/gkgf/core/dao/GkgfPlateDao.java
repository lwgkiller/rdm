package com.redxun.gkgf.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @author zhangzhen
 */
@Repository
public interface GkgfPlateDao {

    /**
     * 根据id获取对象
     *
     * @param id
     * @return JSON
     * */
    JSONObject getObjectById(String id);
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
    JSONArray getDicItems(Map<String, Object> params);
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
     * 批量删除
     *
     * @param params
     * */
    void batchDelete(Map<String, Object> params);
    /**
     * 根据名称获取id
     *
     * @param param
     * @return
     */
    JSONObject getItemIdByName(Map<String, Object> param);
    /**
     * 根据code获取
     *
     * @param param
     * @return
     */
    JSONObject getObjByCode(Map<String, Object> param);
}
