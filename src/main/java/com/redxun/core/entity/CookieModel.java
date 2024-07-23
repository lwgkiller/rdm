package com.redxun.core.entity;

public class CookieModel {
	
	private String name="";
	private String value="";
	private String path="";
	private boolean httpOnly=false;
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
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public boolean isHttpOnly() {
		return httpOnly;
	}
	public void setHttpOnly(boolean httpOnly) {
		this.httpOnly = httpOnly;
	}
	@Override
	public String toString() {
		return "CookieModel [name=" + name + ", value=" + value + "]";
	}
	
	

}
