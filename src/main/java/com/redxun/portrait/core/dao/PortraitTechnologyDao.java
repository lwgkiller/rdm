package com.redxun.portrait.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zhangzhen
 */
@Repository
public interface PortraitTechnologyDao {
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
     * 获取技术数据库数据列表
     *
     * @return list
     * */
    List<JSONObject> getTechnologyList();

    /**
     * 更新技术数据库
     *
     * @param id
     * */
    void updateTechnologyStatus(String id);
    /**
     *获取个人技术数据库
     *
     * @param jsonObject
     * @return json
     * */
    List<JSONObject> getPersonTechnologyList(JSONObject jsonObject);
}
