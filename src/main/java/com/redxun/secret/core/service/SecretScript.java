package com.redxun.secret.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmZhgl.core.dao.JsmmDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.secret.core.dao.SecretDao;
import com.redxun.zlgjNPI.core.dao.ZlgjWTDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

// 项目流程节点的触发事件
@Service
public class SecretScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(SecretScript.class);

    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private CommonInfoDao commonInfoDao;
    @Autowired
    private SecretDao secretDao;
    @Autowired
    private ZlgjWTDao zlgjWTDao;




    /**
     * 流程中，获取产品主管
     *
     * @return
     */
    public Collection<TaskExecutor> getZrr() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String SzrId = formDataJson.getString("cpzgId");
        if (StringUtils.isNotBlank(SzrId)) {
            String SzrName = formDataJson.getString("cpzgName");
            users.add(new TaskExecutor(SzrId, SzrName));
        }

        return users;
    }

    /**
     * 流程中，获取产品主管部门负责人
     *
     * @return
     */
    public Collection<TaskExecutor> getZrrDepRespUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String applyUserId = formDataJson.getString("cpzgId");
        List<JSONObject> deptResps = commonInfoDao.queryDeptRespByUserId(applyUserId);
        if (deptResps != null && !deptResps.isEmpty()) {
            for (JSONObject depRespMan : deptResps) {
                users.add(new TaskExecutor(depRespMan.getString("USER_ID_"), depRespMan.getString("FULLNAME_")));
            }
        }
        return users;
    }
    /**
     * 质量改进工程师
     */
    public Collection<TaskExecutor> getZlgjgcs() {
        List<TaskExecutor> users = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        params.put("REL_TYPE_KEY_", "GROUP-USER-DIREGJGCS");
        List<Map<String, String>> userInfos = zlgjWTDao.queryUserByZlgjgcs(params);
        if (userInfos != null && !userInfos.isEmpty()) {
            for (Map<String, String> oneLeader : userInfos) {
                users.add(new TaskExecutor(oneLeader.get("USER_ID_"), oneLeader.get("FULLNAME_")));
            }
        }
        return users;
    }
}
