package com.redxun.core.annotion.ui;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 字段属性配置定义
 * @author mansan
 * @Email chshxuan@163.com
 * @Copyright (c) 2014-2016 广州红迅软件有限公司（http://www.redxun.cn）
 * 本源代码受软件著作法保护，请在授权允许范围内使用。
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)   
@Documented  
@Inherited 
public @interface FieldConfig {
	public String jsonConfig() default "{}";
}
