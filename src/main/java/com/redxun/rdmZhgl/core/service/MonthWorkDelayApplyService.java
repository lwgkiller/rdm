package com.redxun.rdmZhgl.core.service;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.dao.*;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.query.Page;
import com.redxun.core.util.DateUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.ConstantUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

import groovy.util.logging.Slf4j;

/**
 * @author zz
 */
@Service
@Slf4j
public class MonthWorkDelayApplyService {
    private static Logger logger = LoggerFactory.getLogger(MonthWorkDelayApplyService.class);
    @Resource
    private BpmInstManager bpmInstManager;
    @Resource
    private MonthWorkDelayApplyDao monthWorkDelayApplyDao;
    @Resource
    private MonthWorkDao monthWorkDao;
    @Resource
    private MonthUnProjectPlanDao monthUnProjectPlanDao;
    @Resource
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Resource
    private MonthUnPlanTaskDao monthUnPlanTaskDao;

    public void add(Map<String, Object> params) {
        String preFix = "SP-";
        String id = preFix + XcmgProjectUtil.getNowLocalDateStr("yyyyMMddHHmmssSSS") + (int)(Math.random() * 100);
        params.put("id", id);
        params.put("deptId", ContextUtil.getCurrentUser().getMainGroupId());
        params.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        params.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        params.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        params.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        if (params.get("yearMonth") != null ) {
            String yearMonth = params.get("yearMonth").toString().substring(0,7);
            params.put("yearMonth",yearMonth);
        }
        String planIds = CommonFuns.nullToString(params.get("planIds"));
        if(StringUtil.isNotEmpty(planIds)){
            Map<String, Object> map = new HashMap<>(16);
            String[] idArr = planIds.split(",", -1);
            List<String> idList = Arrays.asList(idArr);
            map.put("ids", idList);
            monthWorkDao.batchUpdate(map);
        }
        String unPlanIds = CommonFuns.nullToString(params.get("unPlanIds"));
        if(StringUtil.isNotEmpty(unPlanIds)){
            Map<String, Object> map = new HashMap<>(16);
            String[] idArr = unPlanIds.split(",", -1);
            List<String> idList = Arrays.asList(idArr);
            map.put("ids", idList);
            monthUnProjectPlanDao.batchUpdate(map);
        }
        String unPlanTaskIds = CommonFuns.nullToString(params.get("unPlanTaskIds"));
        if(StringUtil.isNotEmpty(unPlanTaskIds)){
            Map<String, Object> map = new HashMap<>(16);
            String[] idArr = unPlanTaskIds.split(",", -1);
            List<String> idList = Arrays.asList(idArr);
            map.put("ids", idList);
            monthUnPlanTaskDao.batchUpdate(map);
        }
        monthWorkDelayApplyDao.add(params);

    }

    public void update(Map<String, Object> params) {
        params.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        params.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        monthWorkDelayApplyDao.update(params);
    }

    /**
     * 删除表单及关联的所有信息
     */
    public JsonResult delete(String[] applyIdArr, String[] instIdArr) {
        if (applyIdArr.length != instIdArr.length) {
            return new JsonResult(false, "表单和流程个数不相同！");
        }
        for (int i = 0; i < applyIdArr.length; i++) {
            String applyId = applyIdArr[i];
            monthWorkDelayApplyDao.delete(applyId);
            // 删除实例
            bpmInstManager.deleteCascade(instIdArr[i], "");
        }
        return new JsonResult(true, "成功删除!");
    }

    /**
     * 查询变更列表
     */
    public JsonPageResult<?> queryList(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>();
            // 传入条件(不包括分页)
            params = XcmgProjectUtil.getSearchParam(params, request);
            List<Map<String, Object>> applyList = monthWorkDelayApplyDao.queryList(params);
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
                }
            }
            // 返回结果
            result.setData(finalSubApplyList);
            result.setTotal(finalAllApplyList.size());
        } catch (Exception e) {
            logger.error("Exception in queryList", e);
            result.setSuccess(false);
            result.setMessage("系统异常");
        }
        return result;
    }
    public List<JSONObject> getApplyListByParam(JSONObject jsonObject){
        return monthWorkDelayApplyDao.getApplyListByParam(jsonObject);
    }

    /**
     * 根据登录人部门、角色对列表进行过滤
     */
    public List<Map<String, Object>> filterApplyListByDepRole(List<Map<String, Object>> applyList) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if (applyList == null || applyList.isEmpty()) {
            return result;
        }
        // 刷新任务的当前执行人
        xcmgProjectManager.setTaskCurrentUser(applyList);
        // 管理员查看所有的包括草稿数据
        if (ConstantUtil.ADMIN.equals(ContextUtil.getCurrentUser().getUserNo())) {
            return applyList;
        }
        // 分管领导的查看权限等同于项目管理人员
        boolean showAll = false;
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUser().getUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        for (Map<String, Object> oneRole : currentUserRoles) {
            if (oneRole.get("REL_TYPE_KEY_").toString().equalsIgnoreCase("GROUP-USER-LEADER")
                || oneRole.get("REL_TYPE_KEY_").toString().equalsIgnoreCase("GROUP-USER-BELONG")) {
                if (RdmConst.AllDATA_QUERY_NAME.equalsIgnoreCase(oneRole.get("NAME_").toString())) {
                    showAll = true;
                    break;
                }
                if ("月度工作-管理员".equalsIgnoreCase(oneRole.get("NAME_").toString())) {
                    showAll = true;
                    break;
                }
            }
        }
        // 确定当前登录人是否是部门负责人
        JSONObject currentUserDepInfo = xcmgProjectManager.isCurrentUserDepRespman();
        if (!ConstantUtil.SUCCESS.equals(currentUserDepInfo.getString("result"))) {
            return result;
        }
        boolean isDepRespMan = currentUserDepInfo.getBoolean("isDepRespMan");
        String currentUserMainDepId = currentUserDepInfo.getString("currentUserMainDepId");
        String currentUserId = ContextUtil.getCurrentUserId();
        String currentUserMainDepName = currentUserDepInfo.getString("currentUserMainDepName");
        boolean isDepProjectManager =
            XcmgProjectUtil.judgeIsDepProjectManager(currentUserMainDepName, currentUserRoles);
        // 过滤
        for (Map<String, Object> oneApply : applyList) {
            // 自己是当前流程处理人
            if (oneApply.get("currentProcessUserId") != null
                && oneApply.get("currentProcessUserId").toString().contains(currentUserId)) {
                oneApply.put("processTask", true);
                result.add(oneApply);
            } else if (showAll) {
                // 分管领导和项目管理人员查看所有非草稿的数据或者草稿但是创建人CREATE_BY_是自己的
                if (oneApply.get("instStatus") != null && !"DRAFTED".equals(oneApply.get("instStatus").toString())) {
                    result.add(oneApply);
                } else {
                    if (oneApply.get("CREATE_BY_").toString().equals(currentUserId)) {
                        result.add(oneApply);
                    }
                }
            } else if (isDepRespMan || isDepProjectManager) {
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
            }
        }
        return result;
    }

    public JSONObject getBpmSolution(Map<String, Object> param) {
        param.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> list = monthWorkDelayApplyDao.getBmpSolution(param);
        JSONObject resultJson = new JSONObject();
        if (list != null && list.size() > 0) {
            resultJson = XcmgProjectUtil.convertMap2JsonObject(list.get(0));
        }
        return resultJson;
    }

    public JSONObject getAbolishInfo(JSONObject postDataJson, HttpServletResponse response, HttpServletRequest request)
        throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("taskId_", postDataJson.getString("taskId_"));
        List<Map<String, String>> list = monthWorkDelayApplyDao.queryAbolishInfo(params);
        JSONObject resultJson = new JSONObject();
        if (list != null && list.size() > 0) {
            resultJson = XcmgProjectUtil.convertMap2JsonString(list.get(0));
        }
        return resultJson;
    }

    public JSONObject getActTaskById(JSONObject paramJson){
        String projectId = paramJson.getString("projectId");
        String type = paramJson.getString("type");
        List<JSONObject> list = new ArrayList<>();
        JSONObject resultJson;
        boolean flag = true;
        if("0".equals(type)){
            list = monthWorkDelayApplyDao.getProjectApplyInfo(projectId);
        }else if("1".equals(type)){
            list = monthWorkDelayApplyDao.getUnProjectApplyInfo(projectId);
        }
        if(list!=null&&list.size()==0){
            flag = true;
        }else{
            for(JSONObject temp : list){
                String applyId = temp.getString("id");
                JSONObject jsonObject = monthWorkDelayApplyDao.getActTaskById(applyId);
                if("RUNNING".equals(jsonObject.getString("processStatus"))&&!"调度员发起审批".equals(jsonObject.getString("actName"))){
                    flag = false;
                    break;
                }
            }
        }
        if(flag){
            resultJson =  ResultUtil.result(true,"允许编辑！",null);
        }else{
            resultJson =  ResultUtil.result(false,"流程审批中，不允许编辑！",null);
        }
      return resultJson;
    }
}
