package com.redxun.xcmgjssjk.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
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
public class JssbScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(JssbScript.class);

    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private RjbgDao rjbgDao;
    @Autowired
    private JssbService jssbService;


    public boolean need(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        cmd =(AbstractExecutionCmd) ProcessHandleHelper.getProcessCmd();
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
        String fgsh = formDataJson.getString("szrCheck");
        if(fgsh.equals("false")){
            return true;
        }
        return false;
    }

    public boolean dontneed(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        String fgsh = formDataJson.getString("szrCheck");
        if(fgsh.equals("true")){
            return true;
        }
        return false;
    }

    /**
     * 流程中，获取软件上传人
     *
     * @return
     */
    public Collection<TaskExecutor> getSzr() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String respManId = formDataJson.getString("szrId");
        if (StringUtils.isNotBlank(respManId)) {
            String respManName = formDataJson.getString("szr");
            users.add(new TaskExecutor(respManId, respManName));
        }
        
        return users;
    }

    public void  taskEndScript(Map<String, Object> vars){
        JSONObject jsonObject = ProcessHandleHelper.getProcessCmd().getJsonDataObject();
        jssbService.updateJssb(jsonObject);
    }
}

