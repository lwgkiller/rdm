package com.redxun.environment.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.environment.core.dao.RjbgglDao;
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
import java.util.Map;

@Service
public class RjbgglHandler
    implements ProcessStartPreHandler, ProcessStartAfterHandler, TaskPreHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(RjbgglHandler.class);
    @Autowired
    private RjbgglService rjbgglService;
    @Autowired
    private RjbgglDao rjbgglDao;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;
    @Resource
    CommonInfoDao commonInfoDao;

    // 整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        String id = createOrUpdateRjbgglByFormData(processStartCmd);
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
//        String noticeNo = toGetRjbgglNumber();
//        param.put("noticeNo", noticeNo);
//        rjbgglDao.updateRjbgglNumber(param);
        return id;
    }

    // 任务审批之后的前置处理器
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd)iExecutionCmd;
        createOrUpdateRjbgglByFormData(processNextCmd);
    }

    // 流程成功结束之后的处理
    @Override
    public void endHandle(BpmInst bpmInst) {
        if ("SUCCESS_END".equalsIgnoreCase(bpmInst.getStatus())) {
        }
    }

    // 驳回场景cmd中没有表单数据
    private String createOrUpdateRjbgglByFormData(AbstractExecutionCmd cmd) {
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
            rjbgglService.createRjbggl(formDataJson);
        } else {
            rjbgglService.updateRjbggl(formDataJson);
        }
        return formDataJson.getString("id");
    }

    public String toGetRjbgglNumber() {
        String num = null;
        String nowYearStart = DateFormatUtil.getNowByString("yyyyMM");
        Map<String, Object> param = new HashMap<>();
        param.put("applyTimeStart", nowYearStart);
        JSONObject maxClhb = rjbgglDao.queryMaxRjbgglNum(param);
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
