package com.redxun.xcmgProjectManager.core.dao;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

/**
 * @author zz
 */
@Repository
public interface XcmgProjectFileDao {
    /**
     * 获取申请信息
     *
     * @param params
     * @return
     */
    Map<String, Object> getObject(Map<String, Object> params);

    /**
     * 新增申请单
     *
     * @param params
     */
    void add(Map<String, Object> params);

    /**
     * 更新申请单
     *
     * @param params
     */
    void update(Map<String, Object> params);

    /**
     * 删除申请单信息
     *
     * @param id
     */
    void delete(String id);

    /**
     * 查询流程列表
     *
     * @param params
     * @return
     */
    List<Map<String, Object>> queryList(Map<String, Object> params);

    /**
     * 查询此次审批有哪些交付物列表
     *
     * @param params
     * @return
     */
    List<Map<String, Object>> getFileList(Map<String, Object> params);

    /**
     * 查询项目本阶段交付物列表
     *
     * @param params
     * @return
     */
    List<JSONObject> getStageFileList(Map<String, Object> params);

    /**
     * 查询阶段已经审批交付物列表
     *
     * @param params
     * @return
     */
    List<Map<String, Object>> getApprovalFileList(Map<String, Object> params);

    /**
     * 获取文件信息
     *
     * @param id
     * @return json
     * */
    JSONObject getFileObj(String id);
    /**
     * 获取文件审批信息
     *
     * @param id
     * @return json
     * */
    JSONObject getFileApplyObj(String id);

//    更新项目文件中，最外层项目文件夹
    void updateProjectFileFoldName(JSONObject param);
    /**
     * 更新文件交付物
     *
     * @param paramJson
     * */
    void updateFileProduct(JSONObject paramJson);
}
