package com.redxun.wwrz.core.service;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import com.microsoft.windowsazure.core.pipeline.apache.HttpServiceResponseContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.portrait.core.dao.PortraitDocumentDao;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmZhgl.core.dao.*;
import com.redxun.rdmZhgl.core.service.MaterialApplyService;
import com.redxun.rdmZhgl.core.service.MonthUnProjectPlanService;
import com.redxun.rdmZhgl.core.service.NdkfjhPlanDetailService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import com.redxun.xcmgProjectManager.report.manager.XcmgProjectReportManager;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author zhangzhen
 */
@Service
@EnableScheduling
public class WwrzScheduler {
    private static Logger logger = LoggerFactory.getLogger(WwrzScheduler.class);

    @Resource
    private WwrzPlanApplyService wwrzPlanApplyService;

    /**
     * 每季度第一天自动生成委外认证计划
     */
    @Scheduled(cron = "0 0 8 1 1,4,7,10 *")
    public void dealMaterialInfo() {
        String webappName = WebAppUtil.getProperty("webappName", "rdm");
        if (!"rdm".equalsIgnoreCase(webappName)) {
            return;
        }
        wwrzPlanApplyService.genFlowData();
    }
}
