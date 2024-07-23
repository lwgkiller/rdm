package com.redxun.secret.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface QxsqDao {
    // 表单的新增、更新和删除
    void insertQxsq(JSONObject param);

    void updateQxsq(JSONObject param);

    void updateQxsqNumber(JSONObject param);

    void deleteQxsq(JSONObject param);

    // 列表数据
    List<JSONObject> queryApplyList(Map<String, Object> params);

    // 某个表单的基本信息
    JSONObject queryApplyDetail(JSONObject params);

    // 根据角色查人员列表
    List<Map<String, Object>> queryUserListByRole(Map<String, String> params);
    // 查询子系统列表
    List<Map<String, Object>> querySubsystemList();

    // 查询子系统负责人
    List<JSONObject> queryUserBySysKey(Map<String, String> params);

}
