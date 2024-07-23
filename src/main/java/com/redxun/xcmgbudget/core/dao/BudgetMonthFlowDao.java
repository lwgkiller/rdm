package com.redxun.xcmgbudget.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface BudgetMonthFlowDao {
    JSONObject queryFlowBaseById(String id);

    List<JSONObject> queryBudgetMonthFlowList(Map<String, Object> params);

    void insertMonthProject(JSONObject param);

    void updateMonthProject(JSONObject param);

    void updateProjectId(JSONObject param);

    void updateProjectType(JSONObject param);

    void deleteBudgetMonth(String budgetId);

    void deleteBudgetMonthById(String id);

    void deleteBudgetFlowById(String id);

    List<JSONObject> judgeBudgetMonthStatus(String yearMonth);

    List<JSONObject> queryBudgetNumberById(JSONObject param);

    List<JSONObject> queryProcessNumberById(JSONObject param);

}
