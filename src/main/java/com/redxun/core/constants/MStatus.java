package com.redxun.core.constants;

/**
 * 状态常量
 * @author csx
 * @Email chshxuan@163.com
 * @Copyright (c) 2014-2016 广州红迅软件有限公司（http://www.redxun.cn）
 * 本源代码受软件著作法保护，请在授权允许范围内使用。
 */
public enum MStatus {
	/**
	 * 初始化状态
	 */
	INIT("INIT"),
	/**
	 * 禁用状态
	 */
	DISABLED("DISABLED"),
	/**
	 * 启用状态
	 */
	ENABLED("ENABLED"),
	/**
	 * 删除状态
	 */
	DELETED("DELETED"),
	/**
	 * 未审批状态
	 */
	NOTAPPROVED("NOTAPPROVED"),
	/**
	 * 审批中状态
	 */
	APPROVEDING("APPROVEDING"),
	/**
	 * 审批通过状态
	 */
	APPROVED("APPROVED"),
	/**
	 * 拒绝状态
	 */
	 REFUSED("REFUSED");
	
	private String val="";
	
	private MStatus(String val){
		this.val=val;
	}
	
	@Override
	public String toString() {
		return val;
	}
}
