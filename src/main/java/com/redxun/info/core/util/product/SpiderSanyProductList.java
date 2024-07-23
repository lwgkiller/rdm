package com.redxun.info.core.util.product;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.util.AppBeanUtil;
import com.redxun.info.core.dao.InfoProductDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

/**
 * 三一挖机列表
 */
public class SpiderSanyProductList implements PageProcessor {
    private Logger logger = Logger.getLogger(SpiderSanyProductList.class);
    private Site site = Site.me().setUserAgent(
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31")
        .setRetryTimes(3).setSleepTime(1000).setTimeOut(10000);
    private JSONObject configObj;
    // 分页基本url
    private String baseUrl;

    public SpiderSanyProductList() {}

    public SpiderSanyProductList(JSONObject configObj) {
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
        JSONObject resultJson;
        List<Selectable> list = page.getHtml().xpath("//div[@class='right pagelist_right']/div[@class='plist']/div[@class='item cl']").nodes();
        for(Selectable selectable : list){
            JSONObject oneNew = new JSONObject();
            String model = selectable.xpath("//div[@class='l2']/dl/dd[1]/text()").toString();
            if(model!=null&&!"".equals(model)){
                oneNew.put("model", model);
                //根据型号查询是否存在
                resultJson = AppBeanUtil.getBean(InfoProductDao.class).getObjectByModel(model);
                if(resultJson==null){
                    String company = selectable.xpath("//div[@class='l2']/dl/dd[3]/text()").toString();
                    String productType = selectable.xpath("//div[@class='l2']/dl/dd[4]/text()").toString();
                    oneNew.put("productType", productType);
                    String subUrl = page.getHtml().xpath("div[@class='l2']/a/@href").toString();
                    oneNew.put("subUrl", subUrl);
                    oneNew.put("content", "");
                    oneNew.put("id", IdUtil.getId());
                    oneNew.put("urlConfigId", configObj.getString("urlConfigId"));
                    oneNew.put("company",company);
                    oneNew.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneNew.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    oneNew.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneNew.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    result.add(oneNew);
                }
            }
        }
        AppBeanUtil.getBean(InfoProductDao.class).batchInsertObject(result);

        // 下钻子页面
        for (JSONObject parentPage : result) {
            Spider.create(new SpiderSanyProductContent(parentPage)).addUrl(parentPage.getString("subUrl")+"param/").thread(1).runAsync();
        }

        // 暂时只爬取前20页
        List<String> targetUrls = new ArrayList<>();
        for (int pageSize = 2; pageSize <= 2; pageSize++) {
            targetUrls.add(baseUrl + "/" + pageSize);
        }
        page.addTargetRequests(targetUrls);
    }
}
