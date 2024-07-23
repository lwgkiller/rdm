package com.redxun.core.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 * 
 * <pre> 
 * 描述：内存的cache实现
 * 构建组：ent-base-core
 * 作者：csx
 * 邮箱:chenshangxuan@redxun.cn
 * 日期:2014年8月1日-下午2:30:20
 * 广州红迅软件有限公司（http://www.redxun.cn）
 * </pre>
 */
public class MemoryCache<T> implements ICache<T> {
	
	private  Map<String,T> map=new ConcurrentHashMap<String, T>();

	@Override
	public void add(String key, T obj, int timeout) {
		map.put(key, obj);
	}

	@Override
	public void add(String key, T obj) {
		map.put(key, obj);
	}

	@Override
	public void delByKey(String key) {
		map.remove(key);
	}

	@Override
	public void clearAll() {
		map.clear();
	}

	@Override
	public T getByKey(String key) {
		return map.get(key);
	}

	@Override
	public boolean containKey(String key) {
		
		return map.containsKey(key);
	}

}
