package com.redxun.rdmZhgl.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zz
 * */
@Repository
public interface SaleFileApplyDao {
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
    void addSaleFileInfos(JSONObject param);
    /**
     * @param applyId
     * @return list
     * */
    List<JSONObject> getSaleFiles(Map<String, Object> param);
    /**
     * 删除文件信息
     * @param param
     * */
    void delSaleFile(Map<String, Object> param);
    /**
     * 删除文件信息
     * @param applyId
     * */
    void delSaleFileByApplyId(String applyId);
    /**
     * 查询
     *
     * @param params
     * @return List
     * */
    List<Map<String, Object>> getSaleFileList(Map<String, Object> params);

    //查询是否有重复单据
    List<JSONObject> checkApplyPermition(JSONObject params);

    //查询区域
    List<JSONObject> queryRegionFromSpectrum(JSONObject params);

}
