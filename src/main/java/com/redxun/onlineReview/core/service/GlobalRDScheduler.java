package com.redxun.onlineReview.core.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonResult;
import com.redxun.onlineReview.core.dao.OnlineReviewDao;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.saweb.util.WebAppUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.redxun.core.util.AppBeanUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.serviceEngineering.core.service.SeGeneralKanBanNewService;

@Service
@EnableScheduling
public class GlobalRDScheduler {
    private static Logger logger = LoggerFactory.getLogger(GlobalRDScheduler.class);

    @Resource
    private OnlineReviewDao onlineReviewDao;
    @Resource
    private SendDDNoticeManager sendDDNoticeManager;

    /**
     * 每天10点执行，出口产品订单评审，长周期件明细上传提醒
     */
    @Scheduled(cron = "0 0 10 * * *")
    public void onlineReviewLongPeriodUpload() {
        try {
            logger.info("start onlineReviewLongPeriodUpload");
            List<JSONObject> dataList = onlineReviewDao.queryCzqTime();
            if (dataList != null && !dataList.isEmpty()) {
                for(JSONObject onedata:dataList){
                    JSONObject noticeObj = new JSONObject();
                    noticeObj.put("content","内部订单号:"+onedata.getString("saleNum")
                            +"-机型:"+onedata.getString("designModel")
                            +"长周期件明细上传期限今日截止,请登录RDM进行上传!");
                    sendDDNoticeManager.sendNoticeForCommon(onedata.getString("userId"), noticeObj);
                }
            }
        } catch (Exception e) {
            logger.error("长周期件明细上传提醒发送失败", e);
        }
    }
}
