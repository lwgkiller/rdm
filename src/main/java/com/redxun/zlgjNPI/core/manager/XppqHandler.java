package com.redxun.zlgjNPI.core.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.*;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.zlgjNPI.core.dao.XppqDao;

@Service
public class XppqHandler implements ProcessStartPreHandler, TaskPreHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(XppqHandler.class);
    @Autowired
    private XppqService xppqService;
    @Autowired
    private XppqDao xppqDao;
    @Resource
    private SendDDNoticeManager sendDDNoticeManager;

    // 整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        String wtId = createOrUpdateZlgjByFormData(processStartCmd);
        if (StringUtils.isNotBlank(wtId)) {
            processStartCmd.setBusinessKey(wtId);
        }
    }

    // 任务审批之后的前置处理器
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd)iExecutionCmd;
        createOrUpdateZlgjByFormData(processNextCmd);
    }

    private String createOrUpdateZlgjByFormData(AbstractExecutionCmd cmd) {
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
        if (StringUtils.isBlank(formDataJson.getString("wtId"))) {
            xppqService.createZlgj(formDataJson);
        } else {
            xppqService.updateZlgj(formDataJson);
        }
        return formDataJson.getString("wtId");
    }

    @Override
    public void endHandle(BpmInst bpmInst) {
        if ("SUCCESS_END".equalsIgnoreCase(bpmInst.getStatus())) {
            IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
            String formData = cmd.getJsonData();
            JSONObject formDataJson = JSONObject.parseObject(formData);
            // 设置所有执行为归档
            Map<String, Object> param = new HashMap<>();
            param.put("wtId", formDataJson.getString("wtId"));
            xppqDao.updateProcessStatus(param);
            // 发送通知
            String creator = formDataJson.getString("creator");
            JSONObject noticeObj = new JSONObject();
            StringBuilder stringBuilder = new StringBuilder("【新品剖切验证流程结束通知】");
            stringBuilder.append(creator).append("创建的新品剖切验证流程已结束，请知！");
            noticeObj.put("content", stringBuilder.toString());
            // 查找要通知的人
            param.clear();
            param.put("groupName", "检验技术人员");
            List<JSONObject> users = xppqDao.queryUserByRoleName(param);
            String userIds = "";
            for (JSONObject oneUser : users) {
                userIds += oneUser.getString("USER_ID_") + ",";
            }
            if (StringUtils.isNotBlank(userIds)) {
                userIds = userIds.substring(0, userIds.length() - 1);
            }
            sendDDNoticeManager.sendNoticeForCommon(userIds, noticeObj);
        }
    }
}
