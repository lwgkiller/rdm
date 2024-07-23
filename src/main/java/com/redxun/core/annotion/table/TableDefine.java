package com.redxun.core.annotion.table;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * <pre> 
 * 描述：表模块定义
 * 构建组：ent-base-core
 * 作者：csx
 * 日期:2014年8月19日-下午4:12:04
 * 广州红迅软件有限公司（http://www.redxun.cn）
 * </pre>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)   
@Documented
@Inherited
public @interface TableDefine {
	/**
	 * 表标题
	 * @return 
	 * String
	 * @exception 
	 * @since  1.0.0
	 */
	public String title();
}
