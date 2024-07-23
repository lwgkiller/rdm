package com.redxun.gkgf.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zz
 * */
@Repository
public interface GkgfApplyDao {
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
     * 删除作审请单信息
     * @param id
     * */
    void delete(String id);
    /**
     * 查询详情列表
     *
     * @param jsonObject
     * @return
     */
    List<JSONObject> getItemList(JSONObject jsonObject);
    /**
     * 查询流程列表
     * @param params
     * @return
     * */
    List<Map<String, Object>> queryList(Map<String, Object> params);

    /**
     * 添加文件
     * @param param
     * @return
     * */
    void addFileInfos(JSONObject param);
    /**
     * @param detailId
     * @return list
     * */
    List<JSONObject> getFilesByDetailId(String detailId);
    /**
     * 删除文件信息
     * @param id
     * */
    void delFileById(String id);
    /**
     * 查询
     *
     * @param params
     * @return List
     * */
    List<Map<String, Object>> getFileList(Map<String, Object> params);
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
    int delItemById(String id);
    /**
     * 删除
     *
     * @param id
     * @return
     */
    int delItemByApplyId(String id);
    /**
     * 查询工况分析详情列表
     * @param params
     * @return
     * */
    List<Map<String, Object>> getDetailList(Map<String, Object> params);
}
