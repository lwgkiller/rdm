package com.redxun.core.redis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.redxun.core.util.AppBeanUtil;
import com.redxun.core.util.BeanUtil;
import com.redxun.core.util.ExceptionUtil;
import com.redxun.core.util.FileUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

/**
 * redis 客户端操作。
 * @author ray
 *
 */
public class RedisTemplate {
	
	protected Logger logger=LogManager.getLogger(RedisTemplate.class);
	
	private  JedisSentinelPool pool = null;
	
	private String masterName="";
	private String maxTotal="";
	private String minIdle="";
	private String servers="";
	
	public void setMasterName(String masterName) {
		this.masterName = masterName;
	}

	public void setMaxTotal(String maxTotal) {
		this.maxTotal = maxTotal;
	}

	public void setMinIdle(String minIdle) {
		this.minIdle = minIdle;
	}


	public void setServers(String servers) {
		this.servers = servers;
	}



	/**
	 * 初始化redis池。
	 * redis.masterName:服务名
	 * redis.maxTotal:最大连接数
	 * redis.minIdle:空闲连接
	 * redis.TestOnBorrow:获取连接时判断连接是否有效。
	 * redis.servers:redis服务器配置，指的是哨兵模式。
	 * @throws Exception 
	 */
	public void init() throws Exception{
//		String masterName= sysPropertiesManager.getByAlias("redis.masterName");
//		Integer redisMaxTotal= sysPropertiesManager.getIntByAlias("redis.maxTotal");
//		Integer redisMinIdle= sysPropertiesManager.getIntByAlias("redis.minIdle");
//		boolean blnTestOnBorrow=Boolean.parseBoolean(sysPropertiesManager.getByAlias("redis.TestOnBorrow"));
//		String servers= sysPropertiesManager.getByAlias("redis.servers");
		
		
		JedisPoolConfig config = new JedisPoolConfig();
	    config.setMaxTotal(Integer.parseInt(maxTotal));
	    config.setMinIdle(Integer.parseInt(minIdle) );
	    config.setTestWhileIdle(true);
	    config.setTestOnBorrow(true);
	    config.setTestOnReturn(true);
	    
        Set<String> sentinels = new HashSet<String>();
        String[] aryServer=servers.split(",");
        for(String server:aryServer){
        	sentinels.add(server);
        }
        pool = new JedisSentinelPool(masterName, sentinels, config);
	  
	}
	
	
	
	/**
	 * 获取template。
	 * @return
	 */
	public static RedisTemplate getRedisTemplate(){
		RedisTemplate template=AppBeanUtil.getBean(RedisTemplate.class);
		return template;
	}
	

	/**
	 * 设置字符串键值对。
	 * @param key
	 * @param value
	 */
	public void setString(String key,String value){
		Jedis jedis= getJedis();
		try{
			jedis.set(key, value);
		}
		catch(Exception ex){
			logger.error(ExceptionUtil.getExceptionMessage(ex));
		}
		finally {
			close(jedis);
		}
	}
	
	
	public void setString(String key,String value,int timeOut){
		Jedis jedis= getJedis();
		try{
			jedis.setex(key, timeOut, value);
		}
		catch(Exception ex){
			logger.error(ExceptionUtil.getExceptionMessage(ex));
		}
		finally {
			close(jedis);
		}
	}
	
	/**
	 * 回收jedis。
	 * @param jedis
	 */
	public void close(Jedis jedis){
		if(jedis!=null){
			jedis.close();
		}
	}
	
	/**
	 * 设置对象。
	 * @param key
	 * @param value
	 */
	public void setObject(String key,Object value){
		if(BeanUtil.isEmpty(value)) return ;
		Jedis jedis=  getJedis();
		try{
			byte[] bytes=FileUtil.objToBytes(value);
			jedis.set(key.getBytes("utf-8"), bytes);
		}
		catch(Exception ex){
			logger.error(ExceptionUtil.getExceptionMessage(ex));
		}
		finally {
			close(jedis);
		}
	}
	
	public void setObject(String key,Object value,int expire){
		if(BeanUtil.isEmpty(value)) return ;
		Jedis jedis=  getJedis();
		try{
			byte[] bytes=FileUtil.objToBytes(value);
			jedis.setex(key.getBytes("utf-8"), expire, bytes);
			
		}
		catch(Exception ex){
			logger.error(ExceptionUtil.getExceptionMessage(ex));
		}
		finally {
			close(jedis);
		}
	}
	
	/**
	 * 根据键获取缓存数据。
	 * @param key
	 * @return
	 */
	public String getString(String key){
		Jedis jedis= getJedis();
		try{
			return  jedis.get(key);
		}
		catch(Exception ex){
			logger.error(ExceptionUtil.getExceptionMessage(ex));
			return "";
		}
		finally {
			close(jedis);
		}
	}
	
	/**
	 * 读取对象。
	 * @param key
	 * @return
	 */
	public Object  getObject(String key){
		Jedis jedis= getJedis();
		try{
			byte[] bytes=jedis.get(key.getBytes("utf-8"));
			if(bytes==null) {
				return null;
			}
			return  FileUtil.bytesToObject( bytes);
		}
		catch(Exception ex){
			logger.error(ExceptionUtil.getExceptionMessage(ex));
			return null;
		}
		finally {
			close(jedis);
		}
	}
	
	
	/**
	 * key是否存在。
	 * @param key
	 * @return
	 */
	public boolean isExist(String key){
		Jedis jedis= getJedis();
		try{
			return jedis.exists(key.getBytes("utf-8"));
		}
		catch(Exception ex){
			logger.error(ExceptionUtil.getExceptionMessage(ex));
			return false;
		}
		finally {
			close(jedis);
		}
	}
	
	/**
	 * 获取redis。
	 * @return
	 */
	public synchronized Jedis getJedis(){
		Jedis jedis= pool.getResource();
		return jedis;
	}
	
	/**
	 * 删除。
	 * @param key
	 */
	public void delByKey(String key){
		Jedis jedis= getJedis();
		try{
			jedis.del(key);
		}
		catch(Exception ex){
			logger.error(ExceptionUtil.getExceptionMessage(ex));
		}
		finally {
			close(jedis);
		}
	}
	
	public static void main(String[] args) throws Exception {
		List<String > list=new ArrayList<String>();
		list.add("中文");
		list.add("chinese");
		
		byte[] bytes=FileUtil.objToBytes(list);
		
		List<String> rtList=(List<String>) FileUtil.bytesToObject(bytes);
		for(String str:rtList){
			System.out.println(str);
		}
		
		
	}
	
	/**
	 * 销毁redis连接。
	 */
	public void destroy(){
		pool.destroy();
	}
	
}
