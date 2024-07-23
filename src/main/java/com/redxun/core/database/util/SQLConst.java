package com.redxun.core.database.util;

/**
 * 常用数据库变量
 * 
 * <pre>
 * 作者：redxun
 * 版权：广州红迅软件有限公司版权所有
 * </pre>
 * 
 */
public interface SQLConst {

	//数据库类型
	String DB_ORACLE = "oracle";
	String DB_MYSQL = "mysql";
	String DB_SQLSERVER = "mssql";
	String DB_SQLSERVER2008 = "mssql2008";
	String DB_SQLSERVER2005 = "sqlserver";
	String DB_DB2 = "db2";
	String DB_H2 = "h2";
	String DB_SYBASE = "sybase";
	String DB_DM = "dm";


	/*** 自定义表的主键(ID)*/
	String PK_COLUMN_NAME = "ID_";
	/**
	 * 自定义表的外键(REF_ID_)
	 */
	String FK_COLUMN_NAME = "REF_ID_";
	
	/**
	 * 树路径(PATH_)
	 */
	String PATH_COLUMN_NAME = "PATH_";
	
	/**
	 * 自定义字段的字段前缀(F_)
	 */
	String CUSTOMER_COLUMN_PREFIX = "F_";
	
	/**
	 * 自定义表的索引前缀(IDX_)
	 */
	String CUSTOMER_INDEX_PREFIX = "IDX_";



	/**
	 * 默认创建时间
	 */
	String CUSTOMER_COLUMN_CREATETIME = "CREATETIME";

}
