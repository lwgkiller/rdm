package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SparepartsDataCollectHandler implements ProcessStartPreHandler, TaskPreHandler {
    private Logger logger = LoggerFactory.getLogger(SparepartsDataCollectHandler.class);
    @Autowired
    private SparepartsDataCollectService sparepartsDataCollectService;

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
        //@lwgkiller:阿诺多罗，下面为增加环境变量的作用就是模拟在线表单bo模型的相关属性值的配置
        //当前是用于在流程方案标题栏里面能够获取四大常规变量以外的变量-值，这些值在此处让它们绑定表单
        JSONArray jsonArray = new JSONArray();
        JSONObject designModel = new JSONObject();
        designModel.put("key", "designModel");
        designModel.put("val", formDataJson.getString("designModel"));
        JSONObject materialCode = new JSONObject();
        materialCode.put("key", "materialCode");
        materialCode.put("val", formDataJson.getString("materialCode"));
        jsonArray.add(designModel);
        jsonArray.add(materialCode);
        processStartCmd.getJsonDataObject().put("vars", jsonArray);
    }

    //..任务审批前置处理器,只有第一个编辑节点需要，因为要更新业务单据的
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
            sparepartsDataCollectService.createBusiness(formDataJson);
        } else {
            sparepartsDataCollectService.updateBusiness(formDataJson);
        }
        return formDataJson.getString("id");
    }

}
