package com.redxun.serviceEngineering.core.service;

import java.util.*;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.bpm.core.manager.BpmTaskManager;
import com.redxun.core.script.GroovyScript;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.serviceEngineering.core.dao.AttachedDocTranslateDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Service
public class AttachedDocTranslateScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(AttachedDocTranslateScript.class);
    @Autowired
    AttachedDocTranslateManager attachedDocTranslateManager;

    @Autowired
    private AttachedDocTranslateDao attachedDocTranslateDao;

    @Autowired
    private BpmInstManager bpmInstManager;

    @Autowired
    private BpmTaskManager bpmTaskManager;
    @Autowired
    private CommonInfoManager commonInfoManager;

    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;

    @Resource
    private CommonInfoDao commonInfoDao;

    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;

    // 获取流程中使用的处理人为部门负责人的信息
    public Collection<TaskExecutor> getDepRespUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String applyUserId = formDataJson.getString("applyId");
        if (StringUtils.isBlank(applyUserId)) {
            applyUserId = ContextUtil.getCurrentUserId();
        }
        List<JSONObject> deptResps = commonInfoDao.queryDeptRespByUserId(applyUserId);
        if (deptResps != null && !deptResps.isEmpty()) {
            for (JSONObject depRespMan : deptResps) {
                users.add(new TaskExecutor(depRespMan.getString("USER_ID_"), depRespMan.getString("FULLNAME_")));
            }
        }
        return users;
    }

    // 获取服务工程部部门领导信息
    public Collection<TaskExecutor> getFwgcRepUser() {
        List<TaskExecutor> users = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("groupName", RdmConst.FWGCS_NAME);
        List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepRespManById(params);
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }

    // 获取到翻译组审核的翻译人员
    public Collection<TaskExecutor> getFyzRepUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String translatorId = formDataJson.getString("translatorId");
        String translator = formDataJson.getString("translator");
        if (translatorId != null && !translatorId.isEmpty()) {
            if (translatorId.split(",").length > 1) {
                String[] translatorIds = translatorId.split(",");
                String[] translators = translator.split(",");
                for (int i = 0; i < translatorIds.length; i++) {
                    users.add(new TaskExecutor(translatorIds[i], translators[i]));
                }
            } else {
                users.add(new TaskExecutor(formDataJson.getString("translatorId"), formDataJson.getString("translator")));
            }
        }
        return users;
    }

    // ..编制：以任何形式进入
    // ..服务工程校对：以任何形式进入
    // ..产品所长审核：以任何形式进入
    // ..服务工程所长审核:以任何形式进入
    // ..分管领导审批：以任何形式进入
    // ..执行中：以任何形式进入
    public void taskCreateScript(Map<String, Object> vars) {
        try {
            // 下面这个cmd可以取代getDetail，而且传来的是表单的最新数据
            JSONObject jsonObject1 = ProcessHandleHelper.getProcessCmd().getJsonDataObject();
            JSONObject jsonObject = attachedDocTranslateManager.getDetail(vars.get("busKey").toString());
            String activitiId = vars.get("activityId").toString();
            jsonObject.put("businessStatus", activitiId);
            attachedDocTranslateManager.updateBusiness(jsonObject);
            // todo:重点测测！！！如果进入的是外发翻译执行中状态，驱动需求流程继续，根据demandInstid找taskId，将taskId给往下办了
            if (activitiId.equalsIgnoreCase("G-out")) {
                // String demandInstid = jsonObject.getString("demandInstid");
                // BpmInst bpmInst = bpmInstManager.get(demandInstid);
                // List<BpmTask> bpmTaskList = bpmTaskManager.getByInstId(bpmInst.getInstId());
                // for (BpmTask bpmTask : bpmTaskList) {
                // ProcessNextCmd processNextCmd = new ProcessNextCmd();
                // processNextCmd.setTaskId(bpmTask.getId());
                // processNextCmd.setJumpType("AGREE");
                // JSONObject manualDemand =
                // maintenanceManualDemandDao.queryDetailById(jsonObject.getString("demandId"));
                // processNextCmd.setJsonData(manualDemand.toJSONString());
                // processNextCmd.setAgentToUserId("1");
                // bpmTaskManager.doNext(processNextCmd);
                // }
            }
        } catch (Exception e) {
            logger.error("Exception in F-executing taskCreateScript", e);
        }
    }

    // ..编制：以通过的形式结束,applyUser,applyDep自动生成
    // ..执行中：以通过的形式结束
    public void taskEndScript(Map<String, Object> vars) {
        try {
            // @lwgkiller:阿诺多罗 下面这个cmd可以取代getDetail，而且传来的是表单的最新数据
            // JSONObject jsonObject1 = ProcessHandleHelper.getProcessCmd().getJsonDataObject();
            JSONObject jsonObject = attachedDocTranslateManager.getDetail(vars.get("busKey").toString());
            String activitiId = vars.get("activityId").toString();
            jsonObject.put("businessStatus", activitiId);
            if (activitiId.equalsIgnoreCase("A-editing")) {
                jsonObject.put("applyUserId", ContextUtil.getCurrentUserId());
                jsonObject.put("creatorName", ContextUtil.getCurrentUser().getFullname());
                jsonObject.put("applyDepId", ContextUtil.getCurrentUser().getMainGroupId());
                jsonObject.put("applyDep", ContextUtil.getCurrentUser().getMainGroupName());
                attachedDocTranslateManager.updateBusiness(jsonObject);
            } else if (activitiId.equalsIgnoreCase("B-reviewing")) {
                // 记录翻译组人姓名
                jsonObject.put("translatorId", ContextUtil.getCurrentUserId());
                jsonObject.put("translator", ContextUtil.getCurrentUser().getFullname());
                attachedDocTranslateManager.updateBusiness(jsonObject);
            }

        } catch (Exception e) {
            logger.error("Exception in F-executing taskEndScript", e);
        }
    }

    // ..
    public void sendDingDing(JSONObject jsonObject) {
        JSONObject noticeObj = new JSONObject();
        StringBuilder stringBuilder = new StringBuilder("随机文件翻译申请流程通知:");
        stringBuilder.append(jsonObject.getString("transApplyId")).append("申请人[")
                .append(jsonObject.getString("creatorName")).append("]");
        stringBuilder.append(XcmgProjectUtil.getNowLocalDateStr(DateUtil.DATE_FORMAT_FULL));
        noticeObj.put("content", stringBuilder.toString());
        sendDDNoticeManager.sendNoticeForCommon(jsonObject.getString("receiverIds"), noticeObj);
    }

    // 服务所人员提交的，直接分管领导批准
    public boolean needFgld(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        if (StringUtils.isBlank(formData)) {
            logger.warn("formData is blank");
            return false;
        }
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return false;
        }
        if (StringUtils.isNotBlank(formDataJson.getString("applyDep"))) {
            String applyDep = formDataJson.getString("applyDep");
            if (RdmConst.FWGCS_NAME.equals(applyDep))
                return true;
        }
        return false;
    }

    // 非服务所提交，需要承办部门长审核
    public boolean needFwgcs(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        if (StringUtils.isBlank(formData)) {
            logger.warn("formData is blank");
            return false;
        }
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return false;
        }
        if (StringUtils.isNotBlank(formDataJson.getString("applyDep"))) {
            String applyDep = formDataJson.getString("applyDep");
            if (!RdmConst.FWGCS_NAME.equals(applyDep))
                return true;
        }
        return false;
    }
}
