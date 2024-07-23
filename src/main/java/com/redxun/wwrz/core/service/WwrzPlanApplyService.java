package com.redxun.wwrz.core.service;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.BpmSolution;
import com.redxun.bpm.core.entity.ProcessMessage;
import com.redxun.bpm.core.entity.ProcessStartCmd;
import com.redxun.bpm.core.manager.BpmSolutionManager;
import com.redxun.core.util.ExceptionUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.org.api.service.UserService;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.wwrz.core.dao.WwrzPlanApplyDao;
import com.redxun.wwrz.core.dao.WwrzTestPlanDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.query.Page;
import com.redxun.core.util.DateUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
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
public class WwrzPlanApplyService {
    private static Logger logger = LoggerFactory.getLogger(WwrzPlanApplyService.class);
    @Resource
    private BpmInstManager bpmInstManager;
    @Resource
    private WwrzPlanApplyDao wwrzPlanApplyDao;
    @Resource
    private XcmgProjectManager xcmgProjectManager;
    @Resource
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Resource
    private WwrzTestPlanDao wwrzTestPlanDao;
    @Resource
    private CommonInfoManager commonInfoManager;
    @Resource
    private BpmSolutionManager bpmSolutionManager;
    @Resource
    private UserService userService;

    public void add(Map<String, Object> params) {
        String preFix = "SP-";
        String id = preFix + XcmgProjectUtil.getNowLocalDateStr("yyyyMMddHHmmssSSS") + (int)(Math.random() * 100);
        params.put("id", id);
        params.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        params.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        params.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        params.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        String quarter = CommonFuns.monthToQuarter(month+1);
        String deptName = CommonFuns.nullToString(params.get("deptName"));
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(deptName).append(year).append("年").append(quarter).append("季度").append("整机委外认证计划");
        params.put("applyName", stringBuffer.toString());
        wwrzPlanApplyDao.add(params);
        String planIds = CommonFuns.nullToString(params.get("planIds"));
        String[] planIdArray = planIds.split(",");
        JSONObject paramJson = new JSONObject();
        paramJson.put("planStatus","spz");
        for(String planId:planIdArray){
            paramJson.put("planId",planId);
            wwrzTestPlanDao.updatePlanStatus(paramJson);
        }
    }

    public void update(Map<String, Object> params) {
        params.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        params.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        wwrzPlanApplyDao.update(params);
    }

    /**
     * 删除表单及关联的所有信息
     */
    public JsonResult delete(String[] applyIdArr, String[] instIdArr) {
        if (applyIdArr.length != instIdArr.length) {
            return new JsonResult(false, "表单和流程个数不相同！");
        }
        Map<String, Object> param = new HashMap<>();
        for (int i = 0; i < applyIdArr.length; i++) {
            String applyId = applyIdArr[i];
            param.put("applyId", applyId);
            JSONObject applyObj = wwrzPlanApplyDao.getJsonObject(applyId);
            dealPlan(applyObj.getString("planIds"));
            wwrzPlanApplyDao.delete(applyId);
            // 删除实例
            bpmInstManager.deleteCascade(instIdArr[i], "");

        }
        return new JsonResult(true, "成功删除!");
    }
    public void dealPlan(String planIds){
        if(StringUtil.isNotEmpty(planIds)){
            String[] planIdArray = planIds.split(",");
            for(String planId:planIdArray){
                JSONObject paramJson = new JSONObject();
                paramJson.put("planStatus","dsp");
                paramJson.put("planId",planId);
                wwrzTestPlanDao.updatePlanStatus(paramJson);
            }
        }

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
            List<Map<String, Object>> applyList = wwrzPlanApplyDao.queryList(params);
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
        boolean showAll = false;
        // 管理员查看所有的包括草稿数据
        if (ConstantUtil.ADMIN.equals(ContextUtil.getCurrentUser().getUserNo())) {
            showAll = true;
        }
        // 分管领导的查看权限等同于管理人员
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
            }
        }
        JSONObject adminJson = commonInfoManager.hasPermission("WWRZ-GLY");
        if(adminJson.getBoolean("WWRZ-GLY")){
            showAll = true;
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
    /**
     * 自动创建审批流程
     * */
    public JSONObject genFlowData(){
        try {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            String quarter = CommonFuns.monthToQuarter(month+1);
            JSONObject paramJson = new JSONObject();
            paramJson.put("planStatus","dsp");
            List<JSONObject> planList = wwrzTestPlanDao.getPlanListByStatus(paramJson);
            String deptName;
            for(JSONObject planObj:planList){
                deptName = planObj.getString("deptName");
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(deptName).append(year).append("年").append(quarter).append("季度").append("整机委外认证计划");
                planObj.put("applyName", stringBuffer.toString());
                autoCreateFlow(planObj);
            }

        }catch (Exception e){
            logger.error("Exception in genFlowData", e);
            ResultUtil.result(false, "创建失败", null);
        }
        return ResultUtil.result(true, "创建成功", null);
    }

    /**
     * 自动创建审批流程
     * */
    public void autoCreateFlow(JSONObject postJSON){
        try {
            String flowType = "WwrzPlanApply";
            BpmSolution bpmSolution = bpmSolutionManager.getByKey(flowType,"1");
            IUser user = userService.getByUserId("1");
            startProcess(bpmSolution.getSolId(),postJSON,user);
        }catch (Exception e){
            logger.error("Exception in autoCreateFlow", e);
        }
    }
    private JsonResult startProcess(String solId, JSONObject contentJson, IUser user) throws Exception {
        // 加上处理的消息提示
        ProcessMessage handleMessage = new ProcessMessage();
        JsonResult result = new JsonResult();
        try {
            ContextUtil.setCurUser(user);
            ProcessHandleHelper.setProcessMessage(handleMessage);
            ProcessStartCmd startCmd = new ProcessStartCmd();
            startCmd.setSolId(solId);
            startCmd.setJsonData(contentJson.toJSONString());
            // 启动流程
            bpmInstManager.doStartProcess(startCmd);
            result.setData(startCmd.getBusinessKey());
        } catch (Exception ex) {
            // 把具体的错误放置在内部处理，以显示正确的错误信息提示，在此不作任何的错误处理
            logger.error(ExceptionUtil.getExceptionMessage(ex));
            if (handleMessage.getErrorMsges().size() == 0) {
                handleMessage.addErrorMsg(ex.getMessage());
            }
        } finally {
            // 在处理过程中，是否有错误的消息抛出
            if (handleMessage.getErrorMsges().size() > 0) {
                result.setSuccess(false);
                result.setMessage("启动流程失败!");
                result.setData(handleMessage.getErrors());
            } else {
                result.setSuccess(true);
                result.setMessage("成功启动流程！");
            }
            ProcessHandleHelper.clearProcessCmd();
            ProcessHandleHelper.clearProcessMessage();
        }
        return result;
    }

}
