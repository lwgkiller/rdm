package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.*;
import com.redxun.core.script.GroovyScript;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.LoginRecordManager;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.serviceEngineering.core.dao.DecorationManualCollectDao;
import com.redxun.serviceEngineering.core.dao.DecorationManualFileDao;
import com.redxun.serviceEngineering.core.dao.MaintenanceManualMctDao;
import com.redxun.serviceEngineering.core.dao.MaintenanceManualfileDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.sys.core.manager.SysSeqIdManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DecorationManualDemandScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(DecorationManualDemandScript.class);
    @Autowired
    private MaintenanceManualDemandService maintenanceManualDemandService;
    @Autowired
    private MaintenanceManualfileDao maintenanceManualfileDao;
    @Autowired
    private MaintenanceManualMctDao maintenanceManualMctDao;
    @Autowired
    private DecorationManualDemandService decorationManualDemandService;
    @Autowired
    private DecorationManualFileDao decorationManualFileDao;
    @Autowired
    private DecorationManualCollectDao decorationManualCollectDao;
    @Autowired
    private CommonInfoDao commonInfoDao;
    @Autowired
    private SysSeqIdManager sysSeqIdManager;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;
    @Autowired
    private LoginRecordManager loginRecordManager;

    //..
    public Collection<TaskExecutor> getCpzgDepLeader() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        List<JSONObject> list = new ArrayList<>();
        list = commonInfoDao.queryDeptRespByUserId(formDataJson.getString("cpzgId"));
        for (JSONObject jsonObject : list) {
            users.add(new TaskExecutor(jsonObject.getString("USER_ID_"), jsonObject.getString("FULLNAME_")));
        }
        return users;
    }

    //..
    public Collection<TaskExecutor> getCpzg() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        List<JSONObject> list = new ArrayList<>();
        users.add(new TaskExecutor(formDataJson.getString("cpzgId"), formDataJson.getString("cpzgName")));
        return users;
    }

    //..
    public void taskCreateScript(Map<String, Object> vars) {
        try {
            JSONObject jsonDataObject = ProcessHandleHelper.getProcessCmd().getJsonDataObject();
            String activitiId = vars.get("activityId").toString();
            jsonDataObject.put("businessStatus", activitiId);
            IExecutionCmd processCmd = ProcessHandleHelper.getProcessCmd();
            if (processCmd instanceof ProcessStartCmd) {
                ProcessStartCmd processStartCmd = (ProcessStartCmd) ProcessHandleHelper.getProcessCmd();
                processStartCmd.setJsonData(jsonDataObject.toJSONString());
            } else if (processCmd instanceof ProcessNextCmd) {
                ProcessNextCmd processNextCmd = (ProcessNextCmd) ProcessHandleHelper.getProcessCmd();
                processNextCmd.setJsonData(jsonDataObject.toJSONString());
            }
            decorationManualDemandService.updateBusiness(jsonDataObject);
        } catch (Exception e) {
            ProcessHandleHelper.addErrorMsg(e.getMessage());
            throw e;
        }
    }

    //..
    public void taskEndScript(Map<String, Object> vars) {
        try {
            JSONObject jsonDataObject = ProcessHandleHelper.getProcessCmd().getJsonDataObject();
            String activitiId = vars.get("activityId").toString();
            if (activitiId.equalsIgnoreCase("A")) {
                jsonDataObject.put("applyUserId", ContextUtil.getCurrentUserId());
                jsonDataObject.put("applyUser", ContextUtil.getCurrentUser().getFullname());
                jsonDataObject.put("applyDepId", ContextUtil.getCurrentUser().getMainGroupId());
                jsonDataObject.put("applyDep", ContextUtil.getCurrentUser().getMainGroupName());
                if (StringUtil.isEmpty(jsonDataObject.getString("busunessNo"))) {
                    jsonDataObject.put("busunessNo", sysSeqIdManager.genSequenceNo("decorationManualDemand", ContextUtil.getCurrentTenantId()));
                }
                jsonDataObject.put("applyTime", DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_YMD));
                decorationManualDemandService.updateBusiness(jsonDataObject);
            } else if (activitiId.equalsIgnoreCase("F")) {
                jsonDataObject.put("businessStatus", "Z");
                decorationManualDemandService.updateBusiness(jsonDataObject);
                sendDingDing(jsonDataObject, jsonDataObject.getString("applyUserId") + "," + jsonDataObject.getString("cpzgId"));
            }
        } catch (Exception e) {
            ProcessHandleHelper.addErrorMsg(e.getMessage());
            throw e;
        }
    }

    /**
     * @lwgkiller:阿诺多罗。此处控制执行中的办理页面 1.要想打开执行中办理页面，并点击办理，本脚本必须通过，
     * 条件是明细的状态必须都是“已归档”
     */
    public boolean isManualFileOk(Map<String, Object> vars, BpmTask task) {
        JSONObject params = new JSONObject();
        params.put("mainId", vars.get("busKey").toString());
        List<JSONObject> itemList = decorationManualDemandService.getItemList(params);
        for (JSONObject jsonObject : itemList) {
            if (!jsonObject.getString("businessStatus").equalsIgnoreCase(DecorationManualDemandService.ITEM_STATUS_CLEAR)) {
                return false;
            }
        }
        return true;
    }

    //..
    public void sendDingDing(JSONObject jsonObject, String userIds) {
        JSONObject noticeObj = new JSONObject();
        StringBuilder stringBuilder = new StringBuilder("【装修手册需求完成通知】:编号为 ");
        stringBuilder.append(jsonObject.getString("busunessNo"));
        stringBuilder.append(" 的装修手册需求已经完成").append(XcmgProjectUtil.getNowLocalDateStr(DateUtil.DATE_FORMAT_FULL));
        noticeObj.put("content", stringBuilder.toString());
        sendDDNoticeManager.sendNoticeForCommon(userIds, noticeObj);
    }

    //..是挖掘机械研究院的人发起
    public boolean isTec(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        JSONObject isJSZX = loginRecordManager.judgeIsJSZX(formDataJson.getString("applyDepId"), formDataJson.getString("applyDep"));
        boolean ssbmJszx = isJSZX.getBoolean("isJSZX");
        if (ssbmJszx) {
            return true;
        } else {
            return false;
        }
    }

    //..不是挖掘机械研究院的人发起
    public boolean isNotTec(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        JSONObject isJSZX = loginRecordManager.judgeIsJSZX(formDataJson.getString("applyDepId"), formDataJson.getString("applyDep"));
        boolean ssbmJszx = isJSZX.getBoolean("isJSZX");
        if (ssbmJszx) {
            return false;
        } else {
            return true;
        }
    }
}
