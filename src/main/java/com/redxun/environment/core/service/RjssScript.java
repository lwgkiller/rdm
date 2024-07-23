package com.redxun.environment.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.environment.core.dao.RjssDao;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

// 项目流程节点的触发事件
@Service
public class RjssScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(RjssScript.class);

    @Autowired
    private CommonInfoDao commonInfoDao;
    @Autowired
    private RjssDao rjssDao;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;




    /**
     * 流程中，获取液压/控制/电气
     *
     * @return
     */
    public Collection<TaskExecutor> getSrz() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String yyIdStr = formDataJson.getString("yyId");
        if (StringUtils.isNotBlank(yyIdStr)) {
            String[] yyIds = yyIdStr.split(",");
            for (String yyId:yyIds){
                Map<String, Object> params = new HashMap<>();
                params.put("USER_ID_", yyId);
                List<Map<String, Object>> userMapInfo = xcmgProjectOtherDao.getUserInfoById(params);
                String yyName = userMapInfo.get(0).get("userName").toString();
                users.add(new TaskExecutor(yyId, yyName));
            }
        }
        String kzIdStr = formDataJson.getString("kzId");
        if (StringUtils.isNotBlank(kzIdStr)) {
            String[] kzIds = kzIdStr.split(",");
            for (String kzId:kzIds){
                Map<String, Object> params = new HashMap<>();
                params.put("USER_ID_", kzId);
                List<Map<String, Object>> userMapInfo = xcmgProjectOtherDao.getUserInfoById(params);
                String kzName = userMapInfo.get(0).get("userName").toString();
                users.add(new TaskExecutor(kzId, kzName));
            }
        }
        String dqIdStr = formDataJson.getString("dqId");
        if (StringUtils.isNotBlank(dqIdStr)) {
            String[] dqIds = dqIdStr.split(",");
            for (String dqId:dqIds){
                Map<String, Object> params = new HashMap<>();
                params.put("USER_ID_", dqId);
                List<Map<String, Object>> userMapInfo = xcmgProjectOtherDao.getUserInfoById(params);
                String dqName = userMapInfo.get(0).get("userName").toString();
                users.add(new TaskExecutor(dqId, dqName));
            }
        }
        return users;
    }


    /**
     * 流程中，获取动力工程师
     *
     * @return
     */
    public Collection<TaskExecutor> getDlszr() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String respManId = formDataJson.getString("dlId");
        if (StringUtils.isNotBlank(respManId)) {
            String respManName = formDataJson.getString("dlName");
            users.add(new TaskExecutor(respManId, respManName));
        }
        return users;
    }

    //产品主管
    public Collection<TaskExecutor> getCpzg() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        JSONArray tdmcDataJson = JSONObject.parseArray(formDataJson.getString("detailAll"));
        for (int i = 0; i < tdmcDataJson.size(); i++) {
            JSONObject oneObject = tdmcDataJson.getJSONObject(i);
            users.add(new TaskExecutor(oneObject.getString("cpzgId"), oneObject.getString("cpzgName")));
        }
        return users;
    }

    //产品主管所长
    public Collection<TaskExecutor> getCpzgLeader() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        JSONArray tdmcDataJson = JSONObject.parseArray(formDataJson.getString("detailAll"));
        for (int i = 0; i < tdmcDataJson.size(); i++) {
            JSONObject oneObject = tdmcDataJson.getJSONObject(i);
            String cpzgId = oneObject.getString("cpzgId");
            List<JSONObject> deptResps = commonInfoDao.queryDeptRespByUserId(cpzgId);
            if (deptResps != null && !deptResps.isEmpty()) {
                for (JSONObject depRespMan : deptResps) {
                    users.add(new TaskExecutor(depRespMan.getString("USER_ID_"), depRespMan.getString("FULLNAME_")));
                }
            }
        }
        return users;
    }

    //质量部专员
    public Collection<TaskExecutor> getZlzy() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String dlId = formDataJson.getString("zlId");
        if (StringUtils.isNotBlank(dlId)) {
            String dlName = formDataJson.getString("zlName");
            users.add(new TaskExecutor(dlId, dlName));
        }
        return users;
    }

    //服务部制造部专员
    public Collection<TaskExecutor> getFwZz() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String dlId = formDataJson.getString("zzId");
        if (StringUtils.isNotBlank(dlId)) {
            String dlName = formDataJson.getString("zzName");
            users.add(new TaskExecutor(dlId, dlName));
        }
        String kzId = formDataJson.getString("fwId");
        if (StringUtils.isNotBlank(kzId)) {
            String kzName = formDataJson.getString("fwName");
            users.add(new TaskExecutor(kzId, kzName));
        }
        return users;
    }

    public boolean isLeader(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        JSONArray tdmcDataJson = JSONObject.parseArray(formDataJson.getString("detailAll"));
        for (int i = 0; i < tdmcDataJson.size(); i++) {
            JSONObject oneObject = tdmcDataJson.getJSONObject(i);
            if("是".equals(oneObject.getString("isLeader"))){
                return true;
            }
        }
        return false;
    }
    public boolean noLeader(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        JSONArray tdmcDataJson = JSONObject.parseArray(formDataJson.getString("detailAll"));
        for (int i = 0; i < tdmcDataJson.size(); i++) {
            JSONObject oneObject = tdmcDataJson.getJSONObject(i);
            if("是".equals(oneObject.getString("isLeader"))){
                return false;
            }
        }
        return true;
    }
}

