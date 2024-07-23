package com.redxun.core.annotion.ui;
/**
 * 
 * <pre> 
 * 描述：字段对应的配置列表中的配置选项
 * 构建组：ent-base-core
 * 作者：csx
 * 邮箱:chensx@jee-soft.cn
 * 日期:2014年11月22日-下午6:10:25
 * 广州红迅软件有限公司（http://www.redxun.cn）
 * </pre>
 */
public @interface Option {
	/**
	 * 选择名称
	 * @return 
	 * String
	 * @exception 
	 * @since  1.0.0
	 */
	public String name();
	/**
	 * 选项Key或值
	 * @return 
	 * String
	 * @exception 
	 * @since  1.0.0
	 */
	public String key();
}
