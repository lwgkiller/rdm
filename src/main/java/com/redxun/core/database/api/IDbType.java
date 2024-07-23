package com.redxun.core.database.api;

import org.springframework.jdbc.core.JdbcTemplate;

import com.redxun.core.dao.mybatis.dialect.IDialect;

/**
 * 数据库类型接口。
 * <pre>
 * 1.设置JdbcTemplate。
 * 2.设置方言。
 * 
 * 作者：redxun
 * 版权：广州红迅软件有限公司版权所有
 * </pre>
 */
public interface IDbType {

	/**
	 * 设置spring 的JDBCTemplate
	 * 
	 * @param template
	 */
	void setJdbcTemplate(JdbcTemplate template);
	
	/**
	 * 设置方言。
	 * 
	 * @param dialect
	 *            
	 */
	void setDialect(IDialect dialect);
}
