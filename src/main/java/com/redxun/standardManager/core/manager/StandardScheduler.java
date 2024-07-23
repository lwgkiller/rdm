package com.redxun.standardManager.core.manager;

import java.util.*;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.util.BeanUtil;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.saweb.util.IdUtil;
import com.redxun.standardManager.core.dao.StandardDoCheckDao;

@Service
@EnableScheduling
public class StandardScheduler {
    private static Logger logger = LoggerFactory.getLogger(StandardScheduler.class);

    @Resource
    private StandardDoCheckDao standardDoCheckDao;
    @Resource
    private SendDDNoticeManager sendDDNoticeManager;
    @Resource
    private CommonInfoDao commonInfoDao;
    @Resource
    private CommonInfoManager commonInfoManager;

    /**
     * 每天8:20执行，标准执行自查待办任务的超期提醒
     */
    @Scheduled(cron = "0 20 8 * * *")
    public void standardDoCheckRemind() {
        try {
            logger.info("start standardDoCheckRemind");
            // 取得"标准执行自查"催办实例
            JSONObject param = new JSONObject();
            param.put("solKey", "BZZXXZC");
            List<JSONObject> remindList = standardDoCheckDao.queryRemindInstByParam(param);
            if (BeanUtil.isEmpty(remindList)) {
                return;
            }
            Set<String> delIds = new HashSet<>();
            List<JSONObject> needRemindList = new ArrayList<>();
            for (JSONObject oneRemind : remindList) {
                if (StringUtils.isBlank(oneRemind.getString("processUserId"))) {
                    // 删除找不到任务处理人的提醒（说明该任务已经失效）
                    delIds.add(oneRemind.getString("ID_"));
                } else {
                    boolean expire = DateUtil.judgeEndBigThanStart(oneRemind.getDate("EXPIRE_DATE_"), new Date());
                    if (expire) {
                        // 任务到期的需要提醒，提醒后删除掉
                        needRemindList.add(oneRemind);
                        delIds.add(oneRemind.getString("ID_"));
                    }
                }
            }
            // 已过期的执行催办操作。
            handExpireInst(needRemindList);
            // 删除已失效的和已提醒的
            if (!delIds.isEmpty()) {
                param.clear();
                param.put("ids", delIds);
                standardDoCheckDao.delRemindInstByIds(param);
            }
        } catch (Exception e) {
            logger.error("长周期件明细上传提醒发送失败", e);
        }
    }

    /**
     * 处理到期动作。
     *
     * @param needRemindList
     */
    private void handExpireInst(List<JSONObject> needRemindList) {
        StringBuilder jszxNotice = new StringBuilder();
        StringBuilder otherNotice = new StringBuilder();
        Map<String, String> deptId2Name = commonInfoManager.queryDeptUnderJSZX();
        List<JSONObject> insertPunishData = new ArrayList<>();
        for (JSONObject oneNeedRemind : needRemindList) {
            JSONObject oneData = new JSONObject();
            oneData.put("id", IdUtil.getId());
            oneData.put("baseInfoId", oneNeedRemind.getString("baseInfoId"));
            oneData.put("punishType", "任务超期");
            oneData.put("delayNodeName", oneNeedRemind.getString("NODE_NAME_"));
            oneData.put("delayTaskCreateTime", oneNeedRemind.getDate("CREATE_TIME_"));
            oneData.put("delayTaskExpireTime", oneNeedRemind.getDate("EXPIRE_DATE_"));
            oneData.put("userId", oneNeedRemind.getString("processUserId"));
            oneData.put("userName", oneNeedRemind.getString("processUserName"));
            oneData.put("CREATE_BY_", "1");
            oneData.put("CREATE_TIME_", new Date());
            insertPunishData.add(oneData);
            if (insertPunishData.size() % 10 == 0) {
                standardDoCheckDao.batchInsertDelayPunish(insertPunishData);
                insertPunishData.clear();
            }
            String processUserDeptId = oneNeedRemind.getString("processUserDeptId");
            String processUserDeptName = oneNeedRemind.getString("processUserDeptName");
            if (processUserDeptName.contains(RdmConst.JSZX_NAME) || deptId2Name.containsKey(processUserDeptId)) {
                jszxNotice.append("标准编号：" + oneNeedRemind.getString("standardNumber"))
                    .append("，标准名称：" + oneNeedRemind.getString("standardName"))
                    .append("，自查年份：" + oneNeedRemind.getString("doCheckYear"))
                    .append("，任务节点：" + oneNeedRemind.getString("NODE_NAME_"))
                    .append("，任务创建时间："
                        + DateFormatUtil.format(oneNeedRemind.getDate("CREATE_TIME_"), "yyyy-MM-dd HH:mm:ss"))
                    .append("，任务到期时间："
                        + DateFormatUtil.format(oneNeedRemind.getDate("EXPIRE_DATE_"), "yyyy-MM-dd HH:mm:ss"))
                    .append("，任务处理人：" + oneNeedRemind.getString("processUserName") + "\n");
            } else {
                otherNotice.append("标准编号：" + oneNeedRemind.getString("standardNumber"))
                    .append("，标准名称：" + oneNeedRemind.getString("standardName"))
                    .append("，自查年份：" + oneNeedRemind.getString("doCheckYear"))
                    .append("，任务节点：" + oneNeedRemind.getString("NODE_NAME_"))
                    .append("，任务创建时间："
                        + DateFormatUtil.format(oneNeedRemind.getDate("CREATE_TIME_"), "yyyy-MM-dd HH:mm:ss"))
                    .append("，任务到期时间："
                        + DateFormatUtil.format(oneNeedRemind.getDate("EXPIRE_DATE_"), "yyyy-MM-dd HH:mm:ss"))
                    .append("，任务处理人：" + oneNeedRemind.getString("processUserName") + "\n");
            }
        }
        // 插入考核表
        if (!insertPunishData.isEmpty()) {
            standardDoCheckDao.batchInsertDelayPunish(insertPunishData);
            insertPunishData.clear();
        }
        // 发通知
        if (jszxNotice.length() > 0) {
            sendRemindNotice(RdmConst.STANDARD_DOCHECK_NOTICE_JSZX, jszxNotice.toString());
        }
        if (otherNotice.length() > 0) {
            sendRemindNotice(RdmConst.STANDARD_DOCHECK_NOTICE_OTHER, otherNotice.toString());
        }
    }

    private void sendRemindNotice(String noticeGroupName, String noticeContent) {
        Map<String, Object> params = new HashMap<>();
        params.put("groupName", noticeGroupName);
        params.put("REL_TYPE_KEY_", "GROUP-USER-BELONG");
        List<Map<String, String>> users = commonInfoDao.queryUserByGroupName(params);
        String userIds = "";
        if (users != null && !users.isEmpty()) {
            for (Map<String, String> oneUser : users) {
                userIds += oneUser.get("USER_ID_") + ",";
            }
            userIds = userIds.substring(0, userIds.length() - 1);
            JSONObject noticeObj = new JSONObject();
            noticeObj.put("content", "【标准执行自查超期】\n" + noticeContent);
            sendDDNoticeManager.sendNoticeForCommon(userIds, noticeObj);
        }
    }
}
