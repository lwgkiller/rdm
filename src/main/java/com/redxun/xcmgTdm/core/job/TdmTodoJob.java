package com.redxun.xcmgTdm.core.job;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.scheduler.BaseJob;
import com.redxun.core.util.AppBeanUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.HttpClientUtil;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.sys.core.entity.SysDic;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.org.manager.UserServiceImpl;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

public class TdmTodoJob extends BaseJob {
    private static Logger logger = LoggerFactory.getLogger(TdmTodoJob.class);
    private SysDicManager sysDicManager;
    private SendDDNoticeManager sendDDNoticeManager;
    private UserServiceImpl userService;
    private CommonInfoManager commonInfoManager;

    @Override
    public void executeJob(JobExecutionContext context) {
        try {
            commonInfoManager = AppBeanUtil.getBean(CommonInfoManager.class);
            sysDicManager = AppBeanUtil.getBean(SysDicManager.class);
            userService = AppBeanUtil.getBean(UserServiceImpl.class);
            List<SysDic> sysDicList = sysDicManager.getByTreeKey("TDMIpClass");
            String url = sysDicList.get(1).getValue() + ":8080/pvdm/server/DataReceiver/taskList";
            List<Map<String, String>> deptResps = commonInfoManager.queryJszxAllSuoZhang();
            String urlAct = "";
            for (Map<String, String> deptResp : deptResps) {
                String userid = userService.getByUserId(deptResp.get("USER_ID_")).getUserNo();
                // userid = "admin";
                urlAct = url + "?userid=" + userid;
                String resultString = HttpClientUtil.getFromUrl(urlAct, null);
                JSONObject resultJson = JSONObject.parseObject(resultString);
                JSONArray resultJsonArray = resultJson.getJSONArray("Data");
                for (Object object : resultJsonArray) {
                    JSONObject jsonObject = (JSONObject)object;
                    String startTime = jsonObject.getString("startTime");
                    String startTime2 = startTime.replace('/', '-');
                    Date startTimeDate = DateUtil.parseDate(startTime2, DateUtil.DATE_FORMAT_FULL);
                    double delay = DateUtil.betweenHour(startTimeDate, new Date());
                    if (delay > 24d) {
                        sendDingDing(jsonObject, userid);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Tdm待办延时通知任务执行失败");
        }
    }

    // ..
    public void sendDingDing(JSONObject jsonObject, String userIds) {
        sendDDNoticeManager = AppBeanUtil.getBean(SendDDNoticeManager.class);
        JSONObject noticeObj = new JSONObject();
        StringBuilder stringBuilder = new StringBuilder("【TDM待办超期通知】:名为 ");
        stringBuilder.append(jsonObject.getString("instanceCaption"));
        stringBuilder.append(" 的TDM待办需要你处理，已滞留24小时以上，请抓紧处理。")
            .append(XcmgProjectUtil.getNowLocalDateStr(DateUtil.DATE_FORMAT_FULL));
        noticeObj.put("content", stringBuilder.toString());
        sendDDNoticeManager.sendNoticeForCommon(userIds, noticeObj);
    }
}
