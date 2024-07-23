package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.*;
import com.redxun.core.script.GroovyScript;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OverseasProductRectificationScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(OverseasProductRectificationScript.class);
    @Autowired
    private OverseasProductRectificationService overseasProductRectificationService;

    //..
    public Collection<TaskExecutor> getDesigner() {
        List<TaskExecutor> users = new ArrayList<>();
        JSONObject formDataJson = JSONObject.parseObject(ProcessHandleHelper.getProcessCmd().getJsonData());
        users.add(new TaskExecutor(formDataJson.getString("designerId"), formDataJson.getString("designer")));
        return users;
    }

    //..
    public boolean isNeedDesigner(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        JSONObject formDataJson = cmd.getJsonDataObject();
        if (formDataJson.getString("isNeedDesigner").equalsIgnoreCase("是")) {
            return true;
        } else {
            return false;
        }
    }

    //..
    public boolean isNeedDesignerNeg(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        JSONObject formDataJson = cmd.getJsonDataObject();
        if (formDataJson.getString("isNeedDesigner").equalsIgnoreCase("否")) {
            return true;
        } else {
            return false;
        }
    }

    //..
    public void taskCreateScript(Map<String, Object> vars) {
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
            overseasProductRectificationService.updateBusiness(jsonDataObject);
        } catch (Exception e) {
            ProcessHandleHelper.addErrorMsg(e.getMessage());
            throw e;
        }
    }
}
