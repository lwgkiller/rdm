package com.redxun.zlgjNPI.core.manager;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.zlgjNPI.core.dao.NewItemLcpsDao;
import com.redxun.zlgjNPI.core.dao.ZlgjWTDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class NewItemFxpgScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(NewItemFxpgScript.class);
    @Autowired
    private ZlgjWTDao zlgjWTDao;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private NewItemLcpsDao newItemLcpsDao;
    @Autowired
    private CommonInfoDao commonInfoDao;

    // 改善责任人
    public Collection<TaskExecutor> getGszrrUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String gsrId = formDataJson.getString("gsrId");
        String gsrName = formDataJson.getString("gsrName");
        if (StringUtils.isNotBlank(gsrId) && StringUtils.isNotBlank(gsrName)) {
            users.add(new TaskExecutor(gsrId, gsrName));
        }
        return users;
    }
    // 责任人
    public Collection<TaskExecutor> getZrrUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String zrrId = formDataJson.getString("zrrId");
        String zrrName = formDataJson.getString("zrrName");
        if (StringUtils.isNotBlank(zrrId) && StringUtils.isNotBlank(zrrName)) {
            users.add(new TaskExecutor(zrrId, zrrName));
        }
        return users;
    }
    // 改善责任人的部门负责人
    public Collection<TaskExecutor> getDeptFzr() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String gsrId = formDataJson.getString("gsrId");
        Map<String, Object> queryUserParams = new HashMap<>();
        queryUserParams.put("USER_ID_", gsrId);
        List<Map<String, Object>> userInfos = xcmgProjectOtherDao.getUserInfoById(queryUserParams);
        String groupId = "";
        if (userInfos != null && !userInfos.isEmpty()) {
            for (Map<String, Object> oneMap : userInfos) {
                if (StringUtils.isNotBlank(oneMap.get("dimKey").toString()) && oneMap.get("dimKey").equals("_ADMIN")) {
                    groupId = oneMap.get("groupId").toString();
                    break;
                }
            }
        }
        Map<String, Object> params = new HashMap<>();
        params.put("deptId", groupId);
        params.put("REL_TYPE_KEY_", "GROUP-USER-LEADER");
        List<Map<String, String>> user = commonInfoDao.queryUserByGroupId(params);
        if (user != null && !user.isEmpty()) {
            for (Map<String, String> oneLeader : user) {
                users.add(new TaskExecutor(oneLeader.get("USER_ID_"), oneLeader.get("FULLNAME_")));
            }
        }
        return users;
    }
    // 新品量产-产品主管
    public Collection<TaskExecutor> getCpzgUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String cpzgId = formDataJson.getString("cpzgId");
        String cpzgName = formDataJson.getString("cpzgName");
        if (StringUtils.isNotBlank(cpzgId) && StringUtils.isNotBlank(cpzgName)) {
            users.add(new TaskExecutor(cpzgId, cpzgName));
        }
        return users;
    }
    // 责任人优化措施-会签
    public Collection<TaskExecutor> getZrryhcsUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String xplcId = formDataJson.getString("xplcId");
        List<JSONObject> xplcxxList =newItemLcpsDao.getXplcxxList(xplcId);
        for (JSONObject xplcxx :xplcxxList){
            users.add(new TaskExecutor(xplcxx.getString("zrrId"), xplcxx.getString("zrrName")));
        }
        return users;
    }
    // 产品主管的部门负责人
    public Collection<TaskExecutor> getDeptCpzg() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String cpzgId = formDataJson.getString("cpzgId");
        Map<String, Object> queryUserParams = new HashMap<>();
        queryUserParams.put("USER_ID_", cpzgId);
        List<Map<String, Object>> userInfos = xcmgProjectOtherDao.getUserInfoById(queryUserParams);
        String groupId = "";
        if (userInfos != null && !userInfos.isEmpty()) {
            for (Map<String, Object> oneMap : userInfos) {
                if (StringUtils.isNotBlank(oneMap.get("dimKey").toString()) && oneMap.get("dimKey").equals("_ADMIN")) {
                    groupId = oneMap.get("groupId").toString();
                    break;
                }
            }
        }
        Map<String, Object> params = new HashMap<>();
        params.put("deptId", groupId);
        params.put("REL_TYPE_KEY_", "GROUP-USER-LEADER");
        List<Map<String, String>> user = commonInfoDao.queryUserByGroupId(params);
        if (user != null && !user.isEmpty()) {
            for (Map<String, String> oneLeader : user) {
                users.add(new TaskExecutor(oneLeader.get("USER_ID_"), oneLeader.get("FULLNAME_")));
            }
        }
        return users;
    }
    /**
     * 变更流程 是分管领导批准
     */
    public boolean leaderYesApprove(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String yesOrno = formDataJson.getString("sffgsp");
        if ("YES".equals(yesOrno)) {
            return true;
        }
        return false;
    }
    /**
     * 变更流程 是分管领导批准
     */
    public boolean leaderNoApprove(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String yesOrno = formDataJson.getString("sffgsp");
        if ("NO".equals(yesOrno)) {
            return true;
        }
        return false;
    }
}
