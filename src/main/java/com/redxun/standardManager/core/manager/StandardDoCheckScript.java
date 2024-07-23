package com.redxun.standardManager.core.manager;

import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.standardManager.core.dao.StandardDoCheckDao;

@Service
public class StandardDoCheckScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(StandardDoCheckScript.class);
    @Autowired
    private StandardDoCheckDao standardDoCheckDao;
    @Autowired
    private CommonInfoDao commonInfoDao;
    @Autowired
    private CommonBpmManager commonBpmManager;

    // 标准第一起草人
    public Collection<TaskExecutor> standardFirstWriter() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formJson = JSONObject.parseObject(formData);
        String firstWriterId = formJson.getString("firstWriterId");
        String firstWriterName = formJson.getString("firstWriterName");

        users.add(new TaskExecutor(firstWriterId, firstWriterName));
        return users;
    }

    // 标准第一起草人室主任
    public Collection<TaskExecutor> standardFirstWriterSzr() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formJson = JSONObject.parseObject(formData);
        String szrUserId = formJson.getString("szrUserId");
        String szrUserName = formJson.getString("szrUserName");

        users.add(new TaskExecutor(szrUserId, szrUserName));
        return users;
    }

    // 标准化对接人
    public Collection<TaskExecutor> standardDjrUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formJson = JSONObject.parseObject(formData);
        String djrUserId = formJson.getString("djrUserId");
        String djrUserName = formJson.getString("djrUserName");

        users.add(new TaskExecutor(djrUserId, djrUserName));
        return users;
    }

    // 不符合项责任人
    public Collection<TaskExecutor> inconformityResp() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formJson = JSONObject.parseObject(formData);

        Map<String, String> userId2Name = inconformityRespUser(formJson);
        for (Map.Entry<String, String> oneEntry : userId2Name.entrySet()) {
            users.add(new TaskExecutor(oneEntry.getKey(), oneEntry.getValue()));
        }

        return users;
    }

    // 不符合项责任人的部门负责人
    public Collection<TaskExecutor> inconformityRespDeptLeader() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formJson = JSONObject.parseObject(formData);
        Map<String, String> userId2Name = inconformityRespUser(formJson);
        // 根据人员查询所在部门的负责人信息
        toGetRespDeptLeaderByUserIds(userId2Name.keySet(), users);
        return users;
    }

    // 遍历得到不符合项责任人
    private Map<String, String> inconformityRespUser(JSONObject formJson) {
        Map<String, String> result = new HashMap<>();
        String baseInfoId = formJson.getString("id");
        JSONObject params = new JSONObject();
        params.put("baseInfoId", baseInfoId);
        List<JSONObject> checkDetails = standardDoCheckDao.queryCheckDetailList(params);
        if (checkDetails != null && !checkDetails.isEmpty()) {
            for (int index = 0; index < checkDetails.size(); index++) {
                JSONObject oneData = checkDetails.get(index);
                String judge = oneData.getString("judge");
                if (StringUtils.isNotBlank(judge) && "否".equalsIgnoreCase(judge)
                    && StringUtils.isNotBlank(oneData.getString("respUserId"))) {
                    result.put(oneData.getString("respUserId"), oneData.getString("respUserName"));
                }
            }
        }
        return result;
    }

    // 有不符合项
    public boolean haveInconformity(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String checkResult = formDataJson.getString("checkResult");
        if (StringUtils.isNotBlank(checkResult) && "未完全按标准执行".equalsIgnoreCase(checkResult)) {
            return true;
        }
        return false;
    }

    // 没有不符合项
    public boolean notHaveInconformity(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String checkResult = formDataJson.getString("checkResult");
        if (StringUtils.isBlank(checkResult)
            || (StringUtils.isNotBlank(checkResult) && "完全按标准执行".equalsIgnoreCase(checkResult))) {
            return true;
        }
        return false;
    }

    // 任务创建事件（在此处需要进行节点变量stageState获取用来刷新当前阶段）
    public void taskCreateScript(Map<String, Object> vars) {
        logger.info("taskCreateScript");
        String busKey = vars.get("busKey").toString();
        AbstractExecutionCmd cmd = (AbstractExecutionCmd)vars.get("cmd");
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String zcStatus = formDataJson.getString("zcStatus");
        // 查找该节点上的所有变量
        String solId = vars.get("solId").toString();
        String nodeId = vars.get("activityId").toString();
        List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, null, solId);
        if (nodeVars == null || nodeVars.isEmpty()) {
            logger.warn("no vars in node {}", nodeId);
            return;
        }
        // 查找变量stageState
        String stageState = "";
        for (Map<String, String> oneVar : nodeVars) {
            if ("stageState".equals(oneVar.get("KEY_"))) {
                stageState = oneVar.get("DEF_VAL_");
                break;
            }
        }
        if (StringUtils.isBlank(stageState)) {
            logger.info("stageState in node {} is blank", nodeId);
            return;
        }
        // 根据stageState是否变化进行状态字段的更新
        if (stageState.equals(zcStatus)) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("id", busKey);
        params.put("zcStatus", stageState);
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        standardDoCheckDao.updateDoCheckStatus(params);

    }

    // 标准化对接人判定整改计划不符合的责任人
    public Collection<TaskExecutor> planNotOkResp() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formJson = JSONObject.parseObject(formData);
        JSONArray checkDetailList = formJson.getJSONArray("SUB_checkGrid");
        if (checkDetailList != null && !checkDetailList.isEmpty()) {
            for (int index = 0; index < checkDetailList.size(); index++) {
                String confirmPlan = checkDetailList.getJSONObject(index).getString("confirmPlan");
                if (StringUtils.isNotBlank(confirmPlan) && "不符合".equalsIgnoreCase(confirmPlan)
                    && StringUtils.isNotBlank(checkDetailList.getJSONObject(index).getString("respUserId"))) {
                    users.add(new TaskExecutor(checkDetailList.getJSONObject(index).getString("respUserId"),
                        checkDetailList.getJSONObject(index).getString("respUserName")));
                }
            }
        }
        return users;
    }

    // 标准化对接人判定整改计划不符合的责任人的部门负责人
    public Collection<TaskExecutor> planNotOKRespDeptLeader() {
        List<TaskExecutor> result = new ArrayList<>();
        Set<String> respUserIds = new HashSet<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        List<JSONObject> checkDetailList = toGetCheckDetailsByFormData(formData);
        if (checkDetailList != null && !checkDetailList.isEmpty()) {
            for (int index = 0; index < checkDetailList.size(); index++) {
                String confirmPlan = checkDetailList.get(index).getString("confirmPlan");
                if (StringUtils.isNotBlank(confirmPlan) && "不符合".equalsIgnoreCase(confirmPlan)
                        && StringUtils.isNotBlank(checkDetailList.get(index).getString("respUserId"))) {
                    respUserIds.add(checkDetailList.get(index).getString("respUserId"));
                }
            }
        }
        // 根据人员查询所在部门的负责人信息
        toGetRespDeptLeaderByUserIds(respUserIds, result);
        return result;
    }

    // 问题关闭责任人
    public Collection<TaskExecutor> closeResp() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        List<JSONObject> checkDetailList = toGetCheckDetailsByFormData(formData);
        if (checkDetailList != null && !checkDetailList.isEmpty()) {
            for (JSONObject oneDetail : checkDetailList) {
                String judge = oneDetail.getString("judge");
                if (StringUtils.isNotBlank(judge) && "否".equalsIgnoreCase(judge)
                    && StringUtils.isNotBlank(oneDetail.getString("closeRespUserId"))) {
                    users.add(new TaskExecutor(oneDetail.getString("closeRespUserId"),
                        oneDetail.getString("closeRespUserName")));
                }
            }
        }
        return users;
    }

    // 问题关闭责任人的部门负责人
    public Collection<TaskExecutor> closeRespDeptLeader() {
        List<TaskExecutor> result = new ArrayList<>();
        List<TaskExecutor> respUsers = (List<TaskExecutor>)closeResp();
        Set<String> respUserIds = new HashSet<>();
        for (TaskExecutor oneResp : respUsers) {
            respUserIds.add(oneResp.getId());
        }
        // 根据人员查询所在部门的负责人信息
        toGetRespDeptLeaderByUserIds(respUserIds, result);
        return result;
    }

    // 标准化对接人判定整改结果“待整改”的问题关闭人
    public Collection<TaskExecutor> resultNotOkResp() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formJson = JSONObject.parseObject(formData);
        JSONArray checkDetailList = formJson.getJSONArray("SUB_checkGrid");
        if (checkDetailList != null && !checkDetailList.isEmpty()) {
            for (int index = 0; index < checkDetailList.size(); index++) {
                String confirmResult = checkDetailList.getJSONObject(index).getString("confirmResult");
                if (StringUtils.isNotBlank(confirmResult) && "待整改".equalsIgnoreCase(confirmResult)
                    && StringUtils.isNotBlank(checkDetailList.getJSONObject(index).getString("closeRespUserId"))) {
                    users.add(new TaskExecutor(checkDetailList.getJSONObject(index).getString("closeRespUserId"),
                            checkDetailList.getJSONObject(index).getString("closeRespUserName")));
                }
            }
        }
        return users;
    }

    // 标准化对接人判定整改结果“待整改”的责任人的部门负责人
    public Collection<TaskExecutor> resultNotOkRespDeptLeader() {
        List<TaskExecutor> result = new ArrayList<>();
        Set<String> closeUserIds = new HashSet<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        List<JSONObject> checkDetailList = toGetCheckDetailsByFormData(formData);
        if (checkDetailList != null && !checkDetailList.isEmpty()) {
            for (int index = 0; index < checkDetailList.size(); index++) {
                String confirmResult = checkDetailList.get(index).getString("confirmResult");
                if (StringUtils.isNotBlank(confirmResult) && "待整改".equalsIgnoreCase(confirmResult)
                        && StringUtils.isNotBlank(checkDetailList.get(index).getString("closeRespUserId"))) {
                    closeUserIds.add(checkDetailList.get(index).getString("closeRespUserId"));
                }
            }
        }
        // 根据人员查询所在部门的负责人信息
        toGetRespDeptLeaderByUserIds(closeUserIds, result);
        return result;
    }

    // 计划确认，有不符合项
    public boolean planConfirmNotOk(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formJson = JSONObject.parseObject(formData);
        JSONArray checkDetailList = formJson.getJSONArray("SUB_checkGrid");
        if (checkDetailList != null && !checkDetailList.isEmpty()) {
            for (int index = 0; index < checkDetailList.size(); index++) {
                String confirmPlan = checkDetailList.getJSONObject(index).getString("confirmPlan");
                if (StringUtils.isNotBlank(confirmPlan) && "不符合".equalsIgnoreCase(confirmPlan)) {
                    return true;
                }
            }
        }
        return false;
    }

    // 计划确认，没有不符合项
    public boolean planConfirmOk(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        return !planConfirmNotOk(cmd, vars);
    }

    // 结果确认，有"待整改"项
    public boolean resultConfirmNeedModify(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formJson = JSONObject.parseObject(formData);
        JSONArray checkDetailList = formJson.getJSONArray("SUB_checkGrid");
        if (checkDetailList != null && !checkDetailList.isEmpty()) {
            for (int index = 0; index < checkDetailList.size(); index++) {
                String confirmResult = checkDetailList.getJSONObject(index).getString("confirmResult");
                if (StringUtils.isNotBlank(confirmResult) && "待整改".equalsIgnoreCase(confirmResult)) {
                    return true;
                }
            }
        }
        return false;
    }

    // 结果确认，没有"待整改"项
    public boolean resultConfirmNotNeedModify(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        return !resultConfirmNeedModify(cmd, vars);
    }

    /**
     * 根据表单id,查询返回自查明细
     * 
     * @param formData
     * @return
     */
    private List<JSONObject> toGetCheckDetailsByFormData(String formData) {
        JSONObject formJson = JSONObject.parseObject(formData);
        String baseInfoId = formJson.getString("id");
        JSONObject params = new JSONObject();
        params.put("baseInfoId", baseInfoId);
        List<JSONObject> checkDetails = standardDoCheckDao.queryCheckDetailList(params);
        return checkDetails;
    }

    private void toGetRespDeptLeaderByUserIds(Set<String> userIds, List<TaskExecutor> users) {
        // 根据人员查询所在部门的负责人信息
        if (!userIds.isEmpty()) {
            JSONObject param = new JSONObject();
            param.put("userIds", userIds);
            List<JSONObject> deptRespList = commonInfoDao.queryDeptRespByUserIds(param);
            if (deptRespList != null && !deptRespList.isEmpty()) {
                for (JSONObject oneData : deptRespList) {
                    users.add(new TaskExecutor(oneData.getString("USER_ID_"), oneData.getString("FULLNAME_")));
                }
            }
        }
    }
}
