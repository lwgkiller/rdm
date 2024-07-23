package com.redxun.standardManager.core.manager;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskAfterHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.ProcessNextCmd;
import com.redxun.bpm.core.entity.ProcessStartCmd;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.saweb.util.IdUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectMessageDao;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class JsStandardManagementHandler implements ProcessStartPreHandler, TaskPreHandler, TaskAfterHandler {
    private Logger logger = LoggerFactory.getLogger(JsStandardManagementHandler.class);
    @Autowired
    private JsStandardManager jsStandardManager;
    @Autowired
    private CommonInfoManager commonInfoManager;
    @Resource
    private SendDDNoticeManager sendDDNoticeManager;
    @Autowired
    private XcmgProjectMessageDao xcmgProjectMessageDao;
    // 整个流程启动之前的处理，草稿也会调用这里
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        logger.info("JsStandardDemandHandler processStartPreHandle");
        JSONObject standardDemandObj = addOrUpdateStandardApply(processStartCmd);
        if (standardDemandObj != null) {
            processStartCmd.setBusinessKey(standardDemandObj.getString("applyId"));
        }
    }

    // 任务审批之后的前置处理器
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        logger.info("taskPreHandle");
        ProcessNextCmd processNextCmd = (ProcessNextCmd)iExecutionCmd;
        addOrUpdateStandardApply(processNextCmd);
    }


    // 驳回场景cmd中没有表单数据
    private JSONObject addOrUpdateStandardApply(AbstractExecutionCmd cmd) {
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
            jsStandardManager.createStandardApply(formDataJson);
        } else {
            jsStandardManager.updateStandardApply(formDataJson);
        }
        return formDataJson;
    }

    @Override
    public void taskAfterHandle(IExecutionCmd cmd, String nodeId, String busKey) {
        logger.info("taskAfterHandle_dtgl");
        List<Map<String, String>> groupUsers =
                commonInfoManager.queryUserByGroupNameAndRelType("动态标准管理通知人员", "GROUP-USER-BELONG");

        ProcessNextCmd processNextCmd = (ProcessNextCmd)cmd;
        //@mh 2023年2月9日14:34:45  更新用不到先注了
//        addOrUpdateStandardApply(processNextCmd);
        String formData = processNextCmd.getJsonData();
        if (StringUtils.isBlank(formData)) {
            logger.warn("formData is blank");
        }
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
        }
        JSONObject postDataObj = new JSONObject();
        postDataObj.put("id", IdUtil.getId());
        postDataObj.put("CREATE_BY_", "1");
        postDataObj.put("title", "动态标准管理");
        // 动态标准管理通知人员正式
        postDataObj.put("recGroupIds", "1970504985094667005");
        // 测试库
//        postDataObj.put("recGroupIds", "1971740715934941192");
        postDataObj.put("CREATE_TIME_", new Date());
        postDataObj.put("expireTime", null);
        postDataObj.put("canPopup", "yes");
        postDataObj.put("messageType", "group");
        postDataObj.put("appName", "rdm");
        postDataObj.put("recType", "computer");
        String content = "<p>标准名称《"
                +formDataJson.getString("standardName")
                +formDataJson.getString("oldStandardName")
                +"》已发布"+"<p>请及时申请下载并传递相关供应商，严格按照标准执行时间执行！</p>";
        postDataObj.put("content", content);
        xcmgProjectMessageDao.insertToProjectMessage(postDataObj);
        // 钉钉通知
        JSONObject noticeObj = new JSONObject();
        StringBuilder userIdBuilder = new StringBuilder();
        for (Map<String, String> groupUser : groupUsers) {
            userIdBuilder.append(groupUser.get("USER_ID_")).append(",");
        }
        String userIds = userIdBuilder.deleteCharAt(userIdBuilder.length() - 1).toString();
        content = "标准名称《"+formDataJson.getString("standardName")
                +formDataJson.getString("oldStandardName")
                +"》已发布,请及时申请下载并传递相关供应商，严格按照标准执行时间执行！";
        noticeObj.put("content", content);
        sendDDNoticeManager.sendNoticeForCommon(userIds, noticeObj);


    }
}
