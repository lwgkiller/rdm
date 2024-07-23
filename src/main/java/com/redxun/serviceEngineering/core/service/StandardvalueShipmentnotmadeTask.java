package com.redxun.serviceEngineering.core.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.serviceEngineering.core.dao.StandardvalueShipmentnotmadeDao;

@Service
@EnableScheduling
public class StandardvalueShipmentnotmadeTask {
    private static Logger logger = LoggerFactory.getLogger(StandardvalueShipmentnotmadeTask.class);

    @Autowired
    private StandardvalueShipmentnotmadeDao standardvalueShipmentnotmadeDao;

    /**
     * 每天早上9:30进行扫描处理
     */
    @Scheduled(cron = "0 30 9 * * *")
    public void projectDelayNotice() {
        try {
            String webappName = WebAppUtil.getProperty("webappName", "rdm");
            if (!"rdm".equalsIgnoreCase(webappName)) {
                return;
            }
            List<JSONObject> list = standardvalueShipmentnotmadeDao.queryByResponseTime();
            if (list.size() > 0) {
                for (JSONObject object : list) {
                    Date date = new Date();
                    Long createTime = object.getDate("CREATE_TIME_").getTime();
                    long day = (date.getTime() - createTime) / 24 / 60 / 60 / 1000;
                    if (day <= 7) {
                        object.put("responseStatus", "正常");
                    } else if (7 < day && day <= 14) {
                        object.put("responseStatus", "一级");
                    } else if (14 < day && day <= 17) {
                        object.put("responseStatus", "二级");
                    } else {
                        object.put("responseStatus", "三级");
                    }
                    standardvalueShipmentnotmadeDao.updateData(object);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("标准值表响应状态更新任务执行失败");
        }
    }
}
