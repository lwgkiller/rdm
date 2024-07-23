package com.redxun.environment.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.environment.core.dao.ZjhmDao;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ZjhmHandler
        implements ProcessStartPreHandler, ProcessStartAfterHandler, TaskPreHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(ZjhmHandler.class);

    @Resource
    private ZjhmService zjhmService;

    // 整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd cmd) {
        String projectId = createOrUpdateZjhmByFormData(cmd);
        if (StringUtils.isNotBlank(projectId)) {
            cmd.setBusinessKey(projectId);
        }
    }
    // 第一个任务创建之后流程的后置处理器
    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd cmd, BpmInst bpmInst) {
        logger.info("processStartAfterHandle");
        return null;
    }
    // 任务审批之后的前置处理器
    @Override
    public void taskPreHandle(IExecutionCmd cmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd)cmd;
        createOrUpdateZjhmByFormData(processNextCmd);
    }

    // 流程成功结束之后的处理
    @Override
    public void endHandle(BpmInst bpmInst) {
        logger.info("Process EndHandler");
    }

    //流程保存时的新增或修改
    private String createOrUpdateZjhmByFormData(AbstractExecutionCmd cmd) {
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
        if (StringUtils.isBlank(formDataJson.getString("projectId"))) {
            zjhmService.createZjhm(formDataJson);
        } else {
            zjhmService.updateZjhm(formDataJson);
        }
        return formDataJson.getString("projectId");
    }
}
