package com.redxun.portrait.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zhangzhen
 */
@Repository
public interface PortraitNoticeDao {
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
     *获取个人通报
     *
     * @param jsonObject
     * @return json
     * */
    List<Map<String,Object>> getPersonNotificationList(JSONObject jsonObject);
    /**
     * 批量导入
     *
     * @param dataList
     * */
    void batchInsertNotice(List<Map<String, Object>> dataList);

    /**
     * 根据用户id和通告编号查询
     *
     * @param jsonObject
     * @return list
     * */
    List<JSONObject> getNoticeInfo(JSONObject jsonObject);
}
