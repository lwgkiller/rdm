package com.redxun.info.core.util.paper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.util.AppBeanUtil;
import com.redxun.info.core.dao.InfoPaperDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

/**
 * 铁甲网的新品模块分析
 */
public class SpiderHeavyPaperList implements PageProcessor {
    private Logger logger = Logger.getLogger(SpiderHeavyPaperList.class);
    private Site site = Site.me().setUserAgent(
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31")
        .setRetryTimes(3).setSleepTime(1000).setTimeOut(10000);
    private JSONObject configObj;
    // 分页基本url
    private String baseUrl;

    public SpiderHeavyPaperList() {}

    public SpiderHeavyPaperList(JSONObject configObj) {
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
        List<Selectable> list = page.getHtml().xpath("//div[@id='ctl00_cphContect_divlist']/div[@class='item']").nodes();
        Date asyncDate = configObj.getDate("asyncDate");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for(Selectable selectable : list){
            JSONObject oneNew = new JSONObject();
            String title = selectable.xpath("//div[@class='title']/a/text()").toString();
            oneNew.put("title", title);
            String subUrl = selectable.xpath("//div[@class='title']/a/@href").toString();
            oneNew.put("subUrl", subUrl);
            String author = selectable.xpath("//div[@class='authors']/font/text()").toString();
            oneNew.put("author", author);
            String publishDate = selectable.xpath("//div[@class='digest']/a/text()").toString();
            publishDate = publishDate.replace("年","-").replace("期","-01");
            if(asyncDate!=null){
                if(asyncDate.getTime()>sdf.parse(publishDate).getTime()){
                    continue;
                }
            }
            oneNew.put("publishDate",publishDate);
            oneNew.put("content", "");
            oneNew.put("id", IdUtil.getId());
            oneNew.put("urlConfigId", configObj.getString("urlConfigId"));
            oneNew.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            oneNew.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            oneNew.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            oneNew.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            result.add(oneNew);
        }

        AppBeanUtil.getBean(InfoPaperDao.class).batchInsertObject(result);

        // 下钻子页面
        for (JSONObject parentPage : result) {
            Spider.create(new SpiderHeavyPaperContent(parentPage)).addUrl("http://www.zxjxqk.com"+parentPage.getString("subUrl")).thread(1).runAsync();
        }

        // 暂时只爬取前20页
        List<String> targetUrls = new ArrayList<>();
        for (int pageSize = 2; pageSize <= 5; pageSize++) {
            targetUrls.add(baseUrl + "/" + pageSize);
        }
        page.addTargetRequests(targetUrls);
    }
}
