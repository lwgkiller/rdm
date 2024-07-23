package com.redxun.core.constants;
/**
 * 
 * <pre> 
 * 描述：字段权限
 * 构建组：ent-base-core
 * 作者：csx
 * 邮箱:chensx@jee-soft.cn
 * 日期:2014年11月19日-下午10:40:23
 * 广州红迅软件有限公司（http://www.redxun.cn）
 * </pre>
 */
public enum FieldPermision {
	/**
	 * 编辑 
	 */
	EDIT("EDIT"),
	/**
	 * 只读
	 */
	READ("READ"),
	/**
	 * 隐藏
	 */
	HIDE("HIDE");
	
	/**
	 * 编辑权限
	 */
	private String permision;
	
	FieldPermision(String permision){
		this.permision=permision;
	}
	public String getPermision() {
		return permision;
	}
	public void setPermision(String permision) {
		this.permision = permision;
	}
	
}
