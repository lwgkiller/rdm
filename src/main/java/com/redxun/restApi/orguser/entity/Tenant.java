package com.redxun.restApi.orguser.entity;

/**
 * 租户数据。
 * @author ray
 *
 */
public class Tenant {

	/**
	 * 租户Id
	 */
	private String id="";
	/**
	 * 租户名称
	 */
	private String name="";

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	

}
