package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmZhgl.core.service.PatentInterpretationService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.sys.core.manager.SysSeqIdManager;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DecorationManualTopicPSHandler
        implements ProcessStartPreHandler, ProcessStartAfterHandler, TaskPreHandler {
    private Logger logger = LoggerFactory.getLogger(DecorationManualTopicPSHandler.class);
    @Autowired
    private DecorationManualTopicPSService decorationManualTopicPSService;
    @Autowired
    private SysSeqIdManager sysSeqIdManager;

    //..整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        JSONObject formDataJson = JSONObject.parseObject(processStartCmd.getJsonData());
        formDataJson.put("INST_ID_", processStartCmd.getBpmInstId());
        processStartCmd.setJsonData(formDataJson.toJSONString());
        String businessId = createOrUpdateBusinessByFormData(processStartCmd);
        if (StringUtils.isNotBlank(businessId)) {
            processStartCmd.setBusinessKey(businessId);
            //@lwgkiller:映射下面的话，一个流程事务中的cmd.getJsonData是单例的，如果不想从数据库取数据，就必须保证过程中单例
            //的任何改变都要写回单例中，这个bug到现在才发现，以前是因为没出现过不保存草稿直接提交的场景用这种写法
            formDataJson.put("id", businessId);
            processStartCmd.setJsonData(formDataJson.toJSONString());
        }
    }

    //..
    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd cmd, BpmInst bpmInst) {
        JSONObject formDataJson = JSONObject.parseObject(cmd.getJsonData());
        //@lwgkiller:在这设个启动后置处理器，只是重新回填下INST_ID_，防止草稿直接启动没有INST_ID_，这里并没有进行update操作，但是会通过taskCreateScript的
        //update操作体现出来，也就是说一个流程事务中的cmd.getJsonData是单例的。这样processStartPreHandle里的update操作可以省略了
        formDataJson.put("INST_ID_", cmd.getBpmInstId());
        if (StringUtil.isEmpty(formDataJson.getString("businessNo"))) {
            formDataJson.put("businessNo", sysSeqIdManager.genSequenceNo("decorationManualTopicPS", "1"));
        }
        formDataJson.put("applicantId", ContextUtil.getCurrentUserId());
        formDataJson.put("applicant", ContextUtil.getCurrentUser().getFullname());
        cmd.setJsonData(formDataJson.toJSONString());
        return null;
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
            decorationManualTopicPSService.createBusiness(formDataJson);
        } else {
            decorationManualTopicPSService.updateBusiness(formDataJson);
        }
        return formDataJson.getString("id");
    }


}
