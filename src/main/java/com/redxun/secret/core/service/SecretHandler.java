package com.redxun.secret.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.secret.core.dao.SecretDao;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SecretHandler
    implements ProcessStartPreHandler, ProcessStartAfterHandler, TaskPreHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(SecretHandler.class);
    @Autowired
    private SecretService secretService;
    @Autowired
    private SecretDao secretDao;


    // 整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        String secretId = createOrUpdateSecretByFormData(processStartCmd);
        if (StringUtils.isNotBlank(secretId)) {
            processStartCmd.setBusinessKey(secretId);
        }
    }

    // 第一个任务创建之后流程的后置处理器
    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd processStartCmd,
        BpmInst bpmInst) {
        String secretId = processStartCmd.getBusinessKey();
        String nowYearStart = DateFormatUtil.getNowByString("yyyy") + "-01-01";
        String nowYearStartUTC = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(nowYearStart), -8));
        Map<String, Object> param = new HashMap<>();
        param.put("applyTimeStart", nowYearStartUTC);
        param.put("countnumInfo", "yes");
//        int existNumber = secretDao.countSecretNumber(param);
//        int thisNumber = existNumber + 1;
        String numinfo="";
        String dateNum = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String newDate = dateNum.substring(0,4)+dateNum.substring(5,7)+dateNum.substring(8,10);
        List<JSONObject> maxNumberList = secretDao.queryMaxSecretNumber(param);
        if (maxNumberList == null || maxNumberList.isEmpty()) {
            numinfo = "XGWJJS" + newDate +  "0001";
        }else {
            int existNumber = Integer.parseInt(maxNumberList.get(0).getString("numinfo").substring(14));
            int thisNumber = existNumber + 1;
            if (thisNumber < 10) {
                numinfo= "XGWJJS" + newDate + "000" + thisNumber;
            }else if(thisNumber < 100&&thisNumber >= 10) {
                numinfo= "XGWJJS" + newDate + "00" +thisNumber;
            }else if(thisNumber < 1000&&thisNumber >= 100) {
                numinfo= "XGWJJS" + newDate + "0" +thisNumber;
            }else if(thisNumber < 10000&&thisNumber >= 1000) {
                numinfo= "XGWJJS" + newDate + thisNumber;
            }
        }
        param.clear();
        param.put("secretId",secretId);
        param.put("numinfo",numinfo);
        secretDao.updateSecretNum(param);
        return secretId;
    }

    // 任务审批之后的前置处理器
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd)iExecutionCmd;
        createOrUpdateSecretByFormData(processNextCmd);
    }

    // 流程成功结束之后的处理
    @Override
    public void endHandle(BpmInst bpmInst) {
        if ("SUCCESS_END".equalsIgnoreCase(bpmInst.getStatus())) {
            String secretId = bpmInst.getBusKey();
        }
    }

    // 驳回场景cmd中没有表单数据
    private String createOrUpdateSecretByFormData(AbstractExecutionCmd cmd) {
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
        if (StringUtils.isBlank(formDataJson.getString("secretId"))) {
            secretService.createSecret(formDataJson);
        } else {
            secretService.updateSecret(formDataJson);
        }
        return formDataJson.getString("secretId");
    }
}
