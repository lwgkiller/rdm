package com.redxun.portrait.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zhangzhen
 */
@Repository
public interface PortraitBbsDao {
    /**
     * 查询
     *
     * @MapKey params<>
     * @return List
     * */
    List<Map<String, Object>> query(Map<String, Object> params);
    /**
     * 批量导入
     *
     * @param dataList
     * */
    void batchInsert(List<Map<String, Object>> dataList);

    /**
     * 获取论坛数据列表
     *
     * @return list
     * */
    List<JSONObject> getBbsList();

    /**
     * 更新论坛数据
     *
     * @param id
     * */
    void updateBbsStatus(String id);
    /**
     *获取个人论坛
     *
     * @param jsonObject
     * @return json
     * */
    List<JSONObject> getPersonBbsList(JSONObject jsonObject);
}
