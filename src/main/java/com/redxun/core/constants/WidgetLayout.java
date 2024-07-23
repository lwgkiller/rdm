package com.redxun.core.constants;
/**
 * 
 * <pre> 
 * 描述：控件布局
 * 构建组：ent-base-core
 * 作者：csx
 * 邮箱:chensx@jee-soft.cn
 * 日期:2014年10月16日-下午9:40:21
 * 广州红迅软件有限公司（http://www.redxun.cn）
 * </pre>
 */
public enum WidgetLayout {
	/**
	 * form
	 */
	FORM("form"),
	/**
	 * column
	 */
	COLUMN("column"),
	/**
	 * table
	 */
	TABLE("table"),
	/**
	 * accordion
	 */
	ACCORDION("accordion"),
	/**
	 * fit
	 */
	FIT("fit"),
	/**
	 * none
	 */
	NONE("none");
	/**
	 * 布局
	 */
	private String layout;
	
	private WidgetLayout(String layout){
		this.layout=layout;
	}
	
	public String getLayout() {
		return layout;
	}
}
