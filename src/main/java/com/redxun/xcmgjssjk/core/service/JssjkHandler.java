package com.redxun.xcmgjssjk.core.service;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.xcmgjssjk.core.dao.XcmgjssjkDao;

@Service
public class JssjkHandler
    implements ProcessStartPreHandler, ProcessStartAfterHandler, TaskPreHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(JssjkHandler.class);
    @Autowired
    private XcmgjssjkService xcmgjssjkService;
    @Autowired
    private XcmgjssjkDao xcmgjssjkDao;

    // 整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        String jssjkId = createOrUpdateJsmmByFormData(processStartCmd);
        if (StringUtils.isNotBlank(jssjkId)) {
            processStartCmd.setBusinessKey(jssjkId);
        }
    }

    // 第一个任务创建之后流程的后置处理器（进行编号的生成和更新）
    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd processStartCmd,
        BpmInst bpmInst) {
        String jssjkId = processStartCmd.getBusinessKey();
        // String rjzzNum = rjzzService.toGetRjzzNum();
        // Map<String, Object> param = new HashMap<>();
        // param.put("rjzzId", rjzzId);
        // param.put("rjzzNum", rjzzNum);
        // rjzzDao.updateRjzzNum(param);
        return jssjkId;
    }

    // 任务审批之后的前置处理器
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd)iExecutionCmd;
        createOrUpdateJsmmByFormData(processNextCmd);
    }

    // 流程成功结束之后的处理
    @Override
    public void endHandle(BpmInst bpmInst) {
        if ("SUCCESS_END".equalsIgnoreCase(bpmInst.getStatus())) {
            logger.info("JssjkHandler endHandle");
            IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
            String formData = cmd.getJsonData();
            JSONObject formDataJson = JSONObject.parseObject(formData);
            // 如果是新增场景
            if ("new".equals(formDataJson.getString("splx"))) {
                if (StringUtils.isBlank(formDataJson.getString("ifzb"))
                    && StringUtils.isNotBlank(formDataJson.getString("jssjkId"))) {
                    formDataJson.put("ifzb", "yes");
                    logger.info("new start update");
                    xcmgjssjkService.updateJssjk(formDataJson);
                    logger.info("new end update");
                }
            }
            // 如果是修改或者删除的场景
            if ("update".equals(formDataJson.getString("splx")) || "delete".equals(formDataJson.getString("splx"))) {
                if (StringUtils.isNotBlank(formDataJson.getString("jssjkId"))) {
                    JSONObject formDataObj = xcmgjssjkService.getJssjkDetail(formDataJson.getString("jssjkId"));
                    // 如果上版本id不为空，则将上个版本的“是否终版ifzb”置为no
                    if (StringUtils.isNotBlank(formDataObj.getString("sbbId"))) {
                        JSONObject jssjkObj = xcmgjssjkService.getJssjkDetail(formDataObj.getString("sbbId"));
                        jssjkObj.put("ifzb", "no");
                        logger.info("update start update1");
                        xcmgjssjkService.updateJssjk(jssjkObj);
                        logger.info("update end update1");
                    }
                    // 将本版本置为yes
                    formDataJson.put("ifzb", "yes");
                    logger.info("update start update2");
                    xcmgjssjkService.updateJssjk(formDataJson);
                    logger.info("update end update2");
                }

            }
            // 生成编号
            if (StringUtils.isBlank(formDataJson.getString("jsNum"))) {
                String jsly = formDataJson.getString("jsly");
                String jsfx = formDataJson.getString("jsfx");
                String jsNum = xcmgjssjkService.toGetJssjkNumber(jsly, jsfx);
                Map<String, Object> param = new HashMap<>();
                param.put("jssjkId", formDataJson.getString("jssjkId"));
                param.put("jsNum", jsNum);
                logger.info("update JssjkNumber:" + jsNum);
                xcmgjssjkDao.updateJssjkNumber(param);
            }
        }
    }

    // 驳回场景cmd中没有表单数据
    private String createOrUpdateJsmmByFormData(AbstractExecutionCmd cmd) {
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
        if (StringUtils.isBlank(formDataJson.getString("jssjkId"))) {
            xcmgjssjkService.createJssjk(formDataJson);
        } else {
            xcmgjssjkService.updateJssjk(formDataJson);
        }
        return formDataJson.getString("jssjkId");
    }
}
