package com.redxun.xcmgbudget.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface BudgetMonthDao {

    // 月度费用预算
    List<JSONObject> queryBudgetMonthList(Map<String, Object> params);

    List<JSONObject> querySmallTypeList(Map<String, Object> params);

    void createBudgetMonth(JSONObject param);

    void updateBudgetMonth(JSONObject param);

    List<JSONObject> queryBudgetMonthExist(JSONObject param);

    List<JSONObject> queryBudgetMonthSumList(String yearMonth);

    List<JSONObject> queryBudgetMonthDeptSumList(String yearMonth);

    List<JSONObject> queryBudgetSubjectList(JSONObject param);

    List<JSONObject> queryMonthProcessList(JSONObject param);

    void createBudgetMonthProcess(List<JSONObject> addProcessArr);

    void updateBudgetMonthProcess(JSONObject param);

    void syncBudgetMonthProcess(JSONObject param);

    void deleteBudgetMonthProcessByIds(Map<String, Object> param);

}
