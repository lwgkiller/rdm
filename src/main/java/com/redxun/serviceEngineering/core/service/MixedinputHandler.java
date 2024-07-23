package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.core.util.DateUtil;
import com.redxun.saweb.context.ContextUtil;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class MixedinputHandler implements ProcessStartPreHandler, ProcessStartAfterHandler, TaskPreHandler {
    private Logger logger = LoggerFactory.getLogger(MixedinputHandler.class);
    @Autowired
    private MixedinputService mixedinputService;

    //..整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        JSONObject jsonDataObject = processStartCmd.getJsonDataObject();
        jsonDataObject.put("INST_ID_", processStartCmd.getBpmInstId());
        jsonDataObject.put("applyUserId", ContextUtil.getCurrentUserId());
        jsonDataObject.put("applyTime", DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_YMD));
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
    private String createOrUpdateBusinessByFormData(AbstractExecutionCmd cmd) {
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
        if (formDataJson.getString("businessType").equalsIgnoreCase("filing")) {//归档业务
            if (StringUtils.isBlank(formDataJson.getString("id"))) {
                mixedinputService.createBusinessFiling(formDataJson);
            } else {
                mixedinputService.updateBusinessFiling(formDataJson);
            }
        } else if (formDataJson.getString("businessType").equalsIgnoreCase("discard")) {//作废业务
            if (StringUtils.isBlank(formDataJson.getString("id"))) {
                mixedinputService.createBusinessDiscard(formDataJson);
            } else {
                mixedinputService.updateBusinessDiscard(formDataJson);
            }
        }
        return formDataJson.getString("id");
    }
}
