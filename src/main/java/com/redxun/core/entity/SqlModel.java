package com.redxun.core.entity;

import java.util.HashMap;
import java.util.Map;

public class SqlModel {
	
	private String dsName="";
	
	

	/**
	 * sql语句格式为
	 * insert into table1 (field1,field2) values (#{field1},#{field2})
	 */
	private String sql="";
	
	/**
	 * 参数。
	 */
	private Map<String,Object> params=new HashMap<String, Object>();
	
	public SqlModel() {
	}

	
	public SqlModel(String sql) {
		this.sql = sql;
	}

	public SqlModel(String sql, Map<String, Object> params) {
		this.sql = sql;
		this.params = params;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}
	
	
	public void addParam(String key,Object obj){
		this.params.put(key, obj);
	}
	
	public String getDsName() {
		return dsName;
	}


	public void setDsName(String dsName) {
		this.dsName = dsName;
	}


}
