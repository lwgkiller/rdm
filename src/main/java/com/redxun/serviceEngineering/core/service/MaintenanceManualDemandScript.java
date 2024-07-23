package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.bpm.core.entity.BpmTask;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.serviceEngineering.core.dao.MaintenanceManualDemandDao;
import com.redxun.serviceEngineering.core.dao.MaintenanceManualMctDao;
import com.redxun.serviceEngineering.core.dao.MaintenanceManualfileDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MaintenanceManualDemandScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(MaintenanceManualDemandScript.class);
    @Autowired
    private MaintenanceManualDemandService maintenanceManualDemandService;
    @Autowired
    private MaintenanceManualfileDao maintenanceManualfileDao;
    @Autowired
    private MaintenanceManualMctDao maintenanceManualMctDao;
    @Autowired
    private MaintenanceManualDemandDao maintenanceManualDemandDao;

    //..
    public Collection<TaskExecutor> getBconfirmingUsers() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String BconfirmingId = formDataJson.getString("BconfirmingId");
        String Bconfirming = formDataJson.getString("Bconfirming");
        users.add(new TaskExecutor(BconfirmingId, Bconfirming));
        return users;
    }

    //..编制：以任何形式进入
    //..确认：以任何形式进入
    //..产品主管确认：以任何形式进入
    //..新增中，变更中，翻译中：以任何形式进入
    //..打印：以任何形式进入
    public void taskCreateScript(Map<String, Object> vars) {
        //@lwgkiller:阿诺多罗 下面这个cmd可以取代getDetail，而且传来的是表单的最新数据
        JSONObject jsonObject = ProcessHandleHelper.getProcessCmd().getJsonDataObject();
//        JSONObject jsonObject = maintenanceManualDemandService.getDetail(vars.get("busKey").toString());
        String activitiId = vars.get("activityId").toString();
        jsonObject.put("businessStatus", activitiId);
        //如果进入的是产品主管确认状态，清空指令
        if (activitiId.equalsIgnoreCase("C-confirmingProduct")) {
            jsonObject.put("instructions", "");
        }
        maintenanceManualDemandService.updateBusiness(jsonObject);
    }

    //..编制：以通过的形式结束
    //..确认：以通过形式结束
    //..产品主管确认：以通过形式结束
    //..打印：以通过的形式结束，记录最终状态；记录相关归档文件引用日志。
    public void taskEndScript(Map<String, Object> vars) {
        //@lwgkiller:阿诺多罗 下面这个cmd可以取代getDetail，而且传来的是表单的最新数据
        JSONObject jsonObject = ProcessHandleHelper.getProcessCmd().getJsonDataObject();
//        JSONObject jsonObject = maintenanceManualDemandService.getDetail(vars.get("busKey").toString());
        String activitiId = vars.get("activityId").toString();
        if (activitiId.equalsIgnoreCase("A-editing")) {//如果结束的是编辑状态，applyUser,applyDep自动生成
            jsonObject.put("applyUserId", ContextUtil.getCurrentUserId());
            jsonObject.put("applyUser", ContextUtil.getCurrentUser().getFullname());
            jsonObject.put("applyDepId", ContextUtil.getCurrentUser().getMainGroupId());
            jsonObject.put("applyDep", ContextUtil.getCurrentUser().getMainGroupName());
        } else if (activitiId.equalsIgnoreCase("B-confirming")) {//如果结束的是确认状态，BconfirmingId,Bconfirming自动生成
            jsonObject.put("BconfirmingId", ContextUtil.getCurrentUserId());
            jsonObject.put("Bconfirming", ContextUtil.getCurrentUser().getFullname());
        } else if (activitiId.equalsIgnoreCase("C-confirmingProduct")) {//如果结束的是产品主管确认状态，则源文件可用的匹配记录REF_ID_=2
            JSONObject params = new JSONObject();
            params.put("businessId", jsonObject.getString("id"));
            List<JSONObject> manualMatchList = maintenanceManualDemandDao.getManualMatchList(params);
            for (JSONObject manualMatch : manualMatchList) {
                if (!manualMatch.containsKey("REF_ID_") || !manualMatch.getString("REF_ID_").equalsIgnoreCase("1")) {
                    manualMatch.put("REF_ID_", "2");
                    maintenanceManualDemandDao.updateManualMatchRefId(manualMatch);
                }
            }
        } else if (activitiId.equals("E-printing")) {//如果结束的是打印状态，记录最终状态；记录相关归档文件引用日志。
            jsonObject.put("businessStatus", "F-close");
            JSONObject params = new JSONObject();
            params.put("businessId", jsonObject.getString("id"));
            List<JSONObject> manualMatchListDemand = maintenanceManualDemandDao.getManualMatchList(params);//这个方法是共用的，此处找的是Demand的匹配明细
            if (!manualMatchListDemand.isEmpty()) {
                for (JSONObject manualMatch : manualMatchListDemand) {
                    JSONObject maintenanceManualfileLog = new JSONObject();
                    maintenanceManualfileLog.put("id", IdUtil.getId());
                    maintenanceManualfileLog.put("mainId", manualMatch.getString("manualFileId"));
                    maintenanceManualfileLog.put("demandId", jsonObject.getString("id"));
                    maintenanceManualfileDao.insertLog(maintenanceManualfileLog);
                }
            }
        }
        maintenanceManualDemandService.updateBusiness(jsonObject);
    }

    //..是否直接打印
    public boolean isPrint(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        if (formDataJson.getString("instructions").equalsIgnoreCase("打印")) {
            return true;
        } else {
            return false;
        }
    }

    //..是否产品主管确认
    public boolean isConfirmingProduct(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        if (formDataJson.getString("instructions").equalsIgnoreCase("产品主管确认")) {
            return true;
        } else {
            return false;
        }
    }

    //..是否新增
    public boolean isMake(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        if (formDataJson.getString("instructions").equalsIgnoreCase("新增")) {
            return true;
        } else {
            return false;
        }
    }

    //..是否变更
    public boolean isChange(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        if (formDataJson.getString("instructions").equalsIgnoreCase("变更")) {
            return true;
        } else {
            return false;
        }
    }

    //..是否翻译
    public boolean isTranslate(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        if (formDataJson.getString("instructions").equalsIgnoreCase("翻译")) {
            return true;
        } else {
            return false;
        }
    }

    //..是否源文件可用
    public boolean isAvailable(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        if (formDataJson.getString("instructions").equalsIgnoreCase("原文件可用")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @lwgkiller:阿诺多罗。此处控制产品主管确认的办理页面 1.要想打开产品主管确认办理页面，并点击办理，本脚本必须通过，条件是不存在demandId和vars.buskey一样的MCT申请
     */
    public boolean isCPZGQuerenOk(Map<String, Object> vars, BpmTask task) {
        JSONObject params = new JSONObject();
        params.put("demandId", vars.get("busKey").toString());
        if (maintenanceManualMctDao.dataListQuery(params).size() > 0) {
            return false;
        } else {
            return true;
        }
    }
}
