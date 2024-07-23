package com.redxun.rdmZhgl.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zhangzhen
 */
@Repository
public interface DelayApplyDetailDao {

    /**
     * 查询信息
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
     * 删除
     *
     * @param id
     */
    void del(String id);

    /**
     * 获取领用详情信息
     *
     * @param mainId
     * @return list
     * */
    List<JSONObject> getDetailByMainId(String mainId);
    /**
     * 删除
     *
     * @param mainId
     */
    void delDetail(String mainId);

    List<Map<String, Object>> exportDelayApply(Map<String, Object> params);
}
