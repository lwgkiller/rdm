package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.bpm.core.entity.BpmTask;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.serviceEngineering.core.dao.MaintenanceManualMctDao;
import com.redxun.serviceEngineering.core.dao.MaintenanceManualfileDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
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
public class MaintenanceManualFeedbackScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(MaintenanceManualFeedbackScript.class);
    @Autowired
    private MaintenanceManualFeedbackService maintenanceManualFeedbackService;
    @Autowired
    private MaintenanceManualfileDao maintenanceManualfileDao;
    @Autowired
    private MaintenanceManualMctDao maintenanceManualMctDao;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;

    //..先留着看看指定环节审批人不管用再用这个。事实证明，这个可以不用！
/*    public Collection<TaskExecutor> getBconfirmingUsers() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String BconfirmingId = formDataJson.getString("BconfirmingId");
        String Bconfirming = formDataJson.getString("Bconfirming");
        users.add(new TaskExecutor(BconfirmingId, Bconfirming));
        return users;
    }*/

    //..
    public Collection<TaskExecutor> getCconfirmingProductUsers() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String productSupervisorId = formDataJson.getString("productSupervisorId");
        String productSupervisor = formDataJson.getString("productSupervisor");
        users.add(new TaskExecutor(productSupervisorId, productSupervisor));
        return users;
    }

    //..编制：以任何形式进入
    //..确认：以任何形式进入
    //..产品主管确认：以任何形式进入
    //..MCT进行中：以任何形式进入
    //..再次确认信息：以任何形式进入
    public void taskCreateScript(Map<String, Object> vars) {
        //@lwgkiller:阿诺多罗 下面这个cmd可以取代getDetail，而且传来的是表单的最新数据
        JSONObject jsonObject = ProcessHandleHelper.getProcessCmd().getJsonDataObject();
        String activitiId = vars.get("activityId").toString();
        jsonObject.put("businessStatus", activitiId);
        if (activitiId.equals("C-confirmingProduct")) {
            jsonObject.put("isHasProblem", "");
        }
        maintenanceManualFeedbackService.updateBusiness(jsonObject);
    }

    //..编制：以通过的形式结束,applyUser,applyDep自动生成
    //..确认：以通过形式结束,BconfirmingId,Bconfirming自动生成
    //..再次确认：以通过的形式结束
    public void taskEndScript(Map<String, Object> vars) {
        //@lwgkiller:阿诺多罗 下面这个cmd可以取代getDetail，而且传来的是表单的最新数据
        JSONObject jsonObject = ProcessHandleHelper.getProcessCmd().getJsonDataObject();
        String activitiId = vars.get("activityId").toString();
        //如果结束的是编辑状态，applyUser,applyDep自动生成
        if (activitiId.equalsIgnoreCase("A-editing")) {
            jsonObject.put("applyUserId", ContextUtil.getCurrentUserId());
            jsonObject.put("applyUser", ContextUtil.getCurrentUser().getFullname());
            jsonObject.put("applyDepId", ContextUtil.getCurrentUser().getMainGroupId());
            jsonObject.put("applyDep", ContextUtil.getCurrentUser().getMainGroupName());
        }//如果结束的是确认状态，BconfirmingId,Bconfirming自动生成，isHasProblem清空
        else if (activitiId.equalsIgnoreCase("B-confirming")) {
            jsonObject.put("BconfirmingId", ContextUtil.getCurrentUserId());
            jsonObject.put("Bconfirming", ContextUtil.getCurrentUser().getFullname());
            if (jsonObject.getString("isHasProblem").equals("否")) {
                jsonObject.put("businessStatus", "F-close");
                sendDingDing(jsonObject, "suhuijie,haofei");
            }
        }//如果结束的是再次确认状态，记录最终状态。
        else if (activitiId.equals("E-confirming")) {
            jsonObject.put("businessStatus", "F-close");
            sendDingDing(jsonObject, "suhuijie,haofei");
        }
        maintenanceManualFeedbackService.updateBusiness(jsonObject);
    }

    //是，没有问题
    public boolean isNoProblem(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        if (formDataJson.getString("isHasProblem").equalsIgnoreCase("否")) {
            return true;
        } else {
            return false;
        }
    }

    //否，有问题
    public boolean isHasProblem(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        if (formDataJson.getString("isHasProblem").equalsIgnoreCase("是")) {
            return true;
        } else {
            return false;
        }
    }

    //..
    public void sendDingDing(JSONObject jsonObject, String userIds) {
        JSONObject noticeObj = new JSONObject();
        StringBuilder stringBuilder = new StringBuilder("单号为:[");
        stringBuilder.append(jsonObject.getString("busunessNo"))
                .append("]销售型号为:[").append(jsonObject.getString("salesModel"))
                .append("]设计型号为:[").append(jsonObject.getString("designModel"))
                .append("]的操保手册异常反馈流程已经处理完毕。问题答复:[")
                .append(jsonObject.getString("problemDescription")).append("]。")
                .append(XcmgProjectUtil.getNowLocalDateStr(DateUtil.DATE_FORMAT_FULL));
        noticeObj.put("content", stringBuilder.toString());
        sendDDNoticeManager.sendNoticeForCommon(userIds, noticeObj);
    }
}
