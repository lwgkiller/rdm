package com.redxun.serviceEngineering.core.service;


import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;


// 项目流程节点的触发事件
@Service
public class ZdwjScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(ZdwjScript.class);



    // 从表单获会签人员
    public Collection<TaskExecutor> getCsUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String csIdStr = formDataJson.getString("csId");
        String csNameStr = formDataJson.getString("csName");
        if (StringUtils.isNotEmpty(csIdStr) && StringUtils.isNotEmpty(csNameStr)) {
            String[] csIds = csIdStr.split(",");
            String[] csNames = csNameStr.split(",");
            for (int i = 0; i < csIds.length; i++) {
                TaskExecutor oneUser = new TaskExecutor(csIds[i],csNames[i]);
                users.add(oneUser);
            }
        }
        return users;
    }

    // 从表单获审核人员
    public Collection<TaskExecutor> getCheckUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String idStr = formDataJson.getString("checkerId");
        String nameStr = formDataJson.getString("checkerName");
        if (StringUtils.isNotEmpty(idStr) && StringUtils.isNotEmpty(nameStr)) {
            String[] ids = idStr.split(",");
            String[] names = nameStr.split(",");
            for (int i = 0; i < ids.length; i++) {
                TaskExecutor oneUser = new TaskExecutor(ids[i],names[i]);
                users.add(oneUser);
            }
        }
        return users;
    }

}
