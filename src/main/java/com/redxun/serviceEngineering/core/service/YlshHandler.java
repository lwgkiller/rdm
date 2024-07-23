package com.redxun.serviceEngineering.core.service;

import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.bpm.core.entity.*;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.serviceEngineering.core.dao.AttachedDocTranslateDao;
import com.redxun.serviceEngineering.core.dao.YlshDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 语料库审核
 *
 * @mh 2022年6月011日08:49:56
 */

@Service
public class YlshHandler implements ProcessStartPreHandler, TaskPreHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(YlshHandler.class);
    @Autowired
    private YlshManager ylshManager;
    @Autowired
    private YlshDao ylshDao;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;
    @Autowired
    private AttachedDocTranslateDao attachedDocTranslateDao;

    // 整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        String applyId = createOrUpdateByFormData(processStartCmd);
        if (StringUtils.isNotBlank(applyId)) {
            processStartCmd.setBusinessKey(applyId);
            //添加外发语料文件的关联
            JSONObject params = new JSONObject();
            params.put("id",applyId);
            JSONObject ylsh = ylshDao.queryApplyDetail(params);
            String oriFileId = ylsh.getString("oriFileId");
            if (!StringUtil.isEmpty(oriFileId)) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("REF_ID_", "1");
                jsonObject.put("id",oriFileId);
                attachedDocTranslateDao.updateFileREFInfos(jsonObject);
            }
        }
    }

    // 任务审批之后的前置处理器
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd) iExecutionCmd;
        createOrUpdateByFormData(processNextCmd);
    }

    // 驳回场景cmd中没有表单数据
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
        if (StringUtils.isBlank(formDataJson.getString("id"))) {
            ylshManager.createYlsh(formDataJson);
        } else {
            ylshManager.updateYlsh(formDataJson);
        }
        return formDataJson.getString("id");
    }

    // 流程成功结束之后的处理
    @Override
    public void endHandle(BpmInst bpmInst) {
        if ("SUCCESS_END".equalsIgnoreCase(bpmInst.getStatus())) {
            String id = bpmInst.getBusKey();
            JSONObject params = new JSONObject();
            params.put("id", id);
            JSONObject ylshObj = ylshDao.queryApplyDetail(params);
            // 发送钉钉通知
            JSONObject noticeObj = new JSONObject();
            StringBuilder stringBuilder = new StringBuilder("语料库审核申请流程通知:");
            stringBuilder.append("申请人[").append(ylshObj.getString("creatorName")).append("]");
            stringBuilder.append(XcmgProjectUtil.getNowLocalDateStr(DateUtil.DATE_FORMAT_FULL));
            noticeObj.put("content", stringBuilder.toString());
            // 拼接创建人和校验人的id
            String ids = ylshObj.getString("CREATE_BY_") + "," + ylshObj.getString("checkId");
            sendDDNoticeManager.sendNoticeForCommon(ids, noticeObj);

        }
    }

    //
}
