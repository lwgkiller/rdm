package com.redxun.rdmZhgl.core.service;

import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONArray;
import com.redxun.gkgf.core.dao.GkgfApplyDao;
import com.redxun.rdmZhgl.core.dao.SaleFileOMAApplyDao;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;

/**
 * @author zwt
 * */
@Service
public class SaleFileOMAApplyHandler implements ProcessStartPreHandler, TaskPreHandler, ProcessStartAfterHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(SaleFileOMAApplyHandler.class);
    @Resource
    private SaleFileOMAApplyService saleFileOMAApplyService;
    @Resource
    private SaleFileOMAApplyDao saleFileOMAApplyDao;
    /**
     * 整个流程启动之前的处理，草稿也会调用这里
     * */
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        logger.info("ProcessApplyHandler processStartPreHandle");
        Map<String, Object> changeApply = addOrUpdateApplyInfo(processStartCmd);
        if (changeApply != null) {
            processStartCmd.setBusinessKey(changeApply.get("id").toString());
        }
    }

    /**
     * 任务审批之后的前置处理器id
     * */
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        logger.info("taskPreHandle");
        ProcessNextCmd processNextCmd = (ProcessNextCmd)iExecutionCmd;
        addOrUpdateApplyInfo(processNextCmd);
    }

    /**
     * 驳回场景cmd中没有表单数据
     * */
    private Map<String, Object> addOrUpdateApplyInfo(AbstractExecutionCmd cmd) {
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

        Map<String, Object> applyObj = JSONObject.parseObject(formDataJson.toJSONString(), Map.class);
        //增加关联明细
        JSONArray changeitemDataJson = formDataJson.getJSONArray("SUB_itemListGrid");
        JSONArray jsonArray2 = new JSONArray();
        for (int i = 0; i < changeitemDataJson.size(); i++) {

            JSONObject oneObject = changeitemDataJson.getJSONObject(i);
            if (!oneObject.containsKey("_state") ||
                    oneObject.getString("_state").equalsIgnoreCase("added") ||
                    oneObject.getString("_state").equalsIgnoreCase("modified")){
                oneObject.remove("_state");
                oneObject.remove("_id");
                oneObject.remove("_uid");
                jsonArray2.add(oneObject);
            }
        }
        applyObj.put("note",jsonArray2.toString());
        if (applyObj.get("id") == null || StringUtils.isBlank(applyObj.get("id").toString())) {
            saleFileOMAApplyService.add(applyObj);
        } else {
            saleFileOMAApplyService.update(applyObj);
        }
        return applyObj;
    }
    // 流程成功结束之后的处理
    @Override
    public void endHandle(BpmInst bpmInst) {
        if ("SUCCESS_END".equalsIgnoreCase(bpmInst.getStatus())) {
            String id = bpmInst.getBusKey();
            Map<String,Object> changeObj = saleFileOMAApplyDao.getObjectById(id);
            changeObj.put("fileStatus", "已发布");

            saleFileOMAApplyService.update(changeObj);
        }
    }

    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd cmd, BpmInst bpmInst) {
        if("RUNNING".equals(bpmInst.getStatus())){
        }
        return null;
    }
}
