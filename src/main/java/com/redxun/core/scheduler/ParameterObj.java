package com.redxun.core.scheduler;

/**
 * 任务参数对象。
 * 
 * @author redxun
 * 
 */
public class ParameterObj {
	
	
	
	public static final String TYPE_INT = "Integer";
	public static final String TYPE_LONG = "Long";
	public static final String TYPE_FLOAT = "Float";
	public static final String TYPE_BOOLEAN = "Boolean";
	public static final String TYPE_STRING = "String";

	/**
	 * 参数数据类型： int long float boolean
	 */
	private String type = "";
	/**
	 * 参数名称
	 */
	private String name = "";
	/**
	 * 参数值
	 */
	private String value = "";
	
	
	

	public ParameterObj() {
		
	}

	public ParameterObj(String type, String name, String value) {
		this.type = type;
		this.name = name;
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
