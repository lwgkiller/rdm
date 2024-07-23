package com.redxun.zlgjNPI.core.manager;

import java.util.Date;

import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.core.json.JsonResult;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.core.util.DateUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.zlgjNPI.core.dao.ZlgjjysbDao;

@Service
public class ZlgjjysbHandler implements ProcessStartPreHandler, TaskPreHandler, ProcessStartAfterHandler {

    private Logger logger = LoggerFactory.getLogger(ZlgjjysbHandler.class);

    @Autowired
    private ZlgjjysbDao zlgjjysbDao;
    @Autowired
    private ZlgjjysbService zlgjjysbService;

    // 整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd cmd) {
        String zlgjjysbId = createOrUpdateZlgjjysb(cmd);
        if (StringUtils.isNotBlank(zlgjjysbId)) {
            cmd.setBusinessKey(zlgjjysbId);
        }
    }

    // 任务审批之后的前置处理器
    @Override
    public void taskPreHandle(IExecutionCmd cmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd) cmd;
        createOrUpdateZlgjjysb(processNextCmd);
    }

    // 驳回场景cmd中没有表单数据
    private String createOrUpdateZlgjjysb(AbstractExecutionCmd cmd) {
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
        Date sswcsjDate = null;
        if (StringUtils.isNotBlank(formDataJson.getString("sswcTime"))) {
            sswcsjDate = DateUtil.parseDate(formDataJson.getString("sswcTime"));
        }
        formDataJson.put("sswcTime", sswcsjDate);
        if (StringUtils.isBlank(formDataJson.getString("id"))) {
            formDataJson.put("id", IdUtil.getId());
            formDataJson.put("CREATE_TIME_", new Date());
            zlgjjysbDao.createZlgjjysb(formDataJson);
        } else {
            String declareTime = formDataJson.getString("declareTime");
            if (StringUtils.isNotBlank(declareTime)) {
                Date date = formDataJson.getDate("declareTime");
                formDataJson.put("declareTime", date);
            }
            String step = formDataJson.getString("step");
            if (step.equals("szrfpdcry")) {
                // 新增对策人员
                JSONArray dcryGrid = formDataJson.getJSONArray("SUB_dcryGrid");
                for (int i = 0; i < dcryGrid.size(); i++) {
                    JSONObject dcryGridObject = dcryGrid.getJSONObject(i);
                    if (dcryGridObject.getString("CREATE_BY_").equals(ContextUtil.getCurrentUserId())) {
                        dcryGridObject.put("dcryName", dcryGridObject.getString("dcryId_name"));
                        if (StringUtils.isBlank(dcryGridObject.getString("id"))) {
                            dcryGridObject.put("id", IdUtil.getId());
                            dcryGridObject.put("CREATE_TIME_", new Date());
                            zlgjjysbDao.createDcry(dcryGridObject);
                        } else {
                            dcryGridObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                            dcryGridObject.put("UPDATE_TIME_", new Date());
                            zlgjjysbDao.updateDcry(dcryGridObject);
                        }

                    }

                }
            } else if (step.equals("dcrysh")) {
                // 更新实施状态
                JSONArray dcryGrid = formDataJson.getJSONArray("SUB_dcryGrid");
                for (int i = 0; i < dcryGrid.size(); i++) {
                    JSONObject dcryGridObject = dcryGrid.getJSONObject(i);
                    if (dcryGridObject.getString("dcryId").equals(ContextUtil.getCurrentUserId())) {
                        dcryGridObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                        dcryGridObject.put("UPDATE_TIME_", new Date());
                        zlgjjysbDao.updateDcry(dcryGridObject);
                    }
                }
            }
            formDataJson.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            formDataJson.put("UPDATE_TIME_", new Date());
            zlgjjysbDao.updateZlgjjysb(formDataJson);
        }
        return formDataJson.getString("id");
    }

    // 流程启动之后执行
    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd cmd, BpmInst bpmInst) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        formDataJson.put("declareTime", new Date());
        if (StringUtils.isBlank(formDataJson.getString("id"))) {
            if (StringUtils.isBlank(formDataJson.getString("id"))) {
                formDataJson.put("id", cmd.getBusinessKey());
            }
        }
        formDataJson.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formDataJson.put("UPDATE_TIME_", new Date());
        Date sswcsjDate = null;
        if (StringUtils.isNotBlank(formDataJson.getString("sswcTime"))) {
            sswcsjDate = DateUtil.parseDate(formDataJson.getString("sswcTime"));
        }
        formDataJson.put("sswcTime", sswcsjDate);
        zlgjjysbDao.updateZlgjjysb(formDataJson);
        return formDataJson.getString("id");
    }
}
