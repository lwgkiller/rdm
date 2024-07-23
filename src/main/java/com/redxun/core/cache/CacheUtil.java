package com.redxun.core.cache;

import com.redxun.core.util.AppBeanUtil;

/**
 * 缓存工具类。
 * @author ray
 *
 */
public class CacheUtil {
	
	private static ICache cache=AppBeanUtil.getBean(ICache.class);
	
	
	
	public synchronized static void ensureCache(){
		if(cache!=null) cache=AppBeanUtil.getBean(ICache.class);
	}
	
	/**
	 * 根据键获取缓存。
	 * @param key
	 * @return
	 */
	public static Object getCache(String key){
		return cache.getByKey(key);
	}
	
	/**
	 * 包含键。
	 * @param key
	 * @return
	 */
	public static boolean containKey(String key){
		return cache.containKey(key);
	}
	
	
	
	
	/**
	 * 根据键删除缓存。
	 * @param key
	 */
	public static void delCache(String key){
		cache.delByKey(key);
	}
	
	/**
	 * 添加缓存。
	 * @param key
	 * @param o
	 */
	public static void addCache(String key,Object o){
		cache.add(key, o);
	}
	
	/**
	 * 添加缓存。
	 * @param key
	 * @param o
	 * @param timeout
	 */
	public static void addCache(String key,Object o,int timeout){
		cache.add(key, o,timeout);
	}
	
	

}
