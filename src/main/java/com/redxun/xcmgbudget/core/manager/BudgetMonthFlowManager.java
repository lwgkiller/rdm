package com.redxun.xcmgbudget.core.manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.query.Page;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgbudget.core.dao.BudgetMonthFlowDao;
import com.redxun.xcmgbudget.core.dao.BudgetMonthUserDao;
import com.redxun.xcmgbudget.core.util.XcmgBudgetConstant;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Service
public class BudgetMonthFlowManager {
    private Logger logger = LoggerFactory.getLogger(BudgetMonthFlowManager.class);

    @Resource
    private BpmInstManager bpmInstManager;
    @Autowired
    private BudgetMonthFlowDao budgetMonthFlowDao;
    @Autowired
    private BudgetMonthUserDao budgetMonthUserDao;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private CommonInfoDao commonInfoDao;

    public JSONObject queryBudgetMonthFlowById(String id) {
        return budgetMonthFlowDao.queryFlowBaseById(id);
    }

    /**
     * 查询用户的角色（普通账户(返回部门id）、部门负责人（返回部门id）、分管领导(返回分管部门ids)、业务角色）
     *
     * @param userId
     * @return
     */
    public JSONObject queryUserRoles(String userId) {
        JSONObject result = new JSONObject();
        // 先查询存在的业务角色
        List<JSONObject> roleKeys = budgetMonthUserDao.queryRelInstRoles(userId);
        if (roleKeys != null && !roleKeys.isEmpty()) {
            // 预算管理人员
            Boolean ysManager = false;

            for (JSONObject temp : roleKeys) {
                String roleKey = temp.getString("roleKey");
                if ("YSGLRY".equalsIgnoreCase(roleKey)) {
                    ysManager = true;
                }

            }
            if (ysManager) {
                result.put("ysManager", "yes");
            }
        }
        // 再查询行政组织上的角色关系(分管领导、部门负责人、普通用户)
        List<JSONObject> deptRoles = budgetMonthUserDao.queryUserDeptRoles(userId);
        if (deptRoles == null || deptRoles.isEmpty()) {
            result.put("role", XcmgBudgetConstant.user_role_common);
            result.put("deptIds", Collections.EMPTY_LIST);
            return result;
        }
        Set<String> leaderDeptIds = new HashSet<>();
        Set<String> respDeptIds = new HashSet<>();
        Set<String> commonDeptIds = new HashSet<>();
        for (JSONObject oneDeptRole : deptRoles) {
            String typeKey = oneDeptRole.getString("typeKey");
            String groupId = oneDeptRole.getString("groupId");
            if ("GROUP-DEPT-LEADER".equalsIgnoreCase(typeKey)) {
                leaderDeptIds.add(groupId);
            } else if ("GROUP-USER-LEADER".equalsIgnoreCase(typeKey)) {
                respDeptIds.add(groupId);
            } else if ("GROUP-USER-BELONG".equalsIgnoreCase(typeKey)) {
                commonDeptIds.add(groupId);
            }
        }
        // 分管领导
        if (!leaderDeptIds.isEmpty()) {
            result.put("role", XcmgBudgetConstant.user_role_deptLeader);
            result.put("deptIds", new ArrayList<String>(leaderDeptIds));
            return result;
        }
        // 部门负责人
        if (!respDeptIds.isEmpty()) {
            result.put("role", XcmgBudgetConstant.user_role_deptResper);
            result.put("deptIds", new ArrayList<String>(respDeptIds));
            return result;
        }
        // 普通用户
        if (!commonDeptIds.isEmpty()) {
            result.put("role", XcmgBudgetConstant.user_role_common);
            result.put("deptIds", new ArrayList<String>(commonDeptIds));
            return result;
        }
        return result;
    }

    public boolean judgeIsYsglry(String userId) {
        List<JSONObject> roleKeys = queryAndReturnUserRoles(userId);
        boolean isYsglry = false;
        for (JSONObject oneRole : roleKeys) {
            if (oneRole.getString("roleKey").equalsIgnoreCase("YSGLRY")) {
                isYsglry = true;
            }
        }
        return isYsglry;
    }

    /**
     * 查询用户的角色
     *
     * @param userId
     * @return
     */
    public List<JSONObject> queryAndReturnUserRoles(String userId) {
        List<JSONObject> roleKeys = budgetMonthUserDao.queryRelInstRoles(userId);
        if (roleKeys == null) {
            roleKeys = new ArrayList<>();
        }
        return roleKeys;
    }

    public void createMonthFlow(JSONObject formData) {
        formData.put("budgetId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        budgetMonthFlowDao.insertMonthProject(formData);
    }

    public void updateMonthFlow(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        budgetMonthFlowDao.updateMonthProject(formData);
    }

    public JsonPageResult<?> queryBudgetMonthFlowList(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
//        params.put("sortField", "budget_projectInfo.yearMonth desc");
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
        // 增加角色过滤。1、预算人员和admin是all；2、分管领导、部门负责人和普通用户，可以查看自己创建的或者所在（分管）部门的
        addRole2Param(params);
        List<JSONObject> dataList = budgetMonthFlowDao.queryBudgetMonthFlowList(params);
        // 查询当前处理人
        setTaskCurrentUser(dataList);
        //数据过滤
        List<JSONObject> finalAllProjectList = filterPlanListByDepRole(dataList);
        //封装
        List<JSONObject> finalSubProjectList = new ArrayList<>();
        // 根据分页进行subList截取
        int pageIndex = RequestUtil.getInt(request, "pageIndex", 0);
        int pageSize = RequestUtil.getInt(request, "pageSize", Page.DEFAULT_PAGE_SIZE);
        int startSubListIndex = pageIndex * pageSize;
        int endSubListIndex = startSubListIndex + pageSize;
        if (startSubListIndex < finalAllProjectList.size()) {
            finalSubProjectList = finalAllProjectList.subList(startSubListIndex,
                    endSubListIndex < finalAllProjectList.size() ? endSubListIndex : finalAllProjectList.size());
        }
        if (finalSubProjectList != null && !finalSubProjectList.isEmpty()) {
            for (Map<String, Object> oneProject : finalSubProjectList) {
                if (oneProject.get("CREATE_TIME_") != null) {
                    oneProject.put("CREATE_TIME_",
                            DateUtil.formatDate((Date)oneProject.get("CREATE_TIME_"), "yyyy-MM-dd"));
                }
            }
            //加入预算报销状态（数量）
            setBudgetNumber(finalSubProjectList);
        }
        result.setData(finalSubProjectList);
        result.setTotal(finalAllProjectList.size());
        return result;
    }

    private void addRole2Param(Map<String, Object> params) {
        String userNo = ContextUtil.getCurrentUser().getUserNo();
        if (userNo.equalsIgnoreCase("admin")) {
            params.put("roleName", "all");
            return;
        }
        String userId = ContextUtil.getCurrentUserId();
        JSONObject userRole = queryUserRoles(userId);
        if (userRole.get("ysManager") != null && "yes".equalsIgnoreCase(userRole.getString("ysManager"))) {
            params.put("roleName", "all");
            return;
        }
        params.put("roleName", "other");
        params.put("currentUserId", userId);
        params.put("deptIds", userRole.getJSONArray("deptIds"));
    }

    public void setTaskCurrentUser(List<JSONObject> objList) {
        Map<String, Map<String, Object>> taskId2Pro = new HashMap<>();
        for (JSONObject onePro : objList) {
            if (onePro.get("taskId") != null && StringUtils.isNotBlank(onePro.getString("taskId"))) {
                taskId2Pro.put(onePro.getString("taskId"), onePro);
            }
        }
        if (taskId2Pro.isEmpty()) {
            return;
        }
        Map<String, Object> queryTaskExecutors = new HashMap<>();
        queryTaskExecutors.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        queryTaskExecutors.put("taskIds", new ArrayList<String>(taskId2Pro.keySet()));
        List<Map<String, String>> task2Executors = xcmgProjectOtherDao.queryTaskExecutorsByTaskIds(queryTaskExecutors);
        if (task2Executors == null || task2Executors.isEmpty()) {
            logger.warn("can't find task executors");
            return;
        }
        for (Map<String, String> oneTaskExecutor : task2Executors) {
            String taskId = oneTaskExecutor.get("taskId");
            String executorName = oneTaskExecutor.get("currentProcessUser");
            String executorId = oneTaskExecutor.get("currentProcessUserId");
            Map<String, Object> needPutPro = taskId2Pro.get(taskId);
            if (needPutPro.get("currentProcessUser") != null
                    && StringUtils.isNotBlank(needPutPro.get("currentProcessUser").toString())) {
                executorName += "," + needPutPro.get("currentProcessUser").toString();
                executorId += "," + needPutPro.get("currentProcessUserId").toString();
            }
            needPutPro.put("currentProcessUser", executorName);
            needPutPro.put("currentProcessUserId", executorId);
        }
    }

    private void setBudgetNumber(List<JSONObject> objList) {
        Set<String> budgetIdSet = new HashSet<>();
        for (JSONObject onePro : objList) {
            budgetIdSet.add(onePro.getString("budgetId"));
        }
        JSONObject param = new JSONObject();
        param.put("budgetIds",budgetIdSet);
        Map<String,Integer> budgetNumberMap = new HashMap<>();
        Map<String,Integer> processNumberMap = new HashMap<>();
        List<JSONObject> budgetNumberList = budgetMonthFlowDao.queryBudgetNumberById(param);
        List<JSONObject> processNumberList = budgetMonthFlowDao.queryProcessNumberById(param);
        for (JSONObject oneData:budgetNumberList) {
            budgetNumberMap.put(oneData.getString("budgetId"),oneData.getInteger("numCount"));
        }
        for (JSONObject oneData:processNumberList) {
            processNumberMap.put(oneData.getString("budgetId"),oneData.getInteger("numCount"));
        }
        for (JSONObject onePro : objList) {
            if (StringUtils.isNotBlank(onePro.getString("budgetId"))) {
                //根据预算id查该流程预算申报科目数量
                Integer budgetNumber = 0;
                if (budgetNumberMap.containsKey(onePro.getString("budgetId"))) {
                    budgetNumber = budgetNumberMap.get(onePro.getString("budgetId"));
                }
                //根据预算id查该流程报销条目数量
                Integer processNumber = 0;
                if (processNumberMap.containsKey(onePro.getString("budgetId"))) {
                    processNumber = processNumberMap.get(onePro.getString("budgetId"));
                }
                //封装数据
                onePro.put("budgetNumber",budgetNumber);
                onePro.put("processNumber",processNumber);
            }
        }
    }

    // 删除预算关联的所有信息
    public JsonResult deleteBudgetMonthFlow(String[] idArr, String[] instIdArr) {
        if (idArr.length != instIdArr.length) {
            return new JsonResult(false, "操作失败，表单和流程实例数据不一致！");
        }
        for (int i = 0; i < idArr.length; i++) {
            String id = idArr[i];
            // 删除申报流程数据
            budgetMonthFlowDao.deleteBudgetFlowById(id);
            // 删除流程预算数据
            budgetMonthFlowDao.deleteBudgetMonth(id);
            // 删除实例
            bpmInstManager.deleteCascade(instIdArr[i], "");
            // 删除任务表
            budgetMonthUserDao.delTaskData(instIdArr[i]);
        }
        return new JsonResult(true, "成功删除!");
    }

    /**
     * 月度重点工作计划过滤。
     *
     * @param planList
     * @return
     */
    public List<JSONObject> filterPlanListByDepRole(List<JSONObject> planList) {
        List<JSONObject> result = new ArrayList<JSONObject>();
        if (planList == null || planList.isEmpty()) {
            return result;
        }
        // 管理员查看所有的包括草稿数据
        if ("admin".equalsIgnoreCase(ContextUtil.getCurrentUser().getUserNo())) {
            return planList;
        }
        // 当前用户id
        String currentUserId = ContextUtil.getCurrentUserId();
        // 是否是重点工作管理人员
        boolean isYsManager = false;
        JSONObject userRole = queryUserRoles(currentUserId);
        if (userRole.get("ysManager") != null && "yes".equalsIgnoreCase(userRole.getString("ysManager"))) {
            isYsManager = true;
        }
        // 依次过滤每个数据
        for (JSONObject oneProject : planList) {
            // 创建人
            if (oneProject.get("CREATE_BY_") != null && oneProject.get("CREATE_BY_").toString().equals(currentUserId)) {
                result.add(oneProject);
                continue;
            }
            // 如果是草稿状态，后面角色跳过，都不可见
            if (oneProject.get("instStatus") == null || "DRAFTED".equalsIgnoreCase(oneProject.get("instStatus").toString())) {
                continue;
            }
            // 预算管理人员
            if (isYsManager) {
                result.add(oneProject);
                continue;
            }
            // 任务处理人
            if (oneProject.get("currentProcessUserId") != null && oneProject.get("currentProcessUserId").toString().contains(currentUserId)) {
                result.add(oneProject);
                continue;
            }
            //  历史处理流程补充
            //部门负责人历史数据
            Map<String, Object> params = new HashMap<>();
            if (oneProject.containsKey("CREATE_BY_")){
                String userId = oneProject.getString("CREATE_BY_");
                params.put("userId",currentUserId);
                List<JSONObject> leader = commonInfoDao.queryDeptRespByUserId(userId);
                String leaderId = leader.get(0).get("USER_ID_").toString();
                if (leaderId.equalsIgnoreCase(currentUserId)){
                    result.add(oneProject);
                    continue;
                }
            }
            params.clear();
            //项目负责人历史数据
            if (oneProject.containsKey("projectId")){
                String projectId = oneProject.getString("projectId");
                JSONObject param = new JSONObject();
                param.put("projectId",projectId);
                List<Map<String, Object>> personList = budgetMonthUserDao.queryProjectList(param);
                if (personList != null && personList.size() >0) {
                    String respId = personList.get(0).get("respId").toString();
                    if (respId.equalsIgnoreCase(currentUserId)){
                        result.add(oneProject);
                        continue;
                    }
                }
            }

        }

        return result;
    }

    public JsonPageResult<?> judgeBudgetMonthStatus(String yearMonth){
        JsonPageResult result = new JsonPageResult(true);
        List<JSONObject> dataList = budgetMonthFlowDao.judgeBudgetMonthStatus(yearMonth);
        if (dataList == null || dataList.isEmpty()) {
            return result;
        }else {
            result.setTotal(dataList.size());
            result.setSuccess(false);
            return result;
        }
    }
}
