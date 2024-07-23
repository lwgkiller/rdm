package com.redxun.drbfm.core.service;

import java.util.*;

import com.redxun.drbfm.core.dao.DrbfmTotalDao;
import com.redxun.serviceEngineering.core.dao.MaintenanceManualfileDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.*;
import com.redxun.core.script.GroovyScript;
import com.redxun.drbfm.core.dao.DrbfmSingleDao;
import com.redxun.drbfm.core.dao.DrbfmTestTaskDao;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;

@Service
public class DrbfmScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(DrbfmScript.class);
    @Autowired
    private CommonInfoDao commonInfoDao;
    @Autowired
    private DrbfmSingleDao drbfmSingleDao;
    @Autowired
    private DrbfmTestTaskDao drbfmTestTaskDao;
    @Autowired
    private DrbfmTotalDao drbfmTotalDao;
    @Autowired
    private MaintenanceManualfileDao maintenanceManualfileDao;

    // 单一项目获取部件分析负责人
    public Collection<TaskExecutor> singleGetPartAnalyser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String formId = formDataJson.getString("id");
        if (StringUtils.isBlank(formId)) {
            if (cmd instanceof ProcessStartCmd) {
                formId = ((ProcessStartCmd)cmd).getBusinessKey();
            }
        }
        JSONObject detailObj = drbfmSingleDao.querySingleBaseById(formId);
        if (detailObj == null) {
            return users;
        }
        users.add(new TaskExecutor(detailObj.getString("analyseUserId"), detailObj.getString("analyseUserName")));
        return users;
    }

    // 单一项目获取选择的室主任
    public Collection<TaskExecutor> singleGetPartOwner() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String formId = formDataJson.getString("id");
        if (StringUtils.isBlank(formId)) {
            return users;
        }
        JSONObject detailObj = drbfmSingleDao.querySingleBaseById(formId);
        if (detailObj == null) {
            return users;
        }
        String checkUserId = detailObj.getString("checkUserId");
        if (StringUtils.isBlank(checkUserId)) {
            return users;
        }
        users.add(new TaskExecutor(detailObj.getString("checkUserId"), detailObj.getString("checkUserName")));

        String femaType = detailObj.getString("femaType");
        if (StringUtils.isBlank(femaType) || "product".equalsIgnoreCase(femaType)) {
            return users;
        } else {
            //todo 获取角色列表
            JSONObject params = new JSONObject();
            params.put("roleKey", "baseFemaAdmin");
            List<JSONObject> roleUsers = maintenanceManualfileDao.getUsersByRolekey(params);
            for (JSONObject user : roleUsers) {
                users.add(new TaskExecutor(user.getString("USER_ID_"), user.getString("FULLNAME_")));
            }
        }
        return users;
    }

    // 单一项目获取选择的挖掘机械研究院校对会签人
    public Collection<TaskExecutor> singleGetInnerCheckUsers() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String formId = formDataJson.getString("id");
        if (StringUtils.isBlank(formId)) {
            return users;
        }
        JSONObject detailObj = drbfmSingleDao.querySingleBaseById(formId);
        if (detailObj == null) {
            return users;
        }
        String innerCheckUserIds = detailObj.getString("innerCheckUserIds");
        if (StringUtils.isBlank(innerCheckUserIds)) {
            return users;
        }
        String innerCheckUserNames = detailObj.getString("innerCheckUserNames");
        List<String> userIds = Arrays.asList(innerCheckUserIds.split(",", -1));
        List<String> userNames = Arrays.asList(innerCheckUserNames.split(",", -1));
        for (int index = 0; index < userIds.size(); index++) {
            users.add(new TaskExecutor(userIds.get(index), userNames.get(index)));
        }
        return users;
    }

    // 单一项目获取选择的外部审核会签人
    public Collection<TaskExecutor> singleGetOutSHUsers() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String formId = formDataJson.getString("id");
        if (StringUtils.isBlank(formId)) {
            return users;
        }
        JSONObject detailObj = drbfmSingleDao.querySingleBaseById(formId);
        if (detailObj == null) {
            return users;
        }
        String outSHUserIds = detailObj.getString("outSHUserIds");
        if (StringUtils.isBlank(outSHUserIds)) {
            return users;
        }
        String outSHUserNames = detailObj.getString("outSHUserNames");
        List<String> userIds = Arrays.asList(outSHUserIds.split(",", -1));
        List<String> userNames = Arrays.asList(outSHUserNames.split(",", -1));
        for (int index = 0; index < userIds.size(); index++) {
            users.add(new TaskExecutor(userIds.get(index), userNames.get(index)));
        }
        return users;
    }

    // 单一项目获得部件负责人的领导
    public Collection<TaskExecutor> singleGetPartAnalyserLeader() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String formId = formDataJson.getString("id");
        if (StringUtils.isBlank(formId)) {
            return users;
        }
        JSONObject detailObj = drbfmSingleDao.querySingleBaseById(formId);
        if (detailObj == null) {
            return users;
        }
        String analyseUserId = detailObj.getString("analyseUserId");
        List<JSONObject> deptRespUsers = commonInfoDao.queryDeptRespByUserId(analyseUserId);
        for (JSONObject oneUser : deptRespUsers) {
            users.add(new TaskExecutor(oneUser.getString("USER_ID_"), oneUser.getString("FULLNAME_")));
        }
        return users;
    }

    // 不存在有效的验证任务
    public boolean noValidTestTask(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        return !hasValidTestTask(cmd, vars);
    }

    // 存在有效的验证任务
    public boolean hasValidTestTask(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String belongSingleId = formDataJson.getString("id");
        Map<String, Object> params = new HashMap<>();
        params.put("belongSingleId", belongSingleId);
        params.put("instStatuses",
            Arrays.asList(BpmInst.STATUS_DRAFTED, BpmInst.STATUS_RUNNING, BpmInst.STATUS_SUCCESS_END));
        List<Map<String, Object>> singleList = drbfmTestTaskDao.queryTestTaskList(params);
        if (singleList != null && !singleList.isEmpty()) {
            return true;
        }
        return false;
    }

    // 不需要外部门审核会签
    public boolean noNeedOutSH(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String belongSingleId = formDataJson.getString("id");
        JSONObject detailObj = drbfmSingleDao.querySingleBaseById(belongSingleId);
        if (detailObj == null) {
            return true;
        }
        String outSHUserIds = detailObj.getString("outSHUserIds");
        if (StringUtils.isBlank(outSHUserIds)) {
            return true;
        }
        return false;
    }

    // 需要外部门审核会签
    public boolean needOutSH(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        return !noNeedOutSH(cmd, vars);
    }


    // 需要技术中心内部校对会签（是否设置挖掘机械研究院内部校对会签人）
    public boolean unSetCountersign(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String formId = formDataJson.getString("id");
        JSONObject detailObj = drbfmSingleDao.querySingleBaseById(formId);
        if (detailObj == null) {
            return true;
        }
        String innerCheckUserIds = detailObj.getString("innerCheckUserIds");
        if (StringUtils.isBlank(innerCheckUserIds)) {
            return true;
        }
        return false;
    }

    // 不需要技术中心内部校对会签
    public boolean setCountersign(AbstractExecutionCmd cmd,Map<String, Object> vars) {
        return !unSetCountersign(cmd,vars);
    }

    // 更新验证人员完成时间
    public void updateTestActualEndTime(Map<String, Object> vars) {
        AbstractExecutionCmd cmd = (AbstractExecutionCmd)vars.get("cmd");
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        Map<String, Object> param = new HashMap<>();
        param.put("id", formDataJson.getString("id"));
        param.put("testActualEndTime", new Date());
        drbfmTestTaskDao.updateTestActualEndTime(param);
    }

    public Collection<TaskExecutor> singleGetDemandCollect() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        users.add(new TaskExecutor(formDataJson.getString("demandFeedBackUserId"),
            formDataJson.getString("demandFeedBackUserName")));
        return users;
    }

    // 单一项目获取产品主管信息
    public Collection<TaskExecutor> singleGetProductOwner() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String formId = formDataJson.getString("id");
        if (StringUtils.isBlank(formId)) {
            return users;
        }
        JSONObject detailObj = drbfmSingleDao.querySingleBaseById(formId);
        if (detailObj == null) {
            return users;
        }
        String productOwnerId = detailObj.getString("productOwnerId");
        if (StringUtils.isBlank(productOwnerId)) {
            return users;
        }
        String productOwnerName = detailObj.getString("productOwnerName");
        users.add(new TaskExecutor(productOwnerId, productOwnerName));
        return users;
    }

    // 将单一项目对应总项状态置成 编制/审核/验证/完成
    public void setTotalBz(Map<String, Object> vars) {
        AbstractExecutionCmd cmd = (AbstractExecutionCmd)vars.get("cmd");
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        JSONObject param = new JSONObject();
        String formId = formDataJson.getString("id");
        if (StringUtils.isBlank(formId)) {
            String structId = formDataJson.getString("structId");
            if (StringUtils.isBlank(structId)) {
                return;
            } else {
                param.put("structId", structId);
                param.put("stageStatus", "编制");
                drbfmTotalDao.updateStageStatusById(param);
            }
        } else {
            param.put("singleId", formId);
            param.put("stageStatus", "编制");
            drbfmTotalDao.updateStageStatus(param);
        }
    }

    // 将单一项目对应总项状态置成 编制/审核/验证/完成
    public void setTotalSh(Map<String, Object> vars) {
        AbstractExecutionCmd cmd = (AbstractExecutionCmd)vars.get("cmd");
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        JSONObject param = new JSONObject();
        String formId = formDataJson.getString("id");
        if (StringUtils.isBlank(formId)) {
            return;
        }
        param.put("singleId", formId);
        param.put("stageStatus", "审核");
        drbfmTotalDao.updateStageStatus(param);
    }

    public void setTotalYz(Map<String, Object> vars) {
        AbstractExecutionCmd cmd = (AbstractExecutionCmd)vars.get("cmd");
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        JSONObject param = new JSONObject();
        String formId = formDataJson.getString("id");
        if (StringUtils.isBlank(formId)) {
            return;
        }
        param.put("singleId", formId);
        param.put("stageStatus", "验证");
        drbfmTotalDao.updateStageStatus(param);
    }

    public void setTotalWc(Map<String, Object> vars) {
        AbstractExecutionCmd cmd = (AbstractExecutionCmd)vars.get("cmd");
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        JSONObject param = new JSONObject();
        String formId = formDataJson.getString("id");
        if (StringUtils.isBlank(formId)) {
            return;
        }
        param.put("singleId", formId);
        param.put("stageStatus", "完成");
        drbfmTotalDao.updateStageStatus(param);
    }

    // 判断是否是基础fema
    public boolean isBaseFemaNo(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        return !isBaseFemaYes(cmd, vars);
    }

    // 判断是否是基础fema
    public boolean isBaseFemaYes(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String belongSingleId = formDataJson.getString("id");
        // todo 查total_base的信息
        JSONObject detailObj = drbfmSingleDao.querySingleBaseById(belongSingleId);
        if (detailObj == null) {
            return false;
        }
        String femaType = detailObj.getString("femaType");

        if (StringUtils.isNotBlank(femaType) && "base".equalsIgnoreCase(femaType)) {
            return true;
        }
        return false;
    }



}
