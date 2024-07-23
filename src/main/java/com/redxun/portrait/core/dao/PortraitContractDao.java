package com.redxun.portrait.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zhangzhen
 */
@Repository
public interface PortraitContractDao {
    /**
     * 查询
     *
     * @param params
     * @return List
     * */
    List<Map<String, Object>> query(Map<String, Object> params);

    /**
     * 根据id获取详情信息
     *
     * @return JSONObject
     * @param id
     * */
    JSONObject getObjectById(String id);
    /**
     * 批量导入
     *
     * @param dataList
     * */
    void batchInsert(List<Map<String, Object>> dataList);

    /**
     * 获取合同数据列表
     *
     * @return list
     * */
    List<JSONObject> getContractList();

    /**
     * 更新合同数据
     *
     * @param id
     * */
    void updateContractStatus(String id);
    /**
     *获取个人签订合同信息
     *
     * @param jsonObject
     * @return json
     * */
    List<JSONObject> getPersonContractList(JSONObject jsonObject);
}
