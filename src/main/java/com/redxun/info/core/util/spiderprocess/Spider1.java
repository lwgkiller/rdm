package com.redxun.info.core.util.spiderprocess;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.redxun.core.util.AppBeanUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.info.core.dao.InfoNewsDao;
import com.redxun.info.core.util.InfoUtil;
import com.redxun.saweb.util.IdUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.XGRemarkEntity;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * 铁甲网的新品模块分析
 */
public class Spider1 implements PageProcessor {
    private Logger logger = Logger.getLogger(Spider1.class);
    private Site site = Site.me().setUserAgent(
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31")
        .setRetryTimes(3).setSleepTime(1000).setTimeOut(1000);
    private JSONObject configObj;
    // 分页基本url
    private String baseUrl;

    public Spider1() {}

    public Spider1(JSONObject configObj) {
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
        int onePageNewSize = page.getHtml().xpath("//ul[@id='page_list']/li").nodes().size();
        for (int index = 1; index <= onePageNewSize; index++) {
            JSONObject oneNew = new JSONObject();
            String title =
                page.getHtml().xpath("//ul[@id='page_list']/li[" + index + "]/div[@class='w']/h2/a/text()").toString();
            oneNew.put("title", title);
            String subUrl =
                page.getHtml().xpath("//ul[@id='page_list']/li[" + index + "]/div[@class='w']/h2/a/@href").toString();
            oneNew.put("subUrl", subUrl);
            String temp = page.getHtml().xpath("//ul[@id='page_list']/li[" + index + "]/div[@class='w']/div/text()")
                .toString().trim();
            temp = InfoUtil.trimBlankForStr(temp);
            if (StringUtils.isNotBlank(temp)) {
                String[] tempArr = temp.split(" ", -1);
                if (tempArr.length >= 3) {
                    String publishDate = tempArr[0].trim();
                    oneNew.put("publishDate", publishDate);
                    String source = tempArr[2].trim();
                    if (source.startsWith("来源：")) {
                        source = source.substring(source.indexOf("：") + 1);
                    } else if (source.startsWith("来源:")) {
                        source = source.substring(source.indexOf(":") + 1);
                    }
                    oneNew.put("source", source);
                }
            }
            oneNew.put("content", "");
            oneNew.put("id", IdUtil.getId());
            oneNew.put("urlConfigId", configObj.getString("id"));
            oneNew.put("CREATE_TIME_", new Date());
            result.add(oneNew);
        }
        AppBeanUtil.getBean(InfoNewsDao.class).batchInsertObject(result);

        // 下钻子页面
        for (JSONObject parentPage : result) {
            Spider.create(new Spider1_1(parentPage)).addUrl(parentPage.getString("subUrl")).thread(1).runAsync();
        }

        // 暂时只爬取前20页
        List<String> targetUrls = new ArrayList<>();
        for (int pageSize = 2; pageSize <= 20; pageSize++) {
            targetUrls.add(baseUrl + "/" + pageSize);
        }
        page.addTargetRequests(targetUrls);
    }
}
