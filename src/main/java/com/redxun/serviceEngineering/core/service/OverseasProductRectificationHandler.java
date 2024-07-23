package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OverseasProductRectificationHandler
        implements ProcessStartPreHandler, ProcessStartAfterHandler, TaskPreHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(OverseasProductRectificationHandler.class);
    @Autowired
    private OverseasProductRectificationService overseasProductRectificationService;

    //..整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        JSONObject jsonDataObject = processStartCmd.getJsonDataObject();
        jsonDataObject.put("INST_ID_", processStartCmd.getBpmInstId());
        processStartCmd.setJsonData(jsonDataObject.toJSONString());
        String businessId = createOrUpdateBusinessByFormData(processStartCmd);
        if (StringUtils.isNotBlank(businessId)) {
            processStartCmd.setBusinessKey(businessId);
            jsonDataObject.put("id", businessId);
            processStartCmd.setJsonData(jsonDataObject.toJSONString());
        }
    }

    //..
    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd cmd, BpmInst bpmInst) {
        JSONObject jsonDataObject = cmd.getJsonDataObject();
        jsonDataObject.put("INST_ID_", cmd.getBpmInstId());
        cmd.setJsonData(jsonDataObject.toJSONString());
        return null;
    }

    //..任务审批前置处理器,只有第一个编辑节点需要，因为要更新业务单据的
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd) iExecutionCmd;
        createOrUpdateBusinessByFormData(processNextCmd);
    }

    //..
    @Override
    public void endHandle(BpmInst bpmInst) {
        if ("SUCCESS_END".equalsIgnoreCase(bpmInst.getStatus())) {
            String businessId = bpmInst.getBusKey();
            JSONObject detail = overseasProductRectificationService.getDetail(businessId);
            try {
                detail.put("businessStatus", "Z");
                detail.put("isSynToGss", "否");
                overseasProductRectificationService.updateBusiness(detail);
                overseasProductRectificationService.doSynFormToGss(detail);
            } catch (Exception e) {
                logger.error("传输失败：" + e.getMessage(), e);
            }
        }
    }

    //..
    private String createOrUpdateBusinessByFormData(AbstractExecutionCmd cmd) {
        JSONObject formDataJson = cmd.getJsonDataObject();
        if (StringUtils.isBlank(formDataJson.getString("id"))) {
            overseasProductRectificationService.createBusiness(formDataJson);
        } else {
            overseasProductRectificationService.updateBusiness(formDataJson);
        }
        return formDataJson.getString("id");
    }
}
