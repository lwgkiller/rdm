package com.redxun.serviceEngineering.core.service;

import java.util.HashMap;

import java.util.Map;

import com.redxun.serviceEngineering.core.dao.ExternalTranslationDao;
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
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;

import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * 外部翻译申请
 * @author mh
 * @date 2022/4/22 10:40
 */
@Service
public class ExternalTranslationHandler
    implements ProcessStartPreHandler, ProcessStartAfterHandler, TaskPreHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(ExternalTranslationHandler.class);

    @Autowired
    private ExternalTranslationManager externalTranslationManager;
    @Autowired
    private ExternalTranslationDao externalTranslationDao;


    // 整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        String applyId = createOrUpdateByFormData(processStartCmd);
        if (StringUtils.isNotBlank(applyId)) {
            processStartCmd.setBusinessKey(applyId);
        }
    }

    // 第一个任务创建之后流程的后置处理器（进行编号的生成和更新） 类别-基地-时间-流水
    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd processStartCmd,
                                          BpmInst bpmInst) {
        String applyId = processStartCmd.getBusinessKey();

        String applyNumber =
                "WFFY" +"-"+ XcmgProjectUtil.getNowLocalDateStr("yyyyMMdd") +"-"+ (int)(Math.random() * 100);
        JSONObject param = new JSONObject();
        param.put("applyNumber", applyNumber);
        param.put("id", applyId);
        externalTranslationDao.updateApplyNumber(param);
        return applyId;
    }




    // 任务审批之后的前置处理器
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd)iExecutionCmd;
        createOrUpdateByFormData(processNextCmd);
    }

    // 流程成功结束之后的处理  【暂无】
    @Override
    public void endHandle(BpmInst bpmInst) {

    }



    // 驳回场景cmd中没有表单数据
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
            externalTranslationManager.createApply(formDataJson);
        } else {
            externalTranslationManager.updateApply(formDataJson);
        }
        return formDataJson.getString("id");
    }
}
