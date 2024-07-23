package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.*;
import com.redxun.core.script.GroovyScript;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BucketConfigurationScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(BucketConfigurationScript.class);
    @Autowired
    private BucketConfigurationService bucketConfigurationService;

    //..
    public Collection<TaskExecutor> getRepUser() {
        List<TaskExecutor> users = new ArrayList<>();
        JSONObject formDataJson = JSONObject.parseObject(ProcessHandleHelper.getProcessCmd().getJsonData());
        users.add(new TaskExecutor(formDataJson.getString("repUserId"), formDataJson.getString("repUser")));
        return users;
    }

    //..
    public boolean isAuto(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        JSONObject formDataJson = cmd.getJsonDataObject();
        if (formDataJson.getString("isAuto").equalsIgnoreCase("true")) {
            return true;
        } else {
            return false;
        }
    }

    //..
    public boolean isNotAuto(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        JSONObject formDataJson = cmd.getJsonDataObject();
        if (formDataJson.getString("isAuto").equalsIgnoreCase("false")) {
            return true;
        } else {
            return false;
        }
    }

    //..
    public void taskCreateScript(Map<String, Object> vars) throws Exception {
        try {
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
            bucketConfigurationService.updateBusiness(jsonDataObject);
        } catch (Exception e) {
            ProcessHandleHelper.addErrorMsg(e.getMessage());
            throw e;
        }
    }

    //..
    public void taskEndScript(Map<String, Object> vars) throws Exception {
        try {
            JSONObject jsonDataObject = ProcessHandleHelper.getProcessCmd().getJsonDataObject();
            String activitiId = vars.get("activityId").toString();
            if (activitiId.equalsIgnoreCase("C")) {
                jsonDataObject.put("businessStatus", "Z");
            }
            bucketConfigurationService.updateBusiness(jsonDataObject);
        } catch (Exception e) {
            ProcessHandleHelper.addErrorMsg(e.getMessage());
            throw e;
        }
    }
}
