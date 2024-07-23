package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.portrait.core.dao.PortraitDocumentDao;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmZhgl.core.dao.*;
import com.redxun.rdmZhgl.core.service.MaterialApplyService;
import com.redxun.rdmZhgl.core.service.MonthUnProjectPlanService;
import com.redxun.rdmZhgl.core.service.NdkfjhPlanDetailService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.org.entity.OsUser;
import com.redxun.sys.org.manager.OsUserManager;
import com.redxun.wwrz.core.dao.WwrzFilesDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import com.redxun.xcmgProjectManager.report.manager.XcmgProjectReportManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

import static com.redxun.rdmCommon.core.controller.RdmApiController.START_PROCESS;

@Service
@EnableScheduling
public class BucketConfigurationScheduler {
    private static Logger logger = LoggerFactory.getLogger(BucketConfigurationScheduler.class);
    @Autowired
    private BucketConfigurationService bucketConfigurationService;
    @Autowired
    private OsUserManager osUserManager;
    @Autowired
    private SysDicManager sysDicManager;

    /**
     * {秒数} {分钟} {小时} {日期} {月份} {星期} {年份(可为空)}
     */
    @Scheduled(cron = "0 0 6 1 JAN,APR,JUL,OCT ?")//每个季度的第一个月的第一天早上6点
//    @Scheduled(cron = "0 15 09 * * ?")//测试用
    public void createAuto() throws Exception {
        JSONObject result = new JSONObject();
        result.put("success", true);
        String userNoString = sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringGroups",
                "bucketConfigurationRepUsers").getValue();
        String[] userNos = userNoString.split(",");
        for (String userNo : userNos) {
            OsUser osUser = osUserManager.getByUserName(userNo);
            if (osUser != null) {
                JSONObject businessData = new JSONObject();
                businessData.put("isAuto", "true");
                businessData.put("repUserId", osUser.getUserId());
                businessData.put("repUser", osUser.getFullname());
                businessData.put("applyTime", DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_YMD));
                bucketConfigurationService.createAuto(result, businessData, START_PROCESS);
                if (!result.getBoolean("success")) {
                    logger.error(result.getString("message"));
                    throw new RuntimeException(result.getString("message"));//直接抛运行时，阻断事务，全部回滚
                }
            }
        }
    }
}
