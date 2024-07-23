package com.redxun.rdmZhgl.core.service;

import com.alibaba.fastjson.JSONObject;
import com.hp.hpl.sparta.xpath.TrueExpr;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.dao.AwardGroupProgressDao;
import com.redxun.rdmZhgl.core.dao.JstbDao;
import com.redxun.rdmZhgl.core.dao.RdmZhglDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.report.dao.XcmgProjectReportDao;
import javafx.beans.binding.ObjectExpression;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 应用模块名称
 * <p>
 * 代码描述
 * <p>
 * Copyright: Copyright (C) 2020 XXX, Inc. All rights reserved.
 * <p>
 * Company: 徐工挖掘机械有限公司
 * <p>
 *
 * @author liangchuanjiang
 * @since 2020/6/9 16:21
 */
@Service
public class RdmZhglUtil {
    private static final Logger logger = LoggerFactory.getLogger(RdmZhglUtil.class);
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private XcmgProjectReportDao xcmgProjectReportDao;
    @Autowired
    private RdmZhglDao rdmZhglDao;
    @Autowired
    private JstbDao jstbDao;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private AwardGroupProgressDao awardGroupProgressDao;
    @Autowired
    private CommonInfoDao commonInfoDao;

    // 通过groupName查询组内用户
    /**
     * 查询分管领导信息
     *
     * @return
     */
    public List<Map<String, String>> queryFgld() {
        Map<String, Object> params = new HashMap<>();
        params.put("groupName", "分管领导");
        List<Map<String, String>> leaderInfos = xcmgProjectOtherDao.queryUserByGroupName(params);
        return leaderInfos;
    }

    public List<Map<String, String>> queryUserByGroupName(String groupName) {
        Map<String, Object> params = new HashMap<>();
        params.put("groupName", groupName);
        List<Map<String, String>> userInfos = xcmgProjectOtherDao.queryUserByGroupName(params);
        return userInfos;
    }

    // 查询公司领导
    public List<Map<String, String>> queryGsld() {
        Map<String, Object> params = new HashMap<>();
        params.put("groupName", "公司领导");
        List<Map<String, String>> leaderInfos = xcmgProjectOtherDao.queryUserByGroupName(params);
        return leaderInfos;
    }

    /**
     * 查询智能控制研究所负责人信息
     *
     * @return
     */
    public List<Map<String, String>> queryZksRespUser() {
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("groupName", RdmConst.ZNKZS_NAME);
        List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepRespManById(params);
        return depRespMans;
    }

    /**
     * 查询技术管理部负责人信息
     *
     * @return
     */
    public List<Map<String, String>> queryJsglbRespUser() {
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("groupName", RdmConst.JSGLB_NAME);
        List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepRespManById(params);
        return depRespMans;
    }

    /**
     * 查询供方发展部负责人信息
     *
     * @return
     */
    public List<Map<String, String>> queryGffzbRespUser() {
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("groupName", RdmConst.GFFZB_NAME);
        List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepRespManById(params);
        return depRespMans;
    }

    /**
     * 查询制造管理部负责人信息
     *
     * @return
     */
    public List<Map<String, String>> queryZzglbRespUser() {
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("groupName", RdmConst.ZZGLB_NAME);
        List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepRespManById(params);
        return depRespMans;
    }

    /**
     * 查询营销公司市场部负责人
     *
     * @return
     */
    public List<Map<String, String>> queryScbRespUser() {
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("groupName", "市场部");
        List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepRespManById(params);
        return depRespMans;
    }

    /**
     * 查询质量保证部负责人
     *
     * @return
     */
    public List<Map<String, String>> queryZlbzbfzr() {
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("groupName", RdmConst.ZLBZB_NAME);
        List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepRespManById(params);
        return depRespMans;
    }

    /**
     * 查询采购管理部负责人
     *
     * @return
     */
    public List<Map<String, String>> queryCgglbfzr() {
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("groupName", RdmConst.CGGLB_NAME);
        List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepRespManById(params);
        return depRespMans;
    }

    /**
     * 查询工艺部门负责人
     *
     * @return
     */
    public List<Map<String, String>> queryGybmfzr() {
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("groupName", RdmConst.GYJSB_NAME);
        List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepRespManById(params);
        return depRespMans;
    }

    /**
     * 查询服务所负责人
     *
     * @return
     */
    public List<Map<String, String>> queryFwgcs() {
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("groupName", RdmConst.FWGCS_NAME);
        List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepRespManById(params);
        return depRespMans;
    }

    /**
     * 查询数字化部负责人
     * 
     * @return
     */
    public List<Map<String, String>> queryXXHGLBfzr() {
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("groupName", RdmConst.XXHGLB_NAME);
        List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepRespManById(params);
        return depRespMans;
    }

    /**
     * 判断用户是否是技术管理部负责人
     *
     * @param userId
     * @return
     */
    public boolean judgeUserIsJsglbRespUser(String userId) {
        List<Map<String, String>> userInfos = queryJsglbRespUser();
        for (Map<String, String> userInfo : userInfos) {
            if (userId.equalsIgnoreCase(userInfo.get("PARTY2_"))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断用户是否是分管领导
     *
     * @param userId
     * @return
     */
    public boolean judgeUserIsFgld(String userId) {
        List<Map<String, String>> userInfos = queryFgld();
        for (Map<String, String> userInfo : userInfos) {
            if (userId.equalsIgnoreCase(userInfo.get("USER_ID_"))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断用户是否是指定的角色
     *
     * @param userId
     * @return
     */
    public boolean judgeIsPointRoleUser(String userId, String pointRoleName) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        if (currentUserRoles == null || currentUserRoles.isEmpty()) {
            return false;
        }
        for (Map<String, Object> oneRole : currentUserRoles) {
            String roleName = oneRole.get("NAME_").toString();
            if (pointRoleName.equalsIgnoreCase(roleName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断用户是否是指定的岗位
     *
     * @param userId
     * @return
     */
    public boolean judgeIsPointGwUser(String userId, String pointGwName) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> currentUserGws = xcmgProjectOtherDao.queryUserGW(params);
        if (currentUserGws == null || currentUserGws.isEmpty()) {
            return false;
        }
        for (Map<String, Object> oneGw : currentUserGws) {
            String gwName = oneGw.get("NAME_").toString();
            if (pointGwName.equalsIgnoreCase(gwName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 向条件中增加排序
     *
     * @param request
     * @param params
     */
    public void addOrder(HttpServletRequest request, Map<String, Object> params, String defaultSortField,
        String defaultSortOrder) {
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", defaultSortField);
            params.put("sortOrder", StringUtils.isBlank(defaultSortOrder) ? "asc" : defaultSortOrder);
        }
    }

    public void addOrderJSON(HttpServletRequest request, JSONObject params, String defaultSortField,
        String defaultSortOrder) {
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", defaultSortField);
            params.put("sortOrder", StringUtils.isBlank(defaultSortOrder) ? "asc" : defaultSortOrder);
        }
    }

    public void addPageJSON(HttpServletRequest request, JSONObject params) {
        params.put("startIndex", 0);
        params.put("pageSize", 20);
        String pageIndex = request.getParameter("pageIndex");
        String pageSize = request.getParameter("pageSize");
        if (StringUtils.isNotBlank(pageIndex) && StringUtils.isNotBlank(pageSize)) {
            params.put("startIndex", Integer.parseInt(pageSize) * Integer.parseInt(pageIndex));
            params.put("pageSize", Integer.parseInt(pageSize));
        }
    }

    /**
     * 向条件中增加分页
     *
     * @param request
     * @param params
     */
    public void addPage(HttpServletRequest request, Map<String, Object> params) {
        params.put("startIndex", 0);
        params.put("pageSize", 20);
        String pageIndex = request.getParameter("pageIndex");
        String pageSize = request.getParameter("pageSize");
        if (StringUtils.isNotBlank(pageIndex) && StringUtils.isNotBlank(pageSize)) {
            params.put("startIndex", Integer.parseInt(pageSize) * Integer.parseInt(pageIndex));
            params.put("pageSize", Integer.parseInt(pageSize));
        }
    }

    // 向业务数据中写入任务相关的信息（适用于一个流程可能有多个任务的场景，如会签、并行网关）
    public void setTaskInfo2Data(List<JSONObject> dataList, String currentUserId) {
        if (dataList == null || dataList.isEmpty()) {
            return;
        }
        List<String> actInstIds = new ArrayList<>();
        Map<String, JSONObject> instId2Obj = new HashMap<>();
        for (JSONObject jstb : dataList) {
            if (StringUtils.isNotBlank(jstb.getString("ACT_INST_ID_"))) {
                actInstIds.add(jstb.getString("ACT_INST_ID_"));
                instId2Obj.put(jstb.getString("ACT_INST_ID_"), jstb);
            }
        }
        // 1、通过actInstId去act_ru_task中查询taskId,taskName,proc_inst_ID，返回List<Map<String, Object>>
        Map<String, Object> param = new HashMap<>();
        if (!actInstIds.isEmpty()) {
            param.put("actInstIds", actInstIds);
            List<Map<String, Object>> taskAll = jstbDao.queryTaskAll(param);

            // 2、调用xcmgProjectManager.setTaskCurrentUser(jsmmList);查询每一个task的处理人id和name
            if (taskAll != null && !taskAll.isEmpty()) {
                xcmgProjectManager.setTaskCurrentUser(taskAll);
            }
            // 3、通过PROC_INST_ID_关联所属的代办任务
            Map<String, List<Map<String, Object>>> instId2TaskList = new HashMap<>();
            for (Map<String, Object> oneTask : taskAll) {
                String proc_inst_id_ = oneTask.get("PROC_INST_ID_").toString();
                if (!instId2TaskList.containsKey(proc_inst_id_)) {
                    instId2TaskList.put(proc_inst_id_, new ArrayList<>());
                }
                instId2TaskList.get(proc_inst_id_).add(oneTask);
            }
            // 4、遍历每一个task列表，取出最终的taskName,处理人和myTaskId
            for (Map.Entry<String, List<Map<String, Object>>> entry : instId2TaskList.entrySet()) {
                String proc_inst_id_ = entry.getKey();
                List<Map<String, Object>> oneInstIdTaskList = entry.getValue();
                Set<String> taskNames = new HashSet<>();
                String allTaskUserNames = "";
                StringBuilder stringBuilder = new StringBuilder();
                String myTaskId = "";
                for (Map<String, Object> oneTask : oneInstIdTaskList) {
                    String oneTaskName = oneTask.get("NAME_").toString();
                    taskNames.add(oneTaskName);
                    if (oneTask.get("currentProcessUser") != null) {
                        stringBuilder.append(oneTask.get("currentProcessUser").toString()).append(",");
                    }
                    if (StringUtils.isBlank(myTaskId) && oneTask.get("currentProcessUserId") != null) {
                        List<String> userIds =
                            Arrays.asList(oneTask.get("currentProcessUserId").toString().split(",", -1));
                        if (userIds.contains(currentUserId)) {
                            myTaskId = oneTask.get("taskId").toString();
                        }
                    }
                }
                if (StringUtils.isNotBlank(stringBuilder.toString())) {
                    allTaskUserNames = stringBuilder.substring(0, stringBuilder.length() - 1);
                }
                StringBuilder sbTaskNames = new StringBuilder();
                for (String oneTaskName : taskNames) {
                    sbTaskNames.append(oneTaskName).append(",");
                }
                // 赋值到业务数据中
                JSONObject jstbObj = instId2Obj.get(proc_inst_id_);
                jstbObj.put("taskName", sbTaskNames.substring(0, sbTaskNames.length() - 1));
                jstbObj.put("myTaskId", myTaskId);
                jstbObj.put("allTaskUserNames", allTaskUserNames);
            }
        }
    }

    // 向业务数据中写入任务相关的信息（适用于一个流程可能有多个任务的场景，如会签、并行网关）
    public void setTaskInfo2DataMap(List<Map<String, Object>> dataList, String currentUserId) {
        List<String> actInstIds = new ArrayList<>();
        Map<String, Map<String, Object>> instId2Obj = new HashMap<>();
        for (Map<String, Object> jstb : dataList) {
            if (jstb.get("ACT_INST_ID_") != null && StringUtils.isNotBlank(jstb.get("ACT_INST_ID_").toString())) {
                actInstIds.add(jstb.get("ACT_INST_ID_").toString());
                instId2Obj.put(jstb.get("ACT_INST_ID_").toString(), jstb);
            }
        }
        // 1、通过actInstId去act_ru_task中查询taskId,taskName,proc_inst_ID，返回List<Map<String, Object>>
        Map<String, Object> param = new HashMap<>();
        if (!actInstIds.isEmpty()) {
            param.put("actInstIds", actInstIds);
            List<Map<String, Object>> taskAll = jstbDao.queryTaskAll(param);

            // 2、调用xcmgProjectManager.setTaskCurrentUser(jsmmList);查询每一个task的处理人id和name
            if (taskAll != null && !taskAll.isEmpty()) {
                xcmgProjectManager.setTaskCurrentUser(taskAll);
            }
            // 3、通过PROC_INST_ID_关联所属的代办任务
            Map<String, List<Map<String, Object>>> instId2TaskList = new HashMap<>();
            for (Map<String, Object> oneTask : taskAll) {
                String proc_inst_id_ = oneTask.get("PROC_INST_ID_").toString();
                if (!instId2TaskList.containsKey(proc_inst_id_)) {
                    instId2TaskList.put(proc_inst_id_, new ArrayList<>());
                }
                instId2TaskList.get(proc_inst_id_).add(oneTask);
            }
            // 4、遍历每一个task列表，取出最终的taskName,处理人和myTaskId
            for (Map.Entry<String, List<Map<String, Object>>> entry : instId2TaskList.entrySet()) {
                String proc_inst_id_ = entry.getKey();
                List<Map<String, Object>> oneInstIdTaskList = entry.getValue();
                Set<String> taskNames = new HashSet<>();
                String allTaskUserNames = "";
                StringBuilder stringBuilder = new StringBuilder();
                String allTaskUserIds = "";
                StringBuilder allTaskUserIdSb = new StringBuilder();
                String myTaskId = "";
                for (Map<String, Object> oneTask : oneInstIdTaskList) {
                    String oneTaskName = oneTask.get("NAME_").toString();
                    taskNames.add(oneTaskName);
                    if (oneTask.get("currentProcessUser") != null) {
                        stringBuilder.append(oneTask.get("currentProcessUser").toString()).append(",");
                    }
                    if (oneTask.get("currentProcessUserId") != null) {
                        allTaskUserIdSb.append(oneTask.get("currentProcessUserId").toString()).append(",");
                    }
                    if (StringUtils.isBlank(myTaskId) && oneTask.get("currentProcessUserId") != null) {
                        List<String> userIds =
                            Arrays.asList(oneTask.get("currentProcessUserId").toString().split(",", -1));
                        if (userIds.contains(currentUserId)) {
                            myTaskId = oneTask.get("taskId").toString();
                        }
                    }
                }
                if (StringUtils.isNotBlank(stringBuilder.toString())) {
                    allTaskUserNames = stringBuilder.substring(0, stringBuilder.length() - 1);
                }
                if (StringUtils.isNotBlank(allTaskUserIdSb.toString())) {
                    allTaskUserIds = allTaskUserIdSb.substring(0, allTaskUserIdSb.length() - 1);
                }
                StringBuilder sbTaskNames = new StringBuilder();
                for (String oneTaskName : taskNames) {
                    sbTaskNames.append(oneTaskName).append(",");
                }
                // 赋值到业务数据中
                Map<String, Object> jstbObj = instId2Obj.get(proc_inst_id_);
                jstbObj.put("taskName", sbTaskNames.substring(0, sbTaskNames.length() - 1));
                jstbObj.put("myTaskId", myTaskId);
                jstbObj.put("allTaskUserNames", allTaskUserNames);
                jstbObj.put("allTaskUserIds", allTaskUserIds);
                String taskCreateTime =
                    DateFormatUtil.format((Date)oneInstIdTaskList.get(0).get("CREATE_TIME_"), "yyyy-MM-dd HH:mm:ss");
                jstbObj.put("taskCreateTime", taskCreateTime);
            }
        }
    }

    // 根据中文顿号分隔的名字，查询对应的userId，如果有同名的则返回失败。同时根据条件决定是否过滤公司领导
    public JSONObject toGetUserInfosByNameStr(String userNameStr, boolean filterLd) {
        JSONObject result = new JSONObject();
        result.put("result", true);
        String userNameOriginal = "";
        String userIdFilter = "";
        String userNameFilter = "";
        if (StringUtils.isBlank(userNameStr)) {
            result.put("userNameOriginal", userNameOriginal);
            result.put("userIdFilter", userIdFilter);
            result.put("userNameFilter", userNameFilter);
            return result;
        }
        // 原始名称
        userNameOriginal = userNameStr;
        List<String> tempList = Arrays.asList(userNameOriginal.split("、"));
        List<String> userNameOriginalList = new ArrayList<>();
        for (String name : tempList) {
            userNameOriginalList.add(StringUtils.trim(name));
        }
        Set<String> distinctNames = new HashSet<>(userNameOriginalList);
        Map<String, Object> param = new HashMap<>();
        param.put("names", new ArrayList<String>(distinctNames));
        List<JSONObject> nameCount = awardGroupProgressDao.queryNameCount(param);
        // 系统中存在名称对应的用户信息
        if (nameCount != null && !nameCount.isEmpty()) {
            // 存在名称对应多个用户
            if (nameCount.get(0).getIntValue("countNum") > 1) {
                result.put("result", false);
                StringBuilder errorNames = new StringBuilder();
                for (JSONObject oneName : nameCount) {
                    if (oneName.getIntValue("countNum") > 1) {
                        errorNames.append(oneName.getString("fullName") + ",");
                    }
                }
                result.put("message", errorNames.substring(0, errorNames.length() - 1) + "等名称出现重复，请手动录入。");
            } else {
                Map<String, String> userName2Id = new HashMap<>();
                for (JSONObject oneName : nameCount) {
                    userName2Id.put(oneName.getString("fullName"), oneName.getString("userId"));
                }
                // 生成过滤后的name和id(决定是否过滤领导)
                Set<String> gsldNames = new HashSet<>();
                if (filterLd) {
                    List<Map<String, String>> gsldInfos = queryGsld();
                    for (Map<String, String> oneData : gsldInfos) {
                        gsldNames.add(oneData.get("FULLNAME_"));
                    }
                }
                StringBuilder userIdFilterSb = new StringBuilder();
                StringBuilder userNameFilterSb = new StringBuilder();
                for (String oneName : userNameOriginalList) {
                    if (userName2Id.containsKey(oneName) && !gsldNames.contains(oneName)) {
                        userNameFilterSb.append(oneName).append(",");
                        userIdFilterSb.append(userName2Id.get(oneName)).append(",");
                    }
                }
                userIdFilter = userIdFilterSb.substring(0, userIdFilterSb.length() - 1);
                userNameFilter = userNameFilterSb.substring(0, userNameFilterSb.length() - 1);
            }

        }
        result.put("userNameOriginal", userNameOriginal);
        result.put("userIdFilter", userIdFilter);
        result.put("userNameFilter", userNameFilter);
        return result;
    }

    /**
     * 通过人员的岗位获取对应的专业
     * 
     * @param userId
     * @return
     */
    public String queryUserZyByUserId(String userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        List<Map<String, Object>> currentUserGws = xcmgProjectOtherDao.queryUserGW(params);
        if (currentUserGws == null || currentUserGws.isEmpty()) {
            return "";
        }
        String zyStr = "";
        for (Map<String, Object> oneGw : currentUserGws) {
            if (oneGw.get("NAME_") != null && StringUtils.isNotBlank(oneGw.get("NAME_").toString())
                && (oneGw.get("NAME_").toString().endsWith("工程师") || oneGw.get("NAME_").toString().endsWith("室主任"))
                && oneGw.get("NAME_").toString().length() >= 5) {
                zyStr = oneGw.get("NAME_").toString().substring(oneGw.get("NAME_").toString().length() - 5,
                    oneGw.get("NAME_").toString().length() - 3);
            }
        }

        return zyStr;
    }

    // 解析2021/3/1这种格式为2021-03-01
    public static String toGetImportDateStr(String cellValue) {
        try {
            Date dateResult = DateFormatUtil.parse(cellValue, "yyyy-MM-dd HH:mm:ss");
            return DateFormatUtil.format(dateResult, "yyyy-MM-dd");
        } catch (Exception e) {
            logger.error("exception in toGetImportDateStr", e);
            return null;
        }
    }

    // 设置任务创建时间信息
    public void setTaskCreateTimeInfo(List<Map<String, Object>> dataList, boolean hasQueryPlanEndTime) {
        // 已在数据库中查询planEndTime时针对该字段是否为null去查找taskCreateTime
        if (hasQueryPlanEndTime) {
            for (Map<String, Object> obj : dataList) {
                if (obj.get("planEndTime") == null) {
                    // 计划结束结束时间为空，设置taskCreateTime信息
                    setTaskInfo2DataMap(dataList, ContextUtil.getCurrentUserId());
                }
            }
        } else {// 之前没有查询planEndTime则先查询再根据planEndTime是否为空以查询taskCreateTime
            Map<String, Object> params = new HashMap<>();
            for (Map<String, Object> oneProject : dataList) {
                params.put("projectId", oneProject.get("projectId").toString());
                List<Map<String, Object>> projectInfo = xcmgProjectReportDao.queryPlanEndTimeById(params);
                if (projectInfo != null && !projectInfo.isEmpty()) {
                    oneProject.put("planEndTime", projectInfo.get(0).get("planEndTime"));
                }
            }
            // 批量查询
            // Map<String, Object> params = new HashMap<>();
            // ArrayList<String> projectIds = new ArrayList<>();
            // for (Map<String, Object> oneProject : dataList) {
            // projectIds.add(oneProject.get("projectId").toString());
            // }
            // params.put("projectId", projectIds);
            // List<Map<String, Object>> projectInfo = xcmgProjectReportDao.queryPlanEndTimeByIdBatch(params);
            // if (projectInfo != null && !projectInfo.isEmpty()) {
            // for (Map<String,Object> oneProject:dataList) {
            // String projectId = oneProject.get("projectId").toString();
            // for (Map<String, Object> dataMap : projectInfo) {
            // if (dataMap.get("projectId").toString().equals(projectId)) {
            // oneProject.put("planEndTime", dataMap.get("planEndTime"));
            // }
            // }
            // }
            // }
            hasQueryPlanEndTime = true;
            setTaskCreateTimeInfo(dataList, hasQueryPlanEndTime);
        }

    }
}
