package com.redxun.core.engine;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * FreemarkEngine解析引擎。
 * @author csx
 * @Email chshxuan@163.com
 * @Copyright (c) 2014-2016 广州红迅软件有限公司（http://www.redxun.cn）
 * 本源代码受软件著作法保护，请在授权允许范围内使用。
 */
public class FreemarkEngine {
	//配置来自spring-base.xml
	private Configuration configuration;

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	/**
	 * 把指定的模板生成对应的字符串。
	 * @param templateName  模板名，模板的基础路径为：WEB-INF/template目录。
	 * @param model  传入数据对象。
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 */
	public String mergeTemplateIntoString(String templateName,Object model) throws IOException, TemplateException{
		
		Template template=configuration.getTemplate(templateName);
		return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
	}
	
	/**
	 * 处理模板,把内容放进输出流
	 * @param templateName
	 * @param model
	 * @param writer
	 * @throws IOException
	 * @throws TemplateException
	 */
	public void processTemplate(String templateName,Object model,Writer writer) throws IOException, TemplateException{
		Template template=configuration.getTemplate(templateName);
		template.process(model, writer);
	}

	
	
	/**
	 * 根据字符串模版解析出内容
	 * 
	 * @param model 需要解析的对象。
	 * @param templateSource 字符串模版。
	 * @return
	 * @throws TemplateException
	 * @throws IOException
	 */
	public String parseByStringTemplate(Object model, String templateSource) throws TemplateException, IOException {
		Configuration cfg = new Configuration();
		StringTemplateLoader loader = new StringTemplateLoader();
		cfg.setTemplateLoader(loader);
		cfg.setClassicCompatible(true);
		loader.putTemplate("freemaker", templateSource);
		Template template = cfg.getTemplate("freemaker");
		StringWriter writer = new StringWriter();
		template.process(model, writer);
		return writer.toString();

	}
	
}
