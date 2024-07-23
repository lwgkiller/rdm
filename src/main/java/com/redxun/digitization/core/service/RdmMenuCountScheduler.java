package com.redxun.digitization.core.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.digitization.core.dao.SzhDao;
import com.redxun.saweb.util.WebAppUtil;

/**
 * 定时器，删除菜单点击量，只保留1年的数据，每月1号02:00执行
 */
@Service
@EnableScheduling
public class RdmMenuCountScheduler {
    private static Logger logger = LoggerFactory.getLogger(RdmMenuCountScheduler.class);
    @Autowired
    private SzhDao szhDao;

    /**
     * 每月1号02:00执行,删除1年之前的数据
     */
    @Scheduled(cron = "0 0 2 1 * *")
    public void menuClickClear() {
        String webappName = WebAppUtil.getProperty("webappName", "rdm");
        if (!"rdm".equalsIgnoreCase(webappName)) {
            return;
        }
        logger.error("start delete menuClick");
        String oneYearAgo = DateFormatUtil.format(DateUtil.addYear(new Date(), -1), "yyyy-MM");
        Map<String, Object> param = new HashMap<>();
        param.put("yearMonth", oneYearAgo);
        szhDao.deletePageClickOneYearAgo(param);
        logger.error("end delete menuClick");
    }
}
