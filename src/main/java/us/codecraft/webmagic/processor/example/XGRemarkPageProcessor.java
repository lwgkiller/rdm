package us.codecraft.webmagic.processor.example;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.db.MySQLConnector;
import us.codecraft.webmagic.model.XGRemarkEntity;
import us.codecraft.webmagic.processor.PageProcessor;

public class XGRemarkPageProcessor implements PageProcessor{
	
	private Logger logger = Logger.getLogger(XGRemarkPageProcessor.class);
	
	private Site site = Site.me().setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31")
								.setRetryTimes(3).setSleepTime(1000);

	@Override
	public void process(Page page) {
		List<XGRemarkEntity> remarkList = new ArrayList<XGRemarkEntity>();
		XGRemarkEntity remarkEntity;
		int pageSize = page.getHtml().xpath("//div[@class='warp']//div[@class='grid730']//div[@class='hot_koubei_content piclistLxy']/ul/li").nodes().size();
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
		
		new MySQLConnector().batchInsertXGSQL(remarkList);
	}

	@Override
	public Site getSite() {
		return site;
	}

	public static void main(String[] args) {
		Spider.create(new XGRemarkPageProcessor()).addUrl("https://product.d1cm.com/wajuejixie/4/").thread(1).run();
	}
	
}
