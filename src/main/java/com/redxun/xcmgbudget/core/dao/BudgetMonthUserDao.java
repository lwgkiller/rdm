package com.redxun.xcmgbudget.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface BudgetMonthUserDao {
    // 查询是否有角色
    List<JSONObject> queryRelInstRoles(String userId);

    // 查询用户在行政组织上的角色
    List<JSONObject> queryUserDeptRoles(String userId);

    List<Map<String, String>> queryNodeVarsByNodeId(Map<String, Object> params);

    List<Map<String, Object>> queryProjectList(JSONObject param);

    /**
     * 删除任务表数据
     *
     * @param instId
     */
    void delTaskData(String instId);

    void discardBudgetMonth(String instId);
}
