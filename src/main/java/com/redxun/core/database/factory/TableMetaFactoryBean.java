package com.redxun.core.database.factory;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.jdbc.core.JdbcTemplate;

import com.redxun.core.database.base.BaseTableMeta;
import com.redxun.core.database.datasource.DbContextHolder;
import com.redxun.core.database.util.SQLConst;

/**
 * TableOperator factorybean，用户创建ITableOperator对象。
 * 
 * <pre>
 * 配置文件：app-beans.xml
 * &lt;bean id="tableMetaFactoryBean" class="com.redxun.core.database.factory.TableMetaFactoryBean">
 * 		&lt;property name="dbType" value="${jdbc.dbType}"/>
 * 		&lt;property name="jdbcTemplate" ref="jdbcTemplate"/>
 * &lt;/bean>
 * </pre>
 * 
 * 作者：redxun
 * 版权：广州红迅软件有限公司版权所有
 * 
 */
public class TableMetaFactoryBean implements FactoryBean<BaseTableMeta> {

	private BaseTableMeta tableMeta;


	// 数据源
	private JdbcTemplate jdbcTemplate;

	@Override
	public BaseTableMeta getObject() throws Exception {
		String dbType=DbContextHolder.getDbType();
		tableMeta = DatabaseFactory.getTableMetaByDbType(dbType);
		tableMeta.setJdbcTemplate(jdbcTemplate);
		return tableMeta;
	}

	/**
	 * 设置数据库类型
	 * 
	 * @param dbType
	 */
	public void setDbType(String dbType) {
//		this.dbType = dbType;
	}

	

	/**
	 * @param 设置数据源
	 */
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	

	@Override
	public Class<?> getObjectType() {
		return BaseTableMeta.class;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

}
