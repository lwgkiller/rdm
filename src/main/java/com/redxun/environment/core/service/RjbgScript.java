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
public class RjbgScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(RjbgScript.class);

    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private RjbgDao rjbgDao;




    /**
     * 流程中，获取动力/控制/电气
     *
     * @return
     */
    public Collection<TaskExecutor> getRjhq() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String dlId = formDataJson.getString("dlId");
        if (StringUtils.isNotBlank(dlId)) {
            String dlName = formDataJson.getString("dlName");
            users.add(new TaskExecutor(dlId, dlName));
        }
        String kzId = formDataJson.getString("kzId");
        if (StringUtils.isNotBlank(kzId)) {
            String kzName = formDataJson.getString("kzName");
            users.add(new TaskExecutor(kzId, kzName));
        }
        String dqId = formDataJson.getString("dqId");
        if (StringUtils.isNotBlank(dqId)) {
            String dqName = formDataJson.getString("dqName");
            users.add(new TaskExecutor(dqId, dqName));
        }
        return users;
    }


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


}

