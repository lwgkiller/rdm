package com.redxun.componentTest.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.componentTest.core.dao.ComponentTestApplyDao;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.serviceEngineering.core.service.MixedinputService;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Set;

@Service
public class ComponentTestApplyHandler implements ProcessStartPreHandler, ProcessStartAfterHandler, TaskPreHandler {
    private Logger logger = LoggerFactory.getLogger(ComponentTestApplyHandler.class);
    @Autowired
    private ComponentTestApplyService componentTestApplyService;

    //..整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        JSONObject jsonDataObject = processStartCmd.getJsonDataObject();
        jsonDataObject.put("INST_ID_", processStartCmd.getBpmInstId());
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
        JSONObject formDataJson = cmd.getJsonDataObject();
        try {
            JsonResult jsonResult = componentTestApplyService.saveBusiness(formDataJson);
            return (String) jsonResult.getData();
        } catch (Exception e) {
            logger.error("Exception in createOrUpdateBusinessByFormData", e);
            return null;
        }
    }
}
