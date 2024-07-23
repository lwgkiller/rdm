package com.redxun.standardManager.core.manager;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.query.Page;
import com.redxun.core.util.DateUtil;
import com.redxun.materielextend.core.dao.MaterielApplyDao;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.standardManager.core.dao.StandardDemandDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

import groovy.util.logging.Slf4j;

@Service
@Slf4j
public class StandardDemandManager {
    private static Logger logger = LoggerFactory.getLogger(StandardDemandManager.class);
    @Autowired
    private StandardDemandDao standardDemandDao;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private CommonInfoDao commonInfoDao;
    @Resource
    private BpmInstManager bpmInstManager;
    @Autowired
    private MaterielApplyDao materielApplyDao;

    public JsonPageResult<?> standardApplyList(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>();
            // 传入条件(不包括分页)
            getApplyListParams(params, request);
            List<Map<String, Object>> applyList = standardDemandDao.queryStandardDemandList(params);
            // 增加不同角色和部门的人看到的数据不一样的过滤
            List<Map<String, Object>> finalAllApplyList = null;
            finalAllApplyList = filterApplyListByDepRole(applyList);

            // 根据分页进行subList截取
            int pageIndex = RequestUtil.getInt(request, "pageIndex", 0);
            int pageSize = RequestUtil.getInt(request, "pageSize", Page.DEFAULT_PAGE_SIZE);
            int startSubListIndex = pageIndex * pageSize;
            int endSubListIndex = startSubListIndex + pageSize;
            List<Map<String, Object>> finalSubApplyList = new ArrayList<Map<String, Object>>();
            if (startSubListIndex < finalAllApplyList.size()) {
                finalSubApplyList = finalAllApplyList.subList(startSubListIndex,
                    endSubListIndex < finalAllApplyList.size() ? endSubListIndex : finalAllApplyList.size());
            }

            if (finalSubApplyList != null && !finalSubApplyList.isEmpty()) {
                Set<String> doDeptIdSet = new HashSet<>();
                for (Map<String, Object> oneApply : finalSubApplyList) {
                    if (oneApply.get("CREATE_TIME_") != null) {
                        oneApply.put("CREATE_TIME_",
                            DateUtil.formatDate((Date)oneApply.get("CREATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
                    }
                    if (oneApply.get("applyTime") != null) {
                        oneApply.put("applyTime",
                            DateUtil.formatDate((Date)oneApply.get("applyTime"), "yyyy-MM-dd HH:mm:ss"));
                    }
                    if (oneApply.get("UPDATE_TIME_") != null) {
                        oneApply.put("UPDATE_TIME_",
                            DateUtil.formatDate((Date)oneApply.get("UPDATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
                    }
                    if (oneApply.get("doDeptIds") != null) {
                        String[] doDeptIdArr = oneApply.get("doDeptIds").toString().split(",", -1);
                        if (doDeptIdArr != null && doDeptIdArr.length > 0) {
                            doDeptIdSet.addAll(Arrays.asList(doDeptIdArr));
                        }
                    }
                }
                assignDoDeptNames(finalSubApplyList, doDeptIdSet);
            }
            // 返回结果
            result.setData(finalSubApplyList);
            result.setTotal(finalAllApplyList.size());
        } catch (Exception e) {
            logger.error("Exception in standardApplyList", e);
            result.setSuccess(false);
            result.setMessage("系统异常");
        }
        return result;
    }

    private void assignDoDeptNames(List<Map<String, Object>> finalSubApplyList, Set<String> deptIds) {
        if (deptIds == null || deptIds.isEmpty()) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("deptIds", deptIds);
        List<JSONObject> deptInfos = commonInfoDao.queryDeptByIds(params);
        if (deptInfos == null || deptInfos.isEmpty()) {
            return;
        }
        Map<String, String> deptId2Name = new HashMap<>();
        for (JSONObject oneDept : deptInfos) {
            deptId2Name.put(oneDept.getString("GROUP_ID_"), oneDept.getString("NAME_"));
        }
        for (Map<String, Object> oneApply : finalSubApplyList) {
            if (oneApply.get("doDeptIds") != null && StringUtils.isNotBlank(oneApply.get("doDeptIds").toString())) {
                String[] doDeptIdArr = oneApply.get("doDeptIds").toString().split(",", -1);
                if (doDeptIdArr != null && doDeptIdArr.length > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (String oneDeptId : doDeptIdArr) {
                        sb.append(deptId2Name.get(oneDeptId)).append(",");
                    }
                    oneApply.put("doDeptNames", sb.substring(0, sb.length() - 1));
                }
            }
        }
    }

    // 将过滤条件、排序等信息传入，分页不在此处进行
    // 根据登录人部门、角色对列表进行过滤

    private void getApplyListParams(Map<String, Object> params, HttpServletRequest request) {
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");

        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "applyId");
            params.put("sortOrder", "DESC");
        }
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    // 数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
                    if ("applyTimeStart".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8));
                    }
                    if ("applyTimeEnd".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16));
                    }
                    params.put(name, value);
                }
            }
        }
    }

    private List<Map<String, Object>> filterApplyListByDepRole(List<Map<String, Object>> applyList) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if (applyList == null || applyList.isEmpty()) {
            return result;
        }
        // 刷新任务的当前执行人
        xcmgProjectManager.setTaskCurrentUser(applyList);
        Iterator<Map<String, Object>> iterator = applyList.iterator();
        while (iterator.hasNext()) {
            Map<String, Object> next = iterator.next();
            if ("DRAFTED".equals(next.get("instStatus"))
                && !ContextUtil.getCurrentUserId().equals(next.get("CREATE_BY_"))) {
                iterator.remove();
            }
        }
        // 管理员查看所有的包括草稿数据
        if ("admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
            return applyList;
        }
        // 标准管理领导的查看权限等同于标准管理人员
        /*boolean showAll = false;
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUser().getUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        for (Map<String, Object> oneRole : currentUserRoles) {
            if (oneRole.get("REL_TYPE_KEY_").toString().equalsIgnoreCase("GROUP-USER-LEADER")
                || oneRole.get("REL_TYPE_KEY_").toString().equalsIgnoreCase("GROUP-USER-BELONG")) {
                if ("标准管理领导".equalsIgnoreCase(oneRole.get("NAME_").toString())
                    || oneRole.get("NAME_").toString().indexOf("标准管理人员") != -1) {
                    showAll = true;
                    break;
                }
            }
        }
        // 确定当前登录人是否是部门负责人
        JSONObject currentUserDepInfo = xcmgProjectManager.isCurrentUserDepRespman();
        if (!"success".equals(currentUserDepInfo.getString("result"))) {
            return result;
        }
        boolean isDepRespMan = currentUserDepInfo.getBoolean("isDepRespMan");
        String currentUserMainDepId = currentUserDepInfo.getString("currentUserMainDepId");
        String currentUserId = ContextUtil.getCurrentUserId();*/

        // 过滤
        for (Map<String, Object> oneApply : applyList) {
            // 自己是当前流程处理人
            /*if (oneApply.get("currentProcessUserId") != null
                && oneApply.get("currentProcessUserId").toString().contains(currentUserId)) {
                oneApply.put("processTask", true);
                result.add(oneApply);
            } else if (showAll) {
                // 标准管理领导和标准管理人员查看所有非草稿的数据或者草稿但是创建人CREATE_BY_是自己的
                if (oneApply.get("instStatus") != null && !"DRAFTED".equals(oneApply.get("instStatus").toString())) {
                    result.add(oneApply);
                } else {
                    if (oneApply.get("CREATE_BY_").toString().equals(currentUserId)) {
                        result.add(oneApply);
                    }
                }
            } else if (isDepRespMan) {
                // 部门负责人对于非草稿的且申请人部门是当前部门，或者草稿但是创建人CREATE_BY_是自己的
                if (oneApply.get("instStatus") != null && !"DRAFTED".equals(oneApply.get("instStatus").toString())) {
                    if (oneApply.get("applyUserDepId").toString().equals(currentUserMainDepId)) {
                        result.add(oneApply);
                    }
                } else {
                    if (oneApply.get("CREATE_BY_").toString().equals(currentUserId)) {
                        result.add(oneApply);
                    }
                }
            } else {
                // 其他人对于创建人CREATE_BY_是自己的
                if (oneApply.get("CREATE_BY_").toString().equals(currentUserId)) {
                    result.add(oneApply);
                }
            }*/
            result.add(oneApply);
        }

        return result;
    }

    // 删除表单及关联的所有信息
    public JsonResult deleteApply(String[] applyIdArr, String[] instIdArr) {
        if (applyIdArr.length != instIdArr.length) {
            return new JsonResult(false, "表单和流程个数不相同！");
        }
        for (int i = 0; i < applyIdArr.length; i++) {
            String applyId = applyIdArr[i];
            standardDemandDao.deleteStandardDemandById(applyId);
            // 删除实例
            bpmInstManager.deleteCascade(instIdArr[i], "");
        }
        return new JsonResult(true, "成功删除!");
    }

    // 新增申请单
    public void createStandardApply(JSONObject params) {
        String prefix = "SC-";
        String applyId = prefix + XcmgProjectUtil.getNowLocalDateStr("yyyyMMddHHmmssSSS") + (int)(Math.random() * 100);
        params.put("applyId", applyId);
        params.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        params.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        standardDemandDao.addStandardDemand(params);
        // 更新用户的手机号
        Map<String, Object> updateParams = new HashMap<>();
        updateParams.put("userId", params.getString("applyUserId"));
        updateParams.put("mobile", params.getString("applyUserPhone"));
        materielApplyDao.updateApplyerMobile(updateParams);
    }

    public void updateStandardApply(JSONObject params) {
        params.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        params.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        standardDemandDao.updateStandardDemand(params);
        // 更新用户的手机号
        Map<String, Object> updateParams = new HashMap<>();
        updateParams.put("userId", params.getString("applyUserId"));
        updateParams.put("mobile", params.getString("applyUserPhone"));
        materielApplyDao.updateApplyerMobile(updateParams);
    }

    public JSONObject queryDemandDetail(String formId) {
        JSONObject result = standardDemandDao.queryDemandDetailById(formId);
        if (result != null && StringUtils.isNotBlank(result.getString("doDeptIds"))) {
            Set<String> doDeptIdSet = new HashSet<>();
            String[] doDeptIdArr = result.getString("doDeptIds").split(",", -1);
            if (doDeptIdArr != null && doDeptIdArr.length > 0) {
                doDeptIdSet.addAll(Arrays.asList(doDeptIdArr));
                Map<String, Object> params = new HashMap<>();
                params.put("deptIds", doDeptIdSet);
                List<JSONObject> deptInfos = commonInfoDao.queryDeptByIds(params);
                if (deptInfos != null && !deptInfos.isEmpty()) {
                    Map<String, String> deptId2Name = new HashMap<>();
                    for (JSONObject oneDept : deptInfos) {
                        deptId2Name.put(oneDept.getString("GROUP_ID_"), oneDept.getString("NAME_"));
                    }
                    StringBuilder sb = new StringBuilder();
                    for (String oneDeptId : doDeptIdArr) {
                        sb.append(deptId2Name.get(oneDeptId)).append(",");
                    }
                    result.put("doDeptNames", sb.substring(0, sb.length() - 1));
                }
            }
        }
        return result;
    }
}
