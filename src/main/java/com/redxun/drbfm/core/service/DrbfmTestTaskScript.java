package com.redxun.drbfm.core.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.redxun.drbfm.core.dao.DrbfmTestTaskDao;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.sys.org.dao.OsUserDao;
import com.redxun.sys.org.entity.OsUser;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.ProcessStartCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.drbfm.core.dao.DrbfmSingleDao;
import com.redxun.org.api.service.UserService;

@Service
public class DrbfmTestTaskScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(DrbfmTestTaskScript.class);
    @Autowired
    private UserService userService;
    @Autowired
    private DrbfmSingleDao drbfmSingleDao;
    @Autowired
    private DrbfmTestTaskDao drbfmTestTaskDao;
    @Autowired
    private OsUserDao osUserDao;
    @Autowired
    private CommonInfoDao commonInfoDao;
    @Autowired
    private CommonInfoManager commonInfoManager;

    // 试验任务获取风险验证任务室主任
    public Collection<TaskExecutor> testTaskFxyzszr() {

        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String baseId = formDataJson.getString("belongSingleId");
        JSONObject detailObj = drbfmSingleDao.querySingleBaseById(baseId);
        if (detailObj == null) {
            return users;
        }
        users.add(new TaskExecutor(detailObj.getString("checkUserId"), detailObj.getString("checkUserName")));
        return users;
    }

    // 风险验证任务产品主管审核
    public Collection<TaskExecutor> testTaskCpzgsh() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String baseId = formDataJson.getString("belongSingleId");
        JSONObject detailObj = drbfmSingleDao.querySingleBaseById(baseId);
        if (detailObj == null) {
            return users;
        }
        String userId = detailObj.getString("CREATE_BY_");
        OsUser osUser = osUserDao.get(userId);
        users.add(new TaskExecutor(osUser.getUserId(), osUser.getFullname()));
        return users;
    }

    // 风险验证任务部件负责人所长批准
    public Collection<TaskExecutor> testTaskGetBjszpz() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String baseId = formDataJson.getString("belongSingleId");
        JSONObject detailObj = drbfmSingleDao.querySingleBaseById(baseId);
        if (detailObj == null) {
            return users;
        }
        String applyUserId = detailObj.getString("analyseUserId");
        List<JSONObject> deptResps = commonInfoDao.queryDeptRespByUserId(applyUserId);
        if (deptResps != null && !deptResps.isEmpty()) {
            for (JSONObject depRespMan : deptResps) {
                users.add(new TaskExecutor(depRespMan.getString("USER_ID_"), depRespMan.getString("FULLNAME_")));
            }
        }
        return users;
    }

    // 风险验证任务验证所长批准
    public Collection<TaskExecutor> testTaskGetYzszpz() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String departName = formDataJson.getString("respDeptName");


        List<Map<String, String>> depRespMans = commonInfoManager.queryDeptRespUser(departName);
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }

    // 风险验证任务执行
    public Collection<TaskExecutor> testTaskGetRwzx() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String userId = formDataJson.getString("testRespUserId");
        String userName = formDataJson.getString("testRespUserName");
        users.add(new TaskExecutor(userId, userName));
        return users;
    }

    // 风险验证室主任
    public Collection<TaskExecutor> testTaskGetSzr() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String userId = formDataJson.getString("testRespSZRId");
        String userName = formDataJson.getString("testRespSZRName");
        users.add(new TaskExecutor(userId, userName));
        return users;
    }

}
