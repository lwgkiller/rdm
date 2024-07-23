package com.redxun.qualityfxgj.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.qualityfxgj.core.dao.GyfkDao;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class GyfkHandler
    implements ProcessStartPreHandler, ProcessStartAfterHandler, TaskPreHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(GyfkHandler.class);
    @Autowired
    private GyfkService gyfkService;
    @Autowired
    private GyfkDao gyfkDao;

    // 整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        String gyfkId = createOrUpdateGyfkByFormData(processStartCmd);
        if (StringUtils.isNotBlank(gyfkId)) {
            processStartCmd.setBusinessKey(gyfkId);
        }
    }

    // 第一个任务创建之后流程的后置处理器
    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd processStartCmd,
        BpmInst bpmInst) {
        String gyfkId = processStartCmd.getBusinessKey();
        JSONObject param = new JSONObject();
        param.put("gyfkId", gyfkId);
        gyfkDao.updateGyfk(param);
        return gyfkId;
    }

    // 任务审批之后的前置处理器
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd)iExecutionCmd;
        createOrUpdateGyfkByFormData(processNextCmd);
    }

    // 流程成功结束之后的处理
    @Override
    public void endHandle(BpmInst bpmInst) {
        if ("SUCCESS_END".equalsIgnoreCase(bpmInst.getStatus())) {
            String gyfkId = bpmInst.getBusKey();
            Map<String, Object> param = new HashMap<>();
            param.put("gyfkId", gyfkId);
        }
    }

    // 驳回场景cmd中没有表单数据
    private String createOrUpdateGyfkByFormData(AbstractExecutionCmd cmd) {
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
        if (StringUtils.isBlank(formDataJson.getString("gyfkId"))) {
            gyfkService.createGyfk(formDataJson);
        } else {
            gyfkService.updateGyfk(formDataJson);
        }
        return formDataJson.getString("gyfkId");
    }
}
