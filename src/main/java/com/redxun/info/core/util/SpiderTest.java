package com.redxun.info.core.util;

import org.apache.log4j.Logger;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
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
public class SpiderTest implements PageProcessor {
    private Logger logger = Logger.getLogger(SpiderTest.class);
    private Site site = Site.me().setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31")
            .setRetryTimes(3).setSleepTime(1000).setTimeOut(1000);

    @Override
    public void process(Page page) {
        for(int i=0;i<89;i++) {
            String test=page.getHtml().xpath("//div[@class='right pagelist_right']/div[@class='plist'][1]/div[@class='item cl']").toString();
            System.out.println("********i="+i+"*********"+test);
        }
        System.out.println();
        /*List<XGRemarkEntity> remarkList = new ArrayList<XGRemarkEntity>();
        XGRemarkEntity remarkEntity;
        int pageSize = page.getHtml().xpath("//div[@class='JS_ol_top']").nodes().size();
        logger.info("Current Page Size: "+pageSize);

        for(int i=1;i<=pageSize;i++){
            remarkEntity = new XGRemarkEntity();
            //品牌名称
            String title = page.getHtml().xpath("//div[@class='warp']//div[@class='grid730']//div[@class='hot_koubei_content piclistLxy']/ul/li["+i+"]//div[@class='new_pWen']/a/text()").toString();
            remarkEntity.setTitle(title);
            logger.info("title: "+title);
            //评分
            int score = Integer.parseInt(page.getHtml().xpath("//div[@class='warp']//div[@class='grid730']//div[@class='hot_koubei_content piclistLxy']/ul/li["+i+"]//div[@class='start_koubei']//p[@class='s2']/font/text()").toString());
            remarkEntity.setScore(score);
            logger.info("score: "+score);
            //机型个数  例如:'2个'
            String modelNumStr = page.getHtml().xpath("//div[@class='warp']//div[@class='grid730']//div[@class='hot_koubei_content piclistLxy']/ul/li["+i+"]//div[@class='new_detail']//div[@class='new_detail_l']/p/a/text()").toString();
            String modelNum = modelNumStr.substring(0, modelNumStr.indexOf("个"));
            remarkEntity.setModelNum(modelNum);
            logger.info("modelNum: "+modelNum);
            //好评  例如:'好评: 100%'
            String highCommentPercentStr = page.getHtml().xpath("//div[@class='warp']//div[@class='grid730']//div[@class='hot_koubei_content piclistLxy']/ul/li["+i+"]//div[@class='new_detail']//div[@class='new_detail_l']/p[2]/text()").toString();
            String highCommentPercent = highCommentPercentStr.substring(3, highCommentPercentStr.length());
            remarkEntity.setHighCommentPercent(highCommentPercent);
            logger.info("highCommentPercent: "+highCommentPercent);
            //图片链接
            String imageUrl = page.getHtml().xpath("//div[@class='warp']//div[@class='grid730']//div[@class='hot_koubei_content piclistLxy']/ul/li["+i+"]//div[@class='new_pic']/a/img/@src").toString();
            remarkEntity.setImageUrl(imageUrl);
            logger.info("imageUrl: "+imageUrl);

            remarkList.add(remarkEntity);
        }
        page.putField("test",remarkList);*/
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new SpiderTest()).addUrl("https://zj.lmjx.net/model/wajueji/sany/")
            .addPipeline(new JsonFilePipeline("E:\\webmagic\\")).thread(5).runAsync();
        System.out.println();
    }
}
