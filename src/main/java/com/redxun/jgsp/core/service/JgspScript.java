package com.redxun.jgsp.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

// 项目流程节点的触发事件
@Service
public class JgspScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(JgspScript.class);


    public Collection<TaskExecutor> getXzz() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String SzrId = formDataJson.getString("xzzId");
        if (StringUtils.isNotBlank(SzrId)) {
            String SzrName = formDataJson.getString("xzzName");
            users.add(new TaskExecutor(SzrId, SzrName));
        }

        return users;
    }

    public boolean tg(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        if (StringUtils.isBlank(formData)) {
            logger.warn("formData is blank");
            return false;
        }
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return false;
        }
        if("通过".equals(formDataJson.getString("nextNode"))){
            return true;
        }
        return false;
    }
    public boolean sh(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        if (StringUtils.isBlank(formData)) {
            logger.warn("formData is blank");
            return false;
        }
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return false;
        }
        if("上会".equals(formDataJson.getString("nextNode"))){
            return true;
        }
        return false;
    }

}

