package com.redxun.drbfm.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.*;
import com.redxun.core.script.GroovyScript;
import com.redxun.drbfm.core.dao.DrbfmSingleDao;
import com.redxun.drbfm.core.dao.DrbfmTestTaskDao;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DrbfmInterfaceScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(DrbfmInterfaceScript.class);

    // 部件验证接口需求收集，单一项目部件分析负责人
    public Collection<TaskExecutor> singleGetPartAnalyser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        users.add(new TaskExecutor(formDataJson.getString("analyseUserId"), formDataJson.getString("analyseUserName")));
        return users;
    }

    // 部件验证接口需求收集，单一项目产品主管
    public Collection<TaskExecutor> singleGetProductManager() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        users.add(new TaskExecutor(formDataJson.getString("cpzgId"), formDataJson.getString("cpzgName")));
        return users;
    }

    //会签人员获取
    //..获取评审人员
    public Collection<TaskExecutor> getAssessors() {
        List<TaskExecutor> users = new ArrayList<>();
        JSONObject formDataJson = JSONObject.parseObject(ProcessHandleHelper.getProcessCmd().getJsonData());
//        JSONArray topicPSItems = formDataJson.getJSONArray("itemChangeData");
//        for (Object topicPSItem : topicPSItems) {
//            JSONObject topicPSItemJson = (JSONObject) topicPSItem;
//            users.add(new TaskExecutor(topicPSItemJson.getString("assessorId_item"),
//                    topicPSItemJson.getString("assessor_item")));
//        }
        users.add(new TaskExecutor(formDataJson.getString("CREATE_BY_"), formDataJson.getString("interfaceFeedBackUserName")));
        users.add(new TaskExecutor(formDataJson.getString("analyseUserId"), formDataJson.getString("analyseUserName")));
        return users;
    }


}
