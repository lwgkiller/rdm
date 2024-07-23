package com.redxun.processValidation.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.ProcessNextCmd;
import com.redxun.bpm.core.entity.ProcessStartCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.serviceEngineering.core.service.MixedinputService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ProcessValidationRequirementsScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(ProcessValidationRequirementsScript.class);
    @Autowired
    private ProcessValidationRequirementsService processValidationRequirementsService;

    //..
    public void taskCreateScript(Map<String, Object> vars) {
        JSONObject jsonDataObject = ProcessHandleHelper.getProcessCmd().getJsonDataObject();
        String activitiId = vars.get("activityId").toString();
        jsonDataObject.put("businessStatus", activitiId);
        IExecutionCmd processCmd = ProcessHandleHelper.getProcessCmd();
        if (processCmd instanceof ProcessStartCmd) {
            ProcessStartCmd processStartCmd = (ProcessStartCmd) ProcessHandleHelper.getProcessCmd();
            processStartCmd.setJsonData(jsonDataObject.toJSONString());
        } else if (processCmd instanceof ProcessNextCmd) {
            ProcessNextCmd processNextCmd = (ProcessNextCmd) ProcessHandleHelper.getProcessCmd();
            processNextCmd.setJsonData(jsonDataObject.toJSONString());
        }
        processValidationRequirementsService.updateApply(jsonDataObject);
    }

    //..
    public void taskEndScript(Map<String, Object> vars) {
        JSONObject jsonDataObject = ProcessHandleHelper.getProcessCmd().getJsonDataObject();
        String activitiId = vars.get("activityId").toString();
        if (activitiId.equalsIgnoreCase("D")) {
            jsonDataObject.put("businessStatus", "Z");
            IExecutionCmd processCmd = ProcessHandleHelper.getProcessCmd();
            if (processCmd instanceof ProcessStartCmd) {
                ProcessStartCmd processStartCmd = (ProcessStartCmd) ProcessHandleHelper.getProcessCmd();
                processStartCmd.setJsonData(jsonDataObject.toJSONString());
            } else if (processCmd instanceof ProcessNextCmd) {
                ProcessNextCmd processNextCmd = (ProcessNextCmd) ProcessHandleHelper.getProcessCmd();
                processNextCmd.setJsonData(jsonDataObject.toJSONString());
            }
        }
        processValidationRequirementsService.updateApply(jsonDataObject);
    }

    //..
    public Collection<TaskExecutor> getTaskExecutor() {
//        List<TaskExecutor> users = new ArrayList<>();
//        JSONObject formDataJson = ProcessHandleHelper.getProcessCmd().getJsonDataObject();
//        users.add(new TaskExecutor(formDataJson.getString("taskExecutorId"), formDataJson.getString("taskExecutor")));
//        return users;
        //@lwgkiller:尝试使用流式编程,还是比较优雅的,一句话搞定
        return Stream.of(new TaskExecutor(ProcessHandleHelper.getProcessCmd().getJsonDataObject().getString("taskExecutorId"),
                ProcessHandleHelper.getProcessCmd().getJsonDataObject().getString("taskExecutor")))
                .collect(Collectors.toList());
    }
}
