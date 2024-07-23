package com.redxun.environment.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.environment.core.dao.GsClhbDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

// 项目流程节点的触发事件
@Service
public class GsClhbScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(GsClhbScript.class);

    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private GsClhbDao gsClhbDao;

    

    /**
     * 流程中，获取电气工程师
     *
     * @return
     */
    public Collection<TaskExecutor> getDq() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String respManId = formDataJson.getString("dqId");
        if (StringUtils.isNotBlank(respManId)) {
            String respManName = formDataJson.getString("dqName");
            users.add(new TaskExecutor(respManId, respManName));
        }
        
        return users;
    }

    /**
     * 流程中，获取动力工程师
     *
     * @return
     */
    public Collection<TaskExecutor> getDl() {
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


    public boolean nodq(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String REGEX = "[^0-9.]";
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
        String ticket = formDataJson.getString("cyjedgl").replaceAll(REGEX, "");
        if(Double.parseDouble(ticket)<37||"37kW以下".equals(formDataJson.getString("cyjedgl"))){
            return true;
        }
        return false;
    }
    public boolean yesdq(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String REGEX = "[^0-9.]";
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
        String ticket = formDataJson.getString("cyjedgl").replaceAll(REGEX, "");
        if(Double.parseDouble(ticket)>=37&&!"37kW以下".equals(formDataJson.getString("cyjedgl"))){
            return true;
        }
        return false;
    }

    public boolean noyy(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        if("new".equals(formDataJson.getString("type"))){
            return true;
        }
        return false;
    }
    public boolean yesyy(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        if("old".equals(formDataJson.getString("type"))){
            return true;
        }
        return false;
    }
}

