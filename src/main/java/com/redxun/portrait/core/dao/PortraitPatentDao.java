package com.redxun.portrait.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zhangzhen
 */
@Repository
public interface PortraitPatentDao {
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
     * 获取专利解读数据列表
     *
     * @return list
     * */
    List<JSONObject> getPatentList();

    /**
     * 更新专利解读数据
     *
     * @param id
     * */
    void updatePatentStatus(String id);
    /**
     *获取个人专利解读
     *
     * @param jsonObject
     * @return json
     * */
    List<JSONObject> getPersonPatentReadList(JSONObject jsonObject);
}
