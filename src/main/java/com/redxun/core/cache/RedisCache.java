package com.redxun.core.cache;

import javax.annotation.Resource;

import com.redxun.core.redis.RedisTemplate;

public class RedisCache implements ICache {

	@Resource
	RedisTemplate  redisTemplate;
	
	@Override
	public void add(String key, Object obj, int timeout) {
		redisTemplate.setObject(key,obj,timeout);
		
	}

	@Override
	public void add(String key, Object obj) {
		redisTemplate.setObject(key,obj);
		
	}

	@Override
	public void delByKey(String key) {
		redisTemplate.delByKey(key);
		
	}

	@Override
	public void clearAll() {
		
		
	}

	@Override
	public Object getByKey(String key) {
		
		return redisTemplate.getObject(key);
	}

	@Override
	public boolean containKey(String key) {

		return redisTemplate.isExist(key);
	}

}
