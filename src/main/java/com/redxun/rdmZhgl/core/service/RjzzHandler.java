package com.redxun.rdmZhgl.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmZhgl.core.dao.RjzzDao;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RjzzHandler implements ProcessStartPreHandler, ProcessStartAfterHandler, TaskPreHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(RjzzHandler.class);
    @Autowired
    private RjzzService rjzzService;
    @Autowired
    private RjzzDao rjzzDao;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;

    // 整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        String jsmmId = createOrUpdateJsmmByFormData(processStartCmd);
        if (StringUtils.isNotBlank(jsmmId)) {
            processStartCmd.setBusinessKey(jsmmId);
        }
    }
    // 第一个任务创建之后流程的后置处理器（进行编号的生成和更新）
    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd processStartCmd,
                                          BpmInst bpmInst) {
        String rjzzId = processStartCmd.getBusinessKey();
        String rjzzNum = rjzzService.toGetRjzzNum();
        Map<String, Object> param = new HashMap<>();
        param.put("rjzzId", rjzzId);
        param.put("rjzzNum", rjzzNum);
        rjzzDao.updateRjzzNum(param);
        return rjzzId;
    }
    // 任务审批之后的前置处理器
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd)iExecutionCmd;
        createOrUpdateJsmmByFormData(processNextCmd);
    }

    // 驳回场景cmd中没有表单数据
    private String createOrUpdateJsmmByFormData(AbstractExecutionCmd cmd) {
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
        if (StringUtils.isBlank(formDataJson.getString("rjzzId"))) {
            rjzzService.createRjzz(formDataJson);
        } else {
            rjzzService.updateRjzz(formDataJson);
        }
        return formDataJson.getString("rjzzId");
    }

    @Override
    public void endHandle(BpmInst bpmInst) {
        if ("SUCCESS_END".equalsIgnoreCase(bpmInst.getStatus())) {
            IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
            String formData = cmd.getJsonData();
            JSONObject formDataJson = JSONObject.parseObject(formData);
            // 给创建人发钉钉通知，提醒查看
            JSONObject noticeObj = new JSONObject();
            noticeObj.put("content", "软著申请已审批通过，请将软著申请表、说明书和源代码发送给专利工程师");
            sendDDNoticeManager.sendNoticeForCommon(formDataJson.getString("CREATE_BY_"), noticeObj);
        }
    }
}
