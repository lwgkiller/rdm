package com.redxun.serviceEngineering.core.service;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import com.alibaba.fastjson.JSONArray;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.core.util.StringUtil;
import com.redxun.saweb.context.ContextUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;


// 项目流程节点的触发事件
@Service
public class ZxdpsScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(ZxdpsScript.class);



    // 从表单获会签意见人员
    public Collection<TaskExecutor> getOpinionUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String expertIdStr = formDataJson.getString("expertId");
        String expertNameStr = formDataJson.getString("expertName");
        if (StringUtils.isNotEmpty(expertIdStr) && StringUtils.isNotEmpty(expertNameStr)) {
            String[] expertIdIds = expertIdStr.split(",");
            String[] expertNameNames = expertNameStr.split(",");
            for (int i = 0; i < expertIdIds.length; i++) {
                TaskExecutor oneUser = new TaskExecutor(expertIdIds[i],expertNameNames[i]);
                users.add(oneUser);
            }
        }
        return users;
    }

    // 从表单获审核人员
    public Collection<TaskExecutor> getApprovalUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String idStr = formDataJson.getString("approvalId");
        String nameStr = formDataJson.getString("approvalName");
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

    // 意见评审驳回
    public boolean yjpsReject(AbstractExecutionCmd cmd) {
        String formDataStr = cmd.getJsonData();
        JSONObject formData = JSONObject.parseObject(formDataStr);
        String currentUserId = ContextUtil.getCurrentUserId();

        JSONArray opinionArr = formData.getJSONArray("SUB_opinionGrid");
        for (int index = 0; index < opinionArr.size(); index++) {
            JSONObject oneObject = opinionArr.getJSONObject(index);
            //网关在并行执行时，以最后一次执行结果为准，故需要判断整个表单，不能每个人判断自己的
//            if ((!Objects.equals(currentUserId, "1"))&&(!currentUserId.equalsIgnoreCase(oneObject.getString("userId")))) {
//                continue;
//            }
            // 有改进意见且未确认的需要驳回
            if ("是".equalsIgnoreCase(oneObject.getString("hasOpinion"))) {
                String confirmFeedback = oneObject.getString("confirmFeedback");
                if (StringUtil.isEmpty(confirmFeedback)||"不同意".equalsIgnoreCase(confirmFeedback)) {
                    return true;
                }
            }

        }
        return false;
    }

    // 意见评审通过
    public boolean yjpsApproval(AbstractExecutionCmd cmd) {
        String formDataStr = cmd.getJsonData();
        JSONObject formData = JSONObject.parseObject(formDataStr);
        JSONArray opinionArr = formData.getJSONArray("SUB_opinionGrid");
        String currentUserId = ContextUtil.getCurrentUserId();
        for (int index = 0; index < opinionArr.size(); index++) {
            JSONObject oneObject = opinionArr.getJSONObject(index);
//            if ((!Objects.equals(currentUserId, "1"))&&(!currentUserId.equalsIgnoreCase(oneObject.getString("userId")))) {
//                continue;
//            }
            if ("是".equalsIgnoreCase(oneObject.getString("hasOpinion"))) {
                String confirmFeedback = oneObject.getString("confirmFeedback");
                if (StringUtil.isEmpty(confirmFeedback)||"不同意".equalsIgnoreCase(confirmFeedback)) {
                    return false;
                }
            }
        }
        return true;
    }




}
