package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONArray;
import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.serviceEngineering.core.dao.ZxdpsDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 操保手册制修订评审申请
 *
 * @mh 2022年7月14日15:41:58
 */
@Service
public class ZxdpsHandler implements ProcessStartPreHandler, ProcessStartAfterHandler, TaskPreHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(ZxdpsHandler.class);
    @Autowired
    private ZxdpsManager zxdpsManager;
    @Autowired
    private ZxdpsDao zxdpsDao;

    // 整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        String applyId = createOrUpdateByFormData(processStartCmd);
        if (StringUtils.isNotBlank(applyId)) {
            processStartCmd.setBusinessKey(applyId);
        }
    }

    // 第一个任务创建之后流程的后置处理器（进行编号的生成和更新） 及更新topic的状态
    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd processStartCmd,
        BpmInst bpmInst) {
        String applyId = processStartCmd.getBusinessKey();
        String applyNumber =
            "EOMPS-" + XcmgProjectUtil.getNowLocalDateStr("yyyyMMddHHmmss") + "-" + (int)(Math.random() * 10);
        JSONObject param = new JSONObject();
        param.put("applyNumber", applyNumber);
        param.put("id", applyId);
        zxdpsDao.updateZxdpsNumber(param);
        String formData = processStartCmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        JSONArray demandArr = formDataJson.getJSONArray("SUB_topicGrid");
        List<String> applyIdList = new ArrayList<>();
        for(Object obj:demandArr) {
            JSONObject topicJson = (JSONObject) obj;
            String topicId = topicJson.getString("jumpId");
            if (StringUtils.isEmpty(topicId)) {
                topicId = topicJson.getString("id");
            }
            applyIdList.add(topicId);
        }
        param.put("status", "RUNNING");
        param.put("ids", applyIdList);
        if (applyIdList.size() == 0) {
            return applyId;
        }
        zxdpsDao.updateTopicStatus(param);
        return applyId;
    }

    // 任务审批之后的前置处理器
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd)iExecutionCmd;
        createOrUpdateByFormData(processNextCmd);
    }

    // 驳回场景cmd中没有表单数据
    private String createOrUpdateByFormData(AbstractExecutionCmd cmd) {
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
            zxdpsManager.createZxdps(formDataJson);
        } else {
            zxdpsManager.updateZxdps(formDataJson);
        }
        return formDataJson.getString("id");
    }

    @Override
    public void endHandle(BpmInst bpmInst) {
        if ("SUCCESS_END".equalsIgnoreCase(bpmInst.getStatus())) {
            IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
            String formData = cmd.getJsonData();
            JSONObject formDataJson = JSONObject.parseObject(formData);
            String applyId = formDataJson.getString("id");
            // 将topic状态改为结束
            // 这里jumpId是待更新topic的主键
            JSONArray demandArr = formDataJson.getJSONArray("SUB_topicGrid");
            List<String> applyIdList = new ArrayList<>();
            for(Object obj:demandArr) {
                JSONObject topicJson = (JSONObject) obj;
                String topicId = topicJson.getString("jumpId");
                applyIdList.add(topicId);
            }
            if (applyIdList.size() == 0) {
                return;
            }
            JSONObject param = new JSONObject();
            param.put("ids", applyIdList);
            param.put("status", "SUCCESS_END");
            zxdpsDao.updateTopicStatus(param);
        }
    }
}
