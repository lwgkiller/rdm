package com.redxun.rdmZhgl.core.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.redxun.saweb.util.WebAppUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.BpmSolution;
import com.redxun.bpm.core.entity.ProcessMessage;
import com.redxun.bpm.core.entity.ProcessStartCmd;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.bpm.core.manager.BpmSolutionManager;
import com.redxun.core.util.AppBeanUtil;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.ExceptionUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.org.api.service.UserService;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmZhgl.core.dao.YdgztbDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;

@Service
@EnableScheduling
public class YdgztbScheduler {
    private static Logger logger = LoggerFactory.getLogger(YdgztbScheduler.class);

    @Resource
    private YdgztbDao ydgztbDao;
    @Resource
    private SendDDNoticeManager sendDDNoticeManager;
    @Autowired
    private CommonInfoManager commonInfoManager;
    @Resource
    private BpmSolutionManager bpmSolutionManager;
    @Resource
    private UserService userService;
    @Resource
    private BpmInstManager bpmInstManager;
    @Resource
    private XcmgProjectOtherDao xcmgProjectOtherDao;

    /**
     * 每个月最后一天10点执行，月度工作提报优秀评定提醒，判断如果是本月最后一天则执行
     */
    @Scheduled(cron = "0 0 10 28-31 * *")
    public void ydgztbRemindJob() {
        logger.info("Start ydgztbRemindJob");
        if (!DateUtil.judgeIsLastDayOfMonth()) {
            return;
        }
        try {
            // 这个每月末提醒，时间也是取当月，每个月的最后一天
            String webappName = WebAppUtil.getProperty("webappName", "rdm");
            if (!"rdm".equalsIgnoreCase(webappName)) {
                return;
            }
            String yearMonth = DateFormatUtil.format(new Date(), "yyyy-MM");
            JSONObject params = new JSONObject();
            params.put("yearMonth", yearMonth);
            List<JSONObject> deptList = ydgztbDao.queryUndoDepartList(params);
            StringBuilder userIdsBuilder = new StringBuilder();
            for (JSONObject oneDept : deptList) {
                // 用部门名称获取部门负责人,拼起来之后发钉钉
                String departName = oneDept.getString("departName");
                List<Map<String, String>> depRespMans = commonInfoManager.queryDeptRespUser(departName);
                if (depRespMans != null && !depRespMans.isEmpty()) {
                    for (Map<String, String> depRespMan : depRespMans) {
                        userIdsBuilder.append(depRespMan.get("PARTY2_")).append(",");
                    }
                }
            }
            if (userIdsBuilder.length() > 1) {
                userIdsBuilder.deleteCharAt(userIdsBuilder.length() - 1);
                sendDDNoticeManager = AppBeanUtil.getBean(SendDDNoticeManager.class);
                JSONObject noticeObj = new JSONObject();
                String noticeStr = "【月度工作提报优秀案例未评选提醒】:" + yearMonth + "月度优秀案例暂未评选！请尽快完成评选";
                noticeObj.put("content", noticeStr);
                sendDDNoticeManager.sendNoticeForCommon(userIdsBuilder.toString(), noticeObj);
            }

        } catch (Exception e) {
            logger.error("月度工作提报优秀案例未评选提醒YdgztbRemindJob任务执行失败", e);
        }
    }

    /**
     * 每个月的1号12:30点执行，所有主任级创建月度工作提报流程
     */
    @Scheduled(cron = "0 30 12 1 * *")
    public void ydgztbCreateFlow() {
        try {

            logger.info("start ydgztbCreateFlow");
            String webappName = WebAppUtil.getProperty("webappName", "rdm");
            if (!"rdm".equalsIgnoreCase(webappName)) {
                return;
            }
            // 查询所有主任角色
            BpmSolution bpmSol = bpmSolutionManager.getByKey("YDGZTB", "1");
            String solId = "";
            if (bpmSol != null) {
                solId = bpmSol.getSolId();
            }
            Map<String, Object> roleParams = new HashMap<>();
            roleParams.put("groupName", "月度工作提报-主任");
            roleParams.put("TENANT_ID_", "1");
            List<Map<String, String>> userInfos = xcmgProjectOtherDao.queryUserByGroupName(roleParams);
            if (userInfos != null && !userInfos.isEmpty()) {
                for (Map<String, String> oneUser : userInfos) {
                    if (!"IN_JOB".equalsIgnoreCase(oneUser.get("STATUS_"))) {
                        continue;
                    }
                    IUser user = userService.getByUserId(oneUser.get("USER_ID_"));
                    ContextUtil.setCurUser(user);
                    ProcessMessage handleMessage = new ProcessMessage();
                    try {
                        ProcessHandleHelper.setProcessMessage(handleMessage);
                        ProcessStartCmd startCmd = new ProcessStartCmd();
                        startCmd.setSolId(solId);
                        JSONObject objData = new JSONObject();
                        // 拼接主表数据
                        objData.put("CREATE_BY_", ContextUtil.getCurrentUser().getUserId());
                        objData.put("creatorName", ContextUtil.getCurrentUser().getUsername());
                        // objData.put("workReportName", "auto_create_test3");
                        String yearMonth = DateFormatUtil.format(new Date(), "yyyy-MM");
                        objData.put("yearMonth", yearMonth);
                        JSONObject deptInfo =
                            commonInfoManager.queryDeptByUserId(ContextUtil.getCurrentUser().getUserId());
                        String departName = deptInfo.getString("deptName");
                        objData.put("departName", departName);
                        objData.put("CREATE_TIME", new Date());
                        // 这里要用传过来的类型
                        startCmd.setJsonData(objData.toJSONString());
                        // 启动流程
                        bpmInstManager.doStartProcess(startCmd);
                    } catch (Exception ex) {
                        // 把具体的错误放置在内部处理，以显示正确的错误信息提示，在此不作任何的错误处理
                        logger.error("月度工作提报月初批量启动流程YdgztbScheduleJob任务执行失败" + ExceptionUtil.getExceptionMessage(ex));
                    }
                }
            }
        } catch (Exception e) {
            logger.error("ydgztbCreateFlow任务执行失败", e);
        }
    }

    /**
     * 每个月的26号10点执行，通知所长进行月度工作提报优秀评定通知
     */
    @Scheduled(cron = "0 0 10 26 * *")
    public void ydgztbGoodEvaluateNotice() {
        try {
            logger.info("start ydgztbGoodEvaluateNotice");
            String webappName = WebAppUtil.getProperty("webappName", "rdm");
            if (!"rdm".equalsIgnoreCase(webappName)) {
                return;
            }
            String yearMonth = DateFormatUtil.format(new Date(), "yyyy-MM");
            JSONObject params = new JSONObject();
            params.put("yearMonth", yearMonth);
            // 这里要获取所有技术中心所长
            StringBuilder userIdsBuilder = new StringBuilder();
            List<Map<String, String>> deptResps = commonInfoManager.queryJszxAllSuoZhang();
            if (deptResps != null && !deptResps.isEmpty()) {
                for (Map<String, String> depRespMan : deptResps) {
                    String userId = depRespMan.get("USER_ID_");
                    userIdsBuilder.append(userId).append(",");
                }
            }
            if (userIdsBuilder.length() > 1) {
                userIdsBuilder.deleteCharAt(userIdsBuilder.length() - 1);
                JSONObject noticeObj = new JSONObject();
                String noticeStr = "【月度工作提报评选通知】:请尽快评选" + yearMonth + "月度优秀案例";
                noticeObj.put("content", noticeStr);
                sendDDNoticeManager.sendNoticeForCommon(userIdsBuilder.toString(), noticeObj);
            }
        } catch (Exception e) {
            logger.error("ydgztbGoodEvaluateNotice任务执行失败", e);
        }
    }


    /**
     * 每个月的26号1点执行，终止当前未提报的流程
     */
    @Scheduled(cron = "0 0 1 26 * *")
    public void ydgztbExpire() {
        try {
            logger.info("start ydgztbExpire");
            //查询所有未提报数据instId
            List<JSONObject> instIdsList = ydgztbDao.queryWtbInstIds();
            // 更新当前月未提报的数据TbStatus为ytwt:应提未提
            ydgztbDao.updateYdgztbStatus();
            //根据instId废止流程
            for (JSONObject instIdObj : instIdsList) {
                bpmInstManager.doDiscardProcessInstance(instIdObj.getString("instId"));
            }
        } catch (Exception e) {
            logger.error("ydgztbExpire任务执行失败", e);
        }
    }
}
