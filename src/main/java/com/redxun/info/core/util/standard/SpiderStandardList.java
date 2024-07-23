package com.redxun.info.core.util.standard;

import java.util.ArrayList;
import java.util.List;

import com.redxun.core.util.AppBeanUtil;
import com.redxun.info.core.dao.InfoStandardDao;
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
 * 标准分析
 * @author zhangzhen
 */
public class SpiderStandardList implements PageProcessor {
    private Logger logger = Logger.getLogger(SpiderStandardList.class);
    private Site site = Site.me().setUserAgent(
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31")
        .setRetryTimes(3).setSleepTime(1000).setTimeOut(1000);
    private JSONObject configObj;
    // 分页基本url
    private String baseUrl;

    public SpiderStandardList() {}

    public SpiderStandardList(JSONObject configObj) {
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
        int onePageNewSize = page.getHtml().xpath("//table[@class='layui-table']/tbody/tr").nodes().size();
        for (int index = 1; index <= onePageNewSize; index++) {
            JSONObject oneNew = new JSONObject();
            String standardCode = page.getHtml().xpath("//table[@class='layui-table']/tbody/tr[" + index + "]/td[1]/a/text()").toString();
            oneNew.put("standardCode", standardCode);
            JSONObject resultJson = AppBeanUtil.getBean(InfoStandardDao.class).getObjectByCode(standardCode);
            if(resultJson != null){
                continue;
            }
            String subUrl = page.getHtml().xpath("////table[@class='layui-table']/tbody/tr[" + index + "]/td[1]/a/@href").toString();
            oneNew.put("subUrl", subUrl);
            String standardName = page.getHtml().xpath("//table[@class='layui-table']/tbody/tr[" + index + "]/td[2]/a/@title").toString();
            oneNew.put("standardName", standardName);
            String standardStatus = page.getHtml().xpath("//table[@class='layui-table']/tbody/tr[" + index + "]/td[3]/text()").toString();
            oneNew.put("standardStatus", standardStatus);
            String publishDate = page.getHtml().xpath("//table[@class='layui-table']/tbody/tr[" + index + "]/td[4]/a/text()").toString();
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
        if(!result.isEmpty()){
            AppBeanUtil.getBean(InfoStandardDao.class).batchInsertObject(result);

            // 下钻子页面
            for (JSONObject parentPage : result) {
                Spider.create(new SpiderStandardContent(parentPage)).addUrl("http://www.zggjbz.com"+parentPage.getString("subUrl")).thread(100).runAsync();
            }
        }
        // 暂时只爬取前20页
        List<String> targetUrls = new ArrayList<>();
        for (int pageSize = 2; pageSize <= 3; pageSize++) {
            targetUrls.add(baseUrl + "/" + pageSize);
        }
        page.addTargetRequests(targetUrls);
    }
}
