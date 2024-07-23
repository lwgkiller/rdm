package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.serviceEngineering.core.dao.SchematicDownloadApplyDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SchematicDownloadApplyHandler
    implements ProcessStartPreHandler, ProcessStartAfterHandler, TaskPreHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(SchematicDownloadApplyHandler.class);
    @Autowired
    private SchematicDownloadApplyService schematicDownloadApplyService;
    @Autowired
    private SchematicDownloadApplyDao schematicDownloadApplyDao;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;

    public void endHandle(BpmInst bpmInst) {
        if ("SUCCESS_END".equalsIgnoreCase(bpmInst.getStatus())) {
            IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
            String formData = cmd.getJsonData();
            JSONObject formDataJson = JSONObject.parseObject(formData);
            // 给创建人发钉钉通知，提醒查看
            JSONObject noticeObj = new JSONObject();
            noticeObj.put("content", "您申请的原理图下载流程已审批完成，请前往申请表单下载，申请编号：" + formDataJson.getString("applyNumber"));
            sendDDNoticeManager.sendNoticeForCommon(formDataJson.getString("CREATE_BY_"), noticeObj);
        }
    }

    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd cmd, BpmInst bpmInst) {
        String applyId = cmd.getBusinessKey();
        String applyNumber =
            "yltxz-" + XcmgProjectUtil.getNowLocalDateStr("yyyyMMddHHmmssSSS") + (int)(Math.random() * 100);
        JSONObject param = new JSONObject();
        param.put("applyNumber", applyNumber);
        param.put("id", applyId);
        schematicDownloadApplyDao.updateApplyNumber(param);
        return applyId;
    }

    @Override
    public void processStartPreHandle(ProcessStartCmd cmd) {
        String applyId = createOrUpdateByFormData(cmd);
        if (StringUtils.isNotBlank(applyId)) {
            cmd.setBusinessKey(applyId);
        }
    }

    @Override
    public void taskPreHandle(IExecutionCmd cmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd)cmd;
        createOrUpdateByFormData(processNextCmd);

    }

    // ..驳回场景cmd中没有表单数据
    private String createOrUpdateByFormData(AbstractExecutionCmd cmd) {
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
            schematicDownloadApplyService.createApply(formDataJson);
        } else {
            schematicDownloadApplyService.updateApply(formDataJson);
        }
        return formDataJson.getString("id");
    }
}
