package com.redxun.powerApplicationTechnology.core.job;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.scheduler.BaseJob;
import com.redxun.core.util.AppBeanUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.powerApplicationTechnology.core.dao.RoadtestDao;
import com.redxun.powerApplicationTechnology.core.service.RoadtestService;
import com.redxun.saweb.context.ContextUtil;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RoadtestJob extends BaseJob {
    private static Logger logger = LoggerFactory.getLogger(RoadtestJob.class);
    private RoadtestService roadtestService;
    private RoadtestDao roadtestDao;

    @Override
    public void executeJob(JobExecutionContext context) {
        try {
            roadtestService = AppBeanUtil.getBean(RoadtestService.class);
            roadtestDao = AppBeanUtil.getBean(RoadtestDao.class);
            JSONObject params = new JSONObject();
            params.put("isClose", "否");
            List<JSONObject> roadtestList = roadtestDao.dataListQuery(params);
            List<String> roadtestIdsList = new ArrayList<>();
            for (JSONObject roadtest : roadtestList) {
                if (DateUtil.daysBetween(new Date(), roadtest.getDate("roadtestEndDate")) < 0) {
                    roadtest.put("isClose", "是");
                    roadtest.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    roadtest.put("UPDATE_TIME_", new Date());
                    roadtestDao.updateBusiness(roadtest);
                } else {
                    roadtestIdsList.add(roadtest.getString("id"));
                    roadtestService.synchGPS(roadtest.getString("id"));
                }
            }
            if (!roadtestIdsList.isEmpty()) {
                String[] roadtestIds = StringUtil.join(roadtestIdsList, ":").split(":");
                roadtestService.calculateBusiness(roadtestIds);
            }
        } catch (Exception e) {
            logger.error("RoadtestJob任务执行失败");
        }
    }
}
