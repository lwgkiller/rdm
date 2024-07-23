package com.redxun.meeting.core.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.meeting.core.dao.RwfjDao;
import com.redxun.org.api.model.IUser;
import com.redxun.org.api.service.UserService;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.LoginRecordManager;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Service
@EnableScheduling
public class HyglScheduler {
    private static Logger logger = LoggerFactory.getLogger(HyglScheduler.class);

    @Resource
    private RwfjDao rwfjDao;
    @Resource
    private LoginRecordManager loginRecordManager;
    @Resource
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Resource
    private UserService userService;
    @Resource
    private CommonInfoDao commonInfoDao;
    @Resource
    private SendDDNoticeManager sendDDNoticeManager;

    /**
     * 每天9点执行，会议纪要完成延期提醒
     */
    @Scheduled(cron = "0 0 9 * * *")
    public void hyglDelayNotice() {
        try {
            logger.info("start hyglDelayNotice");
            String nowTime = DateFormatUtil.getNowByString("yyyy-MM-dd");
            JSONObject params = new JSONObject();
            params.put("nowTime", nowTime);
            List<JSONObject> dataList = rwfjDao.getHyglDelay(params);
            for (JSONObject oneData : dataList) {
                StringBuilder userIdBuild = new StringBuilder();
                JSONObject noticeObj = new JSONObject();

                // 是否挖掘机械研究院下的部门
                JSONObject isJSZX = loginRecordManager.judgeIsJSZX(oneData.getString("meetingOrgDepId"),
                    oneData.getString("meetingOrgDepName"));
                boolean isJSZXDept = false;
                if (isJSZX.getBooleanValue("isJSZX")) {
                    isJSZXDept = true;
                }
                if (isJSZXDept) {
                    Map<String, Object> groupParams = new HashMap<>();
                    groupParams.put("groupName", "会议管理专员");
                    List<Map<String, String>> userInfos = xcmgProjectOtherDao.queryUserByGroupName(groupParams);
                    for (Map<String, String> oneUser : userInfos) {
                        userIdBuild.append(oneUser.get("USER_ID_")).append(",");
                    }
                }
                String meetingPlanRespUserIds = oneData.getString("meetingPlanRespUserIds");
                String meetingOrgUserId = oneData.getString("meetingOrgUserId");
                String applyId = oneData.getString("applyId");
                IUser user = userService.getByUserId(meetingPlanRespUserIds);
                String meetingPlanRespUserName = user.getFullname();

                Date planEndTime = oneData.getDate("meetingPlanEndTime");
                String sevenStr = "604800000";
                String fourteen = "1209600000";
                long sevenPardonTime = Long.parseLong(sevenStr);
                long fourteenPardonTime = Long.parseLong(fourteen);
                long currentTime = DateFormatUtil.parseDate(XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd")).getTime();

                // 判断超期时间
                if ((currentTime - planEndTime.getTime() > fourteenPardonTime)
                    && (!"yes".equalsIgnoreCase(oneData.getString("sendHighNotice"))
                        || StringUtils.isBlank(oneData.getString("sendHighNotice")))) {
                    Map<String, Object> groupParams = new HashMap<>();
                    groupParams.put("groupName", "分管领导");
                    List<Map<String, String>> userInfos = xcmgProjectOtherDao.queryUserByGroupName(groupParams);
                    for (Map<String, String> oneUser : userInfos) {
                        userIdBuild.append(oneUser.get("USER_ID_")).append(",");
                    }
                    userIdBuild.deleteCharAt(userIdBuild.length() - 1);
                    noticeObj.put("content", "会议编号:" + oneData.getString("meetingNo") + "+会议主题:"
                        + oneData.getString("meetingTheme") + "存在已延误14天以上的会议纪要分解!");
                    JSONObject param = new JSONObject();
                    param.put("id", oneData.getString("id"));
                    rwfjDao.updateHighSendDD(param);
                } else if ((currentTime - planEndTime.getTime() <= fourteenPardonTime
                    && currentTime - planEndTime.getTime() > sevenPardonTime)
                    && (!"yes".equalsIgnoreCase(oneData.getString("sendMiddleNotice"))
                        || StringUtils.isBlank(oneData.getString("sendMiddleNotice")))) {
                    List<JSONObject> meetingOrgDeptResps = commonInfoDao.queryDeptRespByUserId(meetingOrgUserId);
                    for (JSONObject oneDept : meetingOrgDeptResps) {
                        String respId = oneDept.getString("USER_ID_");
                        userIdBuild.append(respId).append(",");
                    }
                    List<JSONObject> meetingPlanRespDeptResps =
                        commonInfoDao.queryDeptRespByUserId(meetingPlanRespUserIds);
                    for (JSONObject oneDept : meetingPlanRespDeptResps) {
                        String respId = oneDept.getString("USER_ID_");
                        userIdBuild.append(respId).append(",");
                    }
                    userIdBuild.deleteCharAt(userIdBuild.length() - 1);
                    noticeObj.put("content", "会议编号:" + oneData.getString("meetingNo") + "+会议主题:"
                        + oneData.getString("meetingTheme") + "存在已延误7天以上的会议纪要分解!");
                    JSONObject param = new JSONObject();
                    param.put("id", oneData.getString("id"));
                    rwfjDao.updateMiddleSendDD(param);
                } else if ((currentTime - planEndTime.getTime() <= sevenPardonTime
                    && currentTime - planEndTime.getTime() > 0)
                    && (!"yes".equalsIgnoreCase(oneData.getString("sendLowNotice"))
                        || StringUtils.isBlank(oneData.getString("sendLowNotice")))) {
                    userIdBuild.append(meetingPlanRespUserIds).append(",");
                    userIdBuild.append(applyId);
                    noticeObj.put("content", "会议编号:" + oneData.getString("meetingNo") + "+会议主题:"
                        + oneData.getString("meetingTheme") + "中" + meetingPlanRespUserName + "的会议任务已逾期未完成,请协调处理!");
                    JSONObject param = new JSONObject();
                    param.put("id", oneData.getString("id"));
                    rwfjDao.updateLowSendDD(param);
                }
                if (userIdBuild.length()>0 && noticeObj.containsKey("content")) {
                    sendDDNoticeManager.sendNoticeForCommon(userIdBuild.toString(), noticeObj);
                }
            }
        } catch (Exception e) {
            logger.error("会议纪要完成延期提醒发送失败", e);
        }
    }
}
