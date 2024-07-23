package com.redxun.zlgjNPI.core.manager;

import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.zlgjNPI.core.dao.GysWTDao;

@Service
public class GysWTScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(ZlgjHandler.class);
    @Autowired
    private GysWTDao gysWTDao;

    // 质量管理工程师
    public Collection<TaskExecutor> getZlglgcs() {
        return getProcessUserByType("zlglgcs");
    }

    // 供方管理工程师
    public Collection<TaskExecutor> getGfglgcs() {
        return getProcessUserByType("gfglgcs");
    }

    // 质量工程师
    public Collection<TaskExecutor> getZlgcs() {
        return getProcessUserByType("zlgcs");
    }

    /**
     * 供应商质量改进，通过类型配置查询处理人
     */
    private Collection<TaskExecutor> getProcessUserByType(String type) {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String smallTypeId = formDataJson.getString("smallTypeId");
        if (StringUtils.isBlank(smallTypeId)) {
            return users;
        }
        JSONObject oneSmallType = queryConfigUserBySmallType(smallTypeId);
        if (oneSmallType == null) {
            return users;
        }
        String userIds = "";
        String userNames = "";
        switch (type) {
            case "zlglgcs":
                userIds = oneSmallType.getString("zlglgcsIds");
                userNames = oneSmallType.getString("zlglgcsNames");
                break;
            case "gfglgcs":
                userIds = oneSmallType.getString("gfglgcsIds");
                userNames = oneSmallType.getString("gfglgcsNames");
                break;
            case "zlgcs":
                userIds = oneSmallType.getString("zlgcsIds");
                userNames = oneSmallType.getString("zlgcsNames");
                break;
        }

        if (StringUtils.isNotBlank(userIds) && StringUtils.isNotBlank(userNames)) {
            String[] idArr = userIds.split(",", -1);
            String[] nameArr = userNames.split(",", -1);
            if (idArr.length == nameArr.length) {
                for (int index = 0; index < idArr.length; index++) {
                    users.add(new TaskExecutor(idArr[index], nameArr[index]));
                }
            }
        }
        return users;
    }

    private JSONObject queryConfigUserBySmallType(String smallTypeId) {
        Map<String, Object> params = new HashMap<>();
        params.put("smallTypeId", smallTypeId);
        List<JSONObject> smallTypeObjs = gysWTDao.querySmallType(params);
        if (smallTypeObjs == null || smallTypeObjs.isEmpty()) {
            return null;
        }
        return smallTypeObjs.get(0);
    }

    // 外协
    public boolean judgeIsWX(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String bigTypeName = formDataJson.getString("bigTypeName");
        if (bigTypeName.equalsIgnoreCase("外协件")) {
            return true;
        }
        return false;
    }

    // 外购
    public boolean judgeIsWG(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String bigTypeName = formDataJson.getString("bigTypeName");
        if (bigTypeName.equalsIgnoreCase("外购件")) {
            return true;
        }
        return false;
    }

    // 有故障
    public boolean judgeHasGz(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String sfgz = formDataJson.getString("sfgz");
        if (sfgz.equalsIgnoreCase("yes")) {
            return true;
        }
        return false;
    }

    // 无故障
    public boolean judgeNoGz(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String sfgz = formDataJson.getString("sfgz");
        if (sfgz.equalsIgnoreCase("no")) {
            return true;
        }
        return false;
    }

    /**
     * 分管领导批准
     */
    public boolean leaderApprove(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String yesOrno = formDataJson.getString("sffgsp");
        if (yesOrno.equalsIgnoreCase("yes")) {
            return true;
        }
        return false;
    }

    /**
     * 不需要分管领导批准
     */
    public boolean noLeaderApprove(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String yesOrno = formDataJson.getString("sffgsp");
        if (yesOrno.equalsIgnoreCase("no")) {
            return true;
        }
        return false;
    }
}
