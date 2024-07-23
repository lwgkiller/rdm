package com.redxun.core.cache;

import com.redxun.saweb.util.WebAppUtil;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;


public class EhCache<T> implements ICache<T>{
	private CacheManager cacheManager;
	
	private String cacheName;
	
	public EhCache() {
		cacheManager = CacheManager.create(WebAppUtil.getClassPath()+"/config/ehcache.xml");
	}
	
	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}
	
	public Cache getEhCacheManager(){
		return cacheManager.getCache(cacheName);
	}
 
	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	public CacheManager getCachemanager() {
		return cacheManager;
	}

	@Override
	public void add(String key, T val) {
		Element element = new Element(key, val);
		getEhCacheManager().put(element);
	}

	@Override
	public void add(String key, T val, int timeout) {
		Element element = new Element(key, val);
		element.setTimeToLive(timeout);
		getEhCacheManager().put(element);
	}
	
	@Override
	public void clearAll() {
		getEhCacheManager().removeAll();
	}

	@Override
	public boolean containKey(String key) {
		return getEhCacheManager().isKeyInCache(key);
	}

	@Override
	public void delByKey(String key) {
		getEhCacheManager().remove(key);
	}

	@Override
	public Object getByKey(String key) {
		Element e = getEhCacheManager().get(key);
		if (e == null) {
			return null;
		}
		return e.getObjectValue();
	}

}
