package com.redxun.core.util;

import java.util.Collection;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 
 * <pre> 
 * 描述：应用Bean工具获取类
 * 构建组：mibase
 * 作者：keith
 * 邮箱:keith@mitom.cn
 * 日期:2014-2-7-上午10:28:51
 * 广州红迅软件有限公司（http://www.redxun.cn）
 * </pre>
 */
public class AppBeanUtil implements ApplicationContextAware{
	
	protected Logger logger=LogManager.getLogger(AppBeanUtil.class);
	
	private static ApplicationContext appContext=null;
    
    
    public static Object getBean(String id){
    	
        return appContext.getBean(id);
    }
    
    /**
     * 判断平台中是否包含指定的ID实例。
     * @param id
     * @return
     */
    public static boolean containBean(String id){
    
    	return appContext.containsBeanDefinition(id);
    }
    
    /**
     * 根据类型获取系统中配置的对象实例。
     * @param beanClass
     * @return
     */
    public static <T> T getBean(Class<T> beanClass){
        return appContext.getBean(beanClass);
    }
    
    /**
     * 根据接口获取平台中的实现类。
     * @param beanClass
     * @return
     */
    public static  <T> Collection<T>  getBeanList(Class<T> beanClass){
    	Map<String, T> map=appContext.getBeansOfType(beanClass);
		return map.values();
    	
    }

	@Override
    public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		appContext=context;
	}
	
}
