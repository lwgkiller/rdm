package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

// 项目流程节点的触发事件
@Service
public class SchematicApplyScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(SchematicApplyScript.class);

    @Autowired
    private CommonInfoDao commonInfoDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;

    // 责任人
    public Collection<TaskExecutor> getZRR() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String zrrId = formDataJson.getString("zrrId");
        if (StringUtils.isNotBlank(zrrId)) {
            String zrrName = formDataJson.getString("zrrName");
            users.add(new TaskExecutor(zrrId, zrrName));
        }
        return users;
    }

    // 申请人所长
    public Collection<TaskExecutor> getCreatorLeader() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String CREATE_BY_ = formDataJson.getString("CREATE_BY_");
        List<JSONObject> deptResps = commonInfoDao.queryDeptRespByUserId(CREATE_BY_);
        if (deptResps != null && !deptResps.isEmpty()) {
            for (JSONObject depRespMan : deptResps) {
                users.add(new TaskExecutor(depRespMan.getString("USER_ID_"), depRespMan.getString("FULLNAME_")));
            }
        }
        return users;
    }

    public Collection<TaskExecutor> getFwLeader() {
        List<TaskExecutor> users = new ArrayList<>();
        List<Map<String, String>> depRespMans = rdmZhglUtil.queryFwgcs();
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }

    public boolean isFWS(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        if (StringUtils.isBlank(formData)) {
            logger.warn("formData is blank");
            return false;
        }
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return false;
        }
        String CREATE_BY_ = formDataJson.getString("CREATE_BY_");
        Map<String, Object> params = new HashMap<>();
        params.put("userIds", Arrays.asList(CREATE_BY_));
        List<JSONObject> deptInfos = commonInfoDao.getDeptInfoByUserIds(params);
        if (deptInfos == null || deptInfos.isEmpty()) {
            return true;
        }
        String deptName = deptInfos.get(0).getString("deptName");
        if (RdmConst.FWGCS_NAME.equalsIgnoreCase(deptName)) {
            return true;
        }
        return false;
    }

    public boolean noFWS(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        if (StringUtils.isBlank(formData)) {
            logger.warn("formData is blank");
            return false;
        }
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String CREATE_BY_ = formDataJson.getString("CREATE_BY_");
        Map<String, Object> params = new HashMap<>();
        params.put("userIds", Arrays.asList(CREATE_BY_));
        List<JSONObject> deptInfos = commonInfoDao.getDeptInfoByUserIds(params);
        if (deptInfos == null || deptInfos.isEmpty()) {
            return false;
        }
        String deptName = deptInfos.get(0).getString("deptName");
        if (RdmConst.FWGCS_NAME.equalsIgnoreCase(deptName)) {
            return false;
        }
        return true;
    }
}
