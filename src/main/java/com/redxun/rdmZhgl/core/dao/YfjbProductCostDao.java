package com.redxun.rdmZhgl.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zhangzhen
 */
@Repository
public interface YfjbProductCostDao {

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
    int addItem(Map<String, Object> param);

    /**
     * 更新
     *
     * @param params
     * @return
     */
    int updateItem(Map<String, Object> params);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    int delItem(String id);

    /**
     * 根据年份和型号获取产品信息
     *
     * @param jsonObject
     * @return json
     * */
    JSONObject getCostByInfo(JSONObject jsonObject);
    /**
     * 根据年份和型号获取产品信息
     *
     * @param jsonObject
     * @return json
     * */
    JSONObject getObjByInfo(JSONObject jsonObject);

}
