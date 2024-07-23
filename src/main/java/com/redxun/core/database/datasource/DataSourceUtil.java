package com.redxun.core.database.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.redxun.core.util.StringUtil;
import org.springframework.jdbc.core.JdbcTemplate;

import com.redxun.core.json.JsonResult;
import com.redxun.core.util.AppBeanUtil;
import org.springframework.jdbc.support.JdbcUtils;

/**
 * 数据源工具。 可以动态添加删除数据源。
 * 
 * <pre>
 * 作者：ray
 * 日期:2014-4-11-下午2:56:04
 * 版权：广州红迅软件有限公司版权所有
 * </pre>
 */
public class DataSourceUtil {

	public static final String GLOBAL_DATASOURCE = "dataSource";

	public static final String DEFAULT_DATASOURCE = "dataSource_Default";

	public static final String TARGET_DATASOURCES = "targetDataSources";

	public static final String LOCAL = "LOCAL";

	public static final Map<String, String> driverMap = new HashMap<String, String>();

	static {
		driverMap.put("mysql", "com.mysql.jdbc.Driver");
		driverMap.put("oracle", "oracle.jdbc.driver.OracleDriver");
		driverMap.put("sqlserver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
	}

	/**
	 * 添加数据源 。
	 * 
	 * @param key
	 * @param dataSource
	 *            void
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */
	public static void addDataSource(String key, DataSource dataSource, boolean replace)
			throws IllegalAccessException, NoSuchFieldException {
		DynamicDataSource dynamicDataSource = (DynamicDataSource) AppBeanUtil.getBean(GLOBAL_DATASOURCE);
		if (dynamicDataSource.isDataSourceExist(key)) {
			if (!replace)
				return;
			dynamicDataSource.removeDataSource(key);
		}
		dynamicDataSource.addDataSource(key, dataSource);
	}

	/**
	 * 验证数据库连接是否有效。
	 * @param dbType 数据库类型
	 * @param url	 连接
	 * @param user	用户名
	 * @param pwd	密码
	 * @return
	 */
	public static JsonResult validConn(String dbType, String url, String user, String pwd) {
		String className = driverMap.get(dbType.toLowerCase());
		JsonResult result = new JsonResult(true, "数据连接成功!");
		Connection conn=null;
		try {
			Class.forName(className);
			conn= DriverManager.getConnection(url, user, pwd);
		} catch (Exception se) {
			se.printStackTrace();
			result = new JsonResult(false, se.getMessage());
		}
		finally {
			JdbcUtils.closeConnection(conn);
		}
		return result;

	}

	/**
	 * 根据名字删除数据源。
	 * 
	 * @param key
	 *            void
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */
	public static void removeDataSource(String key) throws IllegalAccessException, NoSuchFieldException {
		DynamicDataSource dynamicDataSource = (DynamicDataSource) AppBeanUtil.getBean(GLOBAL_DATASOURCE);
		dynamicDataSource.removeDataSource(key);
	}

	/**
	 * 取得数据源。
	 * 
	 * @return Map&lt;String,DataSource>
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */
	public static Map<Object, Object> getDataSources() throws IllegalAccessException, NoSuchFieldException {
		DynamicDataSource dynamicDataSource = (DynamicDataSource) AppBeanUtil.getBean(GLOBAL_DATASOURCE);
		return dynamicDataSource.getDataSource();
	}

	/**
	 * 根据别名返回容器里对应的数据源
	 * @param alias
	 * @return DataSource
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 */
	public static DataSource getDataSourceByAlias(String alias) throws IllegalAccessException, NoSuchFieldException {
		if(LOCAL.equals(alias) || StringUtil.isEmpty(alias)){
			alias=DEFAULT_DATASOURCE;
		}
		Map<Object, Object> map = getDataSources();
		for (Object key : map.keySet()) {
			if (alias.equals(key.toString())) {
				return (DataSource) map.get(key);
			}
		}
		return null;
	}
	
	
	/**
	 * 根据数据源别名获取连接。
	 * @param alias
	 * @return
	 * @throws Exception
	 */
	public static Connection getConnectionByAlias(String alias) throws Exception {
		DataSource ds= getDataSourceByAlias(alias);
		if(ds==null) return null;
		return ds.getConnection();
	}
	
	/**
	 * 关闭连接。
	 * @param connection
	 * @throws Exception
	 */
	public static void  close(Connection connection) throws Exception {
		if(connection!=null) connection.close();
	}
	

	/**
	 * 根据数据源别名返回容器里对应的jdbcTemplate
	 * @param alias
	 * @return JdbcTemplate
	 * @throws Exception
	 */
	public static JdbcTemplate getJdbcTempByDsAlias(String alias) throws Exception {
		if (alias.equals(DEFAULT_DATASOURCE) || alias.equals(LOCAL)) {
			return (JdbcTemplate) AppBeanUtil.getBean("jdbcTemplate");
		}
		DataSource ds=DataSourceUtil.getDataSourceByAlias(alias);
		return new JdbcTemplate(ds);
	}

}
