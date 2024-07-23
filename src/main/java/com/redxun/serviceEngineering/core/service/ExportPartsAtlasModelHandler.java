package com.redxun.serviceEngineering.core.service;

import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.materielextend.core.util.CommonUtil;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.serviceEngineering.core.dao.ExportPartsAtlasDao;

@Service
public class ExportPartsAtlasModelHandler
    implements ProcessStartPreHandler, ProcessStartAfterHandler, TaskPreHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(ExportPartsAtlasModelHandler.class);
    @Autowired
    private ExportPartsAtlasService exportPartsAtlasService;
    @Autowired
    private ExportPartsAtlasDao exportPartsAtlasDao;

    // ..整个流程启动之前的处理
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        String id = createOrUpdateModelMade(processStartCmd);
        if (StringUtils.isNotBlank(id)) {
            processStartCmd.setBusinessKey(id);
        }
    }

    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd cmd, BpmInst bpmInst) {
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
        if (StringUtils.isBlank(formDataJson.getString("id"))) {
            formDataJson.put("id", cmd.getBusinessKey());
        }
        // 编号提交后自动生成
        String modelMadeNum =
            "JXZZ-" + DateFormatUtil.getNowUTCDateStr("yyyyMMddHHmmssSSS") + CommonUtil.genereate3Random();
        formDataJson.put("modelMadeNum", modelMadeNum);
        exportPartsAtlasDao.updateModelMadeNum(formDataJson);
        return formDataJson.getString("id");
    }

    // ..任务审批前置处理器
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd)iExecutionCmd;
        createOrUpdateModelMade(processNextCmd);
    }

    // 保存或更新表单
    private String createOrUpdateModelMade(AbstractExecutionCmd cmd) {
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
        // 如果是新增的情况
        if (StringUtils.isBlank(formDataJson.getString("id"))) {
            exportPartsAtlasService.createModelMade(formDataJson);
        } else {
            exportPartsAtlasService.updateModelMade(formDataJson);
        }
        return formDataJson.getString("id");
    }

    // 机型制作流程，流程结束时的事件，用来更改状态为“实例制作中”
    @Override
    public void endHandle(BpmInst bpmInst) {
        if ("SUCCESS_END".equalsIgnoreCase(bpmInst.getStatus())) {
            IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
            String formData = cmd.getJsonData();
            JSONObject formDataJson = JSONObject.parseObject(formData);
            String matCode = formDataJson.getString("matCode");
            if (StringUtils.isBlank(matCode)) {
                logger.error("机型制作流程中设计物料编码为空！");
                return;
            }
            exportPartsAtlasService.updateTaskStatusByModelMade(matCode, RdmConst.PARTS_ATLAS_STATUS_SLZZ);
        }
    }

}
