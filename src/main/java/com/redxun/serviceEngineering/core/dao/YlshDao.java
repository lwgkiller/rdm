package com.redxun.serviceEngineering.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface YlshDao {
    void insertYlsh(JSONObject param);

    void updateYlsh(JSONObject param);

    void deleteYlsh(JSONObject param);

    // 列表数据
    List<Map<String, Object>> queryApplyList(Map<String, Object> params);

    // 某个表单的基本信息
    JSONObject queryApplyDetail(JSONObject params);

    // 需求项的增删改查
    List<JSONObject> queryDemandList(JSONObject params);

    //需求项更新
    void updateDemand(JSONObject params);

    // 清除主表的文件信息
    void clearDemand(JSONObject params);

    // 删除已审核后的文件【实际是更新】
    void deleteDemand(JSONObject param);
}
