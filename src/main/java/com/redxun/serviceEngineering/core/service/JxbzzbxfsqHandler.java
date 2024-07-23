package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.serviceEngineering.core.dao.StandardvalueShipmentnotmadeDao;
import com.redxun.sys.org.manager.OsGroupManager;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JxbzzbxfsqHandler
    implements ProcessStartPreHandler, TaskPreHandler {
    private Logger logger = LoggerFactory.getLogger(JxbzzbxfsqHandler.class);
    @Autowired
    private JxbzzbxfsqService jxbzzbxfsqService;

    // 整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        String jxbzzbxfsqId = createOrUpdateJxbzzbxfsqByFormData(processStartCmd);
        if (StringUtils.isNotBlank(jxbzzbxfsqId)) {
            processStartCmd.setBusinessKey(jxbzzbxfsqId);
        }
    }


    // 任务审批之后的前置处理器
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd)iExecutionCmd;
        createOrUpdateJxbzzbxfsqByFormData(processNextCmd);
    }


    // 驳回场景cmd中没有表单数据
    private String createOrUpdateJxbzzbxfsqByFormData(AbstractExecutionCmd cmd) {
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
            jxbzzbxfsqService.insertJxbzzbxfsq(formDataJson);
        } else {
            jxbzzbxfsqService.updateJxbzzbxfsq(formDataJson);
        }
        return formDataJson.getString("id");
    }

}
