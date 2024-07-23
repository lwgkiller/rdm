package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.ProcessNextCmd;
import com.redxun.bpm.core.entity.ProcessStartCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class ReplacementRelationshipScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(ReplacementRelationshipScript.class);
    @Autowired
    private ReplacementRelationshipService replacementRelationshipService;

    //..
    public Collection<TaskExecutor> getApprover() {
        List<TaskExecutor> users = new ArrayList<>();
        JSONObject formDataJson = JSONObject.parseObject(ProcessHandleHelper.getProcessCmd().getJsonData());
        users.add(new TaskExecutor(formDataJson.getString("approverId"), formDataJson.getString("approver")));
        return users;
    }

    //
    public void taskCreateScript(Map<String, Object> vars) {
        try {
            //@lwgkiller:阿诺多罗 下面这个cmd可以取代getDetail，而且传来的是表单的最新数据
            JSONObject formDataJson = JSONObject.parseObject(ProcessHandleHelper.getProcessCmd().getJsonData());
            JSONObject jsonObject = ProcessHandleHelper.getProcessCmd().getJsonDataObject();//观察--可删除
            String activitiId = vars.get("activityId").toString();
            formDataJson.put("businessStatus", activitiId);
            ////--可删除begin
            jsonObject = ProcessHandleHelper.getProcessCmd().getJsonDataObject();//观察-不协变
            IExecutionCmd processCmd = ProcessHandleHelper.getProcessCmd();
            if(processCmd instanceof ProcessStartCmd){
                ProcessStartCmd processStartCmd = (ProcessStartCmd)ProcessHandleHelper.getProcessCmd();
                processStartCmd.setJsonData(formDataJson.toJSONString());
            }else if(processCmd instanceof ProcessNextCmd){
                ProcessNextCmd processNextCmd = (ProcessNextCmd)ProcessHandleHelper.getProcessCmd();
                processNextCmd.setJsonData(formDataJson.toJSONString());
            }
            jsonObject = ProcessHandleHelper.getProcessCmd().getJsonDataObject();//观察-通过setJsonData方式协变
            ////--可删除end
            replacementRelationshipService.updateBusiness(formDataJson);
        } catch (Exception e) {
            logger.error("Exception in taskCreateScript", e);
        }
    }

    //
    public void taskEndScript(Map<String, Object> vars) {
        try {
            //@lwgkiller:阿诺多罗 下面这个cmd可以取代getDetail，而且传来的是表单的最新数据
            JSONObject formDataJson = JSONObject.parseObject(ProcessHandleHelper.getProcessCmd().getJsonData());
            JSONObject jsonObject = ProcessHandleHelper.getProcessCmd().getJsonDataObject();//观察--可删除
            String activitiId = vars.get("activityId").toString();
            if (activitiId.equalsIgnoreCase("C")) {
                formDataJson.put("businessStatus", "Z");
                ////--可删除begin
                jsonObject = ProcessHandleHelper.getProcessCmd().getJsonDataObject();//观察-不协变
                IExecutionCmd processCmd = ProcessHandleHelper.getProcessCmd();
                if(processCmd instanceof ProcessStartCmd){
                    ProcessStartCmd processStartCmd = (ProcessStartCmd)ProcessHandleHelper.getProcessCmd();
                    processStartCmd.setJsonData(formDataJson.toJSONString());
                }else if(processCmd instanceof ProcessNextCmd){
                    ProcessNextCmd processNextCmd = (ProcessNextCmd)ProcessHandleHelper.getProcessCmd();
                    processNextCmd.setJsonData(formDataJson.toJSONString());
                }
                jsonObject = ProcessHandleHelper.getProcessCmd().getJsonDataObject();//观察-通过setJsonData方式协变
                ////--可删除end
                replacementRelationshipService.doCreatMasterdataFromWorkflow(formDataJson);
            }
            replacementRelationshipService.updateBusiness(formDataJson);
        } catch (Exception e) {
            logger.error("Exception in taskEndScript", e);
        }
    }
}
