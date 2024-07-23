package com.redxun.xcmgbudget.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgbudget.core.dao.BudgetMonthFlowDao;
import com.redxun.xcmgbudget.core.dao.BudgetMonthUserDao;
import com.redxun.xcmgbudget.core.manager.BudgetMonthManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/xcmgBudget/core/budgetMonth/")
public class BudgetMonthController {
    private static final Logger logger = LoggerFactory.getLogger(BudgetMonthController.class);

    @Resource
    private BudgetMonthManager budgetMonthManager;
    @Autowired
    private BudgetMonthUserDao budgetMonthUserDao;

    @RequestMapping("queryList")
    @ResponseBody
    public JsonResult queryList(HttpServletRequest request, HttpServletResponse response) {
        String budgetType = RequestUtil.getString(request, "budgetType", "");
        String budgetId = RequestUtil.getString(request, "budgetId", "");
        List<JSONObject> data = budgetMonthManager.queryBudgetMonthList(budgetType, budgetId);
        JsonResult result = new JsonResult(true);
        result.setData(data);
        return result;
    }

    @RequestMapping("querySumList")
    @ResponseBody
    public JsonResult querySumList(HttpServletRequest request, HttpServletResponse response) {
        String yearMonth = RequestUtil.getString(request, "yearMonth", "");
        return budgetMonthManager.queryBudgetMonthListSum(yearMonth);
    }

    @RequestMapping("querySumDeptList")
    @ResponseBody
    public JsonResult querySumDeptList(HttpServletRequest request, HttpServletResponse response) {
        String yearMonth = RequestUtil.getString(request, "yearMonth", "");
        return budgetMonthManager.queryBudgetMonthDeptListSum(yearMonth);
    }

    @RequestMapping("queryBudgetSubjectList")
    @ResponseBody
    public JsonResult queryBudgetSubjectList(HttpServletRequest request, HttpServletResponse response) {
        String yearMonth = RequestUtil.getString(request, "yearMonth", "");
        String subjectId = RequestUtil.getString(request, "subjectId", "");
        return budgetMonthManager.queryBudgetSubjectList(yearMonth,subjectId);
    }

    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "xcmgBudget/core/budgetMonthEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    @RequestMapping("editSumPage")
    public ModelAndView editSumPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "xcmgBudget/core/budgetMonthSumEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    @RequestMapping("getProjectList")
    @ResponseBody
    public JsonPageResult<?> getProjectList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return budgetMonthManager.progressReportList(request, response, true);
    }

    @RequestMapping("getProjectListById")
    @ResponseBody
    public JsonPageResult<?> getProjectListById(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JsonPageResult result = new JsonPageResult(true);
        String projectId = RequestUtil.getString(request, "projectId", "");
        JSONObject params = new JSONObject();
        params.put("projectId",projectId);
        result.setData(budgetMonthUserDao.queryProjectList(params));
        return result;
    }

    @RequestMapping("getProjectListByBudgetId")
    @ResponseBody
    public JsonPageResult<?> getProjectListByBudgetId(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JsonPageResult result = new JsonPageResult(true);
        String budgetId = RequestUtil.getString(request, "budgetId", "");
        JSONObject params = new JSONObject();
        params.put("budgetId",budgetId);
        result.setData(budgetMonthUserDao.queryProjectList(params));
        return result;
    }

    @RequestMapping("saveBudgetMonth")
    @ResponseBody
    public JsonResult saveBudgetMonth(HttpServletRequest request, HttpServletResponse response,
                                      @RequestBody String requestBody) {
        JsonResult result = new JsonResult(true);
        budgetMonthManager.saveBudgetMonth(result, requestBody);
        return result;
    }

    //检查计划写入金额
    @RequestMapping("checkBudgetMoney")
    @ResponseBody
    public JsonResult checkBudgetMoney(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String budgetType = RequestUtil.getString(request, "budgetType", "");
            String budgetId = RequestUtil.getString(request, "budgetId", "");
            List<JSONObject> data = budgetMonthManager.queryBudgetMonthList(budgetType, budgetId);
            double zjMonthExpectSum = 0.0;
            double fyMonthExpectSum = 0.0;
            for (JSONObject oneData:data){
                if (oneData.containsKey("type") && oneData.getString("type").equals("totalSum")){
                    zjMonthExpectSum = oneData.getDouble("zjMonthExpect");
                    fyMonthExpectSum = oneData.getDouble("fyMonthExpect");
                }
            }
            if (zjMonthExpectSum == 0.0 && fyMonthExpectSum == 0.0){
                return new JsonResult(false, "请至少填写一笔资金预算或费用预算!");
            }
            return new JsonResult(true, "执行成功!");
        } catch (Exception e) {
            logger.error("Exception in checkBudgetMoney", e);
            return new JsonResult(false, "系统异常！");
        }
    }

    // 过程费用查询
    @RequestMapping("queryMonthProcess")
    @ResponseBody
    public JsonResult queryMonthProcess(HttpServletRequest request, HttpServletResponse response,
                                        @RequestBody String requestBody) {
        JsonResult result = new JsonResult(true);
        budgetMonthManager.queryMonthProcess(result, requestBody);
        return result;
    }

    // 过程费用保存
    @RequestMapping("saveMonthProcess")
    @ResponseBody
    public JsonResult saveMonthProcess(HttpServletRequest request, HttpServletResponse response,
                                       @RequestBody String requestBody) {
        JsonResult result = new JsonResult(true);
        budgetMonthManager.saveMonthProcess(result, requestBody);
        return result;
    }

    //流程作废
    @RequestMapping("discardMonthProcess")
    @ResponseBody
    public JsonResult discardMonthProcess(HttpServletRequest request, HttpServletResponse response,
                                       @RequestBody String requestBody) {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String instIdStr = RequestUtil.getString(request, "instIds");
            if (StringUtils.isNotEmpty(uIdStr) && StringUtils.isNotEmpty(instIdStr)) {
                String[] ids = uIdStr.split(",");
                String[] instIds = instIdStr.split(",");
                return budgetMonthManager.discardBudgetMonth(ids, instIds);
            }
        } catch (Exception e) {
            logger.error("Exception in deleteBudgetMonthFlow", e);
            return new JsonResult(false, "系统异常！");
        }
        return new JsonResult(true, "作废成功!");
    }
}
