package com.redxun.secret.core.service;

import java.util.*;

import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.secret.core.dao.QxsqDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.zlgjNPI.core.dao.ZlgjWTDao;

// 项目流程节点的触发事件
@Service
public class QxsqScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(QxsqScript.class);

    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private CommonInfoDao commonInfoDao;
    @Autowired
    private QxsqDao qxsqDao;





    /**
     * 获取部门负责人
     *
     * @return
     */
    public Collection<TaskExecutor> getDeptResp() {

        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);

        String applyUserId = formDataJson.getString("CREATE_BY_");
        if(StringUtils.isBlank(applyUserId)) {
            applyUserId= ContextUtil.getCurrentUserId();
        }
        List<JSONObject> deptResps = commonInfoDao.queryDeptRespByUserId(applyUserId);
        if (deptResps != null && !deptResps.isEmpty()) {
            for (JSONObject depRespMan : deptResps) {
                users.add(new TaskExecutor(depRespMan.getString("USER_ID_"), depRespMan.getString("FULLNAME_")));
            }
        }

        return users;
    }


    /**
     * 获取子系统负责人
     *
     * @return
     */
    public Collection<TaskExecutor> getSubSysUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String Key = formDataJson.getString("subSysId");
        Map<String, String> params = new HashMap<>();
        params.put("Key", Key);
        if (StringUtils.isNotBlank(Key)) {
            List<JSONObject> subSysList = qxsqDao.queryUserBySysKey(params);
            for (JSONObject oneRes : subSysList) {
                String[]uIds=oneRes.getString("principalId").split("[,]");
                String[]uNames=oneRes.getString("principal").split("[,]");
                for (int i = 0; i < uIds.length; i++) {
                    users.add(new TaskExecutor(uIds[i], uNames[i]));
                }
            }
        }

        return users;
    }

    // 信息化部门领导
    public Collection<TaskExecutor> xxhLeaderInfos() {
        List<TaskExecutor> users = new ArrayList<>();
        Map<String, Object> leaderParams = new HashMap<>();
        leaderParams.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        leaderParams.put("groupName", RdmConst.XXHGLB_NAME);
        List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepRespManById(leaderParams);
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }


}
