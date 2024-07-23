package com.redxun.info.core.util.reward;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.util.AppBeanUtil;
import com.redxun.info.core.dao.InfoRewardDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * @author zhangzhen
 */
public class SpiderMacRewardList implements PageProcessor {
    private Logger logger = Logger.getLogger(SpiderMacRewardList.class);
    private Site site = Site.me().setUserAgent(
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31")
        .setRetryTimes(3).setSleepTime(1000).setTimeOut(10000);
    private JSONObject configObj;
    // 分页基本url
    private String baseUrl;

    public SpiderMacRewardList() {}

    public SpiderMacRewardList(JSONObject configObj) {
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
    public void process(Page page)  {
        List<JSONObject> result = new ArrayList<>();
        int onePageNewSize = page.getHtml().xpath("//div[@class='body_center_right_content']/ul/li").nodes().size();
        JSONObject resultJson = new JSONObject();
        Date asyncDate = configObj.getDate("asyncDate");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            for (int index = 1; index <= onePageNewSize; index++) {
                JSONObject oneNew = new JSONObject();
                String publishDate = page.getHtml().xpath("//div[@class='body_center_right_content']/ul/li[" + index + "]/span/text()").toString().trim();
                publishDate = publishDate.substring(1,publishDate.length()-1);
                if(asyncDate!=null){
                    if(asyncDate.getTime()>sdf.parse(publishDate).getTime()){
                        continue;
                    }
                }
                String title = page.getHtml().xpath("//div[@class='body_center_right_content']/ul/li[" + index + "]/a/text()").toString().trim();
                oneNew.put("title", title);
                //判断是否存在，存在则不导入
                resultJson = AppBeanUtil.getBean(InfoRewardDao.class).getObjectByTitle(title);
                if(resultJson!=null){
                    continue;
                }
                String subUrl = page.getHtml().xpath("//div[@class='body_center_right_content']/ul/li[" + index + "]/a/@href").toString().trim();
                oneNew.put("subUrl", "http://www.sumia.org/"+subUrl);
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
            AppBeanUtil.getBean(InfoRewardDao.class).batchInsertObject(result);

            // 下钻子页面
            for (JSONObject parentPage : result) {
                Spider.create(new SpiderMacRewardContent(parentPage)).addUrl(parentPage.getString("subUrl")).thread(1).runAsync();
            }

            // 暂时只爬取前20页
            List<String> targetUrls = new ArrayList<>();
            for (int pageSize = 2; pageSize <= 5; pageSize++) {
                targetUrls.add(baseUrl + "/" + pageSize);
            }
            page.addTargetRequests(targetUrls);
        }catch (ParseException e){
            logger.error(e.getMessage());
        }
    }
}
