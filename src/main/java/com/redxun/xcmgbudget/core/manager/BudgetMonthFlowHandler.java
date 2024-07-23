package com.redxun.xcmgbudget.core.manager;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.*;
import com.redxun.bpm.core.entity.BpmInst;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.ProcessNextCmd;
import com.redxun.bpm.core.entity.ProcessStartCmd;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhangzhen
 */
@Service
public class BudgetMonthFlowHandler
    implements ProcessStartPreHandler, ProcessStartAfterHandler, TaskPreHandler, TaskAfterHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(BudgetMonthFlowHandler.class);

    @Autowired
    private BudgetMonthFlowManager budgetMonthFlowManager;

    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        logger.info("BudgetMonthFlowHandler processStartPreHandle");
        String formData = processStartCmd.getJsonData();
        if (StringUtils.isBlank(formData)) {
            logger.warn("formData is blank");
            return;
        }
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String flowId = createOrUpdateByFormData(formDataJson);
        if (StringUtils.isNotBlank(flowId)) {
            processStartCmd.setBusinessKey(flowId);
        }
    }

    // 第一个任务创建之后流程的后置处理器
    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd processStartCmd,
        BpmInst bpmInst) {
        logger.info("processStartAfterHandle");
        return null;
    }

    /**
     * 当前任务处理之后的逻辑（更新表单信息）
     *
     * @param iExecutionCmd
     * @param task
     * @param busKey
     */
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        logger.info("taskPreHandle");
        ProcessNextCmd processNextCmd = (ProcessNextCmd)iExecutionCmd;
        String formData = processNextCmd.getJsonData();
        if (StringUtils.isBlank(formData)) {
            logger.warn("formData is blank");
            return;
        }
        JSONObject formDataJson = JSONObject.parseObject(formData);
        createOrUpdateByFormData(formDataJson);
    }

    // 任务处理之后的后置处理器
    @Override
    public void taskAfterHandle(IExecutionCmd iExecutionCmd, String nodeId, String busKey) {
        logger.info("taskAfterHandle");
    }

    @Override
    public void endHandle(BpmInst bpmInst) {
        logger.info("epm month Process EndHandler");
    }

    // 新增或者更新月度绩效表单
    public String createOrUpdateByFormData(JSONObject formDataJson) {
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return null;
        }
        if (StringUtils.isBlank(formDataJson.getString("budgetId"))) {
            budgetMonthFlowManager.createMonthFlow(formDataJson);
        } else {
            budgetMonthFlowManager.updateMonthFlow(formDataJson);
        }
        return formDataJson.getString("budgetId");
    }

}
