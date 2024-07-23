package com.redxun.zlgjNPI.core.manager;

import java.util.*;

import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.saweb.context.ContextUtil;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;


@Service
public class ManageImproveScript implements GroovyScript {

    private Logger logger = LoggerFactory.getLogger(ManageImproveScript.class);
    @Autowired
    private CommonInfoDao commonInfoDao;
    // 应该改进
    public boolean judgeYesImprove(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String canOrNot = formDataJson.getString("canOrNot");
        if (canOrNot.equalsIgnoreCase("采纳")) {
            return true;
        }
        return false;
    }

    // 不改进
    public boolean judgeNoImprove(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String sfgz = formDataJson.getString("canOrNot");
        if (sfgz.equalsIgnoreCase("不采纳")) {
            return true;
        }
        return false;
    }

    // 对策部门分管领导
    public Collection<TaskExecutor> getDcfglg() {
        List<TaskExecutor> users = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String improveDepartment = formDataJson.getString("improveDepartmentId");
        params.put("deptId", improveDepartment);
        params.put("REL_TYPE_KEY_", "GROUP-DEPT-LEADER");
        List<Map<String, String>> userInfos = commonInfoDao.queryUserByGroupId(params);
        if (userInfos != null && !userInfos.isEmpty()) {
            for (Map<String, String> oneLeader : userInfos) {
                users.add(new TaskExecutor(oneLeader.get("USER_ID_"), oneLeader.get("FULLNAME_")));
            }
        }
        return users;
    }

    // 提出部门分管领导
    public Collection<TaskExecutor> getTcbmfgld() {
        List<TaskExecutor> users = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String applicationUnitId = formDataJson.getString("applicationUnitId");
        params.put("deptId", applicationUnitId);
        params.put("REL_TYPE_KEY_", "GROUP-DEPT-LEADER");
        List<Map<String, String>> userInfos = commonInfoDao.queryUserByGroupId(params);
        if (userInfos != null && !userInfos.isEmpty()) {
            for (Map<String, String> oneLeader : userInfos) {
                users.add(new TaskExecutor(oneLeader.get("USER_ID_"), oneLeader.get("FULLNAME_")));
            }
        }

        return users;
    }

    // 对策部门负责人
    public Collection<TaskExecutor> getDcbmfzr() {
        List<TaskExecutor> users = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String improveDepartment = formDataJson.getString("improveDepartmentId");
        params.put("deptId", improveDepartment);
        params.put("REL_TYPE_KEY_", "GROUP-USER-LEADER");
        List<Map<String, String>> userInfos = commonInfoDao.queryUserByGroupId(params);
        if (userInfos != null && !userInfos.isEmpty()) {
            for (Map<String, String> oneLeader : userInfos) {
                users.add(new TaskExecutor(oneLeader.get("USER_ID_"), oneLeader.get("FULLNAME_")));
            }
        }
        return users;
    }

    // 提出部门负责人
    public Collection<TaskExecutor> getTcbmfzr() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String applyUserId = formDataJson.getString("CREATE_BY_");
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

    // 改进负责人
    public Collection<TaskExecutor> getFzr(){
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String responseManId = formDataJson.getString("responseManId");
        String responsManName = formDataJson.getString("responsManName");
        String[] responseManIds = responseManId.split(",");
        String[] responsManNames = responsManName.split(",");
        for (int i = 0; i < responseManIds.length; i++) {
            String userId = responseManIds[i];
            String userName = responsManNames[i];
            users.add(new TaskExecutor(userId, userName));
        }
        return users;
        }
}
