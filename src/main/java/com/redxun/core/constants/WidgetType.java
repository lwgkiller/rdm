package com.redxun.core.constants;
/**
 * 
 * <pre> 
 * 描述：控件类型
 * 构建组：ent-base-core
 * 作者：csx
 * 邮箱:chensx@jee-soft.cn
 * 日期:2014年9月27日-下午10:55:15
 * 广州红迅软件有限公司（http://www.redxun.cn）
 * </pre>
 */
public enum WidgetType {
	/**
	 * 文本控件
	 */
	HIDDEN("hidden","隐藏控件"),
	/**
	 * 文本控件
	 */
	TEXT("textfield","文本控件"),
	/**
	 * 本地下拉选项
	 */
	LOCAL_COMBO("localcombofield","本地下拉选项"),
	/**
	 * 复选控件
	 */
	CHECKBOX("checkbox","复选控件"),
	/**
	 * 复选控件组
	 */
	CHECKBOX_GROUP("checkboxgroup","复选控件组"),
	/**
	 * 单选控件
	 */
	RADIO("radio","单选控件"),
	/**
	 * 单选控件组
	 */
	RADIO_GROUP("radiogroup","单选控件组"),
	/**
	 * 单选控件,子项有“YES","NO"两值
	 */
	BOOL_RADIO("boolradiofield","是否类型单选控件"),
	/**
	 * 密码控件
	 */
	PASSWORD("password","密码"),
	/**
	 * 多行文件控件
	 */
	TEXTAREA("textarea","多行文本控件"),
	/**
	 * 日期控件
	 */
	DATE("datefield","日期控件"),
	/**
	 * 日期时间控件
	 */
	DATETIME("datetimefield","日期时间控件"),
	/**
	 * 数据控件
	 */
	NUMBER("numberfield","数据控件"),
	/**
	 * 列表子模块
	 */
	LISTMODULE("listmodulefield","列表子模块"),
	/**
	 * 实体模块
	 */
	MODULE("modulefield","实体模块"),
	/**
	 * 字段组
	 */
	FIELDSET("fieldset","字段组"),
	/**
	 * 空
	 */
	NULL("null","空");
	
	/**
	 * 控件类型
	 */
	private String xtype;
	/**
	 * 控件名称
	 */
	private String label;
	
	WidgetType(String xtype,String label){
		this.xtype=xtype;
		this.label=label;
	}
	
	public String getXtype() {
		return xtype;
	}
	
	public String getLabel() {
		return label;
	}

}
