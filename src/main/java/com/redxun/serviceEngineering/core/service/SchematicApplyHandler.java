package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.serviceEngineering.core.dao.SchematicApplyDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SchematicApplyHandler
    implements ProcessStartPreHandler, ProcessStartAfterHandler, TaskPreHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(SchematicApplyHandler.class);
    @Autowired
    private SchematicApplyService schematicApplyService;
    @Autowired
    private SchematicApplyDao schematicApplyDao;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;
    @Resource
    CommonInfoDao commonInfoDao;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;

    // 整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        String id = createOrUpdateSchematicApplyByFormData(processStartCmd);
        if (StringUtils.isNotBlank(id)) {
            processStartCmd.setBusinessKey(id);
        }
    }

    // 第一个任务创建之后流程的后置处理器
    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd processStartCmd,
        BpmInst bpmInst) {
        String id = processStartCmd.getBusinessKey();
        JSONObject param = new JSONObject();
        param.put("id", id);
        String noticeNo = toGetSchematicApplyNumber();
        param.put("noticeNo", noticeNo);
        schematicApplyDao.updateSchematicApplyNumber(param);
        return id;
    }

    // 任务审批之后的前置处理器
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd)iExecutionCmd;
        createOrUpdateSchematicApplyByFormData(processNextCmd);
    }

    // 流程成功结束之后的处理
    @Override
    public void endHandle(BpmInst bpmInst) {
        if ("SUCCESS_END".equalsIgnoreCase(bpmInst.getStatus())) {
            String id = bpmInst.getBusKey();
            JSONObject rjssJson = schematicApplyService.getSchematicApplyDetail(id);
            String noticeNo = rjssJson.getString("noticeNo");
            String modelName = rjssJson.getString("modelName");
            String machineNum = rjssJson.getString("machineNum");

            Map<String, Object> roleParams = new HashMap<>();
            roleParams.put("groupName", "原理图需求申请专员");
            roleParams.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            List<Map<String, String>> userInfos = xcmgProjectOtherDao.queryUserByGroupName(roleParams);
            List<Map<String, String>> depRespMans = rdmZhglUtil.queryFwgcs();
            if (depRespMans != null && !depRespMans.isEmpty()) {
                for (Map<String, String> depRespMan : depRespMans) {
                    JSONObject noticeObj = new JSONObject();
                    StringBuilder stringBuilder = new StringBuilder("【原理图需求申请通知】：");
                    stringBuilder.append("单据编号：").append(noticeNo).append("，设计型号：").append(modelName).append("，整机编号：")
                            .append(machineNum).append("，原理图需求申请流程已通过");
                    noticeObj.put("content", stringBuilder.toString());
                    sendDDNoticeManager.sendNoticeForCommon(depRespMan.get("PARTY2_"), noticeObj);
                }
            }
            if (userInfos.size() > 0) {
                for (Map<String, String> userMap : userInfos) {
                    JSONObject noticeObj = new JSONObject();
                    StringBuilder stringBuilder = new StringBuilder("【原理图需求申请通知】：");
                    stringBuilder.append("单据编号：").append(noticeNo).append("，设计型号：").append(modelName).append("，整机编号：")
                            .append(machineNum).append("，原理图需求申请流程已通过");
                    noticeObj.put("content", stringBuilder.toString());
                    sendDDNoticeManager.sendNoticeForCommon(userMap.get("USER_ID_"), noticeObj);
                } ;
            }
        }
    }

    // 驳回场景cmd中没有表单数据
    private String createOrUpdateSchematicApplyByFormData(AbstractExecutionCmd cmd) {
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
        if (StringUtils.isBlank(formDataJson.getString("id"))) {
            schematicApplyService.createSchematicApply(formDataJson);
        } else {
            schematicApplyService.updateSchematicApply(formDataJson);
        }
        return formDataJson.getString("id");
    }

    public String toGetSchematicApplyNumber() {
        String num = null;
        String nowYearStart = DateFormatUtil.getNowByString("yyyyMMdd");
        Map<String, Object> param = new HashMap<>();
        param.put("applyTimeStart", nowYearStart);
        JSONObject maxClhb = schematicApplyDao.queryMaxSchematicApplyNum(param);
        int existNumber = 0;
        if (maxClhb != null) {
            existNumber = Integer.parseInt(maxClhb.getString("noticeNo").substring(14));
        }
        int thisNumber = existNumber + 1;
        String newDate = DateFormatUtil.getNowByString("yyyyMMdd");
        if (thisNumber < 10) {
            num = "WJYLT" + newDate + "-0" + thisNumber;
        } else if (thisNumber < 100 && thisNumber >= 10) {
            num = "WJYLT" + newDate  +"-"+ thisNumber;
        }
        return num;
    }

}
