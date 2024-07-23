package com.redxun.serviceEngineering.core.service;

import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.scheduler.BaseJob;
import com.redxun.core.util.BeanUtil;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.standardManager.core.dao.StandardDoCheckDao;

/**
 * 操保手册制修订评审任务超时提醒
 * 
 * @author mh 2022年7月14日15:41:58
 *
 */
public class ZxdpsRemindJob extends BaseJob {

    private StandardDoCheckDao standardDoCheckDao = (StandardDoCheckDao)WebAppUtil.getBean(StandardDoCheckDao.class);
    private SendDDNoticeManager sendDDNoticeManager =
        (SendDDNoticeManager)WebAppUtil.getBean(SendDDNoticeManager.class);

    @Override
    public void executeJob(JobExecutionContext context) {
        // 取得"操保手册制修订评审自查"催办实例
        JSONObject param = new JSONObject();
        param.put("solKey", "ZXDPS");
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
    }

    /**
     * 处理到期动作。
     * 
     * @param needRemindList
     */
    private void handExpireInst(List<JSONObject> needRemindList) {
        // 发钉钉通知通知
        String userIds = "";
        for (JSONObject oneRemind : needRemindList) {
            userIds += oneRemind.get("processUserId") + ",";
        }
        userIds = userIds.substring(0, userIds.length() - 1);
        JSONObject noticeObj = new JSONObject();
        noticeObj.put("content", "【操保手册制修订评审审核超期】,请及时处理\n");
        sendDDNoticeManager.sendNoticeForCommon(userIds, noticeObj);

    }

}
