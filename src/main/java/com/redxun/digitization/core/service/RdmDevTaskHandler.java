package com.redxun.digitization.core.service;

import javax.annotation.Resource;

import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.digitization.core.dao.RdmDevTaskDao;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmZhgl.core.dao.ZlglmkDao;
import com.redxun.rdmZhgl.core.dao.ZljsjdsDao;
import com.redxun.rdmZhgl.core.service.ZljsjdsService;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class RdmDevTaskHandler implements ProcessStartPreHandler, TaskPreHandler, ProcessStartAfterHandler {
    private Logger logger = LoggerFactory.getLogger(RdmDevTaskHandler.class);
    @Autowired
    private RdmDevTaskService rdmDevTaskService;
    @Autowired
    private RdmDevTaskDao rdmDevTaskDao;

    // 整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        String applyId = createOrUpdateDevTask(processStartCmd);
        if (StringUtils.isNotBlank(applyId)) {
            processStartCmd.setBusinessKey(applyId);
        }
    }

    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd processStartCmd,
                                          BpmInst bpmInst) {
        String devId = processStartCmd.getBusinessKey();
        String applyNum = DateFormatUtil.format(new Date(),"yyyyMMddHHmmss");
        Map<String, Object> param = new HashMap<>();
        param.put("applyId", devId);
        param.put("applyNum", applyNum);
        rdmDevTaskDao.updateRdmDevApplyNum(param);
        return devId;
    }

    // 任务审批之后的前置处理器
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd)iExecutionCmd;
        createOrUpdateDevTask(processNextCmd);
    }

    // 驳回场景cmd中没有表单数据
    private String createOrUpdateDevTask(AbstractExecutionCmd cmd) {
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
        if (StringUtils.isBlank(formDataJson.getString("applyId"))) {
            rdmDevTaskService.createDevInfo(formDataJson);
        } else {
            rdmDevTaskService.updateDevInfo(formDataJson);
        }
        return formDataJson.getString("applyId");
    }
}
