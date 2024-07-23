package com.redxun.bjjszc.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bjjszc.core.dao.BjqdDao;
import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BjqdHandler
    implements ProcessStartPreHandler, ProcessStartAfterHandler, TaskPreHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(BjqdHandler.class);
    @Autowired
    private BjqdService bjqdService;

    // 整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        String id = createOrUpdateBjqdByFormData(processStartCmd);
        if (StringUtils.isNotBlank(id)) {
            processStartCmd.setBusinessKey(id);
        }
    }

    // 第一个任务创建之后流程的后置处理器
    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd processStartCmd, BpmInst bpmInst) {
        return null;
    }

    // 任务审批之后的前置处理器
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {}

    // 流程成功结束之后的处理
    @Override
    public void endHandle(BpmInst bpmInst) {}

    // 驳回场景cmd中没有表单数据
    private String createOrUpdateBjqdByFormData(AbstractExecutionCmd cmd) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (StringUtils.isBlank(formDataJson.getString("id"))) {
            bjqdService.createBjqd(formDataJson);
        }
        return formDataJson.getString("id");
    }
}
