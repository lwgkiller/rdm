package com.redxun.strategicPlanning.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.strategicPlanning.core.dao.CpxpghDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CpxpghScript implements GroovyScript {

    private Logger logger = LoggerFactory.getLogger(CpxpghScript.class);

    @Autowired
    private CpxpghDao cpxpghDao;

    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;

    //是否为量产(是)
    public boolean isLc(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        String productStatus = formDataJson.getString("productStatus");
        if (productStatus.equals("lc")) {
            return true;
        }
        return  false;
    }

    //是否为量产(否)
    public boolean isNotLc(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        String productStatus = formDataJson.getString("productStatus");
        if (!productStatus.equals("lc")) {
            return true;
        }
        return  false;
    }

    // 产品所所长
    public Collection<TaskExecutor> getCpssz() {
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String department = formDataJson.getString("department");
        List<TaskExecutor> users = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("groupName", department);
        List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepRespManById(params);
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }

    // 技术状态确认人员
    public Collection<TaskExecutor> jsztqrUserInfos() {
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        List<TaskExecutor> users = new ArrayList<>();
        Map<String, Object> roleParams = new HashMap<>();
        roleParams.put("groupName", "产品型谱技术管理部产品主管");
        roleParams.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, String>> userInfos = xcmgProjectOtherDao.queryUserByGroupName(roleParams);
        if (userInfos != null && !userInfos.isEmpty()) {
            for (Map<String, String> oneLeader : userInfos) {
                users.add(new TaskExecutor(oneLeader.get("USER_ID_"), oneLeader.get("FULLNAME_")));
            }
        }
        Map<String, Object> params = new HashMap<>();
        String[] groupNames =new String[4];
        groupNames[0] = RdmConst.BZJSS_NAME;
        groupNames[1] = RdmConst.FWGCS_NAME;
        groupNames[2] = RdmConst.ZNKZS_NAME;
        groupNames[3] = RdmConst.CSYJS_NAME;
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("groupNames", groupNames);
        List<Map<String, String>> depRespMans = cpxpghDao.getDepRespMans(params);
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }

}
