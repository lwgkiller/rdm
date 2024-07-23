package com.redxun.core.database.factory;

import com.redxun.core.database.api.ITableOperator;
import com.redxun.core.database.util.SQLConst;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * TableOperator factorybean，用户创建ITableOperator对象。
 * 
 * <pre>
 * 配置文件：app-beans.xml
 * &lt;bean id="tableOperator" class="com.redxun.core.database.factory.TableOperatorFactoryBean">
 * 		&lt;property name="dbType" value="${jdbc.dbType}"/>
 * 		&lt;property name="jdbcTemplate" ref="jdbcTemplate"/>
 * &lt;/bean>
 * </pre>
 * 
 * 作者：redxun
 * 版权：广州红迅软件有限公司版权所有
 * 
 */
public class TableOperatorFactoryBean implements FactoryBean<ITableOperator> {
	// 表操作
	private ITableOperator tableOperator;
	// 数据库类型
	private String dbType = SQLConst.DB_MYSQL;
	// JdbcTemplate
	private JdbcTemplate jdbcTemplate;


	@Override
	public ITableOperator getObject() throws Exception {
		tableOperator = DatabaseFactory.getTableOperator(dbType);
		tableOperator.setJdbcTemplate(jdbcTemplate);
		return tableOperator;
	}

	/**
	 * 设置数据库类型
	 * 
	 * @param dbType
	 */
	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	/**
	 * 设置jdbcTemplate
	 * 
	 * @param jdbcTemplate
	 */
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}


	@Override
	public Class<?> getObjectType() {
		return ITableOperator.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
