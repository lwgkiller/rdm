package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.ProcessStartCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.serviceEngineering.core.dao.SchematicDownloadApplyDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SchematicDownloadApplyScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(SchematicDownloadApplyScript.class);
    @Autowired
    private SchematicDownloadApplyDao schematicDownloadApplyDao;

    // ..
    public Collection<TaskExecutor> getkeyUsers() {
        List<TaskExecutor> users = new ArrayList<>();
        try {
            // 在流程直接点提交时候，第二步formdata中没有id，所以要从cmd中获取。只有ProcessStartCmd中有BusinessKey，ProcessNextCmd中没有
            // 所以需要先从json中获取，没有再从cmd
            IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
            if (cmd == null) {
                return users;
            }
            String applyId = null;
            String jsonData = cmd.getJsonData();
            if (StringUtils.isNotBlank(jsonData)) {
                JSONObject jsonObject = JSONObject.parseObject(jsonData);
                applyId = jsonObject.getString("id");
            }
            if (StringUtils.isBlank(applyId)) {
                if (cmd instanceof ProcessStartCmd) {
                    applyId = ((ProcessStartCmd)cmd).getBusinessKey();
                }
            }
            if (StringUtils.isBlank(applyId)) {
                return users;
            }
            // ..下面是业务
            JSONObject params = new JSONObject();
            params.put("applyId", applyId);
            List<JSONObject> demandList = schematicDownloadApplyDao.queryDemandList(params);
            for (JSONObject oneDemand : demandList) {
                if (StringUtils.isNotBlank(oneDemand.getString("keyUserId"))) {
                    users.add(new TaskExecutor(oneDemand.getString("keyUserId"), oneDemand.getString("keyUser")));
                }
            }
        } catch (Exception e) {
            logger.error("系统异常", e);
        }
        return users;
    }

}
