package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.core.entity.*;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.serviceEngineering.core.dao.FullLifeCycleCostDao;
import com.redxun.serviceEngineering.core.dao.MaintenanceManualDemandDao;
import com.redxun.serviceEngineering.core.dao.MaintenanceManualfileDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class FullLifeCycleCostHandler
        implements ProcessStartPreHandler, TaskPreHandler , ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(FullLifeCycleCostHandler.class);
    @Resource
    private FullLifeCycleCostService fullLifeCycleCostService;
    @Autowired
    private FullLifeCycleCostDao fullLifeCycleCostDao;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;

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
    }

    //..任务审批前置处理器,只有第一个编辑节点需要，因为要更新业务单据的
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd) iExecutionCmd;
        this.createOrUpdateBusinessByFormData(processNextCmd);
    }

    @Override
    public void endHandle(BpmInst bpmInst){
        logger.info("rdm send Ding Ding EndHandler");
        String id = bpmInst.getBusKey();
        JSONObject data = fullLifeCycleCostDao.queryDetailById(id);
        String userId = data.getString("CREATE_BY_");
        sendDingDing(data,userId);
    }

    //..
    public void sendDingDing(JSONObject jsonObject, String userIds) {
        JSONObject noticeObj = new JSONObject();
        String content = StringUtil.format("【全生命周期成本清单签审】设计型号:${designModel}," +
                "销售型号:${salesModel},物料编码:${salesModel}，流程成功结束，请登录系统查看！", jsonObject);
        content += XcmgProjectUtil.getNowLocalDateStr(DateUtil.DATE_FORMAT_FULL);
        noticeObj.put("content", content);
        sendDDNoticeManager.sendNoticeForCommon(userIds, noticeObj);
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
            fullLifeCycleCostService.createBusiness(formDataJson);
        } else {
            fullLifeCycleCostService.updateBusiness(formDataJson);
        }
        return formDataJson.getString("id");
    }
}
