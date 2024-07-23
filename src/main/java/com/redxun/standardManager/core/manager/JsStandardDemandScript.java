package com.redxun.standardManager.core.manager;

import java.util.*;

import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.saweb.util.WebAppUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.standardManager.core.util.StandardConstant;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;

// 项目流程节点的触发事件
@Service
public class JsStandardDemandScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(JsStandardDemandScript.class);
    @Autowired
    private StandardDemandManager standardDemandManager;

    public void taskBackScript(Map<String, Object> vars) {
        logger.info("流程驳回,表单保存");
        AbstractExecutionCmd cmd = (AbstractExecutionCmd)vars.get("cmd");
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return;
        }
        standardDemandManager.updateStandardApply(formDataJson);
    }

    // 技术标准需求反馈，承办部门确认
    public Collection<TaskExecutor> getdoDeptRespUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String doDeptRespId = formDataJson.getString("doDeptRespId");
        if (StringUtils.isNotBlank(doDeptRespId)) {
            String doDeptRespName = formDataJson.getString("doDeptRespName");
            users.add(new TaskExecutor(doDeptRespId, doDeptRespName));
        }
        return users;
    }
}
