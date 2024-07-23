package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.serviceEngineering.core.dao.MaintenanceManualMctDao;
import com.redxun.serviceEngineering.core.dao.MaintenanceManualfileDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SparepartsConsultationScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(SparepartsConsultationScript.class);
    @Autowired
    private SparepartsConsultationService sparepartsConsultationService;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;

    //..
    public Collection<TaskExecutor> getCconfirmingProductUsers() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String coordinatorId = formDataJson.getString("coordinatorId");
        String coordinator = formDataJson.getString("coordinator");
        users.add(new TaskExecutor(coordinatorId, coordinator));
        return users;
    }

    //..
    public Collection<TaskExecutor> getEconfirmingGssUsers() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String gsserId = formDataJson.getString("gsserId");
        String gsser = formDataJson.getString("gsser");
        users.add(new TaskExecutor(gsserId, gsser));
        return users;
    }

    //..提问者填写：以任何形式进入
    //..技术支持人员审核：以任何形式进入
    //..产品所审核：以任何形式进入
    //..技术支持审核GSS：以任何形式进入
    //..GSS补充：以任何形式进入
    public void taskCreateScript(Map<String, Object> vars) {
        //@lwgkiller:阿诺多罗 下面这个cmd可以取代getDetail，而且传来的是表单的最新数据
        JSONObject jsonObject = ProcessHandleHelper.getProcessCmd().getJsonDataObject();
        String activitiId = vars.get("activityId").toString();
        jsonObject.put("businessStatus", activitiId);
        jsonObject.put("businessStatusTimestamp", DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_FULL));
        sparepartsConsultationService.updateBusiness(jsonObject);
    }

    //..提问者填写：以通过的形式结束
    //..技术支持人员审核：以通过形式结束
    //..技术支持审核GSS：以通过的形式结束
    //..GSS补充：以通过的形式结束
    public void taskEndScript(Map<String, Object> vars) {
        //@lwgkiller:阿诺多罗 下面这个cmd可以取代getDetail，而且传来的是表单的最新数据
        JSONObject jsonObject = ProcessHandleHelper.getProcessCmd().getJsonDataObject();
        String activitiId = vars.get("activityId").toString();
        //如果结束的是提问者填写，applyUser,applyDep自动生成，其实和service的create方法冗余了
        if (activitiId.equalsIgnoreCase("A-editing")) {
            jsonObject.put("applyUserId", ContextUtil.getCurrentUserId());
            jsonObject.put("applyUser", ContextUtil.getCurrentUser().getFullname());
            jsonObject.put("applyDepId", ContextUtil.getCurrentUser().getMainGroupId());
            jsonObject.put("applyDep", ContextUtil.getCurrentUser().getMainGroupName());
            jsonObject.put("submitTimestamp", DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_FULL));
        }//如果结束的是技术支持人员审核且不用确认，记录最终状态
        else if (activitiId.equalsIgnoreCase("B-confirming") &&
                jsonObject.getString("isCoordinate").equals("否")) {
            jsonObject.put("businessStatus", "Z-close");
        }//如果结束的是技术支持审核GSS且不用补充GSS，记录最终状态
        else if (activitiId.equalsIgnoreCase("D-confirming") &&
                jsonObject.getString("isGss").equals("否")) {
            jsonObject.put("businessStatus", "Z-close");
        }//如果结束的是补充GSS，记录最终状态
        else if (activitiId.equalsIgnoreCase("E-confirmingGss")) {
            jsonObject.put("businessStatus", "Z-close");
        }
        sparepartsConsultationService.updateBusiness(jsonObject);
    }

    //是
    public boolean isCoordinate(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        if (formDataJson.getString("isCoordinate").equalsIgnoreCase("是")) {
            return true;
        } else {
            return false;
        }
    }

    //否
    public boolean isNoCoordinate(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        if (formDataJson.getString("isCoordinate").equalsIgnoreCase("否")) {
            return true;
        } else {
            return false;
        }
    }

    //是
    public boolean isGss(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        if (formDataJson.getString("isGss").equalsIgnoreCase("是")) {
            return true;
        } else {
            return false;
        }
    }

    //否
    public boolean isNoGss(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        if (formDataJson.getString("isGss").equalsIgnoreCase("否")) {
            return true;
        } else {
            return false;
        }
    }
}
