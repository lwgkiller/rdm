package com.redxun.wwrz.core.service;

import java.util.Map;

import javax.annotation.Resource;

import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.wwrz.core.dao.WwrzPlanApplyDao;
import com.redxun.wwrz.core.dao.WwrzTestPlanDao;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;

/**
 * @author zz
 * */
@Service
public class WwrzPlanApplyHandler implements ProcessStartPreHandler, TaskPreHandler, ProcessStartAfterHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(WwrzPlanApplyHandler.class);
    @Resource
    private WwrzPlanApplyService wwrzPlanApplyService;
    @Resource
    private WwrzPlanApplyDao wwrzPlanApplyDao;
    @Resource
    private WwrzTestPlanDao wwrzTestPlanDao;
    /**
     * 整个流程启动之前的处理，草稿也会调用这里
     * */
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        logger.info("ProcessApplyHandler processStartPreHandle");
        Map<String, Object> changeApply = addOrUpdateApplyInfo(processStartCmd);
        if (changeApply != null) {
            processStartCmd.setBusinessKey(changeApply.get("id").toString());
        }
    }

    /**
     * 任务审批之后的前置处理器id
     * */
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        logger.info("taskPreHandle");
        ProcessNextCmd processNextCmd = (ProcessNextCmd)iExecutionCmd;
        addOrUpdateApplyInfo(processNextCmd);
    }

    /**
     * 驳回场景cmd中没有表单数据
     * */
    private Map<String, Object> addOrUpdateApplyInfo(AbstractExecutionCmd cmd) {
        String formData = cmd.getJsonData();
        if (StringUtils.isBlank(formData)) {
            logger.warn("formData is blank");
            return null;
        }
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return null;
        }
        Map<String, Object> applyObj = JSONObject.parseObject(formDataJson.toJSONString(), Map.class);
        if (applyObj.get("id") == null || StringUtils.isBlank(applyObj.get("id").toString())) {
            wwrzPlanApplyService.add(applyObj);
        } else {
            wwrzPlanApplyService.update(applyObj);
        }
        return applyObj;
    }
    // 流程成功结束之后的处理
    @Override
    public void endHandle(BpmInst bpmInst) {
        if ("SUCCESS_END".equalsIgnoreCase(bpmInst.getStatus())) {
            String applyId = bpmInst.getBusKey();
            JSONObject applyObj = wwrzPlanApplyDao.getJsonObject(applyId);
            String planIds = applyObj.getString("planIds");
            String[] planIdArray = planIds.split(",");
            JSONObject paramJson = new JSONObject();
            paramJson.put("planStatus","ysp");
            for(String planId:planIdArray){
                paramJson.put("planId",planId);
                wwrzTestPlanDao.updatePlanStatus(paramJson);
            }
        }
    }

    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd cmd, BpmInst bpmInst) {
        if("RUNNING".equals(bpmInst.getStatus())){
        }
        return null;
    }
}
