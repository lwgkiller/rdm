package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.ProcessNextCmd;
import com.redxun.bpm.core.entity.ProcessStartCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.core.util.DateUtil;
import com.redxun.serviceEngineering.core.dao.CustomsDeclarationRawdataDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomsDeclarationSupplementScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(CustomsDeclarationSupplementScript.class);
    @Autowired
    private CustomsDeclarationSupplementService customsDeclarationSupplementService;
    @Autowired
    private CustomsDeclarationRawdataDao customsDeclarationRawdataDao;

    //..
    public Collection<TaskExecutor> getSupplementUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        JSONObject formDataJson = cmd.getJsonDataObject();
        String userId = formDataJson.getString("supplementUserId");
        String userName = formDataJson.getString("supplementUser");
        users.add(new TaskExecutor(userId, userName));
        return users;
    }

    //..
    public void taskCreateScript(Map<String, Object> vars) {
        JSONObject jsonDataObject = ProcessHandleHelper.getProcessCmd().getJsonDataObject();
        String activitiId = vars.get("activityId").toString();
        jsonDataObject.put("businessStatus", activitiId);
        IExecutionCmd processCmd = ProcessHandleHelper.getProcessCmd();
        if (processCmd instanceof ProcessStartCmd) {
            ProcessStartCmd processStartCmd = (ProcessStartCmd) ProcessHandleHelper.getProcessCmd();
            processStartCmd.setJsonData(jsonDataObject.toJSONString());
        } else if (processCmd instanceof ProcessNextCmd) {
            ProcessNextCmd processNextCmd = (ProcessNextCmd) ProcessHandleHelper.getProcessCmd();
            processNextCmd.setJsonData(jsonDataObject.toJSONString());
        }
        customsDeclarationSupplementService.updateBusiness(jsonDataObject);
    }

    //..
    public void taskEndScript(Map<String, Object> vars) {
        JSONObject jsonDataObject = ProcessHandleHelper.getProcessCmd().getJsonDataObject();
        String activitiId = vars.get("activityId").toString();
        if (activitiId.equalsIgnoreCase("B")) {
            jsonDataObject.put("businessStatus", "Z");
            IExecutionCmd processCmd = ProcessHandleHelper.getProcessCmd();
            if (processCmd instanceof ProcessStartCmd) {
                ProcessStartCmd processStartCmd = (ProcessStartCmd) ProcessHandleHelper.getProcessCmd();
                processStartCmd.setJsonData(jsonDataObject.toJSONString());
            } else if (processCmd instanceof ProcessNextCmd) {
                ProcessNextCmd processNextCmd = (ProcessNextCmd) ProcessHandleHelper.getProcessCmd();
                processNextCmd.setJsonData(jsonDataObject.toJSONString());
            }
            JSONArray recordItems = jsonDataObject.getJSONArray("recordItems");
            for (Object o : recordItems) {
                JSONObject recordItem = (JSONObject) o;
                JSONObject rawdataItem = customsDeclarationRawdataDao.getItemById(recordItem.getString("rawDataItemId"));
                rawdataItem.put("elementsFill", recordItem.getString("elementsFill"));
                rawdataItem.put("netWeight", recordItem.getString("netWeight"));
                rawdataItem.put("processTime", DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_YMD));
                customsDeclarationRawdataDao.updateItem(rawdataItem);
            }
            customsDeclarationSupplementService.updateBusiness(jsonDataObject);
        }
    }
}
