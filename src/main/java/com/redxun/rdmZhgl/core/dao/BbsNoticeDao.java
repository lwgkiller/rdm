package com.redxun.rdmZhgl.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zhangzhen
 */
@Repository
public interface BbsNoticeDao {
    /**
     * 查询
     *
     * @param map
     * @return j
     * */
    List<Map<String, Object>> query(Map<String, Object> map);
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
     * 删除
     *
     * @param mainId
     * */
    void delByMainId(String mainId);

    /**
     * 获取发送人的信息
     *
     * @param paramJson
     * @return
     * */
    List<JSONObject> getNoticeUsers(JSONObject paramJson);
    /**
     * 更新读取状态
     *
     * @param params
     * */
    void updateReadStatus(Map<String,Object> params);

    /**
     * 获取发送人的信息
     *
     * @param standardId
     * @return
     * */
    List<JSONObject> getStandardUsers(String standardId);
}
