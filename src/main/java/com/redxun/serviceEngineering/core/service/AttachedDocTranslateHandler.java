/**
 * Created by zhangwentao on 2022-3-15.
 */
package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.manager.CommonBpmMediator;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AttachedDocTranslateHandler implements ProcessStartPreHandler, TaskPreHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(AttachedDocTranslateHandler.class);
    @Autowired
    private AttachedDocTranslateManager attachedDocTranslateManager;
    @Autowired
    private CommonBpmMediator commonBpmMediator;

    //..整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        String formData = processStartCmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        formDataJson.put("INST_ID_", processStartCmd.getBpmInstId());
        processStartCmd.setJsonData(formDataJson.toJSONString());
        String businessId = createOrUpdateBusinessByFormData(processStartCmd);
        if (StringUtils.isNotBlank(businessId)) {
            processStartCmd.setBusinessKey(businessId);
        }
    }

    //..任务审批前置处理器,只有第一个编辑节点需要，因为要更新业务单据
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd) iExecutionCmd;
        createOrUpdateBusinessByFormData(processNextCmd);
    }

    //..
    private String createOrUpdateBusinessByFormData(AbstractExecutionCmd cmd) {
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
        //如果是新增的情况
        if (StringUtils.isBlank(formDataJson.getString("id"))) {
            attachedDocTranslateManager.createBusiness(formDataJson);
        } else {
            attachedDocTranslateManager.updateBusiness(formDataJson);
        }
        return formDataJson.getString("id");
    }

    //..这里调用中介者做各种回调处理
    @Override
    public void endHandle(BpmInst bpmInst) {
        try {
            JSONObject attachedDocTranslate = attachedDocTranslateManager.getDetail(bpmInst.getBusKey());
            if (StringUtil.isNotEmpty(attachedDocTranslate.getString("sourceBpmSolKey"))) {
                switch (attachedDocTranslate.getString("sourceBpmSolKey")) {
                    case "PresaleDocumentApply":
                        commonBpmMediator.doTheFinalCallBack(attachedDocTranslate, "attachedDocTranslate", "PresaleDocumentApply");
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            ProcessHandleHelper.getProcessMessage().addErrorMsg(e.getMessage());
        }
    }
}
