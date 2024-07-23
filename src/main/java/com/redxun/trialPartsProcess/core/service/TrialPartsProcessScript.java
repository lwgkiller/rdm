package com.redxun.trialPartsProcess.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.trialPartsProcess.core.dao.TrialPartsProcessDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TrialPartsProcessScript implements GroovyScript {

    private Logger logger = LoggerFactory.getLogger(TrialPartsProcessScript.class);
    @Autowired
    TrialPartsProcessDao trialPartsProcessDao;
    public Collection<TaskExecutor> getNodeProcessor() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String stageName = formDataJson.getString("stageName");
        String[] userIds = formDataJson.getString("memberInfo").split(",");
        String userId="";
        if (StringUtils.isNotBlank(stageName)) {
            if ("stage_fir".equals(stageName)) {
                userId = userIds[0];
            } else if ("stage_sec".equals(stageName)) {
                userId = userIds[1];
            } else if ("stage_thr".equals(stageName)){
                userId = userIds[2];
            }
        }
        JSONObject param = new JSONObject();
        param.put("userId", userId);
        JSONObject userInfo = trialPartsProcessDao.getUserInfoById(userId);
        users.add(new TaskExecutor(userInfo.getString("userId"), userInfo.getString("userName")));
        return users;
    }
}
