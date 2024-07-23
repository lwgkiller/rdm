package com.redxun.bjjszc.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bjjszc.core.dao.KfsqDao;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.dao.JsmmDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

// 项目流程节点的触发事件
@Service
public class GyqrScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(GyqrScript.class);

    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private KfsqDao kfsqDao;
    @Autowired
    private JsmmDao jsmmDao;
    @Autowired
    private KfsqService kfsqService;




    public List<Map<String, String>> queryFwsRespUser() {
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("groupName", RdmConst.FWGCS_NAME);
        List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepRespManById(params);
        return depRespMans;
    }

    public Collection<TaskExecutor> getFwsRespUser() {
        List<TaskExecutor> users = new ArrayList<>();
        List<Map<String, String>> depRespMans = queryFwsRespUser();
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }

    public List<Map<String, String>> queryBJRespUser() {
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("groupName",RdmConst.BJGS_NAME);
        List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepRespManById(params);
        return depRespMans;
    }


    public Collection<TaskExecutor> getBjRespUser() {
        List<TaskExecutor> users = new ArrayList<>();
        List<Map<String, String>> depRespMans = queryBJRespUser();
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }



    public Collection<TaskExecutor> getFw() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String SzrId = formDataJson.getString("fwId");
        if (StringUtils.isNotBlank(SzrId)) {
            String SzrName = formDataJson.getString("fwName");
            users.add(new TaskExecutor(SzrId, SzrName));
        }

        return users;
    }

    public Collection<TaskExecutor> getWh() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String SzrId = formDataJson.getString("whId");
        if (StringUtils.isNotBlank(SzrId)) {
            String SzrName = formDataJson.getString("whName");
            users.add(new TaskExecutor(SzrId, SzrName));
        }

        return users;
    }
}

