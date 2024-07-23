package com.redxun.core.database.util;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.OracleCodec;

import com.redxun.core.database.datasource.DbContextHolder;
import com.redxun.core.database.util.MySQLCodec.Mode;
import com.redxun.saweb.util.WebAppUtil;

public class DbUtil {
	
	private static OracleCodec ORACLE_CODEC=new OracleCodec();
	private static MySQLCodec MYSQL_CODEC=new MySQLCodec(Mode.STANDARD);
	private static SqlServerCodec SQLSERVER_CODEC=new SqlServerCodec();
	
	/**
	 * 获取列前缀。
	 * @return
	 */
	public static String getColumnPre(){
		String columnPre= WebAppUtil.getProperty("column_pre", "").toUpperCase();
		return columnPre;
	}
	

	
	/**
	 * 获取表前缀。
	 * @return
	 */
	public static String getTablePre(){
		String tablePre= WebAppUtil.getProperty("table_pre",  "");
		return tablePre;
	}
	
	
	/**
	 * 对SQL进行编码。
	 * @param sql
	 * @return
	 */
	public static String encodeSql(String sql){
		 String type=DbContextHolder.getDbType();
		 Codec codec=null;
		 if(SQLConst.DB_MYSQL.equals(type)){
			 codec=MYSQL_CODEC;
		 }
		 else if(SQLConst.DB_ORACLE.equals(type)){
			 codec=ORACLE_CODEC;
		 }
		 else if(SQLConst.DB_SQLSERVER.equals(type)){
			 codec=SQLSERVER_CODEC;
		 }
		 
		 sql = ESAPI.encoder().encodeForSQL(codec, sql);
		 return sql;
	}

	/**
	 * 自定义列表设计，大数据量情况下，根据不同数据库处理分页问题。
	 * @param sql
	 * @return
	 */
	public static String preHandleSql(String sql){
		String type=DbContextHolder.getDbType();
		if(SQLConst.DB_MYSQL.equals(type)){
			sql += " limit 5";

		}else if(SQLConst.DB_ORACLE.equals(type)){
			sql = "select * from (" + sql + ") where rownum 10 order by rownum asc";

		}else if(SQLConst.DB_SQLSERVER.equals(type)){
			sql = sql.toLowerCase().replaceFirst("select","select top 5 ");
		}
		return sql;
	}
}
