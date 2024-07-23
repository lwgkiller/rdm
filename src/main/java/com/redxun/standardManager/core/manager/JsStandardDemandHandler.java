package com.redxun.standardManager.core.manager;

import com.redxun.bpm.activiti.handler.TaskAfterHandler;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.ProcessNextCmd;
import com.redxun.bpm.core.entity.ProcessStartCmd;

@Service
public class JsStandardDemandHandler implements ProcessStartPreHandler, TaskPreHandler, TaskAfterHandler {
    private Logger logger = LoggerFactory.getLogger(JsStandardDemandHandler.class);
    @Autowired
    private StandardDemandManager standardDemandManager;

    // 整个流程启动之前的处理，草稿也会调用这里
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        logger.info("JsStandardDemandHandler processStartPreHandle");
        JSONObject standardDemandObj = addOrUpdateStandardApply(processStartCmd);
        if (standardDemandObj != null) {
            processStartCmd.setBusinessKey(standardDemandObj.getString("applyId"));
        }
    }

    // 任务审批之后的前置处理器
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        logger.info("taskPreHandle");
        ProcessNextCmd processNextCmd = (ProcessNextCmd)iExecutionCmd;
        addOrUpdateStandardApply(processNextCmd);
    }


    // 驳回场景cmd中没有表单数据
    private JSONObject addOrUpdateStandardApply(AbstractExecutionCmd cmd) {
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
        if (StringUtils.isBlank(formDataJson.getString("applyId"))) {
            standardDemandManager.createStandardApply(formDataJson);
        } else {
            standardDemandManager.updateStandardApply(formDataJson);
        }
        return formDataJson;
    }

    @Override
    public void taskAfterHandle(IExecutionCmd cmd, String nodeId, String busKey) {
        logger.info("taskAfterHandle");
        ProcessNextCmd processNextCmd = (ProcessNextCmd)cmd;
        addOrUpdateStandardApply(processNextCmd);
    }
}
