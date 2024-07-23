package com.redxun.serviceEngineering.core.job;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.bpm.core.manager.BpmTaskManager;
import com.redxun.core.scheduler.BaseJob;
import com.redxun.core.util.AppBeanUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.serviceEngineering.core.dao.MaintenanceManualDemandDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class MaintenanceManualDemandJob extends BaseJob {
    private static Logger logger = LoggerFactory.getLogger(MaintenanceManualDemandJob.class);
    private SendDDNoticeManager sendDDNoticeManager;
    private MaintenanceManualDemandDao maintenanceManualDemandDao;
    private BpmTaskManager bpmTaskManager;

    @Override
    public void executeJob(JobExecutionContext context) {
        try {
            sendDDNoticeManager = AppBeanUtil.getBean(SendDDNoticeManager.class);
            maintenanceManualDemandDao = AppBeanUtil.getBean(MaintenanceManualDemandDao.class);
            bpmTaskManager = AppBeanUtil.getBean(BpmTaskManager.class);
            JSONObject params = new JSONObject();
            params.put("businessStatus", "C-confirmingProduct");
            List<Map<String, Object>> maps = maintenanceManualDemandDao.dataListQuery(params);
            for (Map<String, Object> map : maps) {
                if (DateUtil.parseDate(map.get("publishTime").toString()).before(new Date())) {
                    Collection<TaskExecutor> collection =
                            bpmTaskManager.getInstNodeUsers(map.get("INST_ID_").toString(), "C-confirmingProduct");
                    StringBuilder stringBuilder = new StringBuilder();
                    for (TaskExecutor taskExecutor : collection) {
                        stringBuilder.append(taskExecutor.getId()).append(",");
                    }
                    String userIds = stringBuilder.substring(0, stringBuilder.length() - 1);
                    JSONObject noticeObj = new JSONObject();
                    stringBuilder = new StringBuilder("【操保手册需求超期通知】:编号为 ");
                    stringBuilder.append(map.get("busunessNo").toString());
                    stringBuilder.append(" 的操保手册需求单需求已经超期！").append(XcmgProjectUtil.getNowLocalDateStr(DateUtil.DATE_FORMAT_FULL));
                    noticeObj.put("content", stringBuilder.toString());
                    sendDDNoticeManager.sendNoticeForCommon(userIds, noticeObj);
                }
            }

        } catch (Exception e) {
            logger.error("MaintenanceManualDemandJob任务执行失败");
        }
    }
}
