package com.redxun.digitization.core.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.drbfm.core.dao.DrbfmSingleDao;
import com.redxun.drbfm.core.dao.DrbfmTestTaskDao;
import com.redxun.org.api.service.UserService;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.sys.org.dao.OsUserDao;
import com.redxun.sys.org.entity.OsUser;

@Service
public class RdmDevTaskScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(RdmDevTaskScript.class);

    // 程序类型分支，新开发
    public boolean devFlowChangeYes(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String flowChange = formDataJson.getString("flowChange");

        if ("yes".equalsIgnoreCase(flowChange)) {
            return true;
        }
        return false;
    }

    // 程序类型分支，借用，走到开发完成节点
    public boolean devFlowChangeNo(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String flowChange = formDataJson.getString("flowChange");

        if ("no".equalsIgnoreCase(flowChange)) {
            return true;
        }
        return false;
    }

}
