package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.bpm.core.entity.BpmTask;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.bpm.core.manager.BpmTaskManager;
import com.redxun.core.script.GroovyScript;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.serviceEngineering.core.dao.DecorationManualCollectDao;
import com.redxun.serviceEngineering.core.dao.DecorationManualFileDao;
import com.redxun.sys.core.manager.SysSeqIdManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SparepartsDataCollectScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(SparepartsDataCollectScript.class);
    @Autowired
    private SparepartsDataCollectService sparepartsDataCollectService;
    @Autowired
    private SysSeqIdManager sysSeqIdManager;

    //..
    public Collection<TaskExecutor> getCollector() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String userId = formDataJson.getString("collectorId");
        String userName = formDataJson.getString("collector");
        users.add(new TaskExecutor(userId, userName));
        return users;
    }

    //..
    public Collection<TaskExecutor> getCollector2() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String userId = formDataJson.getString("collectorId2");
        String userName = formDataJson.getString("collector2");
        users.add(new TaskExecutor(userId, userName));
        return users;
    }

    //..
    public void taskCreateScript(Map<String, Object> vars) {
        try {
            JSONObject jsonObject = sparepartsDataCollectService.getDetail(vars.get("busKey").toString());
            String activitiId = vars.get("activityId").toString();
            jsonObject.put("businessStatus", activitiId);
            jsonObject.put("INST_ID_", vars.get("instId"));
            sparepartsDataCollectService.updateBusiness(jsonObject);
        } catch (Exception e) {
            logger.error("Exception in taskCreateScript", e);
        }
    }

    //..
    public void taskEndScript(Map<String, Object> vars) {
        try {
            JSONObject jsonObject = sparepartsDataCollectService.getDetail(vars.get("busKey").toString());
            String activitiId = vars.get("activityId").toString();
            if (activitiId.equalsIgnoreCase("A")) {
                jsonObject.put("applyUserId", ContextUtil.getCurrentUserId());
                jsonObject.put("applyUser", ContextUtil.getCurrentUser().getFullname());
                jsonObject.put("applyDepId", ContextUtil.getCurrentUser().getMainGroupId());
                jsonObject.put("applyDep", ContextUtil.getCurrentUser().getMainGroupName());
                if (StringUtil.isEmpty(jsonObject.getString("busunessNo"))) {
                    jsonObject.put("busunessNo", sysSeqIdManager.genSequenceNo("sparepartsDataCollect", ContextUtil.getCurrentTenantId()));
                }
                jsonObject.put("applyTime", DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_YMD));
                sparepartsDataCollectService.updateBusiness(jsonObject);
            } else if (activitiId.equalsIgnoreCase("E")) {
                jsonObject.put("businessStatus", "Z");
                sparepartsDataCollectService.updateBusiness(jsonObject);
            }

        } catch (Exception e) {
            logger.error("Exception in taskEndScript", e);
        }
    }
}
