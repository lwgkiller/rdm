package com.redxun.saweb.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;

import javax.sql.DataSource;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.redxun.core.util.AppBeanUtil;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

/**
 *  Sql工具类
 * @author csx
 * @Email chshxuan@163.com
 * @Copyright (c) 2014-2016 广州红迅软件有限公司（http://www.redxun.cn）
 * 本源代码受软件著作法保护，请在授权允许范围内使用
 */
public class SqlUtil {
	
	protected static Logger logger=LogManager.getLogger(SqlUtil.class);
	/**
	 * 执行脚本内容
	 * @param sqlContent
	 */
	public  static void executeSql(String sqlContent){
		DataSource dataSource=(DataSource)AppBeanUtil.getBean(DataSource.class);
		 Connection conn = null;
		 ScriptRunner runner =null;
		 try{
			 conn=DataSourceUtils.getConnection(dataSource);
			 runner=new ScriptRunner(conn);
			 runner.runScript(new InputStreamReader(new ByteArrayInputStream(sqlContent.getBytes())));
		 }catch(Exception ex){
			 logger.error(ex.getCause().toString());
		 }finally{
			 if(runner!=null){
				runner.closeConnection();
			 }
			 DataSourceUtils.releaseConnection(conn,dataSource);
		 }
	}
	/**
	 * 执行SQL文件
	 * @param filePath 文件路径
	 */
	public static void executeSqlByFile(String filePath){
		executeSqlByFile(new File(filePath));
	}
	/**
	 * 执行SQL文件
	 * @param sqlFile 文件路径
	 */
	public static void executeSqlByFile(File sqlFile){
		DataSource dataSource=(DataSource)AppBeanUtil.getBean(DataSource.class);
		 Connection conn = null;
		 ScriptRunner runner =null;
		 try{
			 conn=DataSourceUtils.getConnection(dataSource);
			 runner=new ScriptRunner(conn);
			 runner.runScript(new InputStreamReader(new FileInputStream(sqlFile)));
		 }catch(Exception ex){
			 logger.error(ex.getCause().toString());
		 }finally{
		 	runner.closeConnection();
		 	DataSourceUtils.releaseConnection(conn,dataSource);
		 }
	}
}
