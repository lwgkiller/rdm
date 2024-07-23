package com.redxun.meeting.core.service;

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
public class RwfjScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(RwfjScript.class);


    public Collection<TaskExecutor> getOrganizer() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String SzrId = formDataJson.getString("meetingOrgUserId");
        if (StringUtils.isNotBlank(SzrId)) {
            String SzrName = formDataJson.getString("meetingOrgName");
            users.add(new TaskExecutor(SzrId, SzrName));
        }

        return users;
    }

    public Collection<TaskExecutor> getMeetingResp() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String SzrId = formDataJson.getString("meetingPlanRespUserIds");
        if (StringUtils.isNotBlank(SzrId)) {
            String SzrName = formDataJson.getString("meetingPlanRespName");
            users.add(new TaskExecutor(SzrId, SzrName));
        }

        return users;
    }

    public Collection<TaskExecutor> getShLeader() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String SzrId = formDataJson.getString("shLeaderId");
        if (StringUtils.isNotBlank(SzrId)) {
            String SzrName = formDataJson.getString("shLeaderName");
            users.add(new TaskExecutor(SzrId, SzrName));
        }

        return users;
    }

    public boolean finish(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        if("true".equals(formDataJson.getString("isComplete"))){
            return true;
        }
        return false;
    }
    public boolean unfinish(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        if("false".equals(formDataJson.getString("isComplete"))){
            return true;
        }
        return false;
    }

    public boolean zlgjMeeting(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        if("0004".equals(formDataJson.getString("meetingModelId"))){
            return true;
        }
        return false;
    }
    public boolean nozlgjMeeting(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        if("0004".equals(formDataJson.getString("meetingModelId"))){
            return false;
        }
        return true;
    }
}

