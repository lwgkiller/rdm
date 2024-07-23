package com.redxun.rdmZhgl.core.service;

import java.util.List;
import java.util.Map;

import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.*;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.rdmZhgl.core.dao.YdgztbDao;
import com.redxun.rdmZhgl.core.service.YdgztbManager;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.saweb.util.IdUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * 月度工作提报
 *
 * @mh 2023年5月4日17:22:02
 */

@Service
public class YdgztbHandler
    implements ProcessStartPreHandler, ProcessStartAfterHandler, TaskPreHandler, TaskAfterHandler {
    private Logger logger = LoggerFactory.getLogger(YdgztbHandler.class);
    @Autowired
    private YdgztbManager ydgztbManager;
    @Autowired
    private CommonInfoManager commonInfoManager;
    @Autowired
    private YdgztbDao ydgztbDao;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;

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
        return applyId;
    }

    // 任务审批之后的前置处理器
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd)iExecutionCmd;
        createOrUpdateByFormData(processNextCmd);
    }

    // 任务审批之后的后置处理器
    @Override
    public void taskAfterHandle(IExecutionCmd iExecutionCmd, String nodeId, String busKey) {
        ProcessNextCmd cmd = (ProcessNextCmd)iExecutionCmd;
        String formData = cmd.getJsonData();
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
            ydgztbManager.createYdgztb(formDataJson);
        } else {
            ydgztbManager.updateYdgztb(formDataJson);
        }
        return formDataJson.getString("id");
    }

}
