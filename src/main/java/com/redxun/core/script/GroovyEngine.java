package com.redxun.core.script;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import com.redxun.saweb.context.ContextUtil;

import groovy.lang.GroovyShell;


/**
 * Groovy引擎
 * @author mansan
 *@Email chshxuan@163.com
 * @Copyright (c) 2014-2016 广州红迅软件有限公司（http://www.redxun.cn）
 * 本源代码受软件著作法保护，请在授权允许范围内使用。
 */
public class GroovyEngine implements BeanPostProcessor{
	
	protected Logger logger=LogManager.getLogger(GroovyEngine.class);
	
	GroovyBinding groovyBinding=new GroovyBinding();

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		
		boolean isInherit=GroovyScript.class.isAssignableFrom(bean.getClass());
		if (isInherit) {
			groovyBinding.setProperty(beanName, bean);
		}
		return bean;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}
		

	/**
	 * 运行脚本
	 * @param scriptText
	 * @param vars
	 * @return
	 */
	public Object executeScripts(String scriptText,Map<String,Object> vars){
		if(StringUtils.isEmpty(scriptText) || scriptText.trim()=="" ) return null;
		Object result=null;
		try{
			if(vars!=null){
				groovyBinding.setVariables(vars);
			}
			//加入当前用户的上下文
			groovyBinding.getVariables().put("curUser", ContextUtil.getCurrentUser());
			
			GroovyShell shell=new GroovyShell(groovyBinding);
			result=shell.evaluate(scriptText);
		}
		catch(Exception ex){
			logger.error(ex);
			throw new RuntimeException("执行脚本出错! 脚本内容："+ scriptText);
		}
		finally{
			groovyBinding.clearVariables();
		}
		return result;
	}
	
	public static void main(String[] args) {
		
	        System.out.println("Object是String的父类:"+Object.class.isAssignableFrom(String.class));  
	}
}
