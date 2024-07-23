package com.redxun.core.database.datasource;

import org.aspectj.lang.JoinPoint;
import org.springframework.jdbc.core.JdbcTemplate;

import com.redxun.core.util.AppBeanUtil;

/**
 * 使用AOP切面对mysq 进行数据库切换。
 * @author ray
 *
 */
public class DbSelectAspect {

	public void doSelectDb(JoinPoint point ){
		JdbcTemplate template=AppBeanUtil.getBean(JdbcTemplate.class);
		String db=DbContextHolder.getCurrentDb();
		template.execute("use " + db);
	}
}
