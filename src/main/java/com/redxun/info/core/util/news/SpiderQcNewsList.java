package com.redxun.info.core.util.news;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.util.AppBeanUtil;
import com.redxun.info.core.dao.InfoNewsDao;
import com.redxun.info.core.util.InfoUtil;
import com.redxun.saweb.util.IdUtil;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * 质量管理信息 QC小组成果发表
 * @author zhangzhen
 */
public class SpiderQcNewsList implements PageProcessor {
    private Logger logger = Logger.getLogger(SpiderQcNewsList.class);
    private Site site = Site.me().setUserAgent(
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31")
        .setRetryTimes(3).setSleepTime(1000).setTimeOut(1000);
    private JSONObject configObj;
    // 分页基本url
    private String baseUrl;

    public SpiderQcNewsList() {}

    public SpiderQcNewsList(JSONObject configObj) {
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
    public void process(Page page) throws ParseException {
        List<JSONObject> result = new ArrayList<>();
        int onePageNewSize = page.getHtml().xpath("//div[@class='listBox Box']/ul/li").nodes().size();
        Date asyncDate = configObj.getDate("asyncDate");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (int index = 1; index <= onePageNewSize; index++) {
            JSONObject oneNew = new JSONObject();
            String title = page.getHtml().xpath("//div[@class='listBox Box']/ul/li[" + index + "]/a/text()").toString();
            oneNew.put("title", title);
            String subUrl = page.getHtml().xpath("//div[@class='listBox Box']/ul/li[" + index + "]/a/@href").toString();
            oneNew.put("subUrl", subUrl);
            String publishDate = page.getHtml().xpath("//div[@class='listBox Box']/ul/li[" + index + "]/span/text()").toString();
            publishDate = publishDate.replace("/","-");
            if(asyncDate!=null){
                if(asyncDate.getTime()>sdf.parse(publishDate).getTime()){
                    continue;
                }
            }
            oneNew.put("publishDate", publishDate);
            oneNew.put("source", "中国质量协会");
            oneNew.put("content", "");
            oneNew.put("id", IdUtil.getId());
            oneNew.put("urlConfigId", configObj.getString("urlConfigId"));
            oneNew.put("CREATE_TIME_", new Date());
            result.add(oneNew);
        }
        AppBeanUtil.getBean(InfoNewsDao.class).batchInsertObject(result);

        // 下钻子页面
        for (JSONObject parentPage : result) {
            Spider.create(new SpiderQcNewsContent(parentPage)).addUrl(parentPage.getString("subUrl")).thread(1).runAsync();
        }

        // 暂时只爬取前20页
        List<String> targetUrls = new ArrayList<>();
        for (int pageSize = 2; pageSize <= 10; pageSize++) {
            targetUrls.add(baseUrl + "/" + pageSize);
        }
        page.addTargetRequests(targetUrls);
    }
}
