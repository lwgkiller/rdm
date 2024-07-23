package com.redxun.xcmgProjectManager.core.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.*;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.core.util.DateUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectScoreDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Service
public class XcmgProjectHandler
    implements ProcessStartPreHandler, ProcessStartAfterHandler, TaskPreHandler, TaskAfterHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(XcmgProjectHandler.class);

    @Resource
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Resource
    private XcmgProjectFileUploadManager xcmgProjectFileUploadManager;
    @Autowired
    private XcmgProjectScoreDao xcmgProjectScoreDao;
    @Autowired
    private XcmgProjectDao xcmgProjectDao;

    // 整个流程启动之前的处理，草稿也会调用这里
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        logger.info("XcmgProjectHandler processStartPreHandle");
        JSONObject xcmgProject = createOrUpdateProjectByFormData(processStartCmd);
        if (xcmgProject != null) {
            processStartCmd.setBusinessKey(xcmgProject.getString("projectId"));
        }
    }

    // 第一个任务创建之后流程的后置处理器
    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd processStartCmd,
        BpmInst bpmInst) {
        logger.info("processStartAfterHandle");
        JSONObject xcmgProject = xcmgProjectManager.getXcmgProject(processStartCmd.getBusinessKey());
        if (StringUtils.isBlank(xcmgProject.getString("INST_ID_"))) {
            xcmgProject.put("instId", bpmInst.getInstId());
            xcmgProjectDao.updateProjectBaseInfoInstId(xcmgProject);
        }
        // 创建以项目Id为名称的文件夹，用于归档本项目的文件
        Map<String, String> fileInfo = new HashMap<>();
        fileInfo.put("fileName", xcmgProject.getString("projectName"));
        fileInfo.put("id", xcmgProject.getString("projectId"));
        fileInfo.put("pid", "-1");
        fileInfo.put("isFolder", "1");
        fileInfo.put("projectId", xcmgProject.getString("projectId"));
        fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        fileInfo.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        fileInfo.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        xcmgProjectFileUploadManager.createFolder(fileInfo, true);
        return xcmgProject.getString("projectId");
    }

    // 任务审批之后的前置处理器
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        logger.info("taskPreHandle");
        ProcessNextCmd processNextCmd = (ProcessNextCmd)iExecutionCmd;
        createOrUpdateProjectByFormData(processNextCmd);
    }

    // 任务处理最后的后置处理器
    @Override
    public void taskAfterHandle(IExecutionCmd iExecutionCmd, String nodeId, String busKey) {
        logger.info("taskAfterHandle");
    }

    // 流程结束之后的处理
    @Override
    public void endHandle(BpmInst bpmInst) {
        logger.info("ProcessEndHandler");
        if ("SUCCESS_END".equalsIgnoreCase(bpmInst.getStatus())) {
            IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
            String formData = cmd.getJsonData();
            JSONObject formDataJson = JSONObject.parseObject(formData);
            String busKey = bpmInst.getBusKey();
            // 更新基本信息表
            String currentUtcTime = XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss");
            Map<String, Object> params = new HashMap<>();
            params.put("knotTime", currentUtcTime);
            params.put("currentStageId", null);
            params.put("currentStageNo", null);
            params.put("projectId", busKey);
            xcmgProjectOtherDao.updateProjectFinish(params);
            // 刷新项目计划表
            String lastStageId = formDataJson.getString("currentStageId");
            String currentLocalTime = XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd");
            currentUtcTime = DateUtil.formatDate(
                DateUtil.addHour(DateUtil.parseDate(currentLocalTime, "yyyy-MM-dd"), -8), "yyyy-MM-dd HH:mm:ss");
            if (StringUtils.isBlank(lastStageId)) {
                logger.error("项目最后阶段ID为空，projectId =" + busKey);
                JSONObject xcmgProject = xcmgProjectDao.queryProjectById(busKey);
                if (xcmgProject != null) {
                    lastStageId = xcmgProject.getString("currentStageId");
                }
                if (StringUtils.isBlank(lastStageId)) {
                    return;
                }
            }
            params.clear();
            params.put("actualEndTime", currentUtcTime);
            params.put("stageId", lastStageId);
            params.put("projectId", busKey);
            xcmgProjectOtherDao.updateProjectPlanTime(params);
            // 计算各个阶段的得分
            xcmgProjectManager.processProjectScore(formDataJson, true);
            // 计算每个成员最终的角色系数并写入成员表
            Map<String, Object> paramUpdateRatio = new HashMap<>();
            paramUpdateRatio.put("projectId", busKey);
            List<JSONObject> ratioData = xcmgProjectScoreDao.queryMemFinalRatio(paramUpdateRatio);
            if (ratioData != null && !ratioData.isEmpty()) {
                paramUpdateRatio.clear();
                paramUpdateRatio.put("dataList", ratioData);
                xcmgProjectScoreDao.updateMemFinalRatio(paramUpdateRatio);
            }
        }
    }

    // 驳回场景cmd中没有表单数据
    private JSONObject createOrUpdateProjectByFormData(AbstractExecutionCmd cmd) {
        String formData = cmd.getJsonData();
        if (StringUtils.isBlank(formData)) {
            logger.warn("formData is blank");
            return null;
        }
        JSONObject xcmgProject = JSONObject.parseObject(formData);
        if (xcmgProject.isEmpty()) {
            logger.warn("formData is blank");
            return null;
        }
        if (StringUtils.isBlank(xcmgProject.getString("projectId"))) {
            xcmgProjectManager.create(xcmgProject);
        } else {
            xcmgProjectManager.update(xcmgProject);
        }
        return xcmgProject;
    }
}
