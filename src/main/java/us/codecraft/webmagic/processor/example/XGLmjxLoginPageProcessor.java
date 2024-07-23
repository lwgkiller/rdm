package us.codecraft.webmagic.processor.example;

import java.util.Set;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

public class XGLmjxLoginPageProcessor implements PageProcessor {
	/**
	 * for login and spider website 
	 * need install chromedriver(match chrome version)
	 * http://npm.taobao.org/mirrors/chromedriver/
	 */

	private Logger logger = Logger.getLogger(XGLmjxLoginPageProcessor.class);

	private final String loginUrl = "https://user.lmjx.net/account/login?o=1";
	private final String spiderUrl = "https://user.lmjx.net/";
	private final String chromDriverPath = "C:\\Program Files (x86)\\Google\\chromedriver.exe";

	private Set<Cookie> cookies;
	private Site site = Site.me()
			.setUserAgent(
					"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31")
			.setRetryTimes(3).setSleepTime(1000);

	@Override
	public void process(Page page) {
		logger.info("--start--");
		String title = page.getHtml()
				.xpath("//div[@class='pcontent']//div[@class='inf_div']//div[@class='notice_wp']//div[@class='pub_tit']/text()")
				.toString();
		logger.info(title);
	}

	@Override
	public Site getSite() {
		for (Cookie cookie : cookies) {
			logger.info("Name:" + cookie.getName() + "  Value:" + cookie.getValue());
			site.addCookie(cookie.getName(), cookie.getValue());
		}
		return site.addHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/22.0.1207.1 Safari/537.1");
	}

	public void login() throws Exception {
		System.setProperty("webdriver.chrome.driver", chromDriverPath);
		WebDriver driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.get(loginUrl);
		// 获取用户名和密码文本框
		driver.findElement(By.id("i_uname")).sendKeys("15042487250");
		driver.findElement(By.id("i_passwd")).sendKeys("1_p_ssw0rd");
		// 获取点击按钮
		WebElement element = driver.findElement(By.xpath("//*[@id='i_form']/ul/li[5]/input"));
		// 模拟点击
		element.click();
		// 加载速度慢的时候需要sleep一段，否则获取的cookies会为空
		Thread.sleep(5*1000);
		// 很重要的一步获取登陆后的cookies
		cookies = driver.manage().getCookies();
		logger.info("Cookie Size: "+cookies.size());
		driver.close();
	}

	public static void main(String[] args) throws Exception {
		XGLmjxLoginPageProcessor lmjxProcessor = new XGLmjxLoginPageProcessor();
		lmjxProcessor.login();
		Spider.create(lmjxProcessor).addUrl(lmjxProcessor.spiderUrl).run();
	}

}
