package com.redxun.environment.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.environment.core.dao.RjssDao;
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
public class RjssHandler
    implements ProcessStartPreHandler, ProcessStartAfterHandler, TaskPreHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(RjssHandler.class);
    @Autowired
    private RjssService rjssService;
    @Autowired
    private RjssDao rjssDao;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;
    @Resource
    CommonInfoDao commonInfoDao;

    // 整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        String rjssId = createOrUpdateRjssByFormData(processStartCmd);
        if (StringUtils.isNotBlank(rjssId)) {
            processStartCmd.setBusinessKey(rjssId);
        }
    }

    // 第一个任务创建之后流程的后置处理器
    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd processStartCmd,
        BpmInst bpmInst) {
        String rjssId = processStartCmd.getBusinessKey();
        JSONObject param = new JSONObject();
        param.put("rjssId", rjssId);
        String noticeNo = toGetRjssNumber();
        param.put("noticeNo", noticeNo);
        rjssDao.updateRjssNumber(param);
        return rjssId;
    }

    // 任务审批之后的前置处理器
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd)iExecutionCmd;
        createOrUpdateRjssByFormData(processNextCmd);
    }

    // 流程成功结束之后的处理
    @Override
    public void endHandle(BpmInst bpmInst) {
        if ("SUCCESS_END".equalsIgnoreCase(bpmInst.getStatus())) {
            String rjssId = bpmInst.getBusKey();
            JSONObject rjssJson = rjssService.getRjssDetail(rjssId);
            StringBuilder userIdBuild = new StringBuilder();
            String applyId = rjssJson.getString("applyId");
            String dqId = rjssJson.getString("dqId");
            String dlId = rjssJson.getString("dlId");
            String kzId = rjssJson.getString("kzId");
            String yyId = rjssJson.getString("yyId");
            String fwId = rjssJson.getString("fwId");
            String zzId = rjssJson.getString("zzId");
            String zlId = rjssJson.getString("zlId");
            List<JSONObject> detailList = rjssService.queryDetail(rjssId);
            for (JSONObject oneDetail:detailList) {
                String cpzgId = oneDetail.getString("cpzgId");
                userIdBuild.append(cpzgId).append(",");
                List<JSONObject> deptResps = commonInfoDao.queryDeptRespByUserId(cpzgId);
                for (JSONObject oneDept:deptResps){
                    String respId = oneDept.getString("USER_ID_");
                    userIdBuild.append(respId).append(",");
                }
            }
            userIdBuild.append(applyId).append(",");
            userIdBuild.append(dqId).append(",");
            userIdBuild.append(dlId).append(",");
            userIdBuild.append(kzId).append(",");
            userIdBuild.append(yyId).append(",");
            userIdBuild.append(fwId).append(",");
            userIdBuild.append(zzId).append(",");
            userIdBuild.append(zlId);
            JSONObject noticeObj = new JSONObject();
            String userIds = userIdBuild.toString();
            noticeObj.put("content", "流程编号:"+rjssJson.getString("noticeNo")+"发动机软件变更实施流程已结束,请查看");
            sendDDNoticeManager.sendNoticeForCommon(userIds, noticeObj);
        }
    }

    // 驳回场景cmd中没有表单数据
    private String createOrUpdateRjssByFormData(AbstractExecutionCmd cmd) {
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
        if (StringUtils.isBlank(formDataJson.getString("rjssId"))) {
            rjssService.createRjss(formDataJson);
        } else {
            rjssService.updateRjss(formDataJson);
        }
        return formDataJson.getString("rjssId");
    }

    public String toGetRjssNumber() {
        String num = null;
        String nowYearStart = DateFormatUtil.getNowByString("yyyyMM");
        Map<String, Object> param = new HashMap<>();
        param.put("applyTimeStart", nowYearStart);
        JSONObject maxClhb = rjssDao.queryMaxRjssNum(param);
        int existNumber = 0;
        if (maxClhb != null) {
            existNumber = Integer.parseInt(maxClhb.getString("noticeNo").substring(13));
        }
        int thisNumber = existNumber + 1;
        String newDate = DateFormatUtil.getNowByString("yyyyMM");
        if (thisNumber < 10) {
            num = "EP-" + newDate + "-B-00" + thisNumber;
        } else if (thisNumber < 100 && thisNumber >= 10) {
            num = "EP-" + newDate + "-B-0" + thisNumber;
        }
        else if (thisNumber < 1000 && thisNumber >= 100) {
            num = "EP-" + newDate +"-B-"+ thisNumber;
        }
        return num;
    }

}
