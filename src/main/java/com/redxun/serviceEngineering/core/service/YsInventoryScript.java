package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// 项目流程节点的触发事件
@Service
public class YsInventoryScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(YsInventoryScript.class);

    @Autowired
    private CommonInfoDao commonInfoDao;

    // 产品主管
    public Collection<TaskExecutor> getCpzg() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String zrrId = formDataJson.getString("cpzgId");
        if (StringUtils.isNotBlank(zrrId)) {
            String zrrName = formDataJson.getString("cpzgName");
            users.add(new TaskExecutor(zrrId, zrrName));
        }
        return users;
    }

    // 产品主管所长
    public Collection<TaskExecutor> getCpzgLeader() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String cpzgId = formDataJson.getString("cpzgId");
        List<JSONObject> deptResps = commonInfoDao.queryDeptRespByUserId(cpzgId);
        if (deptResps != null && !deptResps.isEmpty()) {
            for (JSONObject depRespMan : deptResps) {
                users.add(new TaskExecutor(depRespMan.getString("USER_ID_"), depRespMan.getString("FULLNAME_")));
            }
        }
        return users;
    }

}
