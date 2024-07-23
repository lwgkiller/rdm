package com.redxun.core.bean;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
/**
 * Properties配置文件读取类，用于读取资源的配置文件
 * @author csx
 * @Email chshxuan@163.com
 * @Copyright (c) 2014-2016 广州红迅软件有限公司（http://www.redxun.cn）
 * 本源代码受软件著作法保护，请在授权允许范围内使用。
 */
public class PropertyPlaceholderConfigurerExt extends PropertyPlaceholderConfigurer {
	
	private Map<String,String> properties=new HashMap<String,String>();
	
	@Override
	protected void convertProperties(Properties props) {
		 Set<String> keys = props.stringPropertyNames();  
	        for (String key : keys) {  
	            String value = props.getProperty(key);  
	            properties.put(key, value);  
	        } 
		super.convertProperties(props);
	}
	
	public String getProperty(String key){
		return properties.get(key);
	}
	
}
