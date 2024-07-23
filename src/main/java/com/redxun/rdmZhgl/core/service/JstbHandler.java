package com.redxun.rdmZhgl.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.rdmZhgl.core.dao.JstbDao;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JstbHandler
    implements ProcessStartPreHandler, ProcessStartAfterHandler, TaskPreHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(JstbHandler.class);
    @Autowired
    private JstbService jstbService;
    @Autowired
    private JstbDao jstbDao;

    // 整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        String jstbId = createOrUpdateJstbByFormData(processStartCmd);
        if (StringUtils.isNotBlank(jstbId)) {
            processStartCmd.setBusinessKey(jstbId);
        }
    }

    // 第一个任务创建之后流程的后置处理器（进行编号的生成和更新）
    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd processStartCmd,
        BpmInst bpmInst) {
        String jstbId = processStartCmd.getBusinessKey();
        JSONObject param = new JSONObject();
        param.put("jstbId", jstbId);
        param.put("status", "审批中");
        jstbService.updateJstb(param);
        return jstbId;
    }

    // 任务审批之后的前置处理器
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd)iExecutionCmd;
        createOrUpdateJstbByFormData(processNextCmd);
    }

    // 流程成功结束之后的处理
    @Override
    public void endHandle(BpmInst bpmInst) {
        if ("SUCCESS_END".equalsIgnoreCase(bpmInst.getStatus())) {
            String jstbId = bpmInst.getBusKey();
            JSONObject param = new JSONObject();
            param.put("jstbId", jstbId);
            param.put("status", "发布");
            jstbService.updateJstb(param);
        }
    }

    // 驳回场景cmd中没有表单数据
    private String createOrUpdateJstbByFormData(AbstractExecutionCmd cmd) {
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
        if (StringUtils.isBlank(formDataJson.getString("jstbId"))) {
            String numInfo = jstbService.toGetJstbNumber();
            formDataJson.put("numInfo", numInfo);
            formDataJson.put("status", "草稿");
            jstbService.createJstb(formDataJson);
        } else {
            jstbService.updateJstb(formDataJson);
        }
        return formDataJson.getString("jstbId");
    }
}
