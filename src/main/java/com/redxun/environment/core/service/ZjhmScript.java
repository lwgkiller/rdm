package com.redxun.environment.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
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

@Service
public class ZjhmScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(ZjhmScript.class);

    /**
     * 流程中，获取产品主管
     *
     * @return
     */
    public Collection<TaskExecutor> getCpzg() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String respManIdList = formDataJson.getString("cpzgId");
        String[] respManIds=respManIdList.split(",");
        String respManNameList = formDataJson.getString("cpzgName");
        String[] respManNames=respManNameList.split(",");
        for(int i=0;i<respManIds.length;i++){
            if (StringUtils.isNotBlank(respManIds[i])) {
                users.add(new TaskExecutor(respManIds[i], respManNames[i]));
            }
        }
        return users;
    }

    /**
     * 流程中，获取产品主管
     *
     * @return
     */
    public Collection<TaskExecutor> getDlgcs() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String respManIdList = formDataJson.getString("dlgcsId");
        String[] respManIds=respManIdList.split(",");
        String respManNameList = formDataJson.getString("dlgcsName");
        String[] respManNames=respManNameList.split(",");
        for(int i=0;i<respManIds.length;i++){
            if (StringUtils.isNotBlank(respManIds[i])) {
                users.add(new TaskExecutor(respManIds[i], respManNames[i]));
            }
        }
        return users;
    }
}
