package com.redxun.productDataManagement.core.manager;

import com.redxun.productDataManagement.core.dao.XpbgsqDao;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.serviceEngineering.core.dao.ScxxfkDao;
import com.redxun.serviceEngineering.core.service.ScxxfkManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * 型谱变更申请
 *
 * @mh 2023年3月14日14:16:59
 */

@Service
public class XpbgsqHandler implements ProcessStartPreHandler, ProcessStartAfterHandler, TaskPreHandler {
    private Logger logger = LoggerFactory.getLogger(XpbgsqHandler.class);
    @Autowired
    private XpbgsqManager xpbgsqManager;
    @Autowired
    private XpbgsqDao xpbgsqDao;

    // 整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        String applyId = createOrUpdateByFormData(processStartCmd);
        if (StringUtils.isNotBlank(applyId)) {
            processStartCmd.setBusinessKey(applyId);
        }
    }

    // 第一个任务创建之后流程的后置处理器（进行编号的生成和更新）
    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd processStartCmd,
        BpmInst bpmInst) {
        String applyId = processStartCmd.getBusinessKey();
        String applyNumber =
            "XPBGSQ-" + XcmgProjectUtil.getNowLocalDateStr("yyyyMMddHHmmss") + "-" + (int)(Math.random() * 10);
        JSONObject param = new JSONObject();
        param.put("applyNumber", applyNumber);
        param.put("id", applyId);
        xpbgsqDao.updateXpbgsqNumber(param);
        return applyId;
    }

    // 任务审批之后的前置处理器
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd)iExecutionCmd;
        createOrUpdateByFormData(processNextCmd);
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
            xpbgsqManager.createXpbgsq(formDataJson);
        } else {
            xpbgsqManager.updateXpbgsq(formDataJson);
        }
        return formDataJson.getString("id");
    }
}
