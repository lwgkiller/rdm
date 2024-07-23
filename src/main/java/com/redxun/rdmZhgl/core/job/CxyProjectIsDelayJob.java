package com.redxun.rdmZhgl.core.job;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.scheduler.BaseJob;
import com.redxun.core.util.AppBeanUtil;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmZhgl.core.dao.CxyProjectDao;
import com.redxun.xcmgJsjl.core.manager.XcmgJsjlManager;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class CxyProjectIsDelayJob extends BaseJob {
    private static Logger logger = LoggerFactory.getLogger(XcmgJsjlManager.class);

    private SendDDNoticeManager sendDDNoticeManager;

    @Override
    public void executeJob(JobExecutionContext context) {
        try {
            CxyProjectDao cxyProjectDao = AppBeanUtil.getBean(CxyProjectDao.class);
            List<JSONObject> jsonObjectList = cxyProjectDao.selectAllJson();
            if (jsonObjectList.size() > 0) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                for (JSONObject jsonObject : jsonObjectList) {
                    if (simpleDateFormat.parse(jsonObject.getString("endTime")).
                            before(simpleDateFormat.parse(simpleDateFormat.format(new Date())))
                            && Integer.parseInt(jsonObject.getString("completionRate")) < 100) {
                        jsonObject.put("isDelay", "1");
                        cxyProjectDao.updateCxyProjectData(jsonObject);
                        sendDD(jsonObject);
                    }
                }
            }
            System.out.println(jsonObjectList);
        } catch (Exception e) {
            logger.error("CxyProjectIsDelayJob任务执行失败");
        }
    }

    private void sendDD(JSONObject businessJsonObject) {
        sendDDNoticeManager = AppBeanUtil.getBean(SendDDNoticeManager.class);
        JSONObject noticeObj = new JSONObject();
        StringBuilder stringBuilder = new StringBuilder("【产学研项目超期通知】:");
        stringBuilder.append("您的项目:-").append(businessJsonObject.getString("projectDesc"));
        stringBuilder.append("-已到期，请抓紧处理");
        noticeObj.put("content", stringBuilder.toString());
        sendDDNoticeManager.sendNoticeForCommon(businessJsonObject.getString("responsibleUserId"), noticeObj);
    }
}
