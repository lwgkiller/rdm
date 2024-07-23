package com.redxun.standardManager.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

/**
 * @author zhangzhen
 */
@Repository
public interface SubManagerUserDao {

    /**
     * 查询信息
     *
     * @param params
     * @return List
     */
    List<Map<String, Object>> query(Map<String, Object> params);

    /**
     * 保存
     * 
     * @param param
     * @return
     */
    int add(Map<String, Object> param);

    /**
     * 批量删除
     *
     * @param groupId
     * @return
     */
    void deleteByGroupId(String groupId);

    List<Map<String, String>> querySubManagerSystemIds(String userId);

    Map<String,String> querySubSystemIds(Map<String, Object> param);
}
