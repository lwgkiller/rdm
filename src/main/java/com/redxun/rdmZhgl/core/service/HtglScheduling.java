package com.redxun.rdmZhgl.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.dao.HtglDao;
import com.redxun.rdmZhgl.core.dao.HtglFileDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.sys.core.manager.SysSeqIdManager;
import com.redxun.xcmgJsjl.core.manager.XcmgJsjlManager;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 产学研<p>
 * 产学研模块<p>
 * Copyright: Copyright (C) 2021 XXX, Inc. All rights reserved. <p>
 * Company: 徐工挖掘机械有限公司<p>
 *
 * @author liwenguang
 * @since 2021/2/25
 */
@Service
@EnableScheduling
public class HtglScheduling {
    private static Logger logger = LoggerFactory.getLogger(HtglScheduling.class);
    @Autowired
    private HtglDao htglDao;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private HtglFileService htglFileService;
    @Autowired
    private HtglFileDao htglFileDao;
    @Autowired
    private SysSeqIdManager sysSeqIdManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;

    /**
     * 每天9:00点执行，提醒上传合同文档
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void htglFileUploadNotice() {
        JSONObject params = new JSONObject();
        params.put("isDiscard", "0");
        List<JSONObject> hts = htglDao.dataListQuery(params);
        for (JSONObject ht : hts) {
            if (StringUtil.isNotEmpty(ht.getString("signDate"))) {
                Date signDate = DateUtil.parseDate(ht.getString("signDate"));
                Date currentDate = new Date();
                long diffInMillis = currentDate.getTime() - signDate.getTime();
                long diffInDays = diffInMillis / (24 * 60 * 60 * 1000);
                if (diffInDays > 7) {//时间复杂度已经稀释，勉强可以在其中执行sql
                    Map<String, Boolean> map = new HashedMap();
                    map.put("合同审批表", false);
                    map.put("合同文本", false);
                    map.put("廉洁承诺协议", false);
                    params.clear();
                    params.put("contractIds", Arrays.asList(ht.getString("id")));
                    List<JSONObject> files = htglFileDao.queryFilesByContractIds(params);
                    for (JSONObject file : files) {
                        if (map.containsKey(file.getString("fileType"))) {
                            map.put(file.getString("fileType"), true);
                        }
                    }
                    final boolean[] isNotSend = {true};
                    map.forEach((k, v) -> {
                        isNotSend[0] &= v;
                    });
                    if (!isNotSend[0]) {
                        JSONObject noticeObj = new JSONObject();
                        StringBuilder stringBuilder = new StringBuilder("【合同文件上传通知】:编号为 ");
                        stringBuilder.append(ht.getString("contractNo"));
                        stringBuilder.append(" 的合同已签订超过一周，请抓紧上传附件")
                                .append(XcmgProjectUtil.getNowLocalDateStr(DateUtil.DATE_FORMAT_FULL));
                        noticeObj.put("content", stringBuilder.toString());
                        sendDDNoticeManager.sendNoticeForCommon(ht.getString("signerUserId"), noticeObj);
                    }
                }
            }
        }
    }
}
