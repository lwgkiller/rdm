package com.redxun.world.core.dao;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

/**
 * @author zhangzhen
 */
@Repository
public interface WorldResearchDao {
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
     * 批量删除
     *
     * @param params
     * */
    void batchDelete(Map<String, Object> params);
    /**
     * 批量提交
     *
     * @param params
     * */
    void submit(Map<String, Object> params);
    /**
     * 批量审批
     *
     * @param params
     * */
    void batchAudit(Map<String, Object> params);
    /**
     * 审批
     *
     * @param params
     * */
    void audit(JSONObject params);
}
