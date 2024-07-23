package com.redxun.portrait.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zhangzhen
 */
@Repository
public interface PortraitInformationDao {
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
     * 获取情报数据列表
     *
     * @return list
     * */
    List<JSONObject> getInformationList();

    /**
     * 更新情报数据
     *
     * @param id
     * */
    void updateInformationStatus(String id);
    /**
     *获取个人情报
     *
     * @param jsonObject
     * @return json
     * */
    List<JSONObject> getPersonInformationList(JSONObject jsonObject);
}
