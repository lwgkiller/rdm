package com.redxun.rdmZhgl.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zhangzhen
 */
@Repository
public interface YfjbChangeNoticeDao {

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
     * 根据主表id获取详情信息
     *
     * @return JSONObject
     * @param mainId
     * */
    JSONObject getObjectByMainId(String mainId);



}
