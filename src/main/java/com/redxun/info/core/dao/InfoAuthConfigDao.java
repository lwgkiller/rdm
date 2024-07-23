package com.redxun.info.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zhangzhen
 */
@Repository
public interface InfoAuthConfigDao {
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
     * 根据部门和类别名称 查询需要同步的网站
     *
     * @param param
     * @return list
     * */
    List<JSONObject> getAuthUrls(JSONObject param);

    /**
     * 根据业务方向和情报分类查询
     *
     * @param jsonObject
     * @return json
     * */
    JSONObject getObjectByType(JSONObject jsonObject);
}
