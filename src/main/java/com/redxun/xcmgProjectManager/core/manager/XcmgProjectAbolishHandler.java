package com.redxun.xcmgProjectManager.core.manager;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectAbolishDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * @author zz
 */
@Service
public class XcmgProjectAbolishHandler implements ProcessStartPreHandler, TaskPreHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(XcmgProjectAbolishHandler.class);
    @Autowired
    private XcmgProjectAbolishManager xcmgProjectAbolishManager;
    @Resource
    private XcmgProjectAbolishDao xcmgProjectAbolishDao;
    @Resource
    BpmInstManager bpmInstManager;
    @Resource
    XcmgProjectManager xcmgProjectManager;

    /**
     * 整个流程启动之前的处理，草稿也会调用这里
     */
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        logger.info("ProcessApplyHandler processStartPreHandle");
        Map<String, Object> changeApply = addOrUpdateApplyInfo(processStartCmd);
        if (changeApply != null) {
            processStartCmd.setBusinessKey(changeApply.get("id").toString());
        }
    }

    /**
     * 任务审批之后的前置处理器id
     */
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        logger.info("taskPreHandle");
        ProcessNextCmd processNextCmd = (ProcessNextCmd)iExecutionCmd;
        addOrUpdateApplyInfo(processNextCmd);
    }

    /**
     * 驳回场景cmd中没有表单数据
     */
    private Map<String, Object> addOrUpdateApplyInfo(AbstractExecutionCmd cmd) {
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
        Map<String, Object> applyObj = JSONObject.parseObject(formDataJson.toJSONString(), Map.class);
        if (applyObj.get("id") == null || StringUtils.isBlank(applyObj.get("id").toString())) {
            xcmgProjectAbolishManager.add(applyObj);
        } else {
            xcmgProjectAbolishManager.update(applyObj);
        }
        return applyObj;
    }

    // 流程成功结束之后的处理
    @Override
    public void endHandle(BpmInst bpmInst) {
        if ("SUCCESS_END".equalsIgnoreCase(bpmInst.getStatus())) {
            String id = bpmInst.getBusKey();
            Map<String, Object> abolishApplyObj = null;
            Map<String, Object> params = new HashMap<>(16);
            params.put("id", id);
            abolishApplyObj = xcmgProjectAbolishDao.getObject(params);
            JSONObject applyObj = new JSONObject();
            applyObj = XcmgProjectUtil.convertMap2JsonObject(abolishApplyObj);
            String projectId = applyObj.getString("projectId");
            if (StringUtils.isNotBlank(projectId)) {
                JSONObject xcmgProject = xcmgProjectManager.getXcmgProject(projectId);
                String instId = xcmgProject.getString("INST_ID_");
                if (StringUtils.isNotBlank(instId)) {
                    bpmInstManager.doDiscardProcessInstance(instId);
                }
            }

        }
    }
}
