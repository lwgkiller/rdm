package com.redxun.serviceEngineering.core.service;

import java.util.*;

import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.rdmCommon.core.util.RdmConst;

import javax.annotation.Resource;

@Service
public class ExportPartsAtlasScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(ExportPartsAtlasScript.class);
    @Autowired
    private ExportPartsAtlasService exportPartsAtlasService;
    @Resource
    CommonInfoDao commonInfoDao;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;

    // 机型制作流程，查询表单中的产品主管
    public Collection<TaskExecutor> getCpzgUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String modelOwnerId = formDataJson.getString("modelOwnerId");
        String modelOwnerName = formDataJson.getString("modelOwnerName");
        users.add(new TaskExecutor(modelOwnerId, modelOwnerName));
        return users;
    }

    // 机型制作流程，任务创建时的事件，用来更改状态
    public void modelMadeTaskCreateScript(Map<String, Object> vars) {
        logger.info("modelMadeTaskCreateScript");
        AbstractExecutionCmd cmd = (AbstractExecutionCmd)vars.get("cmd");
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String matCode = formDataJson.getString("matCode");
        if (StringUtils.isBlank(matCode)) {
            logger.error("机型制作流程中设计物料编码为空！");
            return;
        }
        // 根据当前流程任务节点，判断制作任务应该更改的状态
        String taskName = (String) vars.get("activityId");
        if (StringUtils.isBlank(taskName)) {
            logger.error("任务信息为空！");
            return;
        }
        String status = "";
        if (RdmConst.PARTS_ATLAS_MODEL_MADE_BZ.equalsIgnoreCase(taskName)) {
            status = RdmConst.PARTS_ATLAS_STATUS_YLQ;
        } else if (RdmConst.PARTS_ATLAS_MODEL_MADE_SQ.equalsIgnoreCase(taskName)) {
            status = RdmConst.PARTS_ATLAS_STATUS_JXZZSQ;
        } else if (RdmConst.PARTS_ATLAS_MODEL_MADE_ZZ.equalsIgnoreCase(taskName)) {
            status = RdmConst.PARTS_ATLAS_STATUS_JXZZ;
        }
        if (StringUtils.isBlank(status)) {
            logger.error("机型制作流程中状态为空！");
            return;
        }
        exportPartsAtlasService.updateTaskStatusByModelMade(matCode, status);
    }

    // 产品所负责人
    public Collection<TaskExecutor> getDepRespUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String applyUserId = formDataJson.getString("modelOwnerId");
        if (StringUtils.isBlank(applyUserId)) {
            applyUserId = ContextUtil.getCurrentUserId();
        }
        List<JSONObject> deptResps = commonInfoDao.queryDeptRespByUserId(applyUserId);
        if (deptResps != null && !deptResps.isEmpty()) {
            for (JSONObject depRespMan : deptResps) {
                users.add(new TaskExecutor(depRespMan.getString("USER_ID_"), depRespMan.getString("FULLNAME_")));
            }
        }
        return users;
    }

    // 从表单获取对接工艺负责人
    public Collection<TaskExecutor> getGyfzrUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String userIdStr = formDataJson.getString("gyfzrId");
        String userNameStr = formDataJson.getString("gyfzrName");
        if (StringUtils.isNotEmpty(userIdStr) && StringUtils.isNotEmpty(userNameStr)) {
            String[] userIdIds = userIdStr.split(",");
            String[] userNameNames = userNameStr.split(",");
            for (int i = 0; i < userIdIds.length; i++) {
                TaskExecutor oneUser = new TaskExecutor(userIdIds[i],userNameNames[i]);
                users.add(oneUser);
            }
        }
        return users;
    }

    // 机型制作流程，查询表单中的机型制作人
    public Collection<TaskExecutor> getJxzzrUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String userId = formDataJson.getString("jxzzrId");
        String userName = formDataJson.getString("jxzzrName");
        users.add(new TaskExecutor(userId, userName));
        return users;
    }

    // 获取服务工程部部门领导信息
    public Collection<TaskExecutor> getFwgcRepUser() {
        List<TaskExecutor> users = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("groupName", RdmConst.FWGCS_NAME);
        List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepRespManById(params);
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }



}
