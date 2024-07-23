package com.redxun.rdmZhgl.core.service;

import java.util.Map;

import javax.annotation.Resource;

import com.redxun.rdmZhgl.core.dao.BbsBaseInfoDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
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
 * @author zz
 * */
@Service
public class BbsApplyHandler implements ProcessStartPreHandler, TaskPreHandler, ProcessStartAfterHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(BbsApplyHandler.class);
    @Resource
    private BbsBaseInfoDao bbsBaseInfoDao;
    @Resource
    private BbsBaseInfoService bbsBaseInfoService;

    /**
     * 整个流程启动之前的处理，草稿也会调用这里
     * */
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        logger.info("ProcessApplyHandler processStartPreHandle");
        Map<String, Object> changeApply = addOrUpdateApplyInfo(processStartCmd,false,false);
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
        Boolean conformFlag = false;
        Boolean replyFlag = false;
        if("发帖人确认".equals(task.getName())){
            conformFlag = true;
        }
        if("回帖人回复".equals(task.getName())){
            replyFlag = true;
        }
        addOrUpdateApplyInfo(processNextCmd,conformFlag,replyFlag);
    }

    /**
     * 驳回场景cmd中没有表单数据
     * */
    private Map<String, Object> addOrUpdateApplyInfo(AbstractExecutionCmd cmd,Boolean conformFlag,Boolean replyFlag) {
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
        if (applyObj.get("id") == null || StringUtils.isBlank(applyObj.get("id").toString())) {
            String id = IdUtil.getId();
            applyObj.put("id", id);
            applyObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            applyObj.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            applyObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            applyObj.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            bbsBaseInfoDao.add(applyObj);
            bbsBaseInfoService.savePic(id, CommonFuns.nullToString(applyObj.get("picName")));
        } else {
            if(conformFlag){
                applyObj.put("conformDate", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            }
            if(replyFlag){
                applyObj.put("actFinishDate", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            }
            applyObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            applyObj.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            bbsBaseInfoDao.updateObject(applyObj);
            String id = CommonFuns.nullToString(applyObj.get("id"));
            bbsBaseInfoService.savePic(id, CommonFuns.nullToString(applyObj.get("picName")));
            bbsBaseInfoDao.updateObject(applyObj);
        }
        return applyObj;
    }
    // 流程成功结束之后的处理
    @Override
    public void endHandle(BpmInst bpmInst) {
        if ("SUCCESS_END".equalsIgnoreCase(bpmInst.getStatus())) {
        }
    }

    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd cmd, BpmInst bpmInst) {
        if("RUNNING".equals(bpmInst.getStatus())){
        }
        return null;
    }
}
