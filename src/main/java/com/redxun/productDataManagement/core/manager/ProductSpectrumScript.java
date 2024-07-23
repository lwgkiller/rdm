package com.redxun.productDataManagement.core.manager;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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


// 项目流程节点的触发事件
@Service
public class ProductSpectrumScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(ProductSpectrumScript.class);

    @Autowired
    private CommonInfoDao commonInfoDao;


    // 从表单获产品主管人员，获取其部门负责人
    public Collection<TaskExecutor> getProductManagerUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String productManagerId = formDataJson.getString("productManagerId");
        List<JSONObject> deptResps = commonInfoDao.queryDeptRespByUserId(productManagerId);
        if (deptResps != null && !deptResps.isEmpty()) {
            for (JSONObject depRespMan : deptResps) {
                users.add(new TaskExecutor(depRespMan.getString("USER_ID_"), depRespMan.getString("FULLNAME_")));
            }
        }
        return users;
    }

    // 从表单获产品主管人员
    public Collection<TaskExecutor> getDraftUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String productManagerId = formDataJson.getString("productManagerId");
        String productManagerName = formDataJson.getString("productManagerName");
        if (StringUtils.isNotEmpty(productManagerId)||StringUtils.isNotEmpty(productManagerName)) {
            users.add(new TaskExecutor(productManagerId, productManagerName));
        } else {
            users.add(new TaskExecutor("1", "管理员"));
        }
        return users;
    }



}
