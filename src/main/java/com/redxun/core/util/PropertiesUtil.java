package com.redxun.core.util;

import com.redxun.core.bean.PropertyPlaceholderConfigurerExt;

/**
 * 属性工具类。
 * @author ray
 *
 */
public class PropertiesUtil {

	/**
	 * 获取属性值。
	 * @param name
	 * @return
	 */
	public static String getProperty(String name){
		PropertyPlaceholderConfigurerExt ext=AppBeanUtil.getBean(PropertyPlaceholderConfigurerExt.class);
		String val= ext.getProperty(name);
		if(StringUtil.isEmpty(val)) return "";
		return val;
	}
	
	/**
	 * 获取属性值。
	 * @param name
	 * @param defaultVal
	 * @return
	 */
	public static String getProperty(String name,String defaultVal){
		String val=getProperty(name);
		if(StringUtil.isEmpty(val)) return defaultVal;
		return val;
	}
	
	/**
	 * 获取int 的属性值。
	 * @param name
	 * @return
	 */
	public static Integer getIntProperty(String name){
		String val=getProperty(name);
		if(StringUtil.isEmpty(val)) return -1;
		return Integer.parseInt(val);
	}
}
