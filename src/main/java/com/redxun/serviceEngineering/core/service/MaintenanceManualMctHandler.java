package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.bpm.core.manager.BpmTaskManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.serviceEngineering.core.dao.MaintenanceManualDemandDao;
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
public class MaintenanceManualMctHandler
        implements ProcessStartPreHandler, TaskPreHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(MaintenanceManualMctHandler.class);
    @Autowired
    private MaintenanceManualMctService maintenanceManualMctService;
    @Autowired
    private MaintenanceManualDemandDao maintenanceManualDemandDao;
    @Autowired
    private BpmInstManager bpmInstManager;
    @Autowired
    private BpmTaskManager bpmTaskManager;

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
        //当前是用于在流程方案标题栏里面能够获取四大常规变量以外的变量-值，这些值在此处让它们绑定表单 todo:mark！！！
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
    @Override
    public void endHandle(BpmInst bpmInst) {
        try {
            JSONObject manualMct = maintenanceManualMctService.getDetail(bpmInst.getBusKey());
            manualMct.put("businessStatus", "G-close");
            manualMct.put("BconfirmingId", ContextUtil.getCurrentUserId());//取代自动传过来的确认人
            manualMct.put("Bconfirming", ContextUtil.getCurrentUser().getFullname());//取代自动传过来的确认人
            maintenanceManualMctService.updateBusiness(manualMct);
            //1.将MCT自己的匹配明细更新到对应Demand的匹配明细
            JSONObject manualDemand = maintenanceManualDemandDao.queryDetailById(manualMct.getString("demandId"));//Demand主信息
            if (manualDemand != null) {//如果找到了Demand主信息
                JSONObject params = new JSONObject();
                params.put("businessId", manualMct.getString("id"));
                List<JSONObject> manualMatchList = maintenanceManualDemandDao.getManualMatchList(params);//这个方法是共用的，此处找的是MCT的匹配明细
                if (!manualMatchList.isEmpty()) {
                    List<String> businessIds = new ArrayList<>();
                    businessIds.add(manualMct.getString("demandId"));
                    params.clear();
                    params.put("businessIds", businessIds);
                    maintenanceManualDemandDao.deleteManualMatch(params);//删掉Demand原有的匹配明细
                    for (JSONObject jsonObject : manualMatchList) {
                        JSONObject DemandMatch = new JSONObject();
                        DemandMatch.put("id", IdUtil.getId());
                        DemandMatch.put("businessId", manualMct.getString("demandId"));
                        DemandMatch.put("manualFileId", jsonObject.getString("manualFileId"));//此处找的是MCT的匹配明细的manualFileId
                        DemandMatch.put("REF_ID_", jsonObject.getString("REF_ID_"));
                        DemandMatch.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                        DemandMatch.put("CREATE_TIME_", new Date());
                        maintenanceManualDemandDao.insertManualMatch(DemandMatch);//重新新增Demand的匹配明细
                    }
                }
            }
            //2.驱动需求流程继续，根据demandInstid找taskId，将taskId给往下办了
            BpmInst bpmInst2 = bpmInstManager.get(manualMct.getString("demandInstid"));
            if (bpmInst2 != null) {
                List<BpmTask> bpmTaskList = bpmTaskManager.getByInstId(bpmInst2.getInstId());
                for (BpmTask bpmTask : bpmTaskList) {
                    ProcessNextCmd processNextCmd = new ProcessNextCmd();
                    processNextCmd.setTaskId(bpmTask.getId());
                    processNextCmd.setJumpType("AGREE");
                    processNextCmd.setJsonData(manualDemand.toJSONString());
                    processNextCmd.setAgentToUserId("1");
                    bpmTaskManager.doNext(processNextCmd);
                }
            }
        } catch (Exception e) {
            logger.error("Exception in endHandle", e);
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
            maintenanceManualMctService.createBusiness(formDataJson);
        } else {
            maintenanceManualMctService.updateBusiness(formDataJson);
        }
        return formDataJson.getString("id");
    }
}
