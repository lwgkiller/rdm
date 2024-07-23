package com.redxun.rdmZhgl.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.serviceEngineering.core.service.MaintenanceManualMctService;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatentInterpretationHandler
        implements ProcessStartPreHandler, ProcessStartAfterHandler, TaskPreHandler {
    private Logger logger = LoggerFactory.getLogger(PatentInterpretationHandler.class);
    @Autowired
    private PatentInterpretationService patentInterpretationService;

    //..整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        String formData = processStartCmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        formDataJson.put("INST_ID_", processStartCmd.getBpmInstId());
        processStartCmd.setJsonData(formDataJson.toJSONString());
        String businessId = createOrUpdateBusinessByFormData(processStartCmd);
        if (StringUtils.isNotBlank(businessId)) {
            processStartCmd.setBusinessKey(businessId);
        }
    }

    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd cmd, BpmInst bpmInst) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        //@lwgkiller:在这设个启动后置处理器，只是重新回填下INST_ID_，防止草稿直接启动没有INST_ID_，这里并没有进行update操作，但是会通过taskCreateScript的
        //update操作体现出来，也就是说一个流程事务中的cmd.getJsonData是单例的。这样processStartPreHandle里的update操作可以省略了
        formDataJson.put("INST_ID_", cmd.getBpmInstId());
        cmd.setJsonData(formDataJson.toJSONString());
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
        //如果是新增的情况
        if (StringUtils.isBlank(formDataJson.getString("id"))) {
            patentInterpretationService.createBusiness(formDataJson);
        } else {
            patentInterpretationService.updateBusiness(formDataJson);
        }
        return formDataJson.getString("id");
    }


}
