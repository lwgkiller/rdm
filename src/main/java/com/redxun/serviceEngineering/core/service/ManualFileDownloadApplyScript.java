package com.redxun.serviceEngineering.core.service;

import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.ProcessStartCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.serviceEngineering.core.dao.ManualFileDownloadApplyDao;

@Service
public class ManualFileDownloadApplyScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(ManualFileDownloadApplyScript.class);
    @Autowired
    private ManualFileDownloadApplyDao manualFileDownloadApplyDao;

    /**
     * 获取申请单中关联的操保手册的产品主管
     * 
     * @return
     */
    public Collection<TaskExecutor> getManualFileCpzgs() {
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
            JSONObject params = new JSONObject();
            params.put("applyId", applyId);
            List<JSONObject> demandList = manualFileDownloadApplyDao.queryDemandList(params);
            Map<String, String> userId2Name = new HashMap<>();
            for (JSONObject oneDemand : demandList) {
                if (StringUtils.isNotBlank(oneDemand.getString("cpzgId"))) {
                    userId2Name.put(oneDemand.getString("cpzgId"), oneDemand.getString("cpzgName"));
                }
            }
            if (!userId2Name.isEmpty()) {
                for (Map.Entry<String, String> oneEntry : userId2Name.entrySet()) {
                    users.add(new TaskExecutor(oneEntry.getKey(), oneEntry.getValue()));
                }
            }
        } catch (Exception e) {
            logger.error("系统异常", e);
        }
        return users;
    }

}
