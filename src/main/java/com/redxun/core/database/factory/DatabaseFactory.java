package com.redxun.core.database.factory;

import org.apache.commons.lang.StringUtils;

import com.redxun.core.dao.mybatis.dialect.DB2Dialect;
import com.redxun.core.dao.mybatis.dialect.Dialect;
import com.redxun.core.dao.mybatis.dialect.H2Dialect;
import com.redxun.core.dao.mybatis.dialect.MySQLDialect;
import com.redxun.core.dao.mybatis.dialect.OracleDialect;
import com.redxun.core.dao.mybatis.dialect.SQLServer2005Dialect;
import com.redxun.core.database.api.ITableOperator;
import com.redxun.core.database.api.IViewOperator;
import com.redxun.core.database.base.BaseTableMeta;
import com.redxun.core.database.impl.db2.DB2TableMeta;
import com.redxun.core.database.impl.db2.DB2TableOperator;
import com.redxun.core.database.impl.db2.DB2ViewOperator;
import com.redxun.core.database.impl.dm.DmTableMeta;
import com.redxun.core.database.impl.dm.DmTableOperator;
import com.redxun.core.database.impl.dm.DmViewOperator;
import com.redxun.core.database.impl.h2.H2TableMeta;
import com.redxun.core.database.impl.h2.H2TableOperator;
import com.redxun.core.database.impl.h2.H2ViewOperator;
import com.redxun.core.database.impl.mysql.MySQLTableMeta;
import com.redxun.core.database.impl.mysql.MySQLTableOperator;
import com.redxun.core.database.impl.mysql.MySQLViewOperator;
import com.redxun.core.database.impl.oracle.OracleTableMeta;
import com.redxun.core.database.impl.oracle.OracleTableOperator;
import com.redxun.core.database.impl.oracle.OracleViewOperator;
import com.redxun.core.database.impl.sqlserver.SQLServer2008TableMeta;
import com.redxun.core.database.impl.sqlserver.SQLServerTableMeta;
import com.redxun.core.database.impl.sqlserver.SQLServerTableOperator;
import com.redxun.core.database.impl.sqlserver.SQLServerViewOperator;
import com.redxun.core.database.util.SQLConst;

/**
 * 元数据读取工厂。
 * 
 * 作者：redxun
 * 版权：广州红迅软件有限公司版权所有
 * 
 */
public class DatabaseFactory {

	public static String EXCEPTION_MSG = "没有设置合适的数据库类型";

	/**
	 * 通过数据库类型获得表操作
	 * 
	 * @param dbType
	 *            数据库类型
	 * @return
	 * @throws Exception
	 */
	public static ITableOperator getTableOperator(String dbType)
			throws Exception {
		ITableOperator tableOperator = null;
		if (dbType.equals(SQLConst.DB_ORACLE)) {
			tableOperator = new OracleTableOperator();
		} else if (dbType.equals(SQLConst.DB_MYSQL)) {
			tableOperator = new MySQLTableOperator();
		} else if (dbType.equals(SQLConst.DB_SQLSERVER) || dbType.equals(SQLConst.DB_SQLSERVER2008) ||dbType.equals(SQLConst.DB_SQLSERVER2005)) {
			tableOperator = new SQLServerTableOperator();
		} else if (dbType.equals(SQLConst.DB_DB2)) {
			tableOperator = new DB2TableOperator();
		} else if (dbType.equals(SQLConst.DB_H2)) {
			tableOperator = new H2TableOperator();
		} else if (dbType.equals(SQLConst.DB_DM)) {
			tableOperator = new DmTableOperator();
		} else {
			throw new Exception(EXCEPTION_MSG);
		}
		return tableOperator;
	}

	

	/**
	 * 通过数据库类型获得表元操作
	 * 
	 * @param dbType
	 *            数据库类型
	 * @return
	 * @throws Exception
	 */
	public static BaseTableMeta getTableMetaByDbType(String dbType)
			throws Exception {
		BaseTableMeta meta = null;
		if (dbType.equals(SQLConst.DB_ORACLE)) {
			meta = new OracleTableMeta();
		} else if (dbType.equals(SQLConst.DB_MYSQL)) {
			meta = new MySQLTableMeta();
		} else if (dbType.equals(SQLConst.DB_SQLSERVER)||dbType.equals(SQLConst.DB_SQLSERVER2005)) {
			meta = new SQLServerTableMeta();
		}
		else if (dbType.equals(SQLConst.DB_SQLSERVER2008)) {
			meta = new SQLServer2008TableMeta();
		}
		else if (dbType.equals(SQLConst.DB_DB2)) {
			meta = new DB2TableMeta();
		} else if (dbType.equals(SQLConst.DB_H2)) {
			meta = new H2TableMeta();
		} else if (dbType.equals(SQLConst.DB_DM)) {
			meta = new DmTableMeta();
		} else {
			throw new Exception(EXCEPTION_MSG);
		}
		return meta;
	}

	
	/**
	 * 根据数据源获取 视图操作
	 * 
	 * @param dataSource
	 * @return
	 * @throws Exception
	 */
	public static IViewOperator getViewOperator(String dbType)
			throws Exception {
		
		IViewOperator view = null;
		if (dbType.equals(SQLConst.DB_ORACLE)) {
			view = new OracleViewOperator();
		} else if (dbType.equals(SQLConst.DB_MYSQL)) {
			view = new MySQLViewOperator();
		} else if (dbType.equals(SQLConst.DB_SQLSERVER) || dbType.equals(SQLConst.DB_SQLSERVER2008)||dbType.equals(SQLConst.DB_SQLSERVER2005)) {
			view = new SQLServerViewOperator();
		} else if (dbType.equals(SQLConst.DB_DB2)) {
			view = new DB2ViewOperator();
		} else if (dbType.equals(SQLConst.DB_H2)) {
			view = new H2ViewOperator();
		} else if (dbType.equals(SQLConst.DB_DM)) {
			view = new DmViewOperator();
		} else {
			throw new Exception(EXCEPTION_MSG);
		}
		
		return view;
	}

	/**
	 * 
	 * @param dbType
	 * @return
	 * @throws Exception
	 */
	public static Dialect getDialect(String dbType) throws Exception {
		if (StringUtils.isEmpty(dbType))
			throw new Exception(EXCEPTION_MSG);
		Dialect dialect = null;
		if (dbType.equals(SQLConst.DB_ORACLE)) {
			dialect = new OracleDialect();
		} else if (dbType.equals(SQLConst.DB_MYSQL)) {
			dialect = new MySQLDialect();
		} else if (dbType.equals(SQLConst.DB_SQLSERVER) || dbType.equals(SQLConst.DB_SQLSERVER2008)|| dbType.equals(SQLConst.DB_SQLSERVER2005)) {
			dialect = new SQLServer2005Dialect();
		} 
		else if (dbType.equals(SQLConst.DB_DB2)) {
			dialect = new DB2Dialect();
		} else if (dbType.equals(SQLConst.DB_H2)) {
			dialect = new H2Dialect();
		} else if (dbType.equals(SQLConst.DB_DM)) {
			// dialect = new DmDialect();
		} else {
			throw new Exception(EXCEPTION_MSG);
		}
		return dialect;
	}

	
	

}
