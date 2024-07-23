package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.org.manager.OsUserManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SparepartsConsultationHandler
        implements ProcessStartPreHandler, TaskPreHandler, ProcessStartAfterHandler ,ProcessEndHandler{
    private Logger logger = LoggerFactory.getLogger(SparepartsConsultationHandler.class);
    @Autowired
    private SparepartsConsultationService sparepartsConsultationService;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private OsUserManager osUserManager;

    //..整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        String formData = processStartCmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        formDataJson.put("INST_ID_", processStartCmd.getBpmInstId());
        processStartCmd.setJsonData(formDataJson.toJSONString());
        String businessId = createOrUpdateBusinessByFormData(processStartCmd);
        if (StringUtils.isNotBlank(businessId)) {
            processStartCmd.setBusinessKey(businessId);
        }
    }

    //..整个流程启动之后的处理，由于此处可能存在草稿直接提交，processStartPreHandle会没有InstId，所以需要在这里再处理
    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd cmd, BpmInst bpmInst) {
        //@lwgkiller:阿诺多罗，千万注意这里的坑，此处processStartCmd.getJsonData()仍然是一个没有id的原始纪录，和processStartPreHandle
        //用的是同一个，并没有重新读取，因此所有在processStartPreHandle下处理增加记录自动产生的字段并没有重新读取带到这里来，这就造成还会被当成
        //新纪录重复创建。这里不得不用getDetail方法重新读取一下。
        JSONObject jsonObject = sparepartsConsultationService.getDetail(cmd.getBusinessKey());
        jsonObject.put("INST_ID_", bpmInst.getInstId());
        cmd.setJsonData(jsonObject.toJSONString());
        return createOrUpdateBusinessByFormData(cmd);
    }

    //..任务审批前置处理器,只有第一个编辑节点需要，因为要更新业务单据
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd) iExecutionCmd;
        createOrUpdateBusinessByFormData(processNextCmd);
    }

    //..
    private String createOrUpdateBusinessByFormData(AbstractExecutionCmd cmd) {
        String formData = cmd.getJsonData();
        if (StringUtils.isBlank(formData)) {
            logger.warn("formData is blank");
            return null;
        }
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return null;
        }
        //如果是新增的情况
        if (StringUtils.isBlank(formDataJson.getString("id"))) {
            sparepartsConsultationService.createBusiness(formDataJson);
        } else {
            sparepartsConsultationService.updateBusiness(formDataJson);
        }
        return formDataJson.getString("id");
    }

    @Override
    public void endHandle(BpmInst bpmInst) {
        JSONObject jsonObject = sparepartsConsultationService.getDetail(bpmInst.getBusKey());
        String receiverNoString = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringGroups", "sparepartsConsultation").getValue();
        String[] receiverNos = receiverNoString.split(",");
        StringBuilder stringBuilder = new StringBuilder();
        if (receiverNos.length > 0) {
            for (String userNo : receiverNos) {
                stringBuilder.append(osUserManager.getByUserName(userNo).getUserId());
                stringBuilder.append(",");
            }
        }
        stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
        sendDingDing(jsonObject, stringBuilder.toString());
    }

    //..
    public void sendDingDing(JSONObject jsonObject, String userIds) {
        JSONObject noticeObj = new JSONObject();
        StringBuilder stringBuilder = new StringBuilder("【备件问题咨询完成通知】:整机编号为'")
                .append(jsonObject.getString("pin"))
                .append("';设计型号为'")
                .append(jsonObject.getString("designModel"))
                .append("';销售型号为'")
                .append(jsonObject.getString("salesModel"))
                .append("';问题名称为'")
                .append(jsonObject.getString("problemSummary"))
                .append("'的备件问题咨询业务已经完成，请及时查阅！")
                .append(XcmgProjectUtil.getNowLocalDateStr(DateUtil.DATE_FORMAT_FULL));
        noticeObj.put("content", stringBuilder.toString());
        sendDDNoticeManager.sendNoticeForCommon(userIds, noticeObj);
    }


}
