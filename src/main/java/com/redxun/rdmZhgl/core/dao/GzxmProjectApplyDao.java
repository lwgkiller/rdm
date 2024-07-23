package com.redxun.rdmZhgl.core.dao;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

/**
 * @author zz
 * */
@Repository
public interface GzxmProjectApplyDao {
    /**
     * 获取申请信息
     * @param id
     * @return
     * */
    Map<String, Object> getObjectById(String id);

    /**
     * 新增申请单
     * @param params
     * */
    void add(Map<String, Object> params);

    /**
     * 更新申请单
     * @param params
     * */
    void update(Map<String, Object> params);

    /**
     * 删除作审请单信息
     * @param id
     * */
    void delete(String id);

    /**
     * 查询流程列表
     * @param params
     * @return
     * */
    List<Map<String, Object>> queryList(Map<String, Object> params);
    /**
     * 根据solid 查询流程key
     * @param params
     * @return
     * */
    List<Map<String,Object>> getBmpSolution(Map<String, Object> params);

    /**
     * 获取审批项目和课题信息
     *
     * @param params
     * @return
     * */
    List<JSONObject> getSubjectList(Map<String, Object> params);

}
