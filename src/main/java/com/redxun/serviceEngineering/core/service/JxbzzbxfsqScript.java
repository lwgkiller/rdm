package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.serviceEngineering.core.dao.JxbzzbshDao;
import com.redxun.serviceEngineering.core.dao.JxbzzbxfsqDao;
import com.redxun.serviceEngineering.core.dao.StandardvalueShipmentnotmadeDao;
import com.redxun.sys.org.dao.OsUserDao;
import com.redxun.sys.org.entity.OsUser;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JxbzzbxfsqScript implements GroovyScript {

    private Logger logger = LoggerFactory.getLogger(JxbzzbxfsqScript.class);

    @Autowired
    private JxbzzbshDao jxbzzbshDao;

    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;

    @Autowired
    private JxbzzbxfsqDao jxbzzbxfsqDao;

    @Autowired
    private CommonInfoDao commonInfoDao;

    // 获取流程中创建人部门领导
    public Collection<TaskExecutor> getJxbzzbxfsqDepRespUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String applyUserId = formDataJson.getString("CREATE_BY_");
        if(StringUtils.isBlank(applyUserId)) {
            applyUserId=ContextUtil.getCurrentUserId();
        }
        List<JSONObject> deptResps = commonInfoDao.queryDeptRespByUserId(applyUserId);
        if (deptResps != null && !deptResps.isEmpty()) {
            for (JSONObject depRespMan : deptResps) {
                users.add(new TaskExecutor(depRespMan.getString("USER_ID_"), depRespMan.getString("FULLNAME_")));
            }
        }
        return users;
    }

    // 获取产品所所长
    public Collection<TaskExecutor> getJxbzzbxfsqCpsLeader() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String jxbzzbshId = formDataJson.getString("jxbzzbshId");
        String[] jxbzzbshIds = jxbzzbshId.split(",");
        List<String> depts = new ArrayList <>();
        for (String id : jxbzzbshIds) {
            JSONObject jxbzzbsh = jxbzzbshDao.queryJxbzzbshById(id);
            depts.add(jxbzzbsh.getString("productDepartment"));
        }
        JSONObject param = new JSONObject();
        param.put("groupNames", depts);
        List <JSONObject> depRespMan = jxbzzbxfsqDao.getDepRespMan(param);
        for (JSONObject user : depRespMan) {
            users.add(new TaskExecutor(user.getString("PARTY2_"), user.getString("FULLNAME_")));
        }
        return users;
    }

    // 服务工程研究所部门领导
    public Collection<TaskExecutor> getJxbzzbxfsqFwLeader() {
        List<TaskExecutor> users = new ArrayList<>();
        Map<String, Object> leaderParams = new HashMap<>();
        leaderParams.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        leaderParams.put("groupName", "服务工程技术研究所");
        List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepRespManById(leaderParams);
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }


}
