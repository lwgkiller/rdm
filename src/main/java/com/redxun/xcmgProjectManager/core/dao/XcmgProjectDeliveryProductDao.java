package com.redxun.xcmgProjectManager.core.dao;


import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


/**
 * @author zhangzhen
 */
@Repository
public interface XcmgProjectDeliveryProductDao {
    /**
     * 添加人员负责交付物和产品关系
     *
     * @param paramJson
     * */
    void addDeliveryProduct(JSONObject paramJson);

    /**
     * 删除
     * @param paramJson
     * */
    void delDeliveryProduct(JSONObject paramJson);

    void delDeliveryProductByParam(Map<String, Object> params);
    /**
     * 更新人员项目交付物信息
     * @param paramJson
     * */
    void updateDeliveryInfo(JSONObject paramJson);
    /**
     * 查询已经分配的交付物
     *
     * @param projectId
     * @return list
     * */
    List<String> getDeliveryIdList(String projectId);
    /**
     * 根据交付物id获取交付物信息
     *
     * @param deliveryId
     * @return json
     * */
    JSONObject getDeliveryObj(String deliveryId);
    /**
     * 从人员表里面获取交付物
     *
     * @param projectId
     * @return string
     * */
    String getRespDeliveryIds(String projectId);
}
