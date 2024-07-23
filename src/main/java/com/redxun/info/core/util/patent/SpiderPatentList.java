package com.redxun.info.core.util.patent;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * 铁甲网的新品模块分析
 */
public class SpiderPatentList implements PageProcessor {
    private Logger logger = Logger.getLogger(SpiderPatentList.class);
    private Site site = Site.me().setUserAgent(
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31")
        .setRetryTimes(3).setSleepTime(1000).setTimeOut(1000);
    private JSONObject configObj;
    // 分页基本url
    private String baseUrl;

    public SpiderPatentList() {}

    public SpiderPatentList(JSONObject configObj) {
        this.configObj = configObj;
        String originalUrl = configObj.getString("url");
        if (StringUtils.isNotBlank(originalUrl)) {
            baseUrl = originalUrl.substring(0, originalUrl.lastIndexOf("/"));
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    @Override
    public void process(Page page) {
        List<JSONObject> result = new ArrayList<>();
        int onePageNewSize = page.getHtml().xpath("//div[@class='JS_ol_top']/div[@class='u-list-div']").nodes().size();
        for (int index = 1; index <= onePageNewSize; index++) {
            JSONObject oneNew = new JSONObject();
            String title = page.getHtml().xpath("//div[@class='JS_ol_top']/div[@class='u-list-div'][" + index + "]/span/a/text()").toString();
            oneNew.put("title", title);
            String subUrl = page.getHtml().xpath("//ul[@class='tpl2_list mb10']/li[" + index + "]/span/a/@href").toString();
            oneNew.put("subUrl", subUrl);
            String publishDate = page.getHtml().xpath("//ul[@class='tpl2_list mb10']/li[" + index + "]/a/span[@class='kjxw_part_info fr mr10']/text()").toString().trim();
            oneNew.put("publishDate",publishDate);
            oneNew.put("content", "");
            oneNew.put("id", IdUtil.getId());
            oneNew.put("urlConfigId", configObj.getString("id"));
            oneNew.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            oneNew.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            oneNew.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            oneNew.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            result.add(oneNew);
        }
//        AppBeanUtil.getBean(InfoPatentDao.class).batchInsertObject(result);

        // 下钻子页面
        for (JSONObject parentPage : result) {
            Spider.create(new SpiderPatentContent(parentPage)).addUrl(parentPage.getString("subUrl")).thread(1).runAsync();
        }

        // 暂时只爬取前20页
        List<String> targetUrls = new ArrayList<>();
        for (int pageSize = 2; pageSize <= 20; pageSize++) {
            targetUrls.add(baseUrl + "/" + pageSize);
        }
        page.addTargetRequests(targetUrls);
    }
}
