package com.redxun.xcmgProjectManager.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

/**
 * @author zz
 */
@Repository
public interface XcmgProjectChangeDao {
    /**
     * 获取变更申请信息
     * 
     * @param params
     * @return
     */
    Map<String, Object> queryProjectChangeById(Map<String, Object> params);

    /**
     * 新增变更申请单
     * 
     * @param params
     */
    void addChangeApply(Map<String, Object> params);

    /**
     * 更新变更申请单
     * 
     * @param params
     */
    void updateChangeApply(Map<String, Object> params);

    /**
     * 删除变更申请单信息
     * 
     * @param id
     */
    void deleteChangeApplyById(String id);

    /**
     * 查询变更流程列表
     * 
     * @param params
     * @return
     */
    List<Map<String, Object>> queryChangeApplyList(Map<String, Object> params);

    /**
     * 根据项目id获取项目的级别信息，
     * 
     * @param params
     * @return
     */
    List<Map<String, Object>> getProjectLevel(Map<String, Object> params);

    /**
     * 根据项目id获取项目的基本信息，
     * 
     * @param params
     * @return
     */
    List<Map<String, Object>> getProjectBaseInfo(Map<String, Object> params);

    // 更新项目中的成员
    void updateProjectMember(Map<String, Object> params);
}
