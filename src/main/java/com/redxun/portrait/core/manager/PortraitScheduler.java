package com.redxun.portrait.core.manager;

import com.redxun.saweb.util.WebAppUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author zhangzhen
 */
@Service
@EnableScheduling
public class PortraitScheduler {
    private static Logger logger = LoggerFactory.getLogger(PortraitScheduler.class);
    @Resource
    PortraitStandardManager portraitStandardManager;
    @Resource
    PortraitCourseManager portraitCourseManager;
    @Resource
    PortraitCultureManager portraitCultureManager;
    @Resource
    PortraitAttendanceManager portraitAttendanceManager;
    @Resource
    PortraitAsyncRewardManager portraitAsyncRewardManager;
    @Resource
    PortraitContractManager portraitContractManager;
    @Resource
    PortraitPatentManager portraitPatentManager;
    @Resource
    PortraitBbsManager portraitBbsManager;
    @Resource
    PortraitInformationManager portraitInformationManager;
    @Resource
    PortraitTechnologyManager portraitTechnologyManager;
    @Resource
    PortraitSecretManager portraitSecretManager;
    @Resource
    PortraitKnowledgeManager portraitKnowledgeManager;
    @Resource
    PortraitAnalysisImproveManager portraitAnalysisImproveManager;
    /**
     * 每天晚上 11点半同步
     */
    @Scheduled(cron = "0 30 23 * * *")
    public void asyncPortraitData() {
        String webappName = WebAppUtil.getProperty("webappName", "rdm");
        if (!"rdm".equalsIgnoreCase(webappName)) {
            return;
        }
        //1.同步标准
        portraitStandardManager.asyncStandard();
        //2.同步课程，挖机原有的职业发展系统已停掉，暂时不取这块的数据
//        portraitCourseManager.asyncCourse();
        //3.同步导师带徒
//        portraitCultureManager.asyncCulture();
    }
    /**
     * 每个月一号同步上个月数据
     */
    @Scheduled(cron = "0 30 22 1 * * ")
    public void asyncAttendanceData() {
        String webappName = WebAppUtil.getProperty("webappName", "rdm");
        if (!"rdm".equalsIgnoreCase(webappName)) {
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        cal.setTime(date);
        cal.add(Calendar.MONTH, -1);
        String yearMonth = sdf.format(cal.getTime());
        portraitAttendanceManager.asyncAttendance(yearMonth);
    }
    /**
     * 每个月一号同步获奖数据
     */
    @Scheduled(cron = "0 30 23 1 * * ")
    public void asyncAwardData() {
        String webappName = WebAppUtil.getProperty("webappName", "rdm");
        if (!"rdm".equalsIgnoreCase(webappName)) {
            return;
        }
        //1.同步市级及以上科技进步奖
        portraitAsyncRewardManager.asyncCityReward();
        //2.同步集团科技进步奖
        portraitAsyncRewardManager.asyncGroupReward();
        //3.同步科技计划项目
        portraitAsyncRewardManager.asyncScienceReward();
        //4.同步专利奖
        portraitAsyncRewardManager.asyncPatentReward();
        //5.同步高品
        portraitAsyncRewardManager.asyncHeightReward();
        //6.同步新产品
        portraitAsyncRewardManager.asyncNewProductReward();
        //7.同步管理奖
        portraitAsyncRewardManager.asyncManageReward();
        //8.同步人才奖
        portraitAsyncRewardManager.asyncTalentReward();
        //8.同步其他奖
        portraitAsyncRewardManager.asyncOtherReward();
    }
    /**
     * 每天同步绩效数据、合同数据、专利解读、论坛、情报工程、技术数据库
     * 技术密码、知识产权
     */
    @Scheduled(cron = "0 20 23 * * * ")
    public void asyncRewardData() {
        String webappName = WebAppUtil.getProperty("webappName", "rdm");
        if (!"rdm".equalsIgnoreCase(webappName)) {
            return;
        }
        //合同
        portraitContractManager.asyncContract();
        //专利解读
        portraitPatentManager.asyncPatent();
        //论坛
        portraitBbsManager.asyncBbs();
        //情报工程
        portraitInformationManager.asyncInformation();
        //技术数据库
        portraitTechnologyManager.asyncTechnology();
        //技术秘密
        portraitSecretManager.asyncSecret();
        //同步知识产权
        portraitKnowledgeManager.asyncKnowledge();
        //同步分析改进数据
        portraitAnalysisImproveManager.asyncAnalysisImprove();
    }
}
