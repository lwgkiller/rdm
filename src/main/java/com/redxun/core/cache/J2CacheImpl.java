package com.redxun.core.cache;

import java.io.IOException;

import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.J2CacheBuilder;
import net.oschina.j2cache.J2CacheConfig;

public class J2CacheImpl<T>  implements ICache<T> {
	
	private String defaultRegion="default";
	
	public final static String CONFIG_FILE = "/config/j2cache.properties";
	
	CacheChannel cache=null;
	
	public void init(){
		J2CacheConfig config=null;
		try {
			config = J2CacheConfig.initFromConfig(CONFIG_FILE);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		//填充 config 变量所需的配置信息
		J2CacheBuilder builder = J2CacheBuilder.init(config);
		cache= builder.getChannel();
	}
	
	public void destroy(){
		cache.close();
	}

	@Override
	public void add(String key, T obj, int timeout) {
		cache.set(defaultRegion, key, obj,timeout);
		
	}

	@Override
	public void add(String key, T obj) {
		cache.set(defaultRegion, key, obj);
	}

	@Override
	public void delByKey(String key) {
		cache.evict(defaultRegion, key);
	}

	@Override
	public void clearAll() {
		cache.clear(defaultRegion);
	}

	@Override
	public Object getByKey(String key) {
		Object o= cache.get(defaultRegion, key).getValue();
		return o;
	
	}

	@Override
	public boolean containKey(String key) {
		return cache.exists(defaultRegion, key);
	
	}

}
