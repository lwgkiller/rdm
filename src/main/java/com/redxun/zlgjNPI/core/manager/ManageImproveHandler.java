package com.redxun.zlgjNPI.core.manager;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
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
import com.redxun.saweb.context.ContextUtil;
import com.redxun.zlgjNPI.core.dao.ManageImproveDao;

@Service
public class ManageImproveHandler implements ProcessStartPreHandler, TaskPreHandler, ProcessStartAfterHandler {

    private Logger logger = LoggerFactory.getLogger(ManageImproveHandler.class);

    @Autowired
    private ManageImproveDao manageImproveDao;
    @Autowired
    private ManageImproveService manageImproveService;

    // 整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd cmd) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        formDataJson.put("INST_ID_", cmd.getBpmInstId());
        cmd.setJsonData(formDataJson.toJSONString());
        String businessId = createOrUpdateByFormData(cmd);
        if (StringUtils.isNotBlank(businessId)) {
            cmd.setBusinessKey(businessId);
        }
    }

    // 任务审批之后的前置处理器
    @Override
    public void taskPreHandle(IExecutionCmd cmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd) cmd;
        createOrUpdateByFormData(processNextCmd);
    }


    // 流程启动之后执行,第一个任务创建之后流程的后置处理器 （进行编号的生成和更新） 类别-基地-时间-流水 示例：GLGJJY-2022-三位流水
    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd cmd, BpmInst bpmInst) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return null;
        }
		
        if (StringUtils.isBlank(formDataJson.getString("id"))) {
            if (StringUtils.isBlank(formDataJson.getString("id"))) {
                formDataJson.put("id", cmd.getBusinessKey());
            }
        }
        // 管理改进建议编号提交后自动生成
        String suggestionApplynum = suggestionApplynumGenerate(formDataJson);
        formDataJson.put("suggestionApplynum", suggestionApplynum);
        manageImproveService.updateManageImprove(formDataJson);
        formDataJson.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formDataJson.put("UPDATE_TIME_", new Date());

        return formDataJson.getString("id");
    }

    private String suggestionApplynumGenerate(JSONObject formDataJson){
        StringBuilder result = new StringBuilder("GLGJJY-");
        String nowYear = DateFormatUtil.format(new Date(), "yyyy");
        String finalNumber = "001";
        Map<String, Object> param = new HashMap<>();
        param.put("year", nowYear);
        JSONObject maxNum = manageImproveDao.queryNowYearMaxNum(param);
        if (maxNum != null) {
            int existNum = maxNum.getIntValue("num");
            finalNumber = new DecimalFormat("000").format(existNum + 1);
        }
        result.append(XcmgProjectUtil.getNowLocalDateStr("yyyy"));
        result.append("-");
        result.append(finalNumber);
        return result.toString();
    }
    //..
    private String createOrUpdateByFormData(AbstractExecutionCmd cmd) {
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
        manageImproveService.createManageImprove(formDataJson);
        } else {
        manageImproveService.updateManageImprove(formDataJson);
        }
        return formDataJson.getString("id");
    }

}
