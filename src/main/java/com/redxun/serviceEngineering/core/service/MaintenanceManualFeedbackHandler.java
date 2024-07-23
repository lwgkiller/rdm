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
public class MaintenanceManualFeedbackHandler
        implements ProcessStartPreHandler, TaskPreHandler, ProcessStartAfterHandler {
    private Logger logger = LoggerFactory.getLogger(MaintenanceManualFeedbackHandler.class);
    @Autowired
    private MaintenanceManualFeedbackService maintenanceManualFeedbackService;

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

    //..整个流程启动之后的处理，由于此处可能存在草稿直接提交，processStartPreHandle会没有InstId，所以需要在这里再处理
    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd cmd, BpmInst bpmInst) {
        //@lwgkiller:阿诺多罗，千万注意这里的坑，此处processStartCmd.getJsonData()仍然是一个没有id的原始纪录，和processStartPreHandle
        //用的是同一个，并没有重新读取，因此所有在processStartPreHandle下处理增加记录自动产生的字段并没有重新读取带到这里来，这就造成还会被当成
        //新纪录重复创建。这里不得不用getDetail方法重新读取一下。
        JSONObject jsonObject = maintenanceManualFeedbackService.getDetail(cmd.getBusinessKey());
        jsonObject.put("INST_ID_", bpmInst.getInstId());
        cmd.setJsonData(jsonObject.toJSONString());
        return createOrUpdateBusinessByFormData(cmd);
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
            maintenanceManualFeedbackService.createBusiness(formDataJson);
        } else {
            maintenanceManualFeedbackService.updateBusiness(formDataJson);
        }
        return formDataJson.getString("id");
    }


}
