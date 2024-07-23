package com.redxun.qualityfxgj.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.qualityfxgj.core.dao.GyfkDao;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
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
public class GyfkScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(GyfkScript.class);

    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private CommonInfoDao commonInfoDao;
    @Autowired
    private GyfkDao gyfkDao;



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
        String SzrIdList = formDataJson.getString("checkZrId");
        if (StringUtils.isNotBlank(SzrIdList)) {
            String SzrNameList = formDataJson.getString("checkZrName");
            String[] SzrNames=SzrNameList.split(",");
            String[] SzrIds=SzrIdList.split(",");
            for(int i=0;i<SzrIds.length;i++){
                users.add(new TaskExecutor(SzrIds[i], SzrNames[i]));
            }
        }
        return users;
    }

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
        String SzrId = formDataJson.getString("repPersonId");
        if (StringUtils.isNotBlank(SzrId)) {
            String SzrName = formDataJson.getString("repPerson");
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
        String applyUserId = formDataJson.getString("repPersonId");
        List<JSONObject> deptResps = commonInfoDao.queryDeptRespByUserId(applyUserId);
        if (deptResps != null && !deptResps.isEmpty()) {
            for (JSONObject depRespMan : deptResps) {
                users.add(new TaskExecutor(depRespMan.getString("USER_ID_"), depRespMan.getString("FULLNAME_")));
            }
        }
        return users;
    }
    /**
     * 流程中，获取工艺部部长
     *
     * @return
     */
    public Collection<TaskExecutor> getGyjsbRespUser() {
        List<TaskExecutor> users = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("groupName", RdmConst.GYJSB_NAME);
        List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepRespManById(params);
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }

    /**
     * 流程中，获取责任人
     *
     * @return
     */
    public Collection<TaskExecutor> getSolve() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        Map<String,Object> param = new HashMap<>();
        String gyfkId = formDataJson.getString("gyfkId");
        param.put("belongGyfkId",gyfkId);
        List<JSONObject> zzrList = gyfkDao.queryGyfkZzr(param);
        if (zzrList != null && !zzrList.isEmpty()) {
            for (JSONObject zzr : zzrList) {
                users.add(new TaskExecutor(zzr.getString("resId"), zzr.getString("res")));
            }
        }
        return users;
    }
}
