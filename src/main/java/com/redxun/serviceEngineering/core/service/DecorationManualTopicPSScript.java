package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.service.ActInstService;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.bpm.core.manager.BpmTaskManager;
import com.redxun.core.json.JsonResult;
import com.redxun.core.script.GroovyScript;
import com.redxun.core.util.DateUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.org.api.service.UserService;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmZhgl.core.dao.PatentInterpretationDao;
import com.redxun.rdmZhgl.core.service.PatentInterpretationService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.serviceEngineering.core.dao.DecorationManualTopicPSDao;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.db.manager.SysSqlCustomQueryUtil;
import com.redxun.sys.org.manager.OsUserManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DecorationManualTopicPSScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(DecorationManualTopicPSScript.class);
    @Autowired
    private DecorationManualTopicPSService decorationManualTopicPSService;
    @Autowired
    private DecorationManualTopicService decorationManualTopicService;
    @Autowired
    private BpmInstManager bpmInstManager;
    @Autowired
    private BpmTaskManager bpmTaskManager;
    @Autowired
    private ActInstService actInstService;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;
    @Autowired
    private SysDicManager sysDicManager;

    //..获取审核人
    public Collection<TaskExecutor> getReviewer() {
        List<TaskExecutor> users = new ArrayList<>();
        JSONObject formDataJson = JSONObject.parseObject(ProcessHandleHelper.getProcessCmd().getJsonData());
        users.add(new TaskExecutor(formDataJson.getString("reviewerId"), formDataJson.getString("reviewer")));
        return users;
    }

    //..获取评审人员
    public Collection<TaskExecutor> getAssessors() {
        List<TaskExecutor> users = new ArrayList<>();
        JSONObject formDataJson = JSONObject.parseObject(ProcessHandleHelper.getProcessCmd().getJsonData());
        JSONArray topicPSItems = formDataJson.getJSONArray("itemChangeData");
        for (Object topicPSItem : topicPSItems) {
            JSONObject topicPSItemJson = (JSONObject) topicPSItem;
            users.add(new TaskExecutor(topicPSItemJson.getString("assessorId_item"),
                    topicPSItemJson.getString("assessor_item")));
        }
        return users;
    }

    //..获取批准人
    public Collection<TaskExecutor> getApprover() {
        List<TaskExecutor> users = new ArrayList<>();
        JSONObject formDataJson = JSONObject.parseObject(ProcessHandleHelper.getProcessCmd().getJsonData());
        users.add(new TaskExecutor(formDataJson.getString("approverId"), formDataJson.getString("approver")));
        return users;
    }

    //..所有节点：以任何形式进入
    public void taskCreateScript(Map<String, Object> vars) {
        try {
            //@lwgkiller:阿诺多罗 下面这个cmd可以取代getDetail，而且传来的是表单的最新数据
            JSONObject jsonObject = ProcessHandleHelper.getProcessCmd().getJsonDataObject();
            String activitiId = vars.get("activityId").toString();
            jsonObject.put("businessStatus", activitiId);
            decorationManualTopicPSService.updateBusiness(jsonObject);
        } catch (Exception e) {
            logger.error("Exception in taskCreateScript", e);
        }
    }

    //..
    public void taskEndScript(Map<String, Object> vars) {
        try {
            //@lwgkiller:阿诺多罗 下面这个cmd可以取代getDetail，而且传来的是表单的最新数据
            JSONObject jsonObject = ProcessHandleHelper.getProcessCmd().getJsonDataObject();
            String activitiId = vars.get("activityId").toString();
            JSONArray topicPSItemsFinal = new JSONArray();
            if (activitiId.equalsIgnoreCase("C")) {//评审中：以通过的形式结束,记录assessTime_item
                JSONArray topicPSItems = jsonObject.getJSONArray("itemChangeData");
                for (Object topicPSItem : topicPSItems) {
                    JSONObject topicPSItemJson = (JSONObject) topicPSItem;
                    if (topicPSItemJson.getString("assessorId_item").equalsIgnoreCase(ContextUtil.getCurrentUserId())) {
                        topicPSItemJson.put("assessTime_item", DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_YMD));
                    }
                    topicPSItemsFinal.add(topicPSItemJson);
                }
                jsonObject.put("topicPSItems", topicPSItemsFinal.toString());
            } else if (activitiId.equalsIgnoreCase("E")) {//批准中：以通过的形式结束,记录最终状态
                List<JSONObject> topics = decorationManualTopicPSService.getTopicToPS(jsonObject.getString("id"));
                for (JSONObject topic : topics) {
                    topic.put("isPS", "已评审");
                    decorationManualTopicService.saveBusiness(topic);
                }
                jsonObject.put("businessStatus", "Z");
            }
            decorationManualTopicPSService.updateBusiness(jsonObject);
        } catch (Exception e) {
            logger.error("Exception in taskEndScript", e);
        }
    }
}
