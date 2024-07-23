package com.redxun.environment.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.environment.core.dao.RjbgDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

// 项目流程节点的触发事件
@Service
public class YjbgScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(YjbgScript.class);

    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private RjbgDao rjbgDao;




    /**
     * 流程中，获取动力工程师
     *
     * @return
     */
    public Collection<TaskExecutor> getDlgcs() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String respManId = formDataJson.getString("dlId");
        if (StringUtils.isNotBlank(respManId)) {
            String respManName = formDataJson.getString("dlName");
            users.add(new TaskExecutor(respManId, respManName));
        }
        
        return users;
    }


    /**
     * 流程中，获取动力负责人
     *
     * @return
     */
    public Collection<TaskExecutor> gethqfzr() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String respManId = formDataJson.getString("szrId");
        if (StringUtils.isNotBlank(respManId)) {
            String respManName = formDataJson.getString("szrName");
            users.add(new TaskExecutor(respManId, respManName));
        }

        return users;
    }

    /**
     * 流程中，获取动力室主任
     *
     * @return
     */
    public Collection<TaskExecutor> getDlszr() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String respManIdList = formDataJson.getString("szrId");
        String[] respManIds=respManIdList.split(",");
        String respManNameList = formDataJson.getString("szrName");
        String[] respManNames=respManNameList.split(",");
        for(int i=0;i<respManIds.length;i++){
            if (StringUtils.isNotBlank(respManIds[i])) {
                users.add(new TaskExecutor(respManIds[i], respManNames[i]));
            }
        }
        return users;
    }

    /**
     * 流程中，获取质量部专员
     *
     * @return
     */
    public Collection<TaskExecutor> getZl() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String respManId = formDataJson.getString("zlId");
        if (StringUtils.isNotBlank(respManId)) {
            String respManName = formDataJson.getString("zlName");
            users.add(new TaskExecutor(respManId, respManName));
        }

        return users;
    }


}

