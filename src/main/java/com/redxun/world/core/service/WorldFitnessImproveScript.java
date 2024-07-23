package com.redxun.world.core.service;

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
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.world.core.dao.WorldFitnessImproveDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

// 项目流程节点的触发事件
@Service
public class WorldFitnessImproveScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(WorldFitnessImproveScript.class);
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private WorldFitnessImproveDao worldFitnessImproveDao;
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private CommonInfoDao commonInfoDao;

    // 研发基地判断
    public boolean yfjdConfirm(AbstractExecutionCmd cmd) {
        String hwjdJudge = getCmdValueByKey(cmd, "hwjdJudge");
        // 研发基地确认值为yes时为真
        if (hwjdJudge == null) {
            return false;
        }
        if (hwjdJudge.equalsIgnoreCase("yes")) {
            return true;
        }
        return false;
    }

    // 研发基地判断否
    public boolean yfjdConfirmNo(AbstractExecutionCmd cmd) {
        String hwjdJudge = getCmdValueByKey(cmd, "hwjdJudge");
        // 研发基地确认值不为yes时为真
        if (hwjdJudge == null || !hwjdJudge.equalsIgnoreCase("yes")) {
            return true;
        }
        return false;
    }

    // 产品主管确认值为yes时为真
    public boolean pmConfirm(AbstractExecutionCmd cmd) {
        String pmJudge = getCmdValueByKey(cmd, "pmJudge");
        if (pmJudge == null) {
            return false;
        }
        if (pmJudge.equalsIgnoreCase("yes")) {
            return true;
        }
        return false;
    }

    // 产品主管确认值不为yes时为真
    public boolean pmConfirmNo(AbstractExecutionCmd cmd) {
        String pmJudge = getCmdValueByKey(cmd, "pmJudge");
        if (pmJudge == null || !pmJudge.equalsIgnoreCase("yes")) {
            return true;
        }
        return false;
    }

    // 更新节点开始时间
    public void actionBeginTime(AbstractExecutionCmd cmd, Map<String, Object> vars) {

        // 获取表单id
        String id = getCmdValueByKey(cmd, "id");
        if ("".equalsIgnoreCase(id)) {
            id = vars.get("busKey").toString();
        }

        // 获取节点变量
        String stageTime = getNodeVarByKey(vars, "stageTime");
        if ("".equalsIgnoreCase(stageTime)) {
            return;
        }
        // 拼接目标字段名
        String objectTime = stageTime + "StartTime";
        Map<String, Object> params2 = new HashMap<>();
        params2.put(objectTime, new Date());
        params2.put("id", id);
        params2.put("UPDATE_TIME_", new Date());

        // 根据节点名称/状态更新数据库开始时间
        worldFitnessImproveDao.updateActionTime(params2);
    }

    // 更新节点结束时间
    public void actionEndTime(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        // 获取表单id
        String id = getCmdValueByKey(cmd, "id");

        // 获取节点变量
        String stageTime = getNodeVarByKey(vars, "stageTime");
        if ("".equalsIgnoreCase(stageTime)) {
            return;
        }
        // 拼接目标字段名
        String objectTime = stageTime + "EndTime";
        Map<String, Object> params2 = new HashMap<>();
        params2.put(objectTime, XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        params2.put(objectTime, new Date());
        params2.put("id", id);
        params2.put("UPDATE_TIME_", new Date());
        // 根据节点名称/状态更新数据库结束时间
        worldFitnessImproveDao.updateActionTime(params2);
    }

    private String getCmdValueByKey(AbstractExecutionCmd cmd, String key) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String res = formDataJson.getString(key);
        return res;
    }

    /*获取节点变量
    * vars: 表单变量
    * key: 节点变量名
    */
    private String getNodeVarByKey(Map<String, Object> vars, String key) {
        String busKey = vars.get("busKey").toString();
        // 查找该节点上的所有变量
        String solId = vars.get("solId").toString();
        String nodeId = vars.get("activityId").toString();
        List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, null, solId);
        if (nodeVars == null || nodeVars.isEmpty()) {
            logger.warn("no vars in node {}", nodeId);
            return "";
        }
        // 查找变量stageTime【数据库字段前缀】
        String stageTime = "";
        for (Map<String, String> oneVar : nodeVars) {
            if (key.equals(oneVar.get("KEY_"))) {
                stageTime = oneVar.get("DEF_VAL_");
                break;
            }
        }
        if (StringUtils.isBlank(stageTime)) {
            logger.info("stageState in node {} is blank", nodeId);
            return "";
        }
        return stageTime;
    }

    // 海外基地負責人
    public Collection<TaskExecutor> getHwjdResp() {

        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);

        String applyUserId = formDataJson.getString("CREATE_BY_");
        if(StringUtils.isBlank(applyUserId)) {
            applyUserId=ContextUtil.getCurrentUserId();
        }
        List<JSONObject> deptResps = commonInfoDao.queryDeptRespByUserId(applyUserId);
        if (deptResps != null && !deptResps.isEmpty()) {
            for (JSONObject depRespMan : deptResps) {
                users.add(new TaskExecutor(depRespMan.getString("USER_ID_"), depRespMan.getString("FULLNAME_")));
            }
        }



        return users;
    }

    // 从表单获产品负责人
    public Collection<TaskExecutor> getProductManager() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson != null && StringUtils.isNotBlank(formDataJson.getString("productManager"))
            && StringUtils.isNotBlank(formDataJson.getString("productManagerName"))) {
            TaskExecutor oneUser = new TaskExecutor(formDataJson.getString("productManager"),
                formDataJson.getString("productManagerName"));
            users.add(oneUser);
        }
        return users;
    }

}
