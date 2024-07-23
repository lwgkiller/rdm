package com.redxun.core.annotion.table;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.redxun.core.constants.FieldPermision;
import com.redxun.core.constants.MBoolean;
import com.redxun.core.constants.WidgetType;
/**
 * 类属性定义
 * @author mansan
 *
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)   
@Documented  
@Inherited 
public  @interface FieldDefine {
	/**
	 * 标题
	 * @return 
	 * String
	 * @exception 
	 * @since  1.0.0
	 */
	public String title();

	/**
	 * 分组名
	 * @return 
	 * String
	 * @exception 
	 * @since  1.0.0
	 */
	public String group() default "基本信息";
	/**
	 * 字段控件类型
	 * @return 
	 * FieldType
	 * @exception 
	 * @since  1.0.0
	 */
	public WidgetType widgetType() default WidgetType.NULL;
	
	/**
	 * 是否排序
	 * @return 
	 * MBoolean
	 * @exception 
	 * @since  1.0.0
	 */
	public MBoolean sortable() default MBoolean.YES;
	
	/**
	 * 插入记录的字段权限
	 * @return 
	 * FieldPermision
	 * @exception 
	 * @since  1.0.0
	 */
	public FieldPermision insertable() default FieldPermision.EDIT;
	/**
	 * 编辑字段的权限
	 * @return 
	 * MBoolean
	 * @exception 
	 * @since  1.0.0
	 */
	public FieldPermision editable() default FieldPermision.EDIT;
	/**
	 * 是否为缺省显示列
	 * @return 
	 * String
	 * @exception 
	 * @since  1.0.0
	 */
	public MBoolean defaultCol() default MBoolean.NO;
	/**
	 * 是否可搜索列
	 * @return 
	 * MBoolean
	 * @exception 
	 * @since  1.0.0
	 */
	public MBoolean searchCol() default MBoolean.YES;
}
