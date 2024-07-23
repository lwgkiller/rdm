package com.redxun.world.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * @author mh
 */
@Repository
public interface WorldFitnessImproveDao {
    // 表单的新增、更新和删除
    void insertApply(JSONObject param);

    void updateApply(JSONObject param);

    void deleteApply(JSONObject param);

    // 生成编号
    void updateApplyNumber(JSONObject param);

    // 文件的新增、更新和删除
    void insertApplyFile(JSONObject param);

    void deleteApplyFile(JSONObject param);

    List<JSONObject> queryApplyListFile(Map<String, Object> params);

    // 列表数据
    List<JSONObject> queryApplyList(Map<String, Object> params);

    int countApplyList(Map<String, Object> params);

    // 表单的基本信息
    JSONObject queryApplyDetail(JSONObject params);

    void updateActionTime(Map<String, Object> params);

    void statusChange(JSONObject param);

}
