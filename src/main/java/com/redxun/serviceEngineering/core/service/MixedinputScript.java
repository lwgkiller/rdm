package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.ProcessNextCmd;
import com.redxun.bpm.core.entity.ProcessStartCmd;
import com.redxun.core.script.GroovyScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MixedinputScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(MixedinputScript.class);
    @Autowired
    private MixedinputService mixedinputService;

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
        if (jsonDataObject.getString("businessType").equalsIgnoreCase("filing")) {//归档业务
            mixedinputService.updateBusinessFiling(jsonDataObject);
        } else if (jsonDataObject.getString("businessType").equalsIgnoreCase("discard")) {//作废业务
            mixedinputService.updateBusinessDiscard(jsonDataObject);
        }
    }

    //..
    public void taskEndScript(Map<String, Object> vars) {
        JSONObject jsonDataObject = ProcessHandleHelper.getProcessCmd().getJsonDataObject();
        String activitiId = vars.get("activityId").toString();
        if (jsonDataObject.getString("businessType").equalsIgnoreCase("filing")) {//归档业务
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
                mixedinputService.doCreatMasterdataFromWorkflow(jsonDataObject);
            }
            mixedinputService.updateBusinessFiling(jsonDataObject);
        } else if (jsonDataObject.getString("businessType").equalsIgnoreCase("discard")) {//作废业务
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
                mixedinputService.doDiscardMasterdataFromWorkflow(jsonDataObject);
            }
            mixedinputService.updateBusinessDiscard(jsonDataObject);
        }
    }
}
