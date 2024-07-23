package com.redxun.xcmgbudget.core.manager;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.xcmgbudget.core.dao.BudgetMonthUserDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class BudgetMonthScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(BudgetMonthScript.class);

    @Autowired
    private BudgetMonthUserDao budgetMonthUserDao;
    @Autowired
    private CommonInfoDao commonInfoDao;

    public boolean isXml(AbstractExecutionCmd cmd, Map<String, Object> vars){
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
        if (formDataJson.getString("budgetType").equalsIgnoreCase("xml")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isFxml(AbstractExecutionCmd cmd, Map<String, Object> vars){
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

        if (formDataJson.getString("budgetType").equalsIgnoreCase("fxml")) {
            return true;
        } else {
            return false;
        }
    }

    //项目负责人
    public Collection<TaskExecutor> getProjectUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String projectId = formDataJson.getString("projectId");
        JSONObject params = new JSONObject();
        params.put("projectId",projectId);
        List<Map<String, Object>> personList = budgetMonthUserDao.queryProjectList(params);
        String userId = personList.get(0).get("respId").toString();
        String userName = personList.get(0).get("respMan").toString();
        users.add(new TaskExecutor(userId, userName));
        return users;
    }

    //部门领导
    public Collection<TaskExecutor> getJszxLeader() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String appId = formDataJson.getString("userId");
        List<JSONObject> leader = commonInfoDao.queryDeptRespByUserId(appId);
        String userId = leader.get(0).get("USER_ID_").toString();
        String userName = leader.get(0).get("FULLNAME_").toString();
        users.add(new TaskExecutor(userId, userName));
        return users;
    }
}
