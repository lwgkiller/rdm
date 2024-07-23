package com.redxun.core.annotion.cls;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * <pre> 
 * 描述：方法描述
 * 作者：csx
 * 邮箱:
 * 日期:2014年8月19日-下午4:12:04
 * 广州红迅软件有限公司（http://www.redxun.cn）
 * </pre>
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)   
@Documented
@Inherited
public @interface MethodDefine {
	/**
	 * 方法描述
	 * @return 
	 * String
	 * @exception 
	 * @since  1.0.0
	 */
	public String title();
	
	
	/**
	 * 分类 人员脚本
	 * @return
	 */
	public String category() default "";
	/**
	 * 变量参数
	 * @return
	 */
	public ParamDefine[] params() default {};
}
