package com.redxun.xcmgProjectManager.core.dao;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author zz
 * */
@Repository
public interface XcmgProjectAbolishDao {
    /**
     * 获取作废申请信息
     * @param params
     * @return
     * */
    Map<String, Object> getObject(Map<String, Object> params);

    /**
     * 新增作废申请单
     * @param params
     * */
    void add(Map<String, Object> params);

    /**
     * 更新作废申请单
     * @param params
     * */
    void update(Map<String, Object> params);

    /**
     * 删除作废申请单信息
     * @param id
     * */
    void delete(String id);

    /**
     * 查询作废流程列表
     * @param params
     * @return
     * */
    List<Map<String, Object>> queryList(Map<String, Object> params);
    /**
     * 根据solid 查询流程key
     * @param params
     * @return
     * */
    List<Map<String,Object>> getBmpSolution(Map<String,Object> params);
    /**
     * 查询作废申请信息
     * @param params
     * @return
     * */
    List<Map<String, String>> queryAbolishInfo(Map<String, Object> params);

}
