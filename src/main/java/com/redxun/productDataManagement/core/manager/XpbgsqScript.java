package com.redxun.productDataManagement.core.manager;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.saweb.context.ContextUtil;
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


// 项目流程节点的触发事件
@Service
public class XpbgsqScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(XpbgsqScript.class);

    @Autowired
    private CommonInfoDao commonInfoDao;


    // 从表单获产品主管会签人员
    public Collection<TaskExecutor> getProductManagerUsers() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String idStr = formDataJson.getString("productMangerIds");
        String nameStr = formDataJson.getString("productManagerNames");
        if (StringUtils.isNotEmpty(idStr) && StringUtils.isNotEmpty(nameStr)) {
            String[] ids = idStr.split(",");
            String[] names = nameStr.split(",");
            for (int i = 0; i < ids.length; i++) {
                TaskExecutor oneUser = new TaskExecutor(ids[i],names[i]);
                users.add(oneUser);
            }
        }
        return users;
    }

    // 部门负责人
    public Collection<TaskExecutor> getDepRespUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String applyUserId = formDataJson.getString("CREATE_BY_");
        if ("1".equalsIgnoreCase(applyUserId)) {
            users.add(new TaskExecutor("1", "管理员"));
            return users;
        }
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

    public boolean needApproval(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String aimType = formDataJson.getString("aimType");
        if (("销售状态").equalsIgnoreCase(aimType)||("制造状态").equalsIgnoreCase(aimType)) {
            return true;
        }
        return false;
    }

    public boolean noNeedApproval(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String aimType = formDataJson.getString("aimType");
        if (("研发状态").equalsIgnoreCase(aimType)||("其他内容").equalsIgnoreCase(aimType)) {
            return true;
        }
        return false;
    }


}
