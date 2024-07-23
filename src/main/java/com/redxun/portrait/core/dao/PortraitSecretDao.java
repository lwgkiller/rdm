package com.redxun.portrait.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zhangzhen
 */
@Repository
public interface PortraitSecretDao {
    /**
     * 查询
     *
     * @MapKey params<>
     * @return List
     * */
    List<Map<String, Object>> query(Map<String, Object> params);
    /**
     * 添加
     *
     * @param param
     * */
    void addSecret(Map<String, Object> param);
    /**
     * 批量导入
     *
     * @param dataList
     * */
    void batchInsert(List<Map<String, Object>> dataList);

    /**
     * 获取技术密码数据列表
     *
     * @return list
     * */
    List<JSONObject> getSecretList();

    /**
     * 更新技术密码数据
     *
     * @param id
     * */
    void updateSecretStatus(String id);
    /**
     *获取个人技术秘密
     *
     * @param jsonObject
     * @return json
     * */
    List<JSONObject> getPersonSecretList(JSONObject jsonObject);
}
