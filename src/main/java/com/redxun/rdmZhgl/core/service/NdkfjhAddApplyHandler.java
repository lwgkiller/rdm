package com.redxun.rdmZhgl.core.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import com.redxun.rdmZhgl.core.dao.NdkfjhAddApplyDao;
import com.redxun.rdmZhgl.core.dao.NdkfjhPlanDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.rdmZhgl.core.dao.NdkfjhPlanDetailDao;

/**
 * @author zz
 */
@Service
public class NdkfjhAddApplyHandler
    implements ProcessStartPreHandler, TaskPreHandler, ProcessStartAfterHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(NdkfjhAddApplyHandler.class);
    @Resource
    private NdkfjhAddApplyDao ndkfjhAddApplyDao;
    @Resource
    private NdkfjhAddApplyService ndkfjhAddApplyService;
    @Resource
    private NdkfjhPlanDetailDao ndkfjhPlanDetailDao;
    @Resource
    private NdkfjhPlanDao ndkfjhPlanDao;

    /**
     * 整个流程启动之前的处理，草稿也会调用这里
     */
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        logger.info("ProcessApplyHandler processStartPreHandle");
        Map<String, Object> changeApply = addOrUpdateApplyInfo(processStartCmd);
        if (changeApply != null) {
            processStartCmd.setBusinessKey(changeApply.get("id").toString());
        }
    }

    /**
     * 任务审批之后的前置处理器id
     */
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        logger.info("taskPreHandle");
        ProcessNextCmd processNextCmd = (ProcessNextCmd)iExecutionCmd;
        addOrUpdateApplyInfo(processNextCmd);
    }

    /**
     * 驳回场景cmd中没有表单数据
     */
    private Map<String, Object> addOrUpdateApplyInfo(AbstractExecutionCmd cmd) {
        String formData = cmd.getJsonData();
        if (StringUtils.isBlank(formData)) {
            logger.warn("formData is blank");
            return null;
        }
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return null;
        }
        Map<String, Object> applyObj = JSONObject.parseObject(formDataJson.toJSONString(), Map.class);
        if (applyObj.get("id") == null || StringUtils.isBlank(applyObj.get("id").toString())) {
            ndkfjhAddApplyService.add(applyObj);
        } else {
            ndkfjhAddApplyService.update(applyObj);
        }
        return applyObj;
    }

    // 流程成功结束之后的处理
    @Override
    public void endHandle(BpmInst bpmInst) {
        if ("SUCCESS_END".equalsIgnoreCase(bpmInst.getStatus())) {
            String id = bpmInst.getBusKey();
            JSONObject detailObj = ndkfjhAddApplyDao.getJsonObject(id);
            // 年度计划主表中插入数据
            String planId = IdUtil.getId();
            addPlanInfo(detailObj, planId);
            // 插入计划详情数据
            addPlanDetailInfo(detailObj, planId);

        }
    }

    public void addPlanInfo(JSONObject detailObj, String planId) {
        try {
            JSONObject planObj = new JSONObject();
            planObj.put("id", planId);
            planObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            planObj.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            planObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            planObj.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            planObj.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            String year = detailObj.getString("yearMonth").substring(0, 4);
            planObj.put("planYear", year);
            planObj.put("deptId", detailObj.get("chargerDept"));
            Integer maxIndex = ndkfjhPlanDao.getMaxIndex(year);
            String newIndex = "";
            if (maxIndex == null) {
                newIndex = "0";
            } else {
                newIndex = String.valueOf(maxIndex);
            }
            String newCode = CommonFuns.genProjectCode(newIndex, 3);
            String planCode = year + newCode;
            planObj.put("planCode", planCode);
            planObj.put("isBudget", "1");
            planObj.put("sortIndex", Integer.parseInt(newCode));
            ndkfjhPlanDao.addObject(planObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addPlanDetailInfo(JSONObject detailObj, String planId) {
        try {
            JSONObject planDetailObj = new JSONObject();
            planDetailObj.put("id", IdUtil.getId());
            planDetailObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            planDetailObj.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            planDetailObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            planDetailObj.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            planDetailObj.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            planDetailObj.put("mainId", planId);
            planDetailObj.put("productName", detailObj.get("productName"));
            planDetailObj.put("target", detailObj.get("target"));
            planDetailObj.put("planSource", "zxxz");
            planDetailObj.put("startDate", detailObj.get("startDate"));
            planDetailObj.put("endDate", detailObj.get("endDate"));
            planDetailObj.put("chargerMan", detailObj.get("chargerMan"));
            planDetailObj.put("chargerDept", detailObj.get("chargerDept"));
            planDetailObj.put("responsor", detailObj.get("responsor"));
            planDetailObj.put("currentStage", detailObj.get("currentStage"));
            planDetailObj.put("stageFinishDate", detailObj.get("stageFinishDate"));
            planDetailObj.put("finishRate", detailObj.get("finishRate"));
            planDetailObj.put("isDelay", detailObj.get("isDelay"));
            planDetailObj.put("delayDays", detailObj.get("delayDays"));
            planDetailObj.put("remark", detailObj.get("remark"));
            planDetailObj.put("yearMonth", detailObj.get("yearMonth"));
            ndkfjhPlanDetailDao.addObject(planDetailObj);
            addHistoryInfo(planDetailObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void addHistoryInfo(JSONObject planDetailObj) {
        try {
            Map<String, Object> params = new HashMap<>(16);
            params.put("id", IdUtil.getId());
            params.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            params.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            params.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            params.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            params.put("detailId", planDetailObj.get("id"));
            params.put("currentStage", planDetailObj.get("currentStage"));
            params.put("stageFinishDate", planDetailObj.get("stageFinishDate"));
            params.put("finishRate", planDetailObj.get("finishRate"));
            params.put("isDelay", planDetailObj.get("isDelay"));
            params.put("delayDays", planDetailObj.get("delayDays"));
            params.put("remark", planDetailObj.get("remark"));
            params.put("processDate", planDetailObj.get("yearMonth"));
            ndkfjhPlanDetailDao.addProcessHistory(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd cmd, BpmInst bpmInst) {
        return null;
    }
}
