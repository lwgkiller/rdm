package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.serviceEngineering.core.dao.JxbzzbshDao;
import com.redxun.serviceEngineering.core.dao.JxbzzbxfsqDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FullLifeCycleCostScript implements GroovyScript {

    private Logger logger = LoggerFactory.getLogger(FullLifeCycleCostScript.class);

    // 获取流程中创建人部门领导
    public Collection<TaskExecutor> getCpzgUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String cpzgId = formDataJson.getString("cpzgId");
        String cpzgName = formDataJson.getString("cpzgName");
        users.add(new TaskExecutor(cpzgId, cpzgName));
        return users;
    }



}
