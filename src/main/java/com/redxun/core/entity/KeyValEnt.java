package com.redxun.core.entity;

import com.alibaba.fastjson.JSONObject;

public class KeyValEnt<T> {
	
	private String key="";
	private T val=null;
	
	public KeyValEnt() {
	}
	
	
	public KeyValEnt(String key, T val) {
		this.key = key;
		this.val = val;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public T getVal() {
		return val;
	}
	public void setVal(T val) {
		this.val = val;
	}


	@Override
	public String toString() {
		return JSONObject.toJSONString(this);
	}
	
	

}
