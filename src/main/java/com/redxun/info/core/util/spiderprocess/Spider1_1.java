package com.redxun.info.core.util.spiderprocess;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.util.AppBeanUtil;
import com.redxun.info.core.dao.InfoNewsDao;
import com.redxun.saweb.util.IdUtil;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * 应用模块名称
 * <p>
 * 代码描述
 * <p>
 * Copyright: Copyright (C) 2020 XXX, Inc. All rights reserved.
 * <p>
 * Company: 徐工挖掘机械有限公司
 * <p>
 *
 * @author liangchuanjiang
 * @since 2020/12/4 17:08
 */
public class Spider1_1 implements PageProcessor {
    private Logger logger = Logger.getLogger(Spider1_1.class);
    private Site site = Site.me().setUserAgent(
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31")
        .setRetryTimes(3).setSleepTime(1000).setTimeOut(1000);
    private JSONObject configObj;

    public Spider1_1() {}

    public Spider1_1(JSONObject configObj) {
        this.configObj = configObj;
    }

    @Override
    public Site getSite() {
        return site;
    }

    @Override
    public void process(Page page) {
        String content=page.getHtml().xpath("//div[@class='article_main']").toString();
        if(StringUtils.isNotBlank(content)) {
            content=content.replace("\"","'");
            content=content.replaceAll("\r|\n","");
        }
        JSONObject oneResult=new JSONObject();
        oneResult.put("content", content);
        oneResult.put("id", configObj.getString("id"));
        AppBeanUtil.getBean(InfoNewsDao.class).updateObject(oneResult);
    }
}
