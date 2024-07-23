package com.redxun.core.database.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.redxun.core.util.PropertiesUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.saweb.util.WebAppUtil;

/**
 * spring数据源，设置当前数据源。<br>
 * 
 * 设置方法：<br/>
 * 
 * <pre>
 * ApplicationContext c = new ClassPathXmlApplicationContext(locations);
 * 
 * DbContextHolder.setDbType(&quot;dataSource_Default2&quot;);
 * manager.addOracle();
 * 
 * DbContextHolder.setDefaultDbType();
 * manager.addMysql();
 * </pre>
 */
public class DbContextHolder {
	
	/**
	 * 数据库上下文，用于租户切换数据库。
	 */
	private static final ThreadLocal<String> contextDatabase = new ThreadLocal<String>();
	
	private static final ThreadLocal<String> contextHolderAlias = new ThreadLocal<String>();
	private static final ThreadLocal<String> contextHolderDbType = new ThreadLocal<String>();
	
	/**
	 * 设置当前数据库。
	 * @param dbName
	 */
	public static void setCurrentDb(String dbName){
		contextDatabase.set(dbName);
	}
	
	/**
	 * 获取当前DB。
	 * @return
	 */
	public static String getCurrentDb(){
		String str = (String) contextDatabase.get();
		if(StringUtil.isEmpty(str)){
			return PropertiesUtil.getProperty("rootDb","jsaas");
		}
		return str;
	}
	
	public static void clearCurrentDb(){
		contextDatabase.remove();
	}

	/**
	 * 
	 * 设置当前数据库。
	 * 
	 * @param dbAlias 数据源别名
	 * @param dbType 数据源的数据库类型：oracle,mysql... void
	 */
	public static void setDataSource(String dbAlias, String dbType) {
		contextHolderAlias.set(dbAlias);
		contextHolderDbType.set(dbType);
	}
	
	/**
	 * 设置当前数据源。
	 * @param dbAlias
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 */
	public static void setDataSource(String dbAlias) throws IllegalAccessException, NoSuchFieldException{
		
		if(StringUtil.isEmpty(dbAlias) || DataSourceUtil.LOCAL.equals(dbAlias)) {
			setDefaultDataSource();
			return;
		}
		
		
		DruidDataSource ds= (DruidDataSource) DataSourceUtil.getDataSourceByAlias(dbAlias);
		String dbType=ds.getDbType();
		contextHolderAlias.set(dbAlias);
		contextHolderDbType.set(dbType);
	}
	

	/**
	 * 设置默认数据源。
	 */
	public static void setDefaultDataSource() {
		String dbType = WebAppUtil.getDbType();
		contextHolderAlias.remove();
		contextHolderDbType.set(dbType);
	}

	/**
	 * 取得当前数据源。
	 * @return
	 */
	public static String getDataSource() {
		String str = (String) contextHolderAlias.get();
		return str;
	}

	/**
	 * 获取当前的数据库类型。
	 * @return
	 */
	public static String getDbType() {
		String dbType = WebAppUtil.getDbType();
		String str = (String) contextHolderDbType.get();
		if(StringUtil.isEmpty(str)){
			return dbType;
		}
		return str;
	}

	/**
	 * 清除上下文数据
	 */
	public static void clearDataSource() {
		contextHolderAlias.remove();
		contextHolderDbType.remove();
	}

}
