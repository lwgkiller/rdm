package com.redxun.xcmgbudget.core.manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.query.Page;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgbudget.core.dao.BudgetMonthDao;
import com.redxun.xcmgbudget.core.dao.BudgetMonthUserDao;
import com.redxun.xcmgbudget.core.util.ConstantUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.util.*;

@Service
public class BudgetMonthManager {
    private Logger logger = LoggerFactory.getLogger(BudgetMonthManager.class);

    @Autowired
    private BudgetMonthDao budgetMonthDao;
    @Autowired
    private BudgetMonthUserDao budgetMonthUserDao;
    private static DecimalFormat df = new DecimalFormat("0.00");


    public List<JSONObject> queryBudgetMonthList(String budgetType ,String budgetId) {
        List<JSONObject> dataList = new ArrayList<>();
        if (StringUtils.isBlank(budgetType)|| StringUtils.isBlank(budgetId)) {
            return dataList;
        }
        Map<String, Object> params = new HashMap<>();
        if (budgetType == "xml" || budgetType.equalsIgnoreCase("xml")){
            params.put("applyType", 1);
        }else if(budgetType == "fxml" || budgetType.equalsIgnoreCase("fxml")){
            params.put("applyType", 2);
        }
        params.put("budgetId", budgetId);

        dataList = budgetMonthDao.queryBudgetMonthList(params);
        if (dataList == null || dataList.isEmpty()) {
            return dataList;
        }
        List<JSONObject> dataListResult = new ArrayList<>();
        for (JSONObject oneData : dataList) {
            //为空置的资金费用预算补0
            if (!oneData.containsKey("zjMonthExpect")){
                oneData.put("zjMonthExpect",0);
            }
            if (!oneData.containsKey("fyMonthExpect")){
                oneData.put("fyMonthExpect",0);
            }
            formatBudgetMonthObj(oneData);
        }

        // 如果小类型存在，则计算并添加小类型汇总行
        params.clear();
        // 添加合计行
        JSONObject totalSumObj = computeTotalSumByGroupSum(dataList);
        dataListResult.addAll(dataList);
        dataListResult.add(totalSumObj);
        return dataListResult;
    }

    public JsonResult queryBudgetMonthListSum(String yearMonth) {
        JsonResult result = new JsonResult(true);
        //查询当月全部归档流程资金
        List<JSONObject> dataList = budgetMonthDao.queryBudgetMonthSumList(yearMonth);
        if (dataList == null || dataList.isEmpty()) {
            result.setData(dataList);
        }
        List<JSONObject> dataListResult = new ArrayList<>();
        for (JSONObject oneData : dataList) {
            //为空置的资金费用预算补0
            if (!oneData.containsKey("zjMonthExpect")){
                oneData.put("zjMonthExpect",0);
            }
            if (!oneData.containsKey("fyMonthExpect")){
                oneData.put("fyMonthExpect",0);
            }
            formatBudgetMonthObj(oneData);
        }
        JSONObject totalSumObj = computeTotalSumByGroupSum(dataList);
        dataListResult.addAll(dataList);
        dataListResult.add(totalSumObj);
        result.setData(dataListResult);
        return result;

    }

    public JsonResult queryBudgetMonthDeptListSum(String yearMonth) {
        JsonResult result = new JsonResult(true);
        //查询当月全部归档流程资金
        List<JSONObject> dataList = budgetMonthDao.queryBudgetMonthDeptSumList(yearMonth);
        if (dataList == null || dataList.isEmpty()) {
            result.setData(dataList);
        }
        List<JSONObject> dataListResult = new ArrayList<>();
        for (JSONObject oneData : dataList) {
            //为空置的资金费用预算补0
            if (!oneData.containsKey("zjMonthExpect")){
                oneData.put("zjMonthExpect",0);
            }
            if (!oneData.containsKey("fyMonthExpect")){
                oneData.put("fyMonthExpect",0);
            }
            formatBudgetMonthObj(oneData);
        }
        JSONObject totalSumObj = computeTotalSumByGroupSum(dataList);
        dataListResult.addAll(dataList);
        dataListResult.add(totalSumObj);
        result.setData(dataListResult);
        return result;

    }

    public JsonResult queryBudgetSubjectList(String yearMonth,String subjectId) {
        JsonResult result = new JsonResult(true);
        //查询当月全部归档流程资金
        JSONObject param = new JSONObject();
        param.put("yearMonth",yearMonth);
        param.put("subjectId",subjectId);
        List<JSONObject> dataList = budgetMonthDao.queryBudgetSubjectList(param);
        if (dataList == null || dataList.isEmpty()) {
            result.setData(dataList);
        }
        List<JSONObject> dataListResult = new ArrayList<>();
        for (JSONObject oneData : dataList) {
            formatBudgetMonthObj(oneData);
        }
        JSONObject totalSumObj = computeTotalSumByGroupSum(dataList);
        dataListResult.addAll(dataList);
        dataListResult.add(totalSumObj);
        result.setData(dataListResult);
        return result;

    }

    /**
     * 根据分组汇总情况计算总计的值(小类型Id为“”的数据也包含了)
     *
     * @param dataList
     * @return
     */
    private JSONObject computeTotalSumByGroupSum(List<JSONObject> dataList) {
        JSONObject totalSum = new JSONObject();
        totalSum.put("type", "totalSum");
        computeSum(totalSum, dataList);
        return totalSum;
    }

    /**
     * 根据子数据汇总
     *
     * @param result
     * @param dataList
     */
    private void computeSum(JSONObject result, List<JSONObject> dataList) {
        double zjMonthExpectSum = 0.0;
        double fyMonthExpectSum = 0.0;
        for (JSONObject oneSmallTypeGroupSum : dataList) {
            zjMonthExpectSum += oneSmallTypeGroupSum.getDoubleValue("zjMonthExpect");
            fyMonthExpectSum += oneSmallTypeGroupSum.getDoubleValue("fyMonthExpect");
        }
        result.put("zjMonthExpect", formatDouble(zjMonthExpectSum));
        result.put("fyMonthExpect", formatDouble(fyMonthExpectSum));
    }

    public static double formatDouble(Double value) {
        if (value == null) {
            return 0.00;
        }
        return Double.parseDouble(df.format(value));
    }


    /**
     * 格式化月度预算数据
     *
     * @param budgetMonthObj
     */
    private void formatBudgetMonthObj(JSONObject budgetMonthObj) {
        budgetMonthObj.put("zjMonthExpect", formatDouble(budgetMonthObj.getDouble("zjMonthExpect")));
        budgetMonthObj.put("fyMonthExpect", formatDouble(budgetMonthObj.getDouble("fyMonthExpect")));
    }


    /**
     * 查询项目列表
     */
    public JsonPageResult<?> progressReportList(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            JSONObject requestObj = new JSONObject();
            Map<String, Object> params = new HashMap<>();
            String filterParams = request.getParameter("filter");
            if (StringUtils.isNotBlank(filterParams)) {
                JSONArray jsonArray = JSONArray.parseArray(filterParams);
                for (int i = 0; i < jsonArray.size(); i++) {
                    String name = jsonArray.getJSONObject(i).getString("name");
                    String value = jsonArray.getJSONObject(i).getString("value");
                    if (StringUtils.isNotBlank(value)) {
                        params.put(name, value);
                    }
                }
            }
            requestObj.putAll(params);
            //查询项目基础信息
            List<Map<String, Object>> personProjectList = budgetMonthUserDao.queryProjectList(requestObj);
            result.setData(personProjectList);
            List<Map<String, Object>> allData = result.getData();
            // 总数设置并分页
            result.setTotal(allData.size());
            List<Map<String, Object>> finalSubProjectList = new ArrayList<Map<String, Object>>();
            if (doPage) {
                // 根据分页进行subList截取
                int pageIndex = RequestUtil.getInt(request, "pageIndex", 0);
                int pageSize = RequestUtil.getInt(request, "pageSize", Page.DEFAULT_PAGE_SIZE);
                int startSubListIndex = pageIndex * pageSize;
                int endSubListIndex = startSubListIndex + pageSize;
                if (startSubListIndex < allData.size()) {
                    finalSubProjectList = allData.subList(startSubListIndex,
                            endSubListIndex < allData.size() ? endSubListIndex : allData.size());
                }
            } else {
                finalSubProjectList = allData;
            }
            result.setData(finalSubProjectList);

        } catch (Exception e) {
            logger.error("Exception in progressReportList", e);
            result.setSuccess(false);
            result.setMessage("系统异常！");
        }
        return result;
    }

    /**
     * 保存月度预算信息（新增或者更新）
     *
     * @param result
     * @param requestBody
     */
    public void saveBudgetMonth(JsonResult result, String requestBody) {
        if (StringUtils.isBlank(requestBody)) {
            result.setSuccess(false);
            result.setMessage("请求体为空！");
            return;
        }
        JSONObject requestObj = JSONObject.parseObject(requestBody);
        String id = requestObj.getString("id");
        String operate = requestObj.getString("operate");
        if (operate.equalsIgnoreCase("edit")) {
            if (StringUtils.isBlank(id)) {
                // 进一步判断数据库中是否存在，从前台带过来的budgetMonthId不准
                List<JSONObject> existData = budgetMonthDao.queryBudgetMonthExist(requestObj);
                if (existData == null || existData.isEmpty()) {
                    requestObj.put("id", IdUtil.getId());
                    requestObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    requestObj.put("CREATE_TIME_", new Date());
                    budgetMonthDao.createBudgetMonth(requestObj);
                } else if (existData.size() > 1) {
                    result.setMessage("操作失败，数据存在多条！");
                } else {
                    requestObj.put("id", existData.get(0).getString("id"));
                    requestObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    requestObj.put("UPDATE_TIME_", new Date());
                    budgetMonthDao.updateBudgetMonth(requestObj);
                }

            } else {
                requestObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                requestObj.put("UPDATE_TIME_", new Date());
                budgetMonthDao.updateBudgetMonth(requestObj);
            }
        } else {
            requestObj.put("id", IdUtil.getId());
            requestObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            requestObj.put("CREATE_TIME_", new Date());
            budgetMonthDao.createBudgetMonth(requestObj);
        }
        result.setMessage("操作成功！");
    }

    public void queryMonthProcess(JsonResult result, String requestBody) {
        if (StringUtils.isBlank(requestBody)) {
            result.setSuccess(false);
            result.setMessage("参数为空，查询失败！");
            return;
        }

        JSONObject requestObj = JSONObject.parseObject(requestBody);
        List<JSONObject> processList = budgetMonthDao.queryMonthProcessList(requestObj);
        if (processList != null && !processList.isEmpty()) {
            for (JSONObject oneProcess : processList) {
                String operateTime = "";
                if (oneProcess.getDate("UPDATE_TIME_") != null) {
                    operateTime = DateFormatUtil.format(oneProcess.getDate("UPDATE_TIME_"), "yyyy-MM-dd HH:mm:ss");
                } else if (oneProcess.getDate("CREATE_TIME_") != null) {
                    operateTime = DateFormatUtil.format(oneProcess.getDate("CREATE_TIME_"), "yyyy-MM-dd HH:mm:ss");
                }
                oneProcess.put("operateTime", operateTime);
                if (oneProcess.getDouble("moneyNum") != null) {
                    oneProcess.put("moneyNum", df.format(oneProcess.getDoubleValue("moneyNum")));
                }
                if (oneProcess.getDouble("moneyNumBeforeRate") != null) {
                    oneProcess.put("moneyNumBeforeRate", df.format(oneProcess.getDoubleValue("moneyNumBeforeRate")));
                }
            }
        }
        result.setData(processList);
    }

    public void saveMonthProcess(JsonResult result, String requestBody) {
        if (StringUtils.isBlank(requestBody)) {
            result.setSuccess(false);
            result.setMessage("保存失败，数据为空！");
            return;
        }
        JSONObject requestObj = JSONObject.parseObject(requestBody);
        JSONArray dataArr = requestObj.getJSONArray("data");
        if (dataArr == null || dataArr.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("保存失败，数据为空！");
            return;
        }

        // 新增
        List<JSONObject> addProcessArr = new ArrayList<>();
        // 删除
        Set<String> deleteProcessIds = new HashSet<>();
        String currentUserId = ContextUtil.getCurrentUserId();
        Date currentDate = new Date();
        String budgetId = requestObj.getString("budgetId");
        String yearMonth = requestObj.getString("yearMonth");
        String subjectId = requestObj.getString("subjectId");
        for (int index = 0; index < dataArr.size(); index++) {
            JSONObject oneData = dataArr.getJSONObject(index);
            String state = oneData.getString("_state");
            if (ConstantUtil.STATE_ADD.equals(state) || StringUtils.isBlank(oneData.getString("id"))) {
                oneData.put("id", IdUtil.getId());
                oneData.put("budgetId", budgetId);
                oneData.put("yearMonth", yearMonth);
                oneData.put("subjectId", subjectId);
                oneData.put("CREATE_BY_", currentUserId);
                oneData.put("CREATE_TIME_", currentDate);
                oneData.put("applyName",ContextUtil.getCurrentUser().getFullname());
                addProcessArr.add(oneData);
                continue;
            }
            if (ConstantUtil.STATE_REMOVED.equals(state)) {
                String id = oneData.getString("id");
                deleteProcessIds.add(id);
                continue;
            }
            // 逐条更新
            if (ConstantUtil.STATE_MODIFIED.equals(state)) {
                oneData.put("UPDATE_BY_", currentUserId);
                oneData.put("UPDATE_TIME_", currentDate);
                budgetMonthDao.updateBudgetMonthProcess(oneData);
                continue;
            }
        }
        if (!addProcessArr.isEmpty()) {
            budgetMonthDao.createBudgetMonthProcess(addProcessArr);
        }
        if (!deleteProcessIds.isEmpty()) {
            Map<String, Object> param = new HashMap<>();
            param.put("deleteProcessIds", new ArrayList<String>(deleteProcessIds));
            budgetMonthDao.deleteBudgetMonthProcessByIds(param);
        }
        result.setMessage("操作成功！");
        result.setData(dataArr);
    }

    // 作废预算关联的所有信息
    public JsonResult discardBudgetMonth(String[] idArr, String[] instIdArr) {
        if (idArr.length != instIdArr.length) {
            return new JsonResult(false, "操作失败，表单和流程实例数据不一致！");
        }
        for (int i = 0; i < idArr.length; i++) {
            String id = idArr[i];
            // 删除任务表
            budgetMonthUserDao.discardBudgetMonth(instIdArr[i]);
        }
        return new JsonResult(true, "作废成功!");
    }

}
