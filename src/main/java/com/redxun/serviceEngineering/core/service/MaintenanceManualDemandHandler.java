package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.util.StringUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.serviceEngineering.core.dao.MaintenanceManualDemandDao;
import com.redxun.serviceEngineering.core.dao.MaintenanceManualfileDao;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MaintenanceManualDemandHandler
        implements ProcessStartPreHandler, TaskPreHandler {
    private Logger logger = LoggerFactory.getLogger(MaintenanceManualDemandHandler.class);
    @Autowired
    private MaintenanceManualDemandService maintenanceManualDemandService;
    @Autowired
    private MaintenanceManualDemandDao maintenanceManualDemandDao;
    @Autowired
    private MaintenanceManualfileDao maintenanceManualfileDao;
    @Autowired
    private BpmInstManager bpmInstManager;

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
        //todo:@lwgkiller:注意观察，这里为了避免接口自动保存草稿默认生成的发起人脏数据，需要在真正提交动作时重写流程实例的发起人
        BpmInst bpmInst = bpmInstManager.get(processStartCmd.getBpmInstId());
        if (bpmInst != null) {
            bpmInst.setCreateBy(ContextUtil.getCurrentUserId());
            bpmInstManager.updateSkipUpdateTime(bpmInst);
        }
        //按照salesModel，designModel，materialCode匹配手册
        this.manualMatchProcess(processStartCmd);
        //@lwgkiller:阿诺多罗，下面为增加环境变量的作用就是模拟在线表单bo模型的相关属性值的配置
        //当前是用于在流程方案标题栏里面能够获取四大常规变量以外的变量-值，这些值在此处让它们绑定表单 todo:mark!!!
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
        this.createOrUpdateBusinessByFormData(processNextCmd);
    }

    //..
    private void manualMatchProcess(AbstractExecutionCmd cmd) {
        JSONObject jsonObject = cmd.getJsonDataObject();
        if (StringUtil.isNotEmpty(jsonObject.getString("id"))) {
            JSONObject params = new JSONObject();
            params.put("salesModel", jsonObject.getString("salesModel"));
            params.put("designModel", jsonObject.getString("designModel"));
            params.put("materialCode", jsonObject.getString("materialCode"));
            List<JSONObject> manualList = maintenanceManualfileDao.dataListQueryQuick(params);
            List<String> businessIds = new ArrayList<>();
            businessIds.add(jsonObject.getString("id"));
            params.clear();
            params.put("businessIds", businessIds);
            maintenanceManualDemandDao.deleteManualMatch(params);
            for (JSONObject object : manualList) {
                JSONObject manualMatch = new JSONObject();
                manualMatch.put("id", IdUtil.getId());
                manualMatch.put("businessId", jsonObject.getString("id"));
                manualMatch.put("manualFileId", object.getString("id"));
                manualMatch.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                manualMatch.put("CREATE_TIME_", new Date());
                maintenanceManualDemandDao.insertManualMatch(manualMatch);
            }
        }

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
            maintenanceManualDemandService.createBusiness(formDataJson);
        } else {
            maintenanceManualDemandService.updateBusiness(formDataJson);
        }
        return formDataJson.getString("id");
    }
}
