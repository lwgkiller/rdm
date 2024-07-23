package com.redxun.environment.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.environment.core.dao.RjbgDao;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
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
public class RjbgHandler
    implements ProcessStartPreHandler, ProcessStartAfterHandler, TaskPreHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(RjbgHandler.class);
    @Autowired
    private RjbgService rjbgService;
    @Autowired
    private RjbgDao rjbgDao;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;
    @Resource
    CommonInfoDao commonInfoDao;

    // 整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        String rjbgId = createOrUpdateRjbgByFormData(processStartCmd);
        if (StringUtils.isNotBlank(rjbgId)) {
            processStartCmd.setBusinessKey(rjbgId);
        }
    }

    // 第一个任务创建之后流程的后置处理器
    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd processStartCmd,
        BpmInst bpmInst) {
        String rjbgId = processStartCmd.getBusinessKey();
        JSONObject param = new JSONObject();
        param.put("rjbgId", rjbgId);
        String noticeNo = toGetRjbgNumber();
        param.put("noticeNo", noticeNo);
        rjbgDao.updateRjbgNumber(param);
        return rjbgId;
    }

    // 任务审批之后的前置处理器
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd)iExecutionCmd;
        createOrUpdateRjbgByFormData(processNextCmd);
    }

    // 流程成功结束之后的处理
    @Override
    public void endHandle(BpmInst bpmInst) {
        if ("SUCCESS_END".equalsIgnoreCase(bpmInst.getStatus())) {
            String rjbgId = bpmInst.getBusKey();
            StringBuilder userIdBuild = new StringBuilder();
            JSONObject rjbgJson = rjbgService.getRjbgDetail(rjbgId);
            String applyId = rjbgJson.getString("applyId");
            String dqId = rjbgJson.getString("dqId");
            String dlId = rjbgJson.getString("dlId");
            String kzId = rjbgJson.getString("kzId");
            List<JSONObject> deptResps = commonInfoDao.queryDeptRespByUserId(applyId);
            for (JSONObject oneDept:deptResps){
                String respId = oneDept.getString("USER_ID_");
                userIdBuild.append(respId).append(",");
            }
            userIdBuild.append(applyId).append(",");
            userIdBuild.append(dqId).append(",");
            userIdBuild.append(dlId).append(",");
            userIdBuild.append(kzId);
            JSONObject noticeObj = new JSONObject();
            String userIds = userIdBuild.toString();
            noticeObj.put("content", "流程编号:"+rjbgJson.getString("noticeNo")+"发动机软件开发申请流程已结束,请查看");
            sendDDNoticeManager.sendNoticeForCommon(userIds, noticeObj);
        }
    }

    // 驳回场景cmd中没有表单数据
    private String createOrUpdateRjbgByFormData(AbstractExecutionCmd cmd) {
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
        if (StringUtils.isBlank(formDataJson.getString("rjbgId"))) {
            rjbgService.createRjbg(formDataJson);
        } else {
            rjbgService.updateRjbg(formDataJson);
        }
        return formDataJson.getString("rjbgId");
    }

    public String toGetRjbgNumber() {
        String num = null;
        String nowYearStart = DateFormatUtil.getNowByString("yyyyMM");
        Map<String, Object> param = new HashMap<>();
        param.put("applyTimeStart", nowYearStart);
        JSONObject maxClhb = rjbgDao.queryMaxRjbgNum(param);
        int existNumber = 0;
        if (maxClhb != null) {
            existNumber = Integer.parseInt(maxClhb.getString("noticeNo").substring(13));
        }
        int thisNumber = existNumber + 1;
        String newDate = DateFormatUtil.getNowByString("yyyyMM");
        if (thisNumber < 10) {
            num = "EP-" + newDate + "-A-00" + thisNumber;
        } else if (thisNumber < 100 && thisNumber >= 10) {
            num = "EP-" + newDate + "-A-0" + thisNumber;
        }
        else if (thisNumber < 1000 && thisNumber >= 100) {
            num = "EP-" + newDate +"-A-"+ thisNumber;
        }
        return num;
    }

}
