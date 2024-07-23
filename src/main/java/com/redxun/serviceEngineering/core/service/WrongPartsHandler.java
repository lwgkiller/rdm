package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmZhgl.core.dao.JsmmDao;
import com.redxun.rdmZhgl.core.service.JsmmService;
import com.redxun.serviceEngineering.core.dao.WrongPartsDao;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class WrongPartsHandler
        implements ProcessStartPreHandler, TaskPreHandler {
    private Logger logger = LoggerFactory.getLogger(WrongPartsHandler.class);
    @Autowired
    private WrongPartsService wrongPartsService;
    @Autowired
    private WrongPartsDao wrongPartsDao;

    //..
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        JSONObject formDataJson = processStartCmd.getJsonDataObject();
        formDataJson.put("INST_ID_", processStartCmd.getBpmInstId());
        processStartCmd.setJsonData(formDataJson.toJSONString());
        String cjzgId = createOrUpdateCjzgByFormData(processStartCmd);
        if (StringUtils.isNotBlank(cjzgId)) {
            processStartCmd.setBusinessKey(cjzgId);
            //@lwgkiller:一个流程事务中的cmd.getJsonData是单例的，如果不想从数据库取数据，就必须保证过程中单例的任何改变都要写回单例中
            formDataJson.put("id", cjzgId);
            processStartCmd.setJsonData(formDataJson.toJSONString());
        }
    }

    //..
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd) iExecutionCmd;
        createOrUpdateCjzgByFormData(processNextCmd);
    }

    //..
    private String createOrUpdateCjzgByFormData(AbstractExecutionCmd cmd) {
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
            wrongPartsService.createCjzg(formDataJson);
        } else {
            wrongPartsService.updateCjzg(formDataJson);
        }
        return formDataJson.getString("id");
    }
}
