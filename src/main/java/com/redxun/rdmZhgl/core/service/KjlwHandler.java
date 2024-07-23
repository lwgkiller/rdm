package com.redxun.rdmZhgl.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.rdmZhgl.core.dao.KjlwDao;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class KjlwHandler implements ProcessStartPreHandler, ProcessStartAfterHandler, TaskPreHandler {
    private Logger logger = LoggerFactory.getLogger(KjlwHandler.class);
    @Autowired
    private KjlwService kjlwService;
    @Autowired
    private KjlwDao kjlwDao;
    // 整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        String kjlwId = createOrUpdateJsmmByFormData(processStartCmd);
        if (StringUtils.isNotBlank(kjlwId)) {
            processStartCmd.setBusinessKey(kjlwId);
        }
    }
    // 第一个任务创建之后流程的后置处理器（进行编号的生成和更新）
    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd processStartCmd,
                                          BpmInst bpmInst) {
        String kjlwId = processStartCmd.getBusinessKey();
        String kjlwNum = kjlwService.toGetKjlwNum();
        Map<String, Object> param = new HashMap<>();
        param.put("kjlwId", kjlwId);
        param.put("kjlwNum", kjlwNum);
        kjlwDao.updateKjlwNum(param);
        return kjlwId;
    }
    // 任务审批之后的前置处理器
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd)iExecutionCmd;
        createOrUpdateJsmmByFormData(processNextCmd);
    }
    // 驳回场景cmd中没有表单数据
    private String createOrUpdateJsmmByFormData(AbstractExecutionCmd cmd) {
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
        if (StringUtils.isBlank(formDataJson.getString("kjlwId"))) {
            kjlwService.createKjlw(formDataJson);
        } else {
            kjlwService.updateKjlw(formDataJson);
        }
        return formDataJson.getString("kjlwId");
    }
}
