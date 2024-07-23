package com.redxun.portrait.core.manager;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.portrait.core.dao.KpiLeaderDao;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.xcmgbudget.core.dao.BudgetMonthUserDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class KpiLeaderScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(KpiLeaderScript.class);

    @Autowired
    private CommonInfoDao commonInfoDao;
    @Autowired
    private KpiLeaderDao kpiLeaderDao;

    //项目负责人
    public Collection<TaskExecutor> getBkhUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String bkhUserId = formDataJson.getString("bkhUserId");
        String bkhUserName = formDataJson.getString("bkhUserName");
        users.add(new TaskExecutor(bkhUserId, bkhUserName));
        return users;
    }

    //执考人员
    public Collection<TaskExecutor> getZkUsers() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String id = formDataJson.getString("id");
        Map<String, Object> params = new HashMap<>();
        params.put("id",id);
        List<JSONObject> detailResps = kpiLeaderDao.getKpiLeaderDetailById(params);
        if (detailResps != null && !detailResps.isEmpty()) {
            for (JSONObject zkMan : detailResps) {
                if (zkMan.containsKey("copyType") && zkMan.getString("copyType").equalsIgnoreCase("1")){
                    continue;
                }
                users.add(new TaskExecutor(zkMan.getString("zkUserId"), zkMan.getString("zkUserName")));
            }
        }
        return users;
    }

    //执考人员领导
    public Collection<TaskExecutor> getZkUserLeaders() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String id = formDataJson.getString("id");
        Map<String, Object> params = new HashMap<>();
        params.put("id",id);
        List<JSONObject> detailResps = kpiLeaderDao.getKpiLeaderDetailById(params);
        if (detailResps != null && !detailResps.isEmpty()) {
            for (JSONObject zkMan : detailResps) {
                if (zkMan.containsKey("copyType") && zkMan.getString("copyType").equalsIgnoreCase("1")){
                    continue;
                }
                List<JSONObject> leader = commonInfoDao.queryDeptRespByUserId(zkMan.getString("zkUserId"));
                String userId = leader.get(0).get("USER_ID_").toString();
                String userName = leader.get(0).get("FULLNAME_").toString();
                users.add(new TaskExecutor(userId, userName));
            }
        }
        return users;
    }

    public boolean isGS(AbstractExecutionCmd cmd, Map<String, Object> vars){
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
        Map<String, Object> params = new HashMap<>();
        params.put("id",formDataJson.getString("id"));
        List<JSONObject> list = kpiLeaderDao.getKpiLeaderBaseById(params);
        if (list.get(0).getString("type").equalsIgnoreCase("归属类型")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isNotGS(AbstractExecutionCmd cmd, Map<String, Object> vars){
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
        Map<String, Object> params = new HashMap<>();
        params.put("id",formDataJson.getString("id"));
        List<JSONObject> list = kpiLeaderDao.getKpiLeaderBaseById(params);
        if (list.get(0).getString("type").equalsIgnoreCase("归属类型")) {
            return false;
        } else {
            return true;
        }
    }
}
