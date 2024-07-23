package com.redxun.bjjszc.core.service;

import com.redxun.bjjszc.core.dao.JsxxDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
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
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;

@Service
public class JsxxHandler
    implements ProcessStartPreHandler, ProcessStartAfterHandler, TaskPreHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(JsxxHandler.class);
    @Autowired
    private JsxxService jsxxService;
    @Autowired
    private JsxxDao jsxxDao;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;

    // 整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        String jsxxId = createOrUpdateJsxxByFormData(processStartCmd);
        if (StringUtils.isNotBlank(jsxxId)) {
            processStartCmd.setBusinessKey(jsxxId);
        }
    }

    // 第一个任务创建之后流程的后置处理器
    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd processStartCmd,
        BpmInst bpmInst) {
        String jsxxId = processStartCmd.getBusinessKey();

        String applyNumber =
            "bjkfjs-" + XcmgProjectUtil.getNowLocalDateStr("yyyyMMddHHmmss") + (int)(Math.random() * 100);
        JSONObject param = new JSONObject();
        param.put("applyNumber", applyNumber);
        param.put("jsxxId", jsxxId);

        jsxxDao.updateApplyNumber(param);
        return jsxxId;
    }

    // 任务审批之后的前置处理器
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd)iExecutionCmd;
        createOrUpdateJsxxByFormData(processNextCmd);
    }

    // 流程成功结束之后的处理
    @Override
    public void endHandle(BpmInst bpmInst) {
        if ("SUCCESS_END".equalsIgnoreCase(bpmInst.getStatus())) {
            String jsxxId = bpmInst.getBusKey();
            JSONObject noticeObj = new JSONObject();
            JSONObject detail = jsxxService.getJsxxDetail(jsxxId);
            String userIds = detail.getString("CREATE_BY_");
            noticeObj.put("content", "备件开发技术清单流程已结束，请登录RDM查看。");
            sendDDNoticeManager.sendNoticeForCommon(userIds, noticeObj);
        }
    }

    // 驳回场景cmd中没有表单数据
    private String createOrUpdateJsxxByFormData(AbstractExecutionCmd cmd) {
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
        if (StringUtils.isBlank(formDataJson.getString("jsxxId"))) {
            jsxxService.createJsxx(formDataJson);
        } else {
            jsxxService.updateJsxx(formDataJson);
        }
        return formDataJson.getString("jsxxId");
    }
}
