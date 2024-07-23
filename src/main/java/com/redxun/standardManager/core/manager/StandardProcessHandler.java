package com.redxun.standardManager.core.manager;

import java.util.Map;

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
public class StandardProcessHandler implements ProcessStartPreHandler, TaskPreHandler {
    private Logger logger = LoggerFactory.getLogger(StandardProcessHandler.class);
    @Autowired
    private StandardApplyManager standardApplyManager;

    // 整个流程启动之前的处理，草稿也会调用这里
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        logger.info("StandardProcessHandler processStartPreHandle");
        Map<String, Object> standardApply = addOrUpdateStandardApply(processStartCmd);
        if (standardApply != null) {
            processStartCmd.setBusinessKey(standardApply.get("applyId").toString());
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
    private Map<String, Object> addOrUpdateStandardApply(AbstractExecutionCmd cmd) {
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
        if (applyObj.get("applyId") == null || StringUtils.isBlank(applyObj.get("applyId").toString())) {
            standardApplyManager.createStandardApply(applyObj);
        } else {
            standardApplyManager.updateStandardApply(applyObj);
        }
        return applyObj;
    }
}
