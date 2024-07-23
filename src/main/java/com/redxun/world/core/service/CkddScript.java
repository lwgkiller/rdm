package com.redxun.world.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.world.core.dao.CkddDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

// 项目流程节点的触发事件
@Service
public class CkddScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(CkddScript.class);

    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private CkddDao ckddDao;
    @Autowired
    private CommonInfoDao commonInfoDao;




    public List<Map<String, String>> queryGjyybRespUser() {
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("groupName", RdmConst.HWXSGLB_NAME);
        List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepRespManById(params);
        return depRespMans;
    }

    public Collection<TaskExecutor> getGjyybRespUser() {
        List<TaskExecutor> users = new ArrayList<>();
        List<Map<String, String>> depRespMans = queryGjyybRespUser();
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }

    public List<Map<String, String>> queryGjhbRespUser() {
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("groupName", RdmConst.GJHCPS_NAME);
        List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepRespManById(params);
        return depRespMans;
    }


    public List<Map<String, String>> queryZlbRespUser() {
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("groupName", RdmConst.ZLBZB_NAME);
        List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepRespManById(params);
        return depRespMans;
    }

    public Collection<TaskExecutor> getZlbRespUser() {
        List<TaskExecutor> users = new ArrayList<>();
        List<Map<String, String>> depRespMans = queryZlbRespUser();
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }

    public Collection<TaskExecutor> getSolve() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        Map<String,Object> param = new HashMap<>();
        String ckddId = formDataJson.getString("ckddId");
        param.put("belongId",ckddId);
        List<JSONObject> zzrList = ckddDao.queryZzr(param);
        if(zzrList != null && !zzrList.isEmpty()){
            for (JSONObject zzr : zzrList) {
                users.add(new TaskExecutor(zzr.getString("solveId"), zzr.getString("solve")));
            }
        }
        return users;
    }


    public Collection<TaskExecutor> getHq() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        List<Map<String, String>> depRespMans = queryGjhbRespUser();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String applyUserId = formDataJson.getString("cpzgId");
        List<JSONObject> deptResps = commonInfoDao.queryDeptRespByUserId(applyUserId);
        if (deptResps != null && !deptResps.isEmpty()) {
            for (JSONObject depRespMan : deptResps) {
                users.add(new TaskExecutor(depRespMan.getString("USER_ID_"), depRespMan.getString("FULLNAME_")));
            }
        }
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }

    public Collection<TaskExecutor> getCpzg() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String SzrId = formDataJson.getString("cpzgId");
        if (StringUtils.isNotBlank(SzrId)) {
            String SzrName = formDataJson.getString("cpzg");
            users.add(new TaskExecutor(SzrId, SzrName));
        }

        return users;
    }

}

