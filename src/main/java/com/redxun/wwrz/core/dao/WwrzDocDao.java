package com.redxun.wwrz.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zhangzhen
 */
@Repository
public interface WwrzDocDao {

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
     * 根据类型和名称获取产品信息
     *
     * @param jsonObject
     * @return json
     * */
    JSONObject getObjByInfo(JSONObject jsonObject);

    /**
     * 根据类型和名称获取产品信息
     *
     * @param params
     * @return json
     * */
    JSONObject getObjByMapInfo(Map<String, Object> params);
    /**
     * 获取文件列表
     *
     * @param docType
     * @return list
     * */
    List<JSONObject> getDocListByType(String docType);
    /**
     * 获取文件列表
     *
     * @param applyId
     * @return list
     * */
    List<JSONObject> getDocListByApplyId(String applyId);
}
