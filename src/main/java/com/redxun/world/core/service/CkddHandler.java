package com.redxun.world.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.world.core.dao.CkddDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class CkddHandler
    implements ProcessStartPreHandler, ProcessStartAfterHandler, TaskPreHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(CkddHandler.class);
    @Autowired
    private CkddService ckddService;
    @Autowired
    private CkddDao ckddDao;

    // 整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        String ckddId = createOrUpdateCkddByFormData(processStartCmd);
        if (StringUtils.isNotBlank(ckddId)) {
            processStartCmd.setBusinessKey(ckddId);
        }
    }

    // 第一个任务创建之后流程的后置处理器 （进行编号的生成和更新） 类别-基地-时间-流水 示例：WJ-2022-XE215C-三位流水
    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd processStartCmd,
        BpmInst bpmInst) {
        String ckddId = processStartCmd.getBusinessKey();
        String formData = processStartCmd.getJsonData();
        if (StringUtils.isBlank(formData)) {
            logger.warn("formData is blank");
            return null;
        }
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return null;
        }
        if (StringUtils.isBlank(formDataJson.getString("ckddId"))) {
            formDataJson.put("ckddId", processStartCmd.getBusinessKey());
        }
        // rdm特殊订单申请编号提交后自动生成
        String rdmSpecialddNumber = rdmSpecialddNumberGenerate(formDataJson);
        formDataJson.put("rdmSpecialddNumber", rdmSpecialddNumber);
        ckddDao.updateNum(formDataJson);
        return ckddId;
    }
    private String rdmSpecialddNumberGenerate(JSONObject formDataJson){
        StringBuilder result = new StringBuilder("WJ-");
        String model = formDataJson.getString("model");
        String nowYear = DateFormatUtil.format(new Date(), "yyyy");
        String finalNumber = "001";
        Map<String, Object> param = new HashMap<>();
        param.put("year", nowYear);
        JSONObject maxNum = ckddDao.queryNowYearMaxNum(param);
        if (maxNum != null) {
            int existNum = maxNum.getIntValue("num");
            finalNumber = new DecimalFormat("000").format(existNum + 1);
        }
        result.append(XcmgProjectUtil.getNowLocalDateStr("yyyyMM"));
        result.append("-");
        result.append(model+"-");
        result.append(finalNumber);
        return result.toString();
    }

    // 任务审批之后的前置处理器
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd)iExecutionCmd;
        createOrUpdateCkddByFormData(processNextCmd);
    }

    // 流程成功结束之后的处理
    @Override
    public void endHandle(BpmInst bpmInst) {
        if ("SUCCESS_END".equalsIgnoreCase(bpmInst.getStatus())) {
            String ckddId = bpmInst.getBusKey();
            Map<String, Object> param = new HashMap<>();
            param.put("ckddId", ckddId);
        }
    }

    // 驳回场景cmd中没有表单数据
    private String createOrUpdateCkddByFormData(AbstractExecutionCmd cmd) {
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
        if (StringUtils.isBlank(formDataJson.getString("ckddId"))) {
            ckddService.createCkdd(formDataJson);
        } else {
            ckddService.updateCkdd(formDataJson);
        }
        return formDataJson.getString("ckddId");
    }
}
