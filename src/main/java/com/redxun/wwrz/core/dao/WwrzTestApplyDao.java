package com.redxun.wwrz.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author zz
 * */
@Repository
public interface WwrzTestApplyDao {
    /**
     * 获取申请信息
     * @param id
     * @return
     * */
    Map<String, Object> getObjectById(String id);

    /**
     * 获取申请信息
     * @param id
     * @return
     * */
    JSONObject getJsonObject(String id);

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
     * 新增申请单
     * @param params
     * */
    void addProblem(JSONObject params);

    /**
     * 更新申请单
     * @param params
     * */
    void updateProblem(JSONObject params);
    /**
     * 删除问题
     * @param id
     * */
    void delProblemById(String id);
    /**
     * 获取问题列表
     * */
    List<JSONObject> getProblemList(String mainId);
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
     * 查询流程列表 新
     * @param params
     * @return
     * */
    List<JSONObject> dataListQuery(Map<String, Object> params);

    /**
     * 查询列表信息
     *
     * @param params
     * @return List
     * */
    List<Map<String, Object>> getApplyList(Map<String, Object> params);
    /**
     * 查询问题列表信息
     *
     * @param params
     * @return List
     * */
    List<Map<String, Object>> getAllProblemList(Map<String, Object> params);


    int checkNote(Map<String, Object> param);

}
