package com.redxun.core.cache;

/**
 * 
 * <pre> 
 * 描述：缓存操作接口。
 * 			定义了增加缓存，删除缓存，清除缓存，读取缓存接口。
 * 构建组：ent-base-core
 * 作者：csx
 * 邮箱:chshxuan@163.com
 * 日期:2014年8月1日-下午2:26:00
 * 广州红迅软件有限公司（http://www.redxun.cn）
 * </pre>
 */
public interface ICache<T extends Object> {
	
	public static String SYS_BO_LIST_CACHE="SYS_BO_LIST";
	/**
	 * 添加缓存
	 * @param key
	 * @param obj
	 * @param timeout
	 */
	void add(String key,T obj,int timeout);
	
	void add(String key,T obj);
	
	/**
	 * 根据键删除缓存
	 * @param key
	 */
	void delByKey(String key);
	
	/**
	 * 清除所有的缓存
	 */
	void clearAll();
	
	/**
	 * 读取缓存
	 * @param key
	 * @return
	 */
	Object getByKey(String key);
	
	/**
	 * 包含key。
	 * @param key
	 * @return
	 */
	boolean containKey(String key);
}