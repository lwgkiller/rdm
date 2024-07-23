package com.redxun.fzsj.core.service;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.fzsj.core.dao.FzsjDao;

@Service
public class FzsjHandler implements ProcessStartPreHandler, TaskPreHandler, ProcessStartAfterHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(FzsjHandler.class);
    @Autowired
    private FzsjService fzsjService;
    @Autowired
    private FzsjDao fzsjDao;

    // 整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        String fzsjId = createOrUpdateCjzgByFormData(processStartCmd);
        if (StringUtils.isNotBlank(fzsjId)) {
            processStartCmd.setBusinessKey(fzsjId);
        }
    }

    // 任务审批之后的前置处理器
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd) iExecutionCmd;
        createOrUpdateCjzgByFormData(processNextCmd);
    }

    // 驳回场景cmd中没有表单数据
    private String createOrUpdateCjzgByFormData(AbstractExecutionCmd cmd) {
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
        String ldjsspsj = formDataJson.getString("ldjsspsj");
        if (StringUtils.isNotBlank(ldjsspsj)) {
            Date date = formDataJson.getDate("ldjsspsj");
            formDataJson.put("ldjsspsj", date);
        }
        if (StringUtils.isBlank(formDataJson.getString("id"))) {
            fzsjService.createFzsj(formDataJson);
        } else {
            fzsjService.updateFzsj(formDataJson);
        }
        return formDataJson.getString("id");
    }

    // 流程成功结束之后的处理
    @Override
    public void endHandle(BpmInst bpmInst) {
        String applyId = bpmInst.getBusKey();
    }

    /**
     * 流程后处理器，更新申请时间和仿真编号
     */
    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd cmd, BpmInst bpmInst) {
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
            formDataJson.put("id", cmd.getBusinessKey());
        }
        formDataJson.put("applyTime", new Date());
        // 仿真编号提交后自动生成
        String fzNumber = fzNumberGenerate(formDataJson);
        formDataJson.put("fzNumber", fzNumber);
        fzsjService.updateFzsj(formDataJson);
        return formDataJson.getString("id");
    }

    // 生成仿真编号
    private String fzNumberGenerate(JSONObject formDataJson) {
        StringBuilder result = new StringBuilder("WJ-");
        String yearMonth = DateFormatUtil.format(new Date(), "yyyyMM");
        result.append(yearMonth + "-");
        String fzlyCode = "?";
        JSONObject fzlyObj = fzsjDao.queryFZLYEnum(formDataJson.getString("fzlbId"));
        if (fzlyObj != null && StringUtils.isNotBlank(fzlyObj.getString("fieldDesc"))) {
            fzlyCode = fzlyObj.getString("fieldDesc");
        }
        result.append(fzlyCode + "-");
        String nowYear = DateFormatUtil.format(new Date(), "yyyy");
        String finalNumber = "001";
        Map<String, Object> param = new HashMap<>();
        param.put("year", nowYear);
        JSONObject maxNum = fzsjDao.queryNowYearMaxNum(param);
        if (maxNum != null) {
            int existNum = maxNum.getIntValue("num");
            finalNumber = new DecimalFormat("000").format(existNum + 1);
        }
        result.append(finalNumber);
        return result.toString();
    }
}
