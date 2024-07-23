package com.redxun.zlgjNPI.core.manager;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.zlgjNPI.core.dao.ZlgjWTDao;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NewItemLcpsHandler implements ProcessStartPreHandler, ProcessStartAfterHandler, TaskPreHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(NewItemLcpsHandler.class);
    @Resource
    private NewItemLcpsService newItemLcpsService;
    @Autowired
    private ZlgjWTDao zlgjWTDao;
    @Resource
    private SendDDNoticeManager sendDDNoticeManager;
    // 整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        String xplcId = createOrUpdateZlgjByFormData(processStartCmd);
        if (StringUtils.isNotBlank(xplcId)) {
            processStartCmd.setBusinessKey(xplcId);
        }
    }
    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd processStartCmd, BpmInst bpmInst) {
        return null;
    }
    // 任务审批之后的前置处理器
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd)iExecutionCmd;
        createOrUpdateZlgjByFormData(processNextCmd);
    }
    // 驳回场景cmd中没有表单数据
    private String createOrUpdateZlgjByFormData(AbstractExecutionCmd cmd) {
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
        if (StringUtils.isBlank(formDataJson.getString("xplcId"))) {
            newItemLcpsService.createLcps(formDataJson);
        } else {
            newItemLcpsService.updateLcps(formDataJson);
        }
        return formDataJson.getString("xplcId");
    }
    @Override
    public void endHandle(BpmInst bpmInst) {
        if ("SUCCESS_END".equalsIgnoreCase(bpmInst.getStatus())) {
            //流程结束发送通知到人
            IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
            String formData = cmd.getJsonData();
            JSONObject formDataJson = JSONObject.parseObject(formData);
            // 返回检验经理及检验技术角色信息
            Map<String, Object> params = new HashMap<>();
            params.put("userId", "检验经理及检验技术");
            List<Map<String, Object>> currentUserRoles = zlgjWTDao.queryJyjlUserRoles(params);
            StringBuilder strUser = new StringBuilder();
            for (int i =0;i<currentUserRoles.size();i++){
                strUser.append(currentUserRoles.get(i).get("USER_ID_")).append(",");
            }
            JSONObject noticeObj = new JSONObject();
            StringBuilder stringBuilder = new StringBuilder("【新品量产评审】-已发布，");
            stringBuilder.append("产品主管：").append(formDataJson.getString("cpzgName")).append("；");
            stringBuilder.append("创建时间：").append(DateFormatUtil.format(formDataJson.getDate("CREATE_TIME_"), "yyyy-MM-dd")).append("。");
            noticeObj.put("content", stringBuilder.toString());
            sendDDNoticeManager.sendNoticeForCommon(strUser.deleteCharAt(strUser.length()-1).toString(), noticeObj);
        }
    }
}
