package com.redxun.zlgjNPI.core.manager;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.core.entity.*;
import com.redxun.core.util.DateUtil;
import com.redxun.zlgjNPI.core.dao.ZlgjApplyForExtensionDao;
import com.redxun.zlgjNPI.core.dao.ZlgjWTDao;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ZlgjApplyForExtensionHandler
        implements ProcessStartPreHandler, TaskPreHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(ZlgjApplyForExtensionHandler.class);
    @Autowired
    private ZlgjApplyForExtensionService zlgjApplyForExtensionService;
    @Autowired
    private ZlgjApplyForExtensionDao zlgjApplyForExtensionDao;
    @Autowired
    private ZlgjWTDao zlgjWTDao;

    //..整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        String businessId = createOrUpdateCqsmByFormData(processStartCmd);
        if (StringUtils.isNotBlank(businessId)) {
            processStartCmd.setBusinessKey(businessId);
        }
    }

    //..任务审批之后的前置处理器
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd) iExecutionCmd;
        createOrUpdateCqsmByFormData(processNextCmd);
    }

    //..流程成功结束之后的处理
    @Override
    public void endHandle(BpmInst bpmInst) {
        if ("SUCCESS_END".equalsIgnoreCase(bpmInst.getStatus())) {
            String businessId = bpmInst.getBusKey();
            JSONObject apply = zlgjApplyForExtensionDao.queryApplyById(businessId);
            JSONObject zlgjDetail = zlgjWTDao.queryZlgjById(apply.getString("wtId"));
            Date zrcpzgEndPlan = null;
            Date zrrEndPlan = null;
            if (apply.getString("extensionNode").equalsIgnoreCase("产品主管/室主任分配")) {
                if (zlgjDetail.containsKey("zrcpzgEndPlan")) {
                    zrcpzgEndPlan = zlgjDetail.getDate("zrcpzgEndPlan");
                    zrcpzgEndPlan = DateUtil.addDay(zrcpzgEndPlan, apply.getIntValue("extensionDays"));
                    zlgjDetail.put("zrcpzgEndPlan", DateUtil.formatDate(zrcpzgEndPlan, DateUtil.DATE_FORMAT_FULL));
                    zlgjWTDao.updateZrcpzgStart(zlgjDetail);
                }
            } else if (apply.getString("extensionNode").equalsIgnoreCase("责任部门审核分析")) {
                if (zlgjDetail.containsKey("zrrEndPlan")) {
                    zrrEndPlan = zlgjDetail.getDate("zrrEndPlan");
                    zrrEndPlan = DateUtil.addDay(zrrEndPlan, apply.getIntValue("extensionDays"));
                    zlgjDetail.put("zrrEndPlan", DateUtil.formatDate(zrrEndPlan, DateUtil.DATE_FORMAT_FULL));
                    zlgjWTDao.updateZrrStart(zlgjDetail);
                }
            }
        }
    }

    //..驳回场景cmd中没有表单数据
    private String createOrUpdateCqsmByFormData(AbstractExecutionCmd cmd) {
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
            zlgjApplyForExtensionService.createApply(formDataJson);
        } else {
            zlgjApplyForExtensionService.updateApply(formDataJson);
        }
        return formDataJson.getString("id");
    }
}
