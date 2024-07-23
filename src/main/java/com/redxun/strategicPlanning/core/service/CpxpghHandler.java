package com.redxun.strategicPlanning.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.sys.org.manager.OsGroupManager;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CpxpghHandler
    implements ProcessStartPreHandler, TaskPreHandler, ProcessEndHandler, ProcessStartAfterHandler {
    private Logger logger = LoggerFactory.getLogger(CpxpghHandler.class);
    @Autowired
    private CpxpghService cpxpghService;


    @Autowired
    private OsGroupManager osGroupManager;

    // 整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        String jxbzzbshId = createOrUpdateCjzgByFormData(processStartCmd);
        if (StringUtils.isNotBlank(jxbzzbshId)) {
            processStartCmd.setBusinessKey(jxbzzbshId);
        }
    }


    // 任务审批之后的前置处理器
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd)iExecutionCmd;
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
        JSONArray cpxsztGrid = formDataJson.getJSONArray("SUB_cpxsztGrid");
        JSONArray cppzGrid = formDataJson.getJSONArray("SUB_cppzGrid");
        String id = formDataJson.getString("id");
        String changeId = formDataJson.getString("changeId");
        if (StringUtils.isBlank(id)) {
            if(!StringUtils.isBlank(changeId)) {
                formDataJson.put("finalId", changeId);
            }
            id = cpxpghService.insertCpxpgh(formDataJson);
        } else {
            cpxpghService.updateCpxpgh(formDataJson);
            Map<String, Object> param = new HashMap<>();
            List<String> ids= new ArrayList <>();
            ids.add(id);
            param.put("cpxpghIds", ids);
            cpxpghService.delChildsByCpxpghId(param);
        }
        List<JSONObject> list = new ArrayList <>();
        if (cpxsztGrid !=null && !cpxsztGrid.isEmpty()) {
            for (int i = 0; i<cpxsztGrid.size(); i++) {
                JSONObject jsonObject = cpxsztGrid.getJSONObject(i);
                jsonObject.put("id", IdUtil.getId());
                jsonObject.put("CREATE_TIME_", new Date());
                jsonObject.put("cpxpghId", id);
                jsonObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                list.add(jsonObject);
            }
        }
        if (cppzGrid !=null && !cppzGrid.isEmpty()) {
            for (int i = 0; i<cppzGrid.size(); i++) {
                JSONObject jsonObject = cppzGrid.getJSONObject(i);
                jsonObject.put("id", IdUtil.getId());
                jsonObject.put("CREATE_TIME_", new Date());
                jsonObject.put("cpxpghId", id);
                jsonObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                list.add(jsonObject);
            }
        }
        if (list.size() > 0) {
            Map<String, Object> map = new HashMap <>();
            map.put("list",list);
            cpxpghService.insertChilds(map);
        }
        return formDataJson.getString("id");
    }

    // 流程结束时触发
    @Override
    public void endHandle(BpmInst bpmInst) {
        if ("SUCCESS_END".equalsIgnoreCase(bpmInst.getStatus())) {
            IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
            String jsonData = cmd.getJsonData();
            JSONObject formData = JSONObject.parseObject(jsonData);
            String finalId = formData.getString("finalId");
            String id = formData.getString("id");
            //最终版本
            JSONObject finalData = JSONObject.parseObject(jsonData);
            List <JSONObject> childs = cpxpghService.queryChildsBycpxpghId(id);
            if (StringUtils.isBlank(finalId)) {
                finalData.put("finalId","");
                finalData.put("changeStatus","wbg");
                id = cpxpghService.insertCpxpghFinal(finalData);
                formData.put("finalId",id);
                cpxpghService.updateCpxpgh(formData);
            } else {
                id = finalData.getString("finalId");
                finalData.put("id", id);
                finalData.put("finalId","");
                finalData.put("changeStatus","bgwc");
                cpxpghService.updateCpxpgh(finalData);
                Map<String, Object> map = new HashMap <>();
                List<String> cpxpghIds = new ArrayList <>();
                cpxpghIds.add(id);
                map.put("cpxpghIds", cpxpghIds);
                cpxpghService.delChildsByCpxpghId(map);
            }
            if (childs.size() > 0) {
                for (int i =0; i< childs.size(); i++) {
                    childs.get(i).put("cpxpghId", id);
                    childs.get(i).put("id", IdUtil.getId());
                    childs.get(i).put("CREATE_TIME_", new Date());
                    childs.get(i).put("CREATE_BY_", formData.getString("CREATE_BY_"));
                }
                Map<String, Object> map = new HashMap <>();
                map.put("list",childs);
                cpxpghService.insertChilds(map);
            }

        }
    }

    //第一个任务创建后
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
        String changeId = formDataJson.getString("changeId");
        String finalId = formDataJson.getString("finalId");
        if (!StringUtils.isBlank(changeId) || !StringUtils.isBlank(finalId)) {
            JSONObject finalCpxpgh = null;
            if (!StringUtils.isBlank(changeId)) {
                finalCpxpgh = cpxpghService.getCpxpghDetail(changeId);
            } else {
                finalCpxpgh = cpxpghService.getCpxpghDetail(finalId);
            }
            finalCpxpgh.put("changeStatus","bgz");
            cpxpghService.updateCpxpgh(finalCpxpgh);
        }
        return formDataJson.getString("id");
    }
}
