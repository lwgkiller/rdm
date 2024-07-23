package com.redxun.core.database.base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.redxun.core.dao.mybatis.dialect.Dialect;
import com.redxun.core.dao.mybatis.dialect.IDialect;
import com.redxun.core.database.api.IDbType;

/**
 * 
 * 作者：redxun
 * 版权：广州红迅软件有限公司版权所有
 *
 */
public  class BaseDbType implements    IDbType {

		protected static Logger logger=LogManager.getLogger(BaseDbType.class);

		// JdbcTemplate
		protected JdbcTemplate jdbcTemplate;

		// 方言
		protected Dialect dialect;

		@Override
		public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
			this.jdbcTemplate = jdbcTemplate;
		}

		@Override
		public void setDialect(IDialect dialect) {
			this.dialect = (Dialect) dialect;
		}


}
