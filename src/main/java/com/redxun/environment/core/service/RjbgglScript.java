package com.redxun.environment.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.environment.core.dao.RjbgglDao;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

// 项目流程节点的触发事件
@Service
public class RjbgglScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(RjbgglScript.class);

    @Autowired
    private CommonInfoDao commonInfoDao;
    @Autowired
    private RjbgglDao rjbgglDao;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;

    // 对接室主任
    public Collection<TaskExecutor> getdjszr() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String dlId = formDataJson.getString("djId");
        if (StringUtils.isNotBlank(dlId)) {
            String dlName = formDataJson.getString("djName");
            users.add(new TaskExecutor(dlId, dlName));
        }
        return users;
    }

    /**
     * 流程中，获取室主任
     *
     * @return
     */
    public Collection<TaskExecutor> getSzr() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        JSONArray tdmcDataJson = JSONObject.parseArray(formDataJson.getString("allszr"));
        for (int i = 0; i < tdmcDataJson.size(); i++) {
            JSONObject oneObject = tdmcDataJson.getJSONObject(i);
            users.add(new TaskExecutor(oneObject.getString("szrId"), oneObject.getString("szrName")));
        }
        return users;
    }

    // 产品主管
    public Collection<TaskExecutor> getCpzg() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        JSONArray tdmcDataJson = JSONObject.parseArray(formDataJson.getString("allmodel"));
        for (int i = 0; i < tdmcDataJson.size(); i++) {
            JSONObject oneObject = tdmcDataJson.getJSONObject(i);
            users.add(new TaskExecutor(oneObject.getString("cpzgId"), oneObject.getString("cpzgName")));
        }
        return users;
    }

    // 产品主管所长
    public Collection<TaskExecutor> getCpzgLeader() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        JSONArray tdmcDataJson = JSONObject.parseArray(formDataJson.getString("allmodel"));
        for (int i = 0; i < tdmcDataJson.size(); i++) {
            JSONObject oneObject = tdmcDataJson.getJSONObject(i);
            String cpzgId = oneObject.getString("cpzgId");
            List<JSONObject> deptResps = commonInfoDao.queryDeptRespByUserId(cpzgId);
            if (deptResps != null && !deptResps.isEmpty()) {
                for (JSONObject depRespMan : deptResps) {
                    users.add(new TaskExecutor(depRespMan.getString("USER_ID_"), depRespMan.getString("FULLNAME_")));
                }
            }
        }
        return users;
    }

    // 技术人员
    public Collection<TaskExecutor> getJsy() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        JSONArray tdmcDataJson = JSONObject.parseArray(formDataJson.getString("alljsy"));
        for (int i = 0; i < tdmcDataJson.size(); i++) {
            JSONObject oneObject = tdmcDataJson.getJSONObject(i);
            users.add(new TaskExecutor(oneObject.getString("jsyId"), oneObject.getString("jsyName")));
        }
        return users;
    }

    public boolean isSzr(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        String ifszr = formDataJson.getString("ifszr");
        if ("是".equalsIgnoreCase(ifszr)) {
            return true;
        }
        return false;
    }

    public boolean noSzr(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        String ifszr = formDataJson.getString("ifszr");
        if ("否".equalsIgnoreCase(ifszr)) {
            return true;
        }
        return false;
    }

    public boolean isJsy(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        JSONArray tdmcDataJson = JSONObject.parseArray(formDataJson.getString("alljsy"));
        if (tdmcDataJson.size() > 0) {
            return true;
        }
        return false;
    }

    public boolean noJsy(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        JSONArray tdmcDataJson = JSONObject.parseArray(formDataJson.getString("alljsy"));
        if (tdmcDataJson.size() < 1) {
            return true;
        }
        return true;
    }
}
